package it.unibz.inf.ontop.spec.mapping.pp.impl;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;
import it.unibz.inf.ontop.dbschema.*;
import it.unibz.inf.ontop.exception.InvalidMappingSourceQueriesException;
import it.unibz.inf.ontop.injection.IntermediateQueryFactory;
import it.unibz.inf.ontop.injection.ProvenanceMappingFactory;
import it.unibz.inf.ontop.iq.IQ;
import it.unibz.inf.ontop.iq.IQTree;
import it.unibz.inf.ontop.iq.tools.ExecutorRegistry;
import it.unibz.inf.ontop.iq.transform.NoNullValueEnforcer;
import it.unibz.inf.ontop.model.atom.*;
import it.unibz.inf.ontop.model.term.*;
import it.unibz.inf.ontop.model.term.functionsymbol.ExpressionOperation;
import it.unibz.inf.ontop.model.type.TypeFactory;
import it.unibz.inf.ontop.spec.mapping.MappingWithProvenance;
import it.unibz.inf.ontop.spec.mapping.parser.exception.InvalidSelectQueryException;
import it.unibz.inf.ontop.spec.mapping.parser.exception.UnsupportedSelectQueryException;
import it.unibz.inf.ontop.spec.mapping.parser.impl.RAExpression;
import it.unibz.inf.ontop.spec.mapping.parser.impl.SelectQueryAttributeExtractor;
import it.unibz.inf.ontop.spec.mapping.parser.impl.SelectQueryParser;
import it.unibz.inf.ontop.spec.mapping.pp.PPMappingAssertionProvenance;
import it.unibz.inf.ontop.spec.mapping.pp.SQLPPMapping;
import it.unibz.inf.ontop.spec.mapping.pp.SQLPPMappingConverter;
import it.unibz.inf.ontop.spec.mapping.pp.SQLPPTriplesMap;
import it.unibz.inf.ontop.spec.mapping.transformer.impl.IQ2CQ;
import it.unibz.inf.ontop.substitution.ImmutableSubstitution;
import it.unibz.inf.ontop.substitution.SubstitutionFactory;
import it.unibz.inf.ontop.utils.ImmutableCollectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;


/**
 * SQLPPMapping -> MappingWithProvenance
 */
public class LegacySQLPPMappingConverter implements SQLPPMappingConverter {

    private static final Logger LOGGER = LoggerFactory.getLogger(LegacySQLPPMappingConverter.class);

    private final TermFactory termFactory;
    private final ProvenanceMappingFactory provMappingFactory;
    private final NoNullValueEnforcer noNullValueEnforcer;
    private final IntermediateQueryFactory iqFactory;
    private final TypeFactory typeFactory;
    private final AtomFactory atomFactory;
    private final SubstitutionFactory substitutionFactory;

    @Inject
    private LegacySQLPPMappingConverter(TermFactory termFactory,
                                        ProvenanceMappingFactory provMappingFactory,
                                        NoNullValueEnforcer noNullValueEnforcer,
                                        IntermediateQueryFactory iqFactory,
                                        TypeFactory typeFactory,
                                        AtomFactory atomFactory,
                                        SubstitutionFactory substitutionFactory) {
        this.termFactory = termFactory;
        this.provMappingFactory = provMappingFactory;
        this.noNullValueEnforcer = noNullValueEnforcer;
        this.iqFactory = iqFactory;
        this.typeFactory = typeFactory;
        this.atomFactory = atomFactory;
        this.substitutionFactory = substitutionFactory;
    }

    @Override
    public MappingWithProvenance convert(SQLPPMapping ppMapping, RDBMetadata dbMetadata,
                                         ExecutorRegistry executorRegistry) throws InvalidMappingSourceQueriesException {

        return provMappingFactory.create(convert(ppMapping.getTripleMaps(), dbMetadata), ppMapping.getMetadata());
    }


    /**
     * May also add views in the DBMetadata!
     */

    public ImmutableMap<IQ, PPMappingAssertionProvenance> convert(Collection<SQLPPTriplesMap> triplesMaps,
                                                                    RDBMetadata metadata) throws InvalidMappingSourceQueriesException {
        Map<IQ, PPMappingAssertionProvenance> mutableMap = new HashMap<>();

        List<String> errorMessages = new ArrayList<>();

        for (SQLPPTriplesMap mappingAxiom : triplesMaps) {
            try {
                String sourceQuery = mappingAxiom.getSourceQuery().getSQLQuery();

                ImmutableList<DataAtom<RelationPredicate>> dataAtoms;
                Optional<ImmutableExpression> filter;
                ImmutableMap<QualifiedAttributeID, ImmutableTerm> lookupTable;

                try {
                    SelectQueryParser sqp = new SelectQueryParser(metadata, termFactory, typeFactory, atomFactory);
                    RAExpression re = sqp.parse(sourceQuery);
                    lookupTable = re.getAttributes();
                    dataAtoms = re.getDataAtoms();
                    filter = re.getFilterAtoms().reverse().stream()
                                        .reduce((a, b) -> termFactory.getImmutableExpression(ExpressionOperation.AND, b, a));
                }
                catch (UnsupportedSelectQueryException e) {
                    ImmutableList<QuotedID> attributes = new SelectQueryAttributeExtractor(metadata, termFactory)
                            .extract(sourceQuery);
                    ParserViewDefinition view = metadata.createParserView(sourceQuery, attributes);

                    // this is required to preserve the order of the variables
                    ImmutableList<Map.Entry<QualifiedAttributeID, Variable>> list = view.getAttributes().stream()
                            .map(att -> new AbstractMap.SimpleEntry<>(
                                    new QualifiedAttributeID(null, att.getID()), // strip off the ParserViewDefinitionName
                                    termFactory.getVariable(att.getID().getName())))
                            .collect(ImmutableCollectors.toList());

                    lookupTable = list.stream().collect(ImmutableCollectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

                    ImmutableList<Variable> arguments = list.stream().map(Map.Entry::getValue).collect(ImmutableCollectors.toList());

                    dataAtoms = ImmutableList.of(atomFactory.getDataAtom(view.getAtomPredicate(), arguments));
                    filter = Optional.empty();
                }

                final IQTree tree = IQ2CQ.toIQTree(dataAtoms.stream()
                                .map(a -> iqFactory.createExtensionalDataNode(a))
                                .collect(ImmutableCollectors.toList()),
                        filter, iqFactory);

                for (TargetAtom atom : mappingAxiom.getTargetAtoms()) {
                    PPMappingAssertionProvenance provenance = mappingAxiom.getMappingAssertionProvenance(atom);
                    try {
                        ImmutableMap.Builder<Variable, ImmutableTerm> builder = ImmutableMap.builder();
                        ImmutableList.Builder<Variable> varBuilder2 = ImmutableList.builder();
                        for (Variable v : atom.getProjectionAtom().getArguments()) {
                            ImmutableTerm t = atom.getSubstitution().get(v);
                            if (t != null) {
                                builder.put(v, renameVariables(t, lookupTable, metadata.getQuotedIDFactory()));
                                varBuilder2.add(v);
                            }
                            else {
                                ImmutableTerm tt = renameVariables(v, lookupTable, metadata.getQuotedIDFactory());
                                if (tt instanceof Variable) { // avoids Var -> Var
                                    Variable v2 = (Variable) tt;
                                    varBuilder2.add(v2);
                                }
                                else {
                                    builder.put(v, tt);
                                    varBuilder2.add(v);
                                }
                            }
                        }
                        ImmutableList<Variable> varList = varBuilder2.build();
                        ImmutableSubstitution substitution = substitutionFactory.getSubstitution(builder.build());

                        IQ iq0 = iqFactory.createIQ(
                                    atomFactory.getDistinctVariableOnlyDataAtom(atom.getProjectionAtom().getPredicate(), varList),
                                    iqFactory.createUnaryIQTree(
                                            iqFactory.createConstructionNode(ImmutableSet.copyOf(varList), substitution), tree));

                        IQ iq = noNullValueEnforcer.transform(iq0).liftBinding();

                        PPMappingAssertionProvenance previous = mutableMap.put(iq, provenance);
                        if (previous != null)
                            LOGGER.warn("Redundant triples maps: \n" + provenance + "\n and \n" + previous);
                    }
                    catch (AttributeNotFoundException e) {
                        errorMessages.add("Error: " + e.getMessage()
                                + " \nProblem location: source query of the mapping assertion \n["
                                + provenance.getProvenanceInfo() + "]");
                    }
                }
            }
            catch (InvalidSelectQueryException e) {
                errorMessages.add("Error: " + e.getMessage()
                        + " \nProblem location: source query of triplesMap \n["
                        +  mappingAxiom.getTriplesMapProvenance().getProvenanceInfo() + "]");
            }
        }

        if (!errorMessages.isEmpty())
            throw new InvalidMappingSourceQueriesException(Joiner.on("\n\n").join(errorMessages));

        LOGGER.debug("Original mapping size: {}", mutableMap.size());

        return ImmutableMap.copyOf(mutableMap);
    }



    /**
     * Returns a new function by renaming variables occurring in the {@code function}
     *  according to the {@code attributes} lookup table
     */
    private ImmutableTerm renameVariables(ImmutableTerm term,
                                          ImmutableMap<QualifiedAttributeID, ImmutableTerm> attributes,
                                          QuotedIDFactory idfac) throws AttributeNotFoundException {

            if (term instanceof Variable) {
                Variable var = (Variable) term;
                QuotedID attribute = idfac.createAttributeID(var.getName());
                ImmutableTerm newTerm = attributes.get(new QualifiedAttributeID(null, attribute));

                if (newTerm == null) {
                    QuotedID quotedAttribute = QuotedID.createIdFromDatabaseRecord(idfac, var.getName());
                    newTerm = attributes.get(new QualifiedAttributeID(null, quotedAttribute));

                    if (newTerm == null)
                        throw new AttributeNotFoundException("The source query does not provide the attribute " + attribute
                                + " (variable " + var.getName() + ") required by the target atom.");
                }
                return newTerm;
            }
            else if (term instanceof ImmutableFunctionalTerm) {
                ImmutableFunctionalTerm f = (ImmutableFunctionalTerm)term;
                ImmutableList.Builder<ImmutableTerm> builder = ImmutableList.builder();
                for (ImmutableTerm t : f.getTerms()) // for exception handling
                    builder.add(renameVariables(t, attributes, idfac));
                return termFactory.getImmutableFunctionalTerm(f.getFunctionSymbol(), builder.build());
            }
            else if (term instanceof Constant)
                return term;
            else
                throw new RuntimeException("Unknown term type: " + term);
    }

    private static class AttributeNotFoundException extends Exception {
        AttributeNotFoundException(String message) {
            super(message);
        }
    }
}
