package it.unibz.inf.ontop.spec.mapping.transformer.impl;

import com.google.common.collect.*;
import com.google.inject.Inject;
import it.unibz.inf.ontop.injection.IntermediateQueryFactory;
import it.unibz.inf.ontop.injection.OntopMappingSettings;
import it.unibz.inf.ontop.injection.QueryTransformerFactory;
import it.unibz.inf.ontop.iq.IQ;
import it.unibz.inf.ontop.iq.tools.UnionBasedQueryMerger;
import it.unibz.inf.ontop.iq.transform.QueryRenamer;
import it.unibz.inf.ontop.model.atom.AtomFactory;
import it.unibz.inf.ontop.model.atom.DistinctVariableOnlyDataAtom;
import it.unibz.inf.ontop.model.atom.RDFAtomPredicate;
import it.unibz.inf.ontop.model.term.Variable;
import it.unibz.inf.ontop.model.vocabulary.OWL;
import it.unibz.inf.ontop.spec.mapping.Mapping;
import it.unibz.inf.ontop.spec.mapping.transformer.MappingSameAsInverseRewriter;
import it.unibz.inf.ontop.substitution.InjectiveVar2VarSubstitution;
import it.unibz.inf.ontop.substitution.SubstitutionFactory;
import it.unibz.inf.ontop.utils.ImmutableCollectors;
import it.unibz.inf.ontop.utils.VariableGenerator;
import org.apache.commons.rdf.api.IRI;

import java.util.stream.Stream;

public class MappingSameAsInverseRewriterImpl implements MappingSameAsInverseRewriter {

    private final AtomFactory atomFactory;
    private final IntermediateQueryFactory iqFactory;
    private final QueryTransformerFactory transformerFactory;
    private final SubstitutionFactory substitutionFactory;
    private final UnionBasedQueryMerger queryMerger;
    private final boolean enabled;

    @Inject
    private MappingSameAsInverseRewriterImpl(AtomFactory atomFactory, IntermediateQueryFactory iqFactory,
                                             QueryTransformerFactory transformerFactory,
                                             SubstitutionFactory substitutionFactory, UnionBasedQueryMerger queryMerger,
                                             OntopMappingSettings settings) {
        this.atomFactory = atomFactory;
        this.iqFactory = iqFactory;
        this.transformerFactory = transformerFactory;
        this.substitutionFactory = substitutionFactory;
        this.queryMerger = queryMerger;
        this.enabled = settings.isSameAsInMappingsEnabled();
    }

    @Override
    public Mapping rewrite(Mapping mapping) {
        if (!enabled)
            return mapping;

        ImmutableTable<RDFAtomPredicate, IRI, IQ> mappingUpdate = mapping.getRDFAtomPredicates().stream()
                .flatMap(p -> mapping.getRDFPropertyDefinition(p, OWL.SAME_AS)
                        .map(sameAsDef -> completeSameAsDefinition(sameAsDef, p))
                        .map(sameAsDef -> Tables.immutableCell(p, OWL.SAME_AS, sameAsDef))
                        .map(Stream::of)
                        .orElseGet(Stream::empty))
                .collect(ImmutableCollectors.toTable());

        return mapping.update(mappingUpdate, ImmutableTable.of());
    }

    private IQ completeSameAsDefinition(IQ originalDefinition, RDFAtomPredicate rdfAtomPredicate) {
        ImmutableList<Variable> originalProjectedVariables = originalDefinition.getProjectionAtom().getArguments();

        Variable originalSubject = rdfAtomPredicate.getSubject(originalProjectedVariables);
        Variable originalObject = rdfAtomPredicate.getObject(originalProjectedVariables);

        VariableGenerator originalVariableGenerator = originalDefinition.getVariableGenerator();

        Variable newSubject = originalVariableGenerator.generateNewVariableFromVar(originalSubject);
        Variable newObject = originalVariableGenerator.generateNewVariableFromVar(originalObject);

        DistinctVariableOnlyDataAtom newProjectionAtom = atomFactory.getDistinctVariableOnlyDataAtom(rdfAtomPredicate,
                rdfAtomPredicate.updateSO(originalProjectedVariables, newSubject, newObject));

        /*
         * We shift subjects and objects
         */
        InjectiveVar2VarSubstitution renamingSubstitution = substitutionFactory.getInjectiveVar2VarSubstitution(
                ImmutableMap.of(
                        originalSubject, newObject,
                        originalObject, newSubject));

        QueryRenamer queryRenamer = transformerFactory.createRenamer(renamingSubstitution);

        IQ inversedDefinition = iqFactory.createIQ(newProjectionAtom,
                queryRenamer.transform(originalDefinition).getTree());

        return queryMerger.mergeDefinitions(ImmutableList.of(originalDefinition, inversedDefinition))
                .get()
                .liftBinding();
    }
}
