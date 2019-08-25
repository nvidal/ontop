package it.unibz.inf.ontop.iq.executor;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import it.unibz.inf.ontop.iq.exception.EmptyQueryException;
import it.unibz.inf.ontop.iq.node.*;
import it.unibz.inf.ontop.iq.*;
import it.unibz.inf.ontop.iq.optimizer.impl.AbstractIntensionalQueryMerger;
import it.unibz.inf.ontop.model.atom.AtomPredicate;
import it.unibz.inf.ontop.model.atom.DistinctVariableOnlyDataAtom;
import it.unibz.inf.ontop.model.term.functionsymbol.DatatypePredicate;
import it.unibz.inf.ontop.model.term.*;
import it.unibz.inf.ontop.model.vocabulary.RDF;
import it.unibz.inf.ontop.model.vocabulary.XSD;
import it.unibz.inf.ontop.utils.VariableGenerator;
import org.apache.commons.rdf.api.IRI;
import org.junit.Ignore;
import org.junit.Test;

import java.util.*;

import static it.unibz.inf.ontop.NoDependencyTestDBMetadata.*;
import static junit.framework.TestCase.assertEquals;
import static it.unibz.inf.ontop.OptimizationTestingTools.*;

public class QueryMergingTest {

    private static AtomPredicate ANS0_PREDICATE = ATOM_FACTORY.getRDFAnswerPredicate( 0);
    private static AtomPredicate ANS1_PREDICATE = ATOM_FACTORY.getRDFAnswerPredicate( 2);
    private static AtomPredicate ANS2_PREDICATE = ATOM_FACTORY.getRDFAnswerPredicate( 1);
    private static AtomPredicate ANS4_PREDICATE = ATOM_FACTORY.getRDFAnswerPredicate( 3);

    private static IRI P1_IRI = RDF_FACTORY.createIRI("http://example.com/voc#p1");
    private static IRI C3_IRI = RDF_FACTORY.createIRI("http://example.com/voc#C3");
    private static Variable X = TERM_FACTORY.getVariable("x");
    private static Variable Y = TERM_FACTORY.getVariable("y");
    private static Variable Z = TERM_FACTORY.getVariable("z");
    private static Variable S = TERM_FACTORY.getVariable("s");
    private static Variable T = TERM_FACTORY.getVariable("t");
    private static Variable P = TERM_FACTORY.getVariable("p");
    private static Variable O = TERM_FACTORY.getVariable("o");
    private static Variable R = TERM_FACTORY.getVariable("r");
    private static Variable U = TERM_FACTORY.getVariable("u");
    private static Variable A = TERM_FACTORY.getVariable("a");
    private static Variable B = TERM_FACTORY.getVariable("b");
    private static Variable BF3F7 = TERM_FACTORY.getVariable("bf3f7");
    private static Variable C = TERM_FACTORY.getVariable("c");
    private static Variable D = TERM_FACTORY.getVariable("d");
    private static Variable DF6 = TERM_FACTORY.getVariable("df6");
    private static Variable E = TERM_FACTORY.getVariable("e");
    private static DistinctVariableOnlyDataAtom ANS1_XY_ATOM = ATOM_FACTORY.getDistinctVariableOnlyDataAtom(
            ANS1_PREDICATE, ImmutableList.of(X, Y));
    private static DistinctVariableOnlyDataAtom ANS1_X_ATOM = ATOM_FACTORY.getDistinctVariableOnlyDataAtom(
            ANS2_PREDICATE, ImmutableList.of(X));
    private static DistinctVariableOnlyDataAtom ANS0_ATOM = ATOM_FACTORY.getDistinctVariableOnlyDataAtom(
            ANS0_PREDICATE, ImmutableList.of());
    private static DistinctVariableOnlyDataAtom P1_ST_ATOM = ATOM_FACTORY.getDistinctTripleAtom(S, P, T);
    private static Constant URI_TEMPLATE_STR_1 = TERM_FACTORY.getConstantLiteral("http://example.org/ds1/{}");
    private static Constant URI_TEMPLATE_STR_2 = TERM_FACTORY.getConstantLiteral("http://example.org/ds2/{}");
    private static Constant URI_TEMPLATE_STR_3 = TERM_FACTORY.getConstantLiteral("http://example.org/ds3/{}/{}");
    private static Constant ONE = TERM_FACTORY.getConstantLiteral("1", TYPE_FACTORY.getXsdIntegerDatatype());
    private static Constant TWO = TERM_FACTORY.getConstantLiteral("2", TYPE_FACTORY.getXsdIntegerDatatype());
    private static DatatypePredicate XSD_INTEGER = TERM_FACTORY.getRequiredTypePredicate(TYPE_FACTORY.getXsdIntegerDatatype());
    private static Constant THREE = TERM_FACTORY.getConstantLiteral("3", XSD.INTEGER);
    private static GroundTerm INT_OF_THREE = (GroundTerm) TERM_FACTORY.getImmutableFunctionalTerm(XSD_INTEGER, THREE);
    private static GroundTerm INT_OF_ONE = (GroundTerm) TERM_FACTORY.getImmutableFunctionalTerm(XSD_INTEGER, ONE);
    private static GroundTerm INT_OF_TWO = (GroundTerm) TERM_FACTORY.getImmutableFunctionalTerm(XSD_INTEGER, TWO);
    private static ImmutableFunctionalTerm INT_OF_B = TERM_FACTORY.getImmutableFunctionalTerm(XSD_INTEGER, B);
    private static ExtensionalDataNode DATA_NODE_1 = IQ_FACTORY.createExtensionalDataNode(ATOM_FACTORY.getDataAtom(TABLE1_AR2, A, B));
    private static ExtensionalDataNode DATA_NODE_3 = IQ_FACTORY.createExtensionalDataNode(ATOM_FACTORY.getDataAtom(TABLE3_AR2, B, C));
    private static ExtensionalDataNode DATA_NODE_4 = IQ_FACTORY.createExtensionalDataNode(ATOM_FACTORY.getDataAtom(TABLE1_AR2, S, T));

    @Test
    public void testPruning1() throws EmptyQueryException {
        GroundFunctionalTerm xValue = (GroundFunctionalTerm) generateURI1(ONE);

        IntermediateQuery mainQuery = createBasicSparqlQuery(ImmutableMap.of(X, xValue), xValue, Y);

        /**
         * Sub-query
         */
        IntermediateQueryBuilder subQueryBuilder = createQueryBuilder(DB_METADATA);
        ConstructionNode subQueryRoot = IQ_FACTORY.createConstructionNode(P1_ST_ATOM.getVariables(),
                SUBSTITUTION_FACTORY.getSubstitution(ImmutableMap.of(T, generateURI1(B),
                        P, generateGroundTerm(P1_IRI))));
        subQueryBuilder.init(P1_ST_ATOM, subQueryRoot);
        ImmutableSet<Variable> unionProjectedVariables = ImmutableSet.of(S, B);
        UnionNode unionNode = IQ_FACTORY.createUnionNode(unionProjectedVariables);
        subQueryBuilder.addChild(subQueryRoot, unionNode);

        ConstructionNode leftConstructionNode = IQ_FACTORY.createConstructionNode(unionProjectedVariables,
                SUBSTITUTION_FACTORY.getSubstitution(ImmutableMap.of(S, generateURI2(A))));
        subQueryBuilder.addChild(unionNode, leftConstructionNode);
        subQueryBuilder.addChild(leftConstructionNode, DATA_NODE_1);

        ConstructionNode rightConstructionNode = IQ_FACTORY.createConstructionNode(unionProjectedVariables,
                SUBSTITUTION_FACTORY.getSubstitution(ImmutableMap.of(S, generateURI1(C))));
        subQueryBuilder.addChild(unionNode, rightConstructionNode);
        subQueryBuilder.addChild(rightConstructionNode, DATA_NODE_3);

        /**
         * Expected
         */
        IntermediateQueryBuilder expectedBuilder = createQueryBuilder(DB_METADATA);
        QueryNode expectedRootNode = mainQuery.getRootNode();
        expectedBuilder.init(mainQuery.getProjectionAtom(), expectedRootNode);
        ConstructionNode remainingConstructionNode = IQ_FACTORY.createConstructionNode(ImmutableSet.of(Y),
                SUBSTITUTION_FACTORY.getSubstitution(ImmutableMap.of(Y, generateURI1(B))));
        expectedBuilder.addChild(expectedRootNode, remainingConstructionNode);

        ExtensionalDataNode expectedDataNode = IQ_FACTORY.createExtensionalDataNode(ATOM_FACTORY.getDataAtom(TABLE3_AR2, B, ONE));
        expectedBuilder.addChild(remainingConstructionNode, expectedDataNode);


        optimizeAndCompare(mainQuery, subQueryBuilder.build(), expectedBuilder.build(), mainQuery.getIntensionalNodes().findFirst().get());
    }

    private GroundFunctionalTerm generateGroundTerm(IRI iri) {
        return (GroundFunctionalTerm) TERM_FACTORY.getImmutableUriTemplate(
                TERM_FACTORY.getConstantLiteral(iri.getIRIString()));
    }


    @Test
    public void testEx1() throws EmptyQueryException {

        /*
         * Original query
         */
        IntermediateQueryBuilder queryBuilder = createQueryBuilder(DB_METADATA);

        ConstructionNode rootNode = IQ_FACTORY.createConstructionNode(ANS1_X_ATOM.getVariables(),
                SUBSTITUTION_FACTORY.getSubstitution(ImmutableMap.of()));

        queryBuilder.init(ANS1_X_ATOM, rootNode);

        IntensionalDataNode dataNode = IQ_FACTORY.createIntensionalDataNode(
                ATOM_FACTORY.getIntensionalTripleAtom(X, P1_IRI, INT_OF_THREE));
        queryBuilder.addChild(rootNode, dataNode);

        IntermediateQuery mainQuery = queryBuilder.build();

        /*
         * Sub-queryI
         */
        IntermediateQueryBuilder subQueryBuilder = createQueryBuilder(DB_METADATA);
        ConstructionNode subQueryRoot = IQ_FACTORY.createConstructionNode(P1_ST_ATOM.getVariables(),
                SUBSTITUTION_FACTORY.getSubstitution(ImmutableMap.of(
                        S, generateURI1(A),
                        T, INT_OF_B,
                        P, generateGroundTerm(P1_IRI))));
        subQueryBuilder.init(P1_ST_ATOM, subQueryRoot);
        subQueryBuilder.addChild(subQueryRoot, DATA_NODE_1);


        /*
         * Expected
         */
        IntermediateQueryBuilder expectedBuilder = createQueryBuilder(DB_METADATA);
        QueryNode expectedRootNode = mainQuery.getRootNode();
        expectedBuilder.init(mainQuery.getProjectionAtom(), expectedRootNode);
        ConstructionNode remainingConstructionNode = IQ_FACTORY.createConstructionNode(ImmutableSet.of(X),
                SUBSTITUTION_FACTORY.getSubstitution(ImmutableMap.of(X, generateURI1(A))));
        expectedBuilder.addChild(expectedRootNode, remainingConstructionNode);

        ExtensionalDataNode expectedDataNode = IQ_FACTORY.createExtensionalDataNode(ATOM_FACTORY.getDataAtom(TABLE1_AR2, A, THREE));
        expectedBuilder.addChild(remainingConstructionNode, expectedDataNode);

        optimizeAndCompare(mainQuery, subQueryBuilder.build(), expectedBuilder.build(), dataNode);
    }

    @Test
    public void testEx2() throws EmptyQueryException {

        /*
         * Original query
         */
        IntermediateQueryBuilder queryBuilder = createQueryBuilder(DB_METADATA);

        ConstructionNode rootNode = IQ_FACTORY.createConstructionNode(ANS1_X_ATOM.getVariables(),
                SUBSTITUTION_FACTORY.getSubstitution(ImmutableMap.of()));

        queryBuilder.init(ANS1_X_ATOM, rootNode);

        IntensionalDataNode dataNode = IQ_FACTORY.createIntensionalDataNode(
                ATOM_FACTORY.getIntensionalTripleAtom(X, P1_IRI, X));
        queryBuilder.addChild(rootNode, dataNode);

        IntermediateQuery mainQuery = queryBuilder.build();

        /*
         * Sub-query
         */
        IntermediateQueryBuilder subQueryBuilder = createQueryBuilder(DB_METADATA);
        ConstructionNode subQueryRoot = IQ_FACTORY.createConstructionNode(P1_ST_ATOM.getVariables(),
                SUBSTITUTION_FACTORY.getSubstitution(ImmutableMap.of(
                        S, generateURI1(A),
                        T, generateURI1(B),
                        P, generateGroundTerm(P1_IRI))));
        subQueryBuilder.init(P1_ST_ATOM, subQueryRoot);
        subQueryBuilder.addChild(subQueryRoot, DATA_NODE_1);

        /*
         * Expected
         */
        IntermediateQueryBuilder expectedBuilder = createQueryBuilder(DB_METADATA);
        QueryNode expectedRootNode = mainQuery.getRootNode();
        expectedBuilder.init(mainQuery.getProjectionAtom(), expectedRootNode);
        ConstructionNode remainingConstructionNode = IQ_FACTORY.createConstructionNode(ImmutableSet.of(X),
                SUBSTITUTION_FACTORY.getSubstitution(ImmutableMap.of(X, generateURI1(A))));
        expectedBuilder.addChild(expectedRootNode, remainingConstructionNode);

        ExtensionalDataNode expectedDataNode = IQ_FACTORY.createExtensionalDataNode(ATOM_FACTORY.getDataAtom(TABLE1_AR2, A, A));
        expectedBuilder.addChild(remainingConstructionNode, expectedDataNode);

        optimizeAndCompare(mainQuery, subQueryBuilder.build(), expectedBuilder.build(), dataNode);
    }

    @Test
    public void testEx3() throws EmptyQueryException {

        /*
         * Original query
         */
        IntermediateQueryBuilder queryBuilder = createQueryBuilder(DB_METADATA);

        ConstructionNode rootNode = IQ_FACTORY.createConstructionNode(ANS1_X_ATOM.getVariables(),
                SUBSTITUTION_FACTORY.getSubstitution(ImmutableMap.of()));

        queryBuilder.init(ANS1_X_ATOM, rootNode);

        IntensionalDataNode dataNode = IQ_FACTORY.createIntensionalDataNode(
                ATOM_FACTORY.getIntensionalTripleAtom(X, P1_IRI, INT_OF_THREE));
        queryBuilder.addChild(rootNode, dataNode);

        IntermediateQuery mainQuery = queryBuilder.build();

        /*
         * Sub-query
         */
        IntermediateQueryBuilder subQueryBuilder = createQueryBuilder(DB_METADATA);
        ConstructionNode subQueryRoot = IQ_FACTORY.createConstructionNode(P1_ST_ATOM.getVariables(),
                SUBSTITUTION_FACTORY.getSubstitution(ImmutableMap.of(
                        P, generateGroundTerm(P1_IRI)
                )));
        subQueryBuilder.init(P1_ST_ATOM, subQueryRoot);
        subQueryBuilder.addChild(subQueryRoot, DATA_NODE_4);

        /*
         * Expected
         */
        IntermediateQueryBuilder expectedBuilder = createQueryBuilder(DB_METADATA);
        QueryNode expectedRootNode = mainQuery.getRootNode();
        expectedBuilder.init(mainQuery.getProjectionAtom(), expectedRootNode);
        ExtensionalDataNode expectedDataNode = IQ_FACTORY.createExtensionalDataNode(ATOM_FACTORY.getDataAtom(TABLE1_AR2, X, INT_OF_THREE));
        expectedBuilder.addChild(expectedRootNode, expectedDataNode);

        optimizeAndCompare(mainQuery, subQueryBuilder.build(), expectedBuilder.build(), dataNode);
    }

    @Test
    public void testEx4() throws EmptyQueryException {

        /*
         * Original query
         */
        IntermediateQueryBuilder queryBuilder = createQueryBuilder(DB_METADATA);

        ConstructionNode rootNode = IQ_FACTORY.createConstructionNode(ANS1_X_ATOM.getVariables(),
                SUBSTITUTION_FACTORY.getSubstitution(ImmutableMap.of()));

        queryBuilder.init(ANS1_X_ATOM, rootNode);

        IntensionalDataNode dataNode = IQ_FACTORY.createIntensionalDataNode(
                ATOM_FACTORY.getIntensionalTripleAtom(X, P1_IRI, X));
        queryBuilder.addChild(rootNode, dataNode);

        IntermediateQuery mainQuery = queryBuilder.build();

        /*
         * Sub-query
         */
        IntermediateQueryBuilder subQueryBuilder = createQueryBuilder(DB_METADATA);
        ConstructionNode subQueryRoot = IQ_FACTORY.createConstructionNode(P1_ST_ATOM.getVariables(),
                SUBSTITUTION_FACTORY.getSubstitution(ImmutableMap.of(P, generateGroundTerm(P1_IRI))));
        subQueryBuilder.init(P1_ST_ATOM, subQueryRoot);
        subQueryBuilder.addChild(subQueryRoot, DATA_NODE_4);

        /*
         * Expected
         */
        IntermediateQueryBuilder expectedBuilder = createQueryBuilder(DB_METADATA);
        QueryNode expectedRootNode = mainQuery.getRootNode();
        expectedBuilder.init(mainQuery.getProjectionAtom(), expectedRootNode);


        ExtensionalDataNode expectedDataNode = IQ_FACTORY.createExtensionalDataNode(ATOM_FACTORY.getDataAtom(TABLE1_AR2, X, X));
        expectedBuilder.addChild(expectedRootNode, expectedDataNode);

        optimizeAndCompare(mainQuery, subQueryBuilder.build(), expectedBuilder.build(), dataNode);
    }

    @Test
    public void testEx5() throws EmptyQueryException {

        /*
         * Original query
         */
        IntermediateQueryBuilder queryBuilder = createQueryBuilder(DB_METADATA);

        ConstructionNode rootNode = IQ_FACTORY.createConstructionNode(ANS1_XY_ATOM.getVariables(),
                SUBSTITUTION_FACTORY.getSubstitution(ImmutableMap.of()));

        queryBuilder.init(ANS1_XY_ATOM, rootNode);

        IntensionalDataNode dataNode = IQ_FACTORY.createIntensionalDataNode(
                ATOM_FACTORY.getIntensionalTripleAtom(X, P1_IRI, Y));
        queryBuilder.addChild(rootNode, dataNode);

        IntermediateQuery mainQuery = queryBuilder.build();

        /*
         * Sub-query
         */
        ExtensionalDataNode dataNodeSubquery = IQ_FACTORY.createExtensionalDataNode(ATOM_FACTORY.getDataAtom(TABLE1_AR2, S, U));
        IntermediateQueryBuilder subQueryBuilder = createQueryBuilder(DB_METADATA);
        ConstructionNode subQueryRoot = IQ_FACTORY.createConstructionNode(P1_ST_ATOM.getVariables(),
                SUBSTITUTION_FACTORY.getSubstitution(ImmutableMap.of(
                        T, S,
                        P, generateGroundTerm(P1_IRI))));
        subQueryBuilder.init(P1_ST_ATOM, subQueryRoot);
        subQueryBuilder.addChild(subQueryRoot, dataNodeSubquery);

        /*
         * Expected
         */
        IntermediateQueryBuilder expectedBuilder = createQueryBuilder(DB_METADATA);
        QueryNode expectedRootNode = mainQuery.getRootNode();
        expectedBuilder.init(mainQuery.getProjectionAtom(), expectedRootNode);
        ConstructionNode remainingConstructionNode = IQ_FACTORY.createConstructionNode(ImmutableSet.of(X, Y),
                SUBSTITUTION_FACTORY.getSubstitution(ImmutableMap.of(X, Y)));
        expectedBuilder.addChild(expectedRootNode, remainingConstructionNode);
        ExtensionalDataNode expectedDataNode = IQ_FACTORY.createExtensionalDataNode(ATOM_FACTORY.getDataAtom(TABLE1_AR2, Y, U));
        expectedBuilder.addChild(remainingConstructionNode, expectedDataNode);

        optimizeAndCompare(mainQuery, subQueryBuilder.build(), expectedBuilder.build(), dataNode);
    }

    @Test
    public void testEx6() throws EmptyQueryException {

        /*
         * Original query
         */
        IntermediateQueryBuilder queryBuilder = createQueryBuilder(DB_METADATA);

        ConstructionNode rootNode = IQ_FACTORY.createConstructionNode(ANS1_X_ATOM.getVariables(),
                SUBSTITUTION_FACTORY.getSubstitution(ImmutableMap.of()));

        queryBuilder.init(ANS1_X_ATOM, rootNode);

        IntensionalDataNode dataNode = IQ_FACTORY.createIntensionalDataNode(
                ATOM_FACTORY.getIntensionalTripleAtom(X, P1_IRI, INT_OF_THREE));
        queryBuilder.addChild(rootNode, dataNode);

        IntermediateQuery mainQuery = queryBuilder.build();

        /*
         * Sub-query
         */
        ExtensionalDataNode dataNodeSubquery = IQ_FACTORY.createExtensionalDataNode(ATOM_FACTORY.getDataAtom(TABLE1_AR2, S, B));
        IntermediateQueryBuilder subQueryBuilder = createQueryBuilder(DB_METADATA);
        ConstructionNode subQueryRoot = IQ_FACTORY.createConstructionNode(P1_ST_ATOM.getVariables(),
                SUBSTITUTION_FACTORY.getSubstitution(ImmutableMap.of(
                        T, INT_OF_B,
                        P, generateGroundTerm(P1_IRI))));
        subQueryBuilder.init(P1_ST_ATOM, subQueryRoot);
        subQueryBuilder.addChild(subQueryRoot, dataNodeSubquery);

        /*
         * Expected
         */
        IntermediateQueryBuilder expectedBuilder = createQueryBuilder(DB_METADATA);
        QueryNode expectedRootNode = mainQuery.getRootNode();
        expectedBuilder.init(ANS1_X_ATOM, expectedRootNode);
        ConstructionNode expectedIntermediateConstructionNode = IQ_FACTORY.createConstructionNode(ImmutableSet.of(X));
        expectedBuilder.addChild(expectedRootNode, expectedIntermediateConstructionNode);
        ExtensionalDataNode expectedDataNode = IQ_FACTORY.createExtensionalDataNode(ATOM_FACTORY.getDataAtom(TABLE1_AR2, X, THREE));
        expectedBuilder.addChild(expectedIntermediateConstructionNode, expectedDataNode);

        optimizeAndCompare(mainQuery, subQueryBuilder.build(), expectedBuilder.build(), dataNode);
    }

    @Test
    public void testEx7() throws EmptyQueryException {

        /*
         * Original query
         */
        IntermediateQueryBuilder queryBuilder = createQueryBuilder(DB_METADATA);

        ConstructionNode rootNode = IQ_FACTORY.createConstructionNode(ANS1_X_ATOM.getVariables(),
                SUBSTITUTION_FACTORY.getSubstitution(ImmutableMap.of()));

        queryBuilder.init(ANS1_X_ATOM, rootNode);

        IntensionalDataNode dataNode = IQ_FACTORY.createIntensionalDataNode(
                ATOM_FACTORY.getIntensionalTripleAtom(X, X, X));
        queryBuilder.addChild(rootNode, dataNode);

        IntermediateQuery mainQuery = queryBuilder.build();

        /*
         * Sub-query
         */
        DistinctVariableOnlyDataAtom p1Atom = ATOM_FACTORY.getDistinctTripleAtom(S, T, U);
        ExtensionalDataNode dataNodeSubquery = IQ_FACTORY.createExtensionalDataNode(ATOM_FACTORY.getDataAtom(TABLE4_AR3, A, B, C));
        IntermediateQueryBuilder subQueryBuilder = createQueryBuilder(DB_METADATA);
        ConstructionNode subQueryRoot = IQ_FACTORY.createConstructionNode(p1Atom.getVariables(),
                SUBSTITUTION_FACTORY.getSubstitution(ImmutableMap.of(S, generateURI1(A), T, generateURI1(B),
                        U, generateURI1(C))));
        subQueryBuilder.init(p1Atom, subQueryRoot);
        subQueryBuilder.addChild(subQueryRoot, dataNodeSubquery);

        /*
         * Expected
         */
        IntermediateQueryBuilder expectedBuilder = createQueryBuilder(DB_METADATA);
        QueryNode expectedRootNode = mainQuery.getRootNode();
        expectedBuilder.init(mainQuery.getProjectionAtom(), expectedRootNode);
        ConstructionNode remainingConstructionNode = IQ_FACTORY.createConstructionNode(ImmutableSet.of(X),
                SUBSTITUTION_FACTORY.getSubstitution(ImmutableMap.of(X, generateURI1(A))));
        expectedBuilder.addChild(expectedRootNode, remainingConstructionNode);

        ExtensionalDataNode expectedDataNode = IQ_FACTORY.createExtensionalDataNode(ATOM_FACTORY.getDataAtom(TABLE4_AR3, A, A, A));
        expectedBuilder.addChild(remainingConstructionNode, expectedDataNode);

        optimizeAndCompare(mainQuery, subQueryBuilder.build(), expectedBuilder.build(), dataNode);
    }

    @Test
    public void testEx8WithEx15() throws EmptyQueryException {

        /*
         * Original query
         */
        IntermediateQueryBuilder queryBuilder = createQueryBuilder(DB_METADATA);

        ConstructionNode rootNode = IQ_FACTORY.createConstructionNode(ANS1_X_ATOM.getVariables(),
                SUBSTITUTION_FACTORY.getSubstitution(ImmutableMap.of()));

        queryBuilder.init(ANS1_X_ATOM, rootNode);

        IntensionalDataNode dataNode = IQ_FACTORY.createIntensionalDataNode(
                ATOM_FACTORY.getIntensionalTripleAtom(X, P1_IRI, X));
        queryBuilder.addChild(rootNode, dataNode);

        IntermediateQuery mainQuery = queryBuilder.build();

        /*
         * Sub-query
         */
        DistinctVariableOnlyDataAtom p1Atom = P1_ST_ATOM;
        ExtensionalDataNode dataNodeSubquery = IQ_FACTORY.createExtensionalDataNode(ATOM_FACTORY.getDataAtom(TABLE1_AR2, A, B));
        IntermediateQueryBuilder subQueryBuilder = createQueryBuilder(DB_METADATA);
        ConstructionNode subQueryRoot = IQ_FACTORY.createConstructionNode(p1Atom.getVariables(),
                SUBSTITUTION_FACTORY.getSubstitution(ImmutableMap.of(
                        S, generateURI1(A),
                        P, generateGroundTerm(P1_IRI))));
        subQueryBuilder.init(p1Atom, subQueryRoot);

        ConstructionNode constructionNode2 = IQ_FACTORY.createConstructionNode(ImmutableSet.of(T, A),
                SUBSTITUTION_FACTORY.getSubstitution(ImmutableMap.of(
                        T, generateURI1(B)
                )));
        subQueryBuilder.addChild(subQueryRoot, constructionNode2);

        subQueryBuilder.addChild(constructionNode2, dataNodeSubquery);

        /*
         * Expected
         */
        IntermediateQueryBuilder expectedBuilder = createQueryBuilder(DB_METADATA);
        QueryNode expectedRootNode = mainQuery.getRootNode();
        expectedBuilder.init(mainQuery.getProjectionAtom(), expectedRootNode);
        ConstructionNode remainingConstructionNode = IQ_FACTORY.createConstructionNode(ImmutableSet.of(X),
                SUBSTITUTION_FACTORY.getSubstitution(ImmutableMap.of(X, generateURI1(A))));
        expectedBuilder.addChild(expectedRootNode, remainingConstructionNode);

        ExtensionalDataNode expectedDataNode = IQ_FACTORY.createExtensionalDataNode(ATOM_FACTORY.getDataAtom(TABLE1_AR2, A, A));
        expectedBuilder.addChild(remainingConstructionNode, expectedDataNode);

        optimizeAndCompare(mainQuery, subQueryBuilder.build(), expectedBuilder.build(), dataNode);
    }

    @Test
    public void testEx8_1() throws EmptyQueryException {

        /*
         * Original query
         */
        IntermediateQueryBuilder queryBuilder = createQueryBuilder(DB_METADATA);

        ConstructionNode rootNode = IQ_FACTORY.createConstructionNode(ANS1_X_ATOM.getVariables(),
                SUBSTITUTION_FACTORY.getSubstitution(ImmutableMap.of()));

        queryBuilder.init(ANS1_X_ATOM, rootNode);

        IntensionalDataNode dataNode = IQ_FACTORY.createIntensionalDataNode(
                ATOM_FACTORY.getIntensionalTripleAtom(X, P1_IRI, X));
        queryBuilder.addChild(rootNode, dataNode);

        IntermediateQuery mainQuery = queryBuilder.build();

        /*
         * Sub-query
         */
        ExtensionalDataNode dataNodeSubquery = IQ_FACTORY.createExtensionalDataNode(ATOM_FACTORY.getDataAtom(TABLE1_AR2, A, B));
        IntermediateQueryBuilder subQueryBuilder = createQueryBuilder(DB_METADATA);
        ConstructionNode subQueryRoot = IQ_FACTORY.createConstructionNode(P1_ST_ATOM.getVariables(),
                SUBSTITUTION_FACTORY.getSubstitution(ImmutableMap.of(
                        T, generateURI1(A),
                        P, generateGroundTerm(P1_IRI))));
        subQueryBuilder.init(P1_ST_ATOM, subQueryRoot);

        ConstructionNode constructionNode2 = IQ_FACTORY.createConstructionNode(ImmutableSet.of(S, A),
                SUBSTITUTION_FACTORY.getSubstitution(ImmutableMap.of(
                        S, generateURI1(B)
                )));
        subQueryBuilder.addChild(subQueryRoot, constructionNode2);

        subQueryBuilder.addChild(constructionNode2, dataNodeSubquery);

        /*
         * Expected
         */
        IntermediateQueryBuilder expectedBuilder = createQueryBuilder(DB_METADATA);
        QueryNode expectedRootNode = mainQuery.getRootNode();
        expectedBuilder.init(mainQuery.getProjectionAtom(), expectedRootNode);
        ConstructionNode remainingConstructionNode = IQ_FACTORY.createConstructionNode(ImmutableSet.of(X),
                SUBSTITUTION_FACTORY.getSubstitution(ImmutableMap.of(X, generateURI1(A))));
        expectedBuilder.addChild(expectedRootNode, remainingConstructionNode);

        ExtensionalDataNode expectedDataNode = IQ_FACTORY.createExtensionalDataNode(ATOM_FACTORY.getDataAtom(TABLE1_AR2, A, A));
        expectedBuilder.addChild(remainingConstructionNode, expectedDataNode);

        optimizeAndCompare(mainQuery, subQueryBuilder.build(), expectedBuilder.build(), dataNode);
    }

    @Test
    public void testEx9() throws EmptyQueryException {

        /*
         * Original query
         */
        IntermediateQueryBuilder queryBuilder = createQueryBuilder(DB_METADATA);

        ConstructionNode rootNode = IQ_FACTORY.createConstructionNode(ANS1_X_ATOM.getVariables(),
                SUBSTITUTION_FACTORY.getSubstitution(ImmutableMap.of()));

        queryBuilder.init(ANS1_X_ATOM, rootNode);

        IntensionalDataNode dataNode = IQ_FACTORY.createIntensionalDataNode(
                ATOM_FACTORY.getIntensionalTripleAtom(X, X, X));
        queryBuilder.addChild(rootNode, dataNode);

        IntermediateQuery mainQuery = queryBuilder.build();

        /*
         * Sub-query
         */
        DistinctVariableOnlyDataAtom p1Atom = ATOM_FACTORY.getDistinctTripleAtom(S, T, U);

        IntermediateQueryBuilder subQueryBuilder = createQueryBuilder(DB_METADATA);
        ConstructionNode subQueryRoot = IQ_FACTORY.createConstructionNode(p1Atom.getVariables(),
                SUBSTITUTION_FACTORY.getSubstitution(ImmutableMap.of(
                        S, generateURI1(A), T, generateURI1(B))));
        subQueryBuilder.init(p1Atom, subQueryRoot);

        ConstructionNode constructionNode2 = IQ_FACTORY.createConstructionNode(ImmutableSet.of(U, A, B),
                SUBSTITUTION_FACTORY.getSubstitution(ImmutableMap.of(
                        U, generateURI1(C)
                )));
        subQueryBuilder.addChild(subQueryRoot, constructionNode2);

        ExtensionalDataNode dataNodeSubquery = IQ_FACTORY.createExtensionalDataNode(ATOM_FACTORY.getDataAtom(TABLE1_AR3, A, B, C));
        subQueryBuilder.addChild(constructionNode2, dataNodeSubquery);

        /*
         * Expected
         */
        IntermediateQueryBuilder expectedBuilder = createQueryBuilder(DB_METADATA);
        QueryNode expectedRootNode = mainQuery.getRootNode();
        expectedBuilder.init(mainQuery.getProjectionAtom(), expectedRootNode);
        ConstructionNode remainingConstructionNode = IQ_FACTORY.createConstructionNode(ImmutableSet.of(X),
                SUBSTITUTION_FACTORY.getSubstitution(ImmutableMap.of(X, generateURI1(A))));
        expectedBuilder.addChild(expectedRootNode, remainingConstructionNode);

        ExtensionalDataNode expectedDataNode = IQ_FACTORY.createExtensionalDataNode(ATOM_FACTORY.getDataAtom(TABLE1_AR3, A, A, A));
        expectedBuilder.addChild(remainingConstructionNode, expectedDataNode);

        optimizeAndCompare(mainQuery, subQueryBuilder.build(), expectedBuilder.build(), dataNode);
    }

    @Test
    public void testEx10() throws EmptyQueryException {

        /*
         * Original query
         */
        IntermediateQueryBuilder queryBuilder = createQueryBuilder(DB_METADATA);

        ConstructionNode rootNode = IQ_FACTORY.createConstructionNode(ANS1_X_ATOM.getVariables(),
                SUBSTITUTION_FACTORY.getSubstitution(ImmutableMap.of()));

        queryBuilder.init(ANS1_X_ATOM, rootNode);

        IntensionalDataNode dataNode = IQ_FACTORY.createIntensionalDataNode(
                ATOM_FACTORY.getIntensionalTripleAtom(X, P1_IRI, X));
        queryBuilder.addChild(rootNode, dataNode);

        IntermediateQuery mainQuery = queryBuilder.build();

        /*
         * Sub-query
         */
        DistinctVariableOnlyDataAtom p1Atom = P1_ST_ATOM;
        IntermediateQueryBuilder subQueryBuilder = createQueryBuilder(DB_METADATA);
        ConstructionNode subQueryRoot = IQ_FACTORY.createConstructionNode(p1Atom.getVariables(),
                SUBSTITUTION_FACTORY.getSubstitution(ImmutableMap.of(
                        T, generateURI1(B),
                        P, generateGroundTerm(P1_IRI))));
        subQueryBuilder.init(p1Atom, subQueryRoot);
        ConstructionNode subQueryConstruction2 = IQ_FACTORY.createConstructionNode(ImmutableSet.of(S, B),
                SUBSTITUTION_FACTORY.getSubstitution(ImmutableMap.of(S, generateURI1(A))));
        subQueryBuilder.addChild(subQueryRoot, subQueryConstruction2);
        ExtensionalDataNode dataNodeSubquery = IQ_FACTORY.createExtensionalDataNode(ATOM_FACTORY.getDataAtom(TABLE1_AR2, A, B));
        subQueryBuilder.addChild(subQueryConstruction2, dataNodeSubquery);

        /*
         * Expected
         */
        IntermediateQueryBuilder expectedBuilder = createQueryBuilder(DB_METADATA);
        QueryNode expectedRootNode = mainQuery.getRootNode();
        expectedBuilder.init(mainQuery.getProjectionAtom(), expectedRootNode);
        ConstructionNode remainingConstructionNode = IQ_FACTORY.createConstructionNode(ImmutableSet.of(X),
                SUBSTITUTION_FACTORY.getSubstitution(ImmutableMap.of(X, generateURI1(B))));
        expectedBuilder.addChild(expectedRootNode, remainingConstructionNode);

        ExtensionalDataNode expectedDataNode = IQ_FACTORY.createExtensionalDataNode(ATOM_FACTORY.getDataAtom(TABLE1_AR2, B, B));
        expectedBuilder.addChild(remainingConstructionNode, expectedDataNode);

        optimizeAndCompare(mainQuery, subQueryBuilder.build(), expectedBuilder.build(), dataNode);
    }

    @Test
    public void testEx11() throws EmptyQueryException {

        /*
         * Original query
         */
        IntermediateQueryBuilder queryBuilder = createQueryBuilder(DB_METADATA);

        ConstructionNode rootNode = IQ_FACTORY.createConstructionNode(ANS1_XY_ATOM.getVariables(),
                SUBSTITUTION_FACTORY.getSubstitution(ImmutableMap.of()));

        queryBuilder.init(ANS1_XY_ATOM, rootNode);

        IntensionalDataNode dataNode = IQ_FACTORY.createIntensionalDataNode(
                ATOM_FACTORY.getIntensionalTripleAtom(X, P1_IRI, Y));
        queryBuilder.addChild(rootNode, dataNode);

        IntermediateQuery mainQuery = queryBuilder.build();


        /*
         * Sub-query
         */
        ExtensionalDataNode dataNodeSubquery = IQ_FACTORY.createExtensionalDataNode(ATOM_FACTORY.getDataAtom(TABLE2_AR1, A));
        IntermediateQueryBuilder subQueryBuilder = createQueryBuilder(DB_METADATA);
        ConstructionNode subQueryRoot = IQ_FACTORY.createConstructionNode(P1_ST_ATOM.getVariables(),
                SUBSTITUTION_FACTORY.getSubstitution(ImmutableMap.of(
                        S, generateURI1(A),
                        T, generateURI1(INT_OF_ONE),
                        P, generateGroundTerm(P1_IRI))));
        subQueryBuilder.init(P1_ST_ATOM, subQueryRoot);
        subQueryBuilder.addChild(subQueryRoot, dataNodeSubquery);

        /*
         * Expected
         */
        IntermediateQueryBuilder expectedBuilder = createQueryBuilder(DB_METADATA);
        QueryNode expectedRootNode = mainQuery.getRootNode();
        expectedBuilder.init(mainQuery.getProjectionAtom(), expectedRootNode);
        ConstructionNode remainingConstructionNode = IQ_FACTORY.createConstructionNode(ImmutableSet.of(X, Y),
                SUBSTITUTION_FACTORY.getSubstitution(ImmutableMap.of(X, generateURI1(A), Y, generateURI1(INT_OF_ONE))));
        expectedBuilder.addChild(expectedRootNode, remainingConstructionNode);

        ExtensionalDataNode expectedDataNode = IQ_FACTORY.createExtensionalDataNode(ATOM_FACTORY.getDataAtom(TABLE2_AR1, A));
        expectedBuilder.addChild(remainingConstructionNode, expectedDataNode);

        optimizeAndCompare(mainQuery, subQueryBuilder.build(), expectedBuilder.build(), dataNode);
    }

    @Ignore("TODO: decide what to do with ground functional terms")
    @Test
    public void testEx12() throws EmptyQueryException {

        /*
         * Original query
         */
        IntermediateQueryBuilder queryBuilder = createQueryBuilder(DB_METADATA);

        ConstructionNode rootNode = IQ_FACTORY.createConstructionNode(ANS1_X_ATOM.getVariables(),
                SUBSTITUTION_FACTORY.getSubstitution(ImmutableMap.of()));

        queryBuilder.init(ANS1_X_ATOM, rootNode);

        IntensionalDataNode dataNode = IQ_FACTORY.createIntensionalDataNode(
                ATOM_FACTORY.getIntensionalTripleAtom(X, P1_IRI, X));
        queryBuilder.addChild(rootNode, dataNode);

        IntermediateQuery mainQuery = queryBuilder.build();

        /*
         * Sub-query
         */
        ExtensionalDataNode dataNodeSubquery = IQ_FACTORY.createExtensionalDataNode(ATOM_FACTORY.getDataAtom(TABLE1_AR2, A, INT_OF_ONE));
        IntermediateQueryBuilder subQueryBuilder = createQueryBuilder(DB_METADATA);
        ConstructionNode subQueryRoot = IQ_FACTORY.createConstructionNode(P1_ST_ATOM.getVariables(),
                SUBSTITUTION_FACTORY.getSubstitution(ImmutableMap.of(
                        S, generateURI1(A),
                        T, generateURI1(INT_OF_ONE),
                        P, generateGroundTerm(P1_IRI))));
        subQueryBuilder.init(P1_ST_ATOM, subQueryRoot);
        subQueryBuilder.addChild(subQueryRoot, dataNodeSubquery);

        /*
         * Expected
         */
        IntermediateQueryBuilder expectedBuilder = createQueryBuilder(DB_METADATA);
        QueryNode expectedRootNode = mainQuery.getRootNode();
        expectedBuilder.init(mainQuery.getProjectionAtom(), expectedRootNode);
        ConstructionNode remainingConstructionNode = IQ_FACTORY.createConstructionNode(ImmutableSet.of(X),
                SUBSTITUTION_FACTORY.getSubstitution(ImmutableMap.of(X, generateURI1(INT_OF_ONE))));
        expectedBuilder.addChild(expectedRootNode, remainingConstructionNode);

        ExtensionalDataNode expectedDataNode = IQ_FACTORY.createExtensionalDataNode(ATOM_FACTORY.getDataAtom(TABLE1_AR2, INT_OF_ONE, INT_OF_ONE));
        expectedBuilder.addChild(remainingConstructionNode, expectedDataNode);

        optimizeAndCompare(mainQuery, subQueryBuilder.build(), expectedBuilder.build(), dataNode);
    }

    @Ignore("TODO: decide what to do with ground functional terms")
    @Test
    public void testEx13() throws EmptyQueryException {

        /*
         * Original query
         */
        IntermediateQueryBuilder queryBuilder = createQueryBuilder(DB_METADATA);

        ConstructionNode rootNode = IQ_FACTORY.createConstructionNode(ANS1_X_ATOM.getVariables(),
                SUBSTITUTION_FACTORY.getSubstitution(ImmutableMap.of()));

        queryBuilder.init(ANS1_X_ATOM, rootNode);

        IntensionalDataNode dataNode = IQ_FACTORY.createIntensionalDataNode(
                ATOM_FACTORY.getIntensionalTripleAtom(X, P1_IRI, X));
        queryBuilder.addChild(rootNode, dataNode);

        IntermediateQuery mainQuery = queryBuilder.build();

        /*
         * Sub-query
         */
        ExtensionalDataNode dataNodeSubquery = IQ_FACTORY.createExtensionalDataNode(ATOM_FACTORY.getDataAtom(TABLE1_AR2, INT_OF_ONE, B));
        IntermediateQueryBuilder subQueryBuilder = createQueryBuilder(DB_METADATA);
        ConstructionNode subQueryRoot = IQ_FACTORY.createConstructionNode(P1_ST_ATOM.getVariables(),
                SUBSTITUTION_FACTORY.getSubstitution(ImmutableMap.of(
                        S, generateURI1(INT_OF_ONE),
                        T, generateURI1(B),
                        P, generateGroundTerm(P1_IRI))));
        subQueryBuilder.init(P1_ST_ATOM, subQueryRoot);
        subQueryBuilder.addChild(subQueryRoot, dataNodeSubquery);

        /*
         * Expected
         */
        IntermediateQueryBuilder expectedBuilder = createQueryBuilder(DB_METADATA);
        QueryNode expectedRootNode = mainQuery.getRootNode();
        expectedBuilder.init(mainQuery.getProjectionAtom(), expectedRootNode);
        ConstructionNode remainingConstructionNode = IQ_FACTORY.createConstructionNode(ImmutableSet.of(X),
                SUBSTITUTION_FACTORY.getSubstitution(ImmutableMap.of(X, generateURI1(INT_OF_ONE))));
        expectedBuilder.addChild(expectedRootNode, remainingConstructionNode);

        ExtensionalDataNode expectedDataNode = IQ_FACTORY.createExtensionalDataNode(ATOM_FACTORY.getDataAtom(TABLE1_AR2, INT_OF_ONE, INT_OF_ONE));
        expectedBuilder.addChild(remainingConstructionNode, expectedDataNode);

        optimizeAndCompare(mainQuery, subQueryBuilder.build(), expectedBuilder.build(), dataNode);
    }

    @Test
    public void testEx14() throws EmptyQueryException {

        /*
         * Original query
         */
        IntermediateQueryBuilder queryBuilder = createQueryBuilder(DB_METADATA);

        AtomPredicate emptyAns1 = ATOM_FACTORY.getRDFAnswerPredicate( 0);
        DistinctVariableOnlyDataAtom projectionAtom = ATOM_FACTORY.getDistinctVariableOnlyDataAtom(
                emptyAns1, ImmutableList.of());

        ConstructionNode rootNode = IQ_FACTORY.createConstructionNode(projectionAtom.getVariables(),
                SUBSTITUTION_FACTORY.getSubstitution(ImmutableMap.of()));
        queryBuilder.init(projectionAtom, rootNode);
        IntensionalDataNode dataNode = IQ_FACTORY.createIntensionalDataNode(
                ATOM_FACTORY.getIntensionalTripleAtom(
                        (GroundFunctionalTerm) generateURI1(INT_OF_ONE),
                        P1_IRI,
                        (GroundFunctionalTerm) generateURI1(INT_OF_ONE)));
        queryBuilder.addChild(rootNode, dataNode);
        IntermediateQuery mainQuery = queryBuilder.build();

        /*
         * Sub-query
         */
        IntermediateQueryBuilder subQueryBuilder = createQueryBuilder(DB_METADATA);
        ConstructionNode subQueryRoot = IQ_FACTORY.createConstructionNode(P1_ST_ATOM.getVariables(),
                SUBSTITUTION_FACTORY.getSubstitution(ImmutableMap.of(
                        S, generateURI1(INT_OF_ONE),
                        T, generateURI1(INT_OF_ONE),
                        P, generateGroundTerm(P1_IRI))));
        subQueryBuilder.init(P1_ST_ATOM, subQueryRoot);
        ExtensionalDataNode tableNode = IQ_FACTORY.createExtensionalDataNode(
                ATOM_FACTORY.getDataAtom(TABLE1_AR2, TERM_FACTORY.getConstantLiteral("2"),
                TERM_FACTORY.getConstantLiteral("2")));
        subQueryBuilder.addChild(subQueryRoot, tableNode);

        /*
         * Expected
         */
        IntermediateQueryBuilder expectedBuilder = createQueryBuilder(DB_METADATA);
        QueryNode expectedRootNode = mainQuery.getRootNode();
        expectedBuilder.init(mainQuery.getProjectionAtom(), expectedRootNode);
        expectedBuilder.addChild(expectedRootNode, tableNode);

        optimizeAndCompare(mainQuery, subQueryBuilder.build(), expectedBuilder.build(), dataNode);
    }

    @Test
    public void testConflictingVariables() {

        /*
         * Original query
         */
        IntermediateQueryBuilder queryBuilder = createQueryBuilder(DB_METADATA);
        DistinctVariableOnlyDataAtom projectionAtom = ATOM_FACTORY.getDistinctVariableOnlyDataAtom(
                ANS4_PREDICATE, ImmutableList.of(X, Y, Z));
        ConstructionNode rootNode = IQ_FACTORY.createConstructionNode(projectionAtom.getVariables(),
                SUBSTITUTION_FACTORY.getSubstitution(ImmutableMap.of()));
        queryBuilder.init(projectionAtom, rootNode);

        InnerJoinNode joinNode = IQ_FACTORY.createInnerJoinNode();
        queryBuilder.addChild(rootNode, joinNode);

        IRI firstNameIRI = RDF_FACTORY.createIRI("http://example.com/voc#firstName");
        IRI lastNameIRI = RDF_FACTORY.createIRI("http://example.com/voc#lastName");

        IntensionalDataNode firstIntentional = IQ_FACTORY.createIntensionalDataNode(
                ATOM_FACTORY.getIntensionalTripleAtom(X, firstNameIRI, Y));
        IntensionalDataNode lastIntentional = IQ_FACTORY.createIntensionalDataNode(
                ATOM_FACTORY.getIntensionalTripleAtom(X, lastNameIRI, Z));
        queryBuilder.addChild(joinNode, firstIntentional);
        queryBuilder.addChild(joinNode, lastIntentional);

        IntermediateQuery query = queryBuilder.build();
        System.out.println("Initial query: \n" + query);


        /*
         * First name mapping
         */
        IntermediateQueryBuilder firstMappingBuilder = createQueryBuilder(DB_METADATA);
        DistinctVariableOnlyDataAtom firstMappingAtom = ATOM_FACTORY.getDistinctTripleAtom(S, P, T);
        ConstructionNode firstMappingRootNode = IQ_FACTORY.createConstructionNode(firstMappingAtom.getVariables(),
                SUBSTITUTION_FACTORY.getSubstitution(ImmutableMap.of(
                        S, generateURI1(A),
                        T, generateString(B),
                        P, generateGroundTerm(firstNameIRI))));

        firstMappingBuilder.init(firstMappingAtom, firstMappingRootNode);

        ExtensionalDataNode firstNameDataNode = IQ_FACTORY.createExtensionalDataNode(ATOM_FACTORY.getDataAtom(TABLE4_AR3, A, B, C));
        firstMappingBuilder.addChild(firstMappingRootNode, firstNameDataNode);

        IQ firstMapping = IQ_CONVERTER.convert(firstMappingBuilder.build());
        System.out.println("First name mapping: \n" + firstMapping);

        /*
         * Last name mapping
         */
        IntermediateQueryBuilder lastMappingBuilder = createQueryBuilder(DB_METADATA);
        DistinctVariableOnlyDataAtom lastMappingAtom = ATOM_FACTORY.getDistinctTripleAtom(S, P, T);
        ConstructionNode lastMappingRootNode = IQ_FACTORY.createConstructionNode(lastMappingAtom.getVariables(),
                SUBSTITUTION_FACTORY.getSubstitution(ImmutableMap.of(S, generateURI1(D),
                        T, generateString(B),
                        P, generateGroundTerm(lastNameIRI)
                        )));

        lastMappingBuilder.init(lastMappingAtom, lastMappingRootNode);

        ExtensionalDataNode lastNameDataNode = IQ_FACTORY.createExtensionalDataNode(ATOM_FACTORY.getDataAtom(TABLE4_AR3, D, E, B));
        lastMappingBuilder.addChild(lastMappingRootNode, lastNameDataNode);

        IQ lastMapping = IQ_CONVERTER.convert(lastMappingBuilder.build());
        System.out.println("Last name mapping: \n" + lastMapping);

        IQ mergedMappingDefinition = UNION_BASED_QUERY_MERGER.mergeDefinitions(ImmutableList.of(firstMapping, lastMapping))
                .get();

        IQ newQuery = merge(IQ_CONVERTER.convert(query), mergedMappingDefinition, firstIntentional);
        System.out.println("\n After merging the first mapping: \n" + newQuery);

        /*
         * Expected query
         */
        IntermediateQueryBuilder expectedQueryBuilder = createQueryBuilder(DB_METADATA);
        expectedQueryBuilder.init(projectionAtom, rootNode);
        expectedQueryBuilder.addChild(rootNode, joinNode);

        ConstructionNode leftConstructionNode = IQ_FACTORY.createConstructionNode(ImmutableSet.of(X, Y),
                SUBSTITUTION_FACTORY.getSubstitution(ImmutableMap.of(X, generateURI1(A), Y, generateString(B))));
        expectedQueryBuilder.addChild(joinNode, leftConstructionNode);
        expectedQueryBuilder.addChild(leftConstructionNode, firstNameDataNode);

        Variable b1 = BF3F7;
        Variable ef8 = TERM_FACTORY.getVariable("ef8");
        ConstructionNode rightConstructionNode = IQ_FACTORY.createConstructionNode(ImmutableSet.of(X, Z),
                SUBSTITUTION_FACTORY.getSubstitution(ImmutableMap.of(X, generateURI1(DF6), Z, generateString(b1))));
        expectedQueryBuilder.addChild(joinNode, rightConstructionNode);

        ExtensionalDataNode expectedlastNameDataNode = IQ_FACTORY.createExtensionalDataNode(
                ATOM_FACTORY.getDataAtom(TABLE4_AR3, DF6, ef8, b1));
        expectedQueryBuilder.addChild(rightConstructionNode, expectedlastNameDataNode);

        IntermediateQuery expectedQuery = expectedQueryBuilder.build();

        optimizeAndCompare(newQuery, mergedMappingDefinition, IQ_CONVERTER.convert(expectedQuery),
                lastIntentional);
    }

    @Test
    public void testUnionSameVariable() {

        /*
         * Original query
         */
        IntermediateQueryBuilder queryBuilder = createQueryBuilder(DB_METADATA);

        ConstructionNode rootNode = IQ_FACTORY.createConstructionNode(ImmutableSet.of(X),
                SUBSTITUTION_FACTORY.getSubstitution(ImmutableMap.of()));

        queryBuilder.init(ANS1_X_ATOM, rootNode);

        IntensionalDataNode intensionalDataNode = IQ_FACTORY.createIntensionalDataNode(
                ATOM_FACTORY.getIntensionalTripleAtom(X, C3_IRI));
        queryBuilder.addChild(rootNode, intensionalDataNode);

        IntermediateQuery mainQuery = queryBuilder.build();

        /*
         * Mapping
         */
        IntermediateQueryBuilder mappingBuilder = createQueryBuilder(DB_METADATA);
        DistinctVariableOnlyDataAtom mappingProjectionAtom = ATOM_FACTORY.getDistinctTripleAtom(X, P, O);
        ConstructionNode mappingRootNode = IQ_FACTORY.createConstructionNode(mappingProjectionAtom.getVariables(),
                SUBSTITUTION_FACTORY.getSubstitution(ImmutableMap.of(
                        P, generateGroundTerm(RDF.TYPE),
                        O, generateGroundTerm(C3_IRI)
                )));

        mappingBuilder.init(mappingProjectionAtom, mappingRootNode);

        UnionNode unionNode = IQ_FACTORY.createUnionNode(ImmutableSet.of(X));
        mappingBuilder.addChild(mappingRootNode, unionNode);


        ExtensionalDataNode extensionalDataNode1 = IQ_FACTORY.createExtensionalDataNode(
                ATOM_FACTORY.getDataAtom(TABLE1_AR1, X));
        mappingBuilder.addChild(unionNode, extensionalDataNode1);
        ExtensionalDataNode extensionalDataNode2 = IQ_FACTORY.createExtensionalDataNode(
                ATOM_FACTORY.getDataAtom(TABLE2_AR1, X));
        mappingBuilder.addChild(unionNode, extensionalDataNode2);

        IntermediateQuery mapping = mappingBuilder.build();

        /*
         * Expected query
         */

        IntermediateQueryBuilder expectedQueryBuilder = createQueryBuilder(DB_METADATA);


        expectedQueryBuilder.init(ANS1_X_ATOM, rootNode);
        expectedQueryBuilder.addChild(rootNode, unionNode);
        expectedQueryBuilder.addChild(unionNode, extensionalDataNode1);
        expectedQueryBuilder.addChild(unionNode, extensionalDataNode2);

        IntermediateQuery expectedQuery = expectedQueryBuilder.build();


        optimizeAndCompare(mainQuery, mapping, expectedQuery, intensionalDataNode);
    }



    @Test
    public void testDescendingSubstitutionOnRenamedNode() {
        /**
         * The bug was the following: during query merging (QueryMergingExecutorImpl.analyze()),
         * variables of the current node were possibly renamed,
         * and then a (possible) substitution applied to the renamed node.
         * In some implementations (e.g. for ConstructionNode),
         * the substitution method (QueryNode.applyDescendingSubstitution(node, query)) may require information about
         * the node's parent/children.
         * But the renamed node not being (yet) part of the query (as opposed to the original node),
         * this raises an exception.
         * This behavior has been prevented by performing variable renaming beforehand for the whole merged query,
         * i.e. before starting substitution lift.
          */
        IntermediateQueryBuilder queryBuilder = createQueryBuilder(DB_METADATA);

        ConstructionNode rootNode = IQ_FACTORY.createConstructionNode(ImmutableSet.of(X),
                SUBSTITUTION_FACTORY.getSubstitution(ImmutableMap.of()));

        queryBuilder.init(ANS1_X_ATOM, rootNode);

        IntensionalDataNode intensionalDataNode = IQ_FACTORY.createIntensionalDataNode(
                ATOM_FACTORY.getIntensionalTripleAtom(X, C3_IRI));
        queryBuilder.addChild(rootNode, intensionalDataNode);

        IntermediateQuery mainQuery = queryBuilder.build();

        System.out.println("main query:\n"+mainQuery);

        /**
         * Mapping
         */
        IntermediateQueryBuilder mappingBuilder = createQueryBuilder(DB_METADATA);
        DistinctVariableOnlyDataAtom mappingProjectionAtom = ATOM_FACTORY.getDistinctTripleAtom(X, P, O);
        ConstructionNode mappingRootNode = IQ_FACTORY.createConstructionNode(mappingProjectionAtom.getVariables(),
                SUBSTITUTION_FACTORY.getSubstitution(ImmutableMap.of(
                        P, generateGroundTerm(RDF.TYPE),
                        O, generateGroundTerm(C3_IRI)
                )));
        mappingBuilder.init(mappingProjectionAtom, mappingRootNode);
        ExtensionalDataNode extensionalDataNode1 = IQ_FACTORY.createExtensionalDataNode(
                ATOM_FACTORY.getDataAtom(TABLE4_AR1, X));
        mappingBuilder.addChild(mappingRootNode, extensionalDataNode1);
        IntermediateQuery mapping = mappingBuilder.build();
        System.out.println("query to be merged:\n" +mapping);

        IQ mergedQuery = merge(mainQuery, mapping, intensionalDataNode);
        System.out.println("\n Optimized query: \n" + mergedQuery);
    }


    @Test
    public void testTrueNodeCreation() {

        /*
         *  Main  query.
         */
        IntermediateQueryBuilder queryBuilder = createQueryBuilder(DB_METADATA);

        ConstructionNode rootNode = IQ_FACTORY.createConstructionNode(ImmutableSet.of());

        queryBuilder.init(ANS0_ATOM, rootNode);
        IntensionalDataNode intensionalDataNode = IQ_FACTORY.createIntensionalDataNode(ATOM_FACTORY.getIntensionalTripleAtom(
                TERM_FACTORY.getConstantLiteral("1", XSD.INTEGER), C3_IRI));
        queryBuilder.addChild(rootNode, intensionalDataNode);
        IntermediateQuery mainQuery = queryBuilder.build();

        System.out.println("main query:\n"+mainQuery);

        /*
         * Mapping assertion
         */
        IntermediateQueryBuilder mappingBuilder = createQueryBuilder(DB_METADATA);
        DistinctVariableOnlyDataAtom mappingProjectionAtom = ATOM_FACTORY.getDistinctTripleAtom(X, P, O);
        ConstructionNode mappingRootNode = IQ_FACTORY.createConstructionNode(mappingProjectionAtom.getVariables(),
                SUBSTITUTION_FACTORY.getSubstitution(ImmutableMap.of(
                        X, TERM_FACTORY.getConstantLiteral("1", XSD.INTEGER),
                        P, generateGroundTerm(RDF.TYPE),
                        O, generateGroundTerm(C3_IRI)
                )));
        mappingBuilder.init(mappingProjectionAtom, mappingRootNode);
        mappingBuilder.addChild(mappingRootNode, IQ_FACTORY.createTrueNode());
        IntermediateQuery mapping = mappingBuilder.build();
        System.out.println("query to be merged:\n" +mapping);

        /*
         * Expected query
         */
        IntermediateQueryBuilder expectedQueryBuilder = createQueryBuilder(DB_METADATA);
        expectedQueryBuilder.init(ANS0_ATOM, rootNode);
        expectedQueryBuilder.addChild(rootNode, IQ_FACTORY.createTrueNode());
        IntermediateQuery expectedQuery = expectedQueryBuilder.build();
        System.out.println("expected query:\n"+expectedQuery);

        optimizeAndCompare(mainQuery, mapping, expectedQuery, intensionalDataNode);
    }


    private static IntermediateQuery createBasicSparqlQuery(
            ImmutableMap<Variable, ImmutableTerm> topBindings,
            VariableOrGroundTerm p1Arg1, VariableOrGroundTerm p1Arg2) {

        IntermediateQueryBuilder queryBuilder = createQueryBuilder(DB_METADATA);
        ConstructionNode rootNode = IQ_FACTORY.createConstructionNode(ANS1_XY_ATOM.getVariables(),
                SUBSTITUTION_FACTORY.getSubstitution(topBindings));

        queryBuilder.init(ANS1_XY_ATOM, rootNode);

        IntensionalDataNode dataNode = IQ_FACTORY.createIntensionalDataNode(
                ATOM_FACTORY.getIntensionalTripleAtom(p1Arg1, P1_IRI, p1Arg2));
        queryBuilder.addChild(rootNode, dataNode);

        return queryBuilder.build();
    }

    private static void optimizeAndCompare(IntermediateQuery mainQuery, IntermediateQuery subQuery,
                                           IntermediateQuery expectedQuery, IntensionalDataNode intensionalNode) {
        optimizeAndCompare(IQ_CONVERTER.convert(mainQuery), IQ_CONVERTER.convert(subQuery),
                IQ_CONVERTER.convert(expectedQuery), intensionalNode);
    }

    private static void optimizeAndCompare(IQ mainQuery, IQ subQuery, IQ expectedQuery, IntensionalDataNode intensionalNode) {

        System.out.println("\n Original query: \n" + mainQuery);
        System.out.println("\n Sub-query: \n" + subQuery);
        System.out.println("\n Expected query: \n" + expectedQuery);

        IQ mergedQuery = merge(mainQuery, subQuery, intensionalNode);
        System.out.println("\n Optimized query: \n" + mergedQuery);

        assertEquals(expectedQuery, mergedQuery);
    }

    private static IQ merge(IntermediateQuery mainQuery, IntermediateQuery subQuery, IntensionalDataNode intensionalNode) {
        return merge(IQ_CONVERTER.convert(mainQuery), IQ_CONVERTER.convert(subQuery), intensionalNode);
    }

    private static IQ merge(IQ mainQuery, IQ subQuery, IntensionalDataNode intensionalNode) {
        BasicIntensionalQueryMerger queryMerger = new BasicIntensionalQueryMerger(
                ImmutableMap.of(intensionalNode.getProjectionAtom().getPredicate(), subQuery));

        return queryMerger.optimize(mainQuery);
    }


    private static ImmutableFunctionalTerm generateURI1(VariableOrGroundTerm argument) {
        return TERM_FACTORY.getImmutableUriTemplate(URI_TEMPLATE_STR_1, argument);
    }

    private static ImmutableFunctionalTerm generateURI2(VariableOrGroundTerm argument) {
        return TERM_FACTORY.getImmutableUriTemplate(URI_TEMPLATE_STR_2, argument);
    }

    private static ImmutableFunctionalTerm generateURI3(VariableOrGroundTerm arg1, VariableOrGroundTerm arg2) {
        return TERM_FACTORY.getImmutableUriTemplate(URI_TEMPLATE_STR_3, arg1, arg2);
    }

    private static ImmutableFunctionalTerm generateString(VariableOrGroundTerm argument) {
        return TERM_FACTORY.getImmutableFunctionalTerm(
                TERM_FACTORY.getRequiredTypePredicate(XSD.STRING), argument);
    }


    /**
     * Basic implementation
     */
    private static class BasicIntensionalQueryMerger extends AbstractIntensionalQueryMerger {

        private final ImmutableMap<AtomPredicate, IQ> map;

        protected BasicIntensionalQueryMerger(ImmutableMap<AtomPredicate, IQ> map) {
            super(IQ_FACTORY);
            this.map = map;
        }

        @Override
        protected QueryMergingTransformer createTransformer(ImmutableSet<Variable> knownVariables) {
            VariableGenerator variableGenerator = CORE_UTILS_FACTORY.createVariableGenerator(knownVariables);
            return new BasicQueryMergingTransformer(variableGenerator);
        }

        private class BasicQueryMergingTransformer extends QueryMergingTransformer {

            protected BasicQueryMergingTransformer(VariableGenerator variableGenerator) {
                super(variableGenerator, IQ_FACTORY, SUBSTITUTION_FACTORY, TRANSFORMER_FACTORY);
            }

            @Override
            protected Optional<IQ> getDefinition(IntensionalDataNode dataNode) {
                return Optional.ofNullable(map.get(dataNode.getProjectionAtom().getPredicate()));
            }

            @Override
            protected IQTree handleIntensionalWithoutDefinition(IntensionalDataNode dataNode) {
                return dataNode;
            }
        }

    }

}


