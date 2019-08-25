package it.unibz.inf.ontop.answering.reformulation.impl;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import it.unibz.inf.ontop.answering.reformulation.ExecutableQuery;
import it.unibz.inf.ontop.answering.reformulation.QueryCache;
import it.unibz.inf.ontop.answering.reformulation.QueryReformulator;
import it.unibz.inf.ontop.answering.reformulation.generation.NativeQueryGenerator;
import it.unibz.inf.ontop.answering.reformulation.input.InputQuery;
import it.unibz.inf.ontop.answering.reformulation.input.InputQueryFactory;
import it.unibz.inf.ontop.answering.reformulation.input.translation.InputQueryTranslator;
import it.unibz.inf.ontop.answering.reformulation.rewriting.QueryRewriter;
import it.unibz.inf.ontop.answering.reformulation.rewriting.SameAsRewriter;
import it.unibz.inf.ontop.answering.reformulation.unfolding.QueryUnfolder;
import it.unibz.inf.ontop.datalog.*;
import it.unibz.inf.ontop.dbschema.DBMetadata;
import it.unibz.inf.ontop.exception.OntopInvalidInputQueryException;
import it.unibz.inf.ontop.exception.OntopReformulationException;
import it.unibz.inf.ontop.injection.OntopReformulationSettings;
import it.unibz.inf.ontop.injection.TranslationFactory;
import it.unibz.inf.ontop.iq.IQ;
import it.unibz.inf.ontop.iq.IntermediateQuery;
import it.unibz.inf.ontop.iq.exception.EmptyQueryException;
import it.unibz.inf.ontop.iq.optimizer.*;
import it.unibz.inf.ontop.iq.tools.ExecutorRegistry;
import it.unibz.inf.ontop.iq.tools.IQConverter;
import it.unibz.inf.ontop.model.term.Variable;
import it.unibz.inf.ontop.spec.OBDASpecification;
import it.unibz.inf.ontop.spec.mapping.Mapping;
import it.unibz.inf.ontop.utils.ImmutableCollectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO: rename it QueryTranslatorImpl ?
 */
public class QuestQueryProcessor implements QueryReformulator {

	private final QueryRewriter rewriter;
	private final NativeQueryGenerator datasourceQueryGenerator;
	private final QueryCache queryCache;

	private final QueryUnfolder queryUnfolder;
	private final SameAsRewriter sameAsRewriter;
	private final BindingLiftOptimizer bindingLiftOptimizer;

	private static final Logger log = LoggerFactory.getLogger(QuestQueryProcessor.class);
	private final ExecutorRegistry executorRegistry;
	private final OntopReformulationSettings settings;
	private final DBMetadata dbMetadata;
	private final JoinLikeOptimizer joinLikeOptimizer;
	private final InputQueryTranslator inputQueryTranslator;
	private final InputQueryFactory inputQueryFactory;
	private final DatalogFactory datalogFactory;
	private final FlattenUnionOptimizer flattenUnionOptimizer;
	private final EQNormalizer eqNormalizer;
	private final PushUpBooleanExpressionOptimizer pullUpExpressionOptimizer;
	private final IQConverter iqConverter;
    private final DatalogProgram2QueryConverter datalogConverter;

	@AssistedInject
	private QuestQueryProcessor(@Assisted OBDASpecification obdaSpecification,
                                @Assisted ExecutorRegistry executorRegistry,
                                QueryCache queryCache,
                                BindingLiftOptimizer bindingLiftOptimizer, OntopReformulationSettings settings,
                                TranslationFactory translationFactory,
                                QueryRewriter queryRewriter,
                                JoinLikeOptimizer joinLikeOptimizer,
                                InputQueryFactory inputQueryFactory,
                                DatalogFactory datalogFactory,
                                FlattenUnionOptimizer flattenUnionOptimizer,
                                EQNormalizer eqNormalizer,
                                PushUpBooleanExpressionOptimizer pullUpExpressionOptimizer,
                                IQConverter iqConverter, DatalogProgram2QueryConverter datalogConverter) {
		this.bindingLiftOptimizer = bindingLiftOptimizer;
		this.settings = settings;
		this.joinLikeOptimizer = joinLikeOptimizer;
		this.inputQueryFactory = inputQueryFactory;
		this.datalogFactory = datalogFactory;
		this.flattenUnionOptimizer = flattenUnionOptimizer;
		this.eqNormalizer = eqNormalizer;
		this.pullUpExpressionOptimizer = pullUpExpressionOptimizer;
		this.iqConverter = iqConverter;
		this.rewriter = queryRewriter;
        this.datalogConverter = datalogConverter;

        this.rewriter.setTBox(obdaSpecification.getSaturatedTBox());

		Mapping saturatedMapping = obdaSpecification.getSaturatedMapping();

		if(log.isDebugEnabled()){
			log.debug("Mapping: \n{}", Joiner.on("\n").join(
					saturatedMapping.getRDFAtomPredicates().stream()
						.flatMap(p -> saturatedMapping.getQueries(p).stream())
						.iterator()));
		}

		this.queryUnfolder = translationFactory.create(saturatedMapping);

		this.dbMetadata = obdaSpecification.getDBMetadata();
		this.datasourceQueryGenerator = translationFactory.create(dbMetadata);
		this.inputQueryTranslator = translationFactory.createInputQueryTranslator(saturatedMapping.getMetadata()
				.getUriTemplateMatcher());
		this.sameAsRewriter = translationFactory.createSameAsRewriter(saturatedMapping);
		this.queryCache = queryCache;
		this.executorRegistry = executorRegistry;

		log.info("Ontop has completed the setup and it is ready for query answering!");
	}

	private IQ preProcess(InternalSparqlQuery translation) throws OntopInvalidInputQueryException, EmptyQueryException {
		DatalogProgram program = translation.getProgram();
		log.debug("Datalog program translated from the SPARQL query: \n{}", program);

		if(settings.isSameAsInMappingsEnabled()){
			program = sameAsRewriter.getSameAsRewriting(program);
			log.debug("Datalog program after SameAs rewriting \n{}", program);
		}

		log.debug("Replacing equivalences...");
		DatalogProgram newprogramEq = datalogFactory.getDatalogProgram(program.getQueryModifiers());
		for (CQIE query : program.getRules()) {
			CQIE rule = query.clone();
			// EQNormalizer cannot be removed because it is used in NULL propagation in OPTIONAL
			eqNormalizer.enforceEqualities(rule);
			newprogramEq.appendRule(rule);
		}

		if (newprogramEq.getRules().isEmpty())
			throw new OntopInvalidInputQueryException("Error, the translation of the query generated 0 rules. " +
					"This is not possible for any SELECT query (other queries are not supported by the translator).");



        return  datalogConverter.convertDatalogProgram(newprogramEq, translation.getSignature());
    }
	


	@Override
	public ExecutableQuery reformulateIntoNativeQuery(InputQuery inputQuery)
			throws OntopReformulationException {

		ExecutableQuery cachedQuery = queryCache.get(inputQuery);
		if (cachedQuery != null)
			return cachedQuery;

		try {
            InternalSparqlQuery translation = inputQuery.translate(inputQueryTranslator);

            try {
                IQ convertedIQ = preProcess(translation);

                log.debug("Start the rewriting process...");
                IQ rewrittenIQ = rewriter.rewrite(convertedIQ);

                log.debug("Directly translated (SPARQL) IQ: \n" + rewrittenIQ.toString());

                log.debug("Start the unfolding...");

                IQ unfoldedIQ = queryUnfolder.optimize(rewrittenIQ);
                if (unfoldedIQ.getTree().isDeclaredAsEmpty())
                    throw new EmptyQueryException();
                log.debug("Unfolded query: \n" + unfoldedIQ.toString());

                // Non-final
                IntermediateQuery intermediateQuery = iqConverter.convert(unfoldedIQ, executorRegistry);

                //lift bindings and union when it is possible
                intermediateQuery = bindingLiftOptimizer.optimize(intermediateQuery);
                log.debug("New query after substitution lift optimization: \n" + intermediateQuery.toString());

                log.debug("New lifted query: \n" + intermediateQuery.toString());

                intermediateQuery = pullUpExpressionOptimizer.optimize(intermediateQuery);
                log.debug("After pushing up boolean expressions: \n" + intermediateQuery.toString());

                intermediateQuery = new ProjectionShrinkingOptimizer().optimize(intermediateQuery);

                log.debug("After projection shrinking: \n" + intermediateQuery.toString());


                intermediateQuery = joinLikeOptimizer.optimize(intermediateQuery);
                log.debug("New query after fixed point join optimization: \n" + intermediateQuery.toString());

                intermediateQuery = flattenUnionOptimizer.optimize(intermediateQuery);
                log.debug("New query after flattening Unions: \n" + intermediateQuery.toString());

                ExecutableQuery executableQuery = generateExecutableQuery(intermediateQuery);
                queryCache.put(inputQuery, executableQuery);
                return executableQuery;

            }
            catch (EmptyQueryException e) {
                // No solution.
                ExecutableQuery emptyQuery = datasourceQueryGenerator.generateEmptyQuery(
                		translation.getSignature().stream()
								.map(Variable::getName)
								.collect(ImmutableCollectors.toList()));

                log.debug("Empty query --> no solution.");
                queryCache.put(inputQuery, emptyQuery);
                return emptyQuery;
            }
            catch (OntopReformulationException e) {
                throw e;
            }
        }
		/*
		 * Bug: should normally not be reached
		 * TODO: remove it
		 */
		catch (Exception e) {
			log.warn("Unexpected exception: " + e.getMessage(), e);
			throw new OntopReformulationException(e);
			//throw new OntopReformulationException("Error rewriting and unfolding into SQL\n" + e.getMessage());
		}
	}

	private ExecutableQuery generateExecutableQuery(IntermediateQuery intermediateQuery)
			throws OntopReformulationException {
		log.debug("Producing the native query string...");

		ExecutableQuery executableQuery = datasourceQueryGenerator.generateSourceQuery(intermediateQuery);

		log.debug("Resulting native query: \n{}", executableQuery);

		return executableQuery;
	}


	/**
	 * Returns the final rewriting of the given query
	 */
	@Override
	public String getRewritingRendering(InputQuery query) throws OntopReformulationException {
		InternalSparqlQuery translation = query.translate(inputQueryTranslator);
		try {
            IQ converetedIQ = preProcess(translation);
			IQ rewrittenIQ = rewriter.rewrite(converetedIQ);
			return rewrittenIQ.toString();
		}
		catch (EmptyQueryException e) {
			e.printStackTrace();
		}
		return "EMPTY REWRITING";
	}

	@Override
	public InputQueryFactory getInputQueryFactory() {
		return inputQueryFactory;
	}
}
