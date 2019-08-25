package it.unibz.inf.ontop.iq.optimizer;

import it.unibz.inf.ontop.iq.exception.EmptyQueryException;
import it.unibz.inf.ontop.iq.node.*;
import it.unibz.inf.ontop.model.atom.DistinctVariableOnlyDataAtom;
import it.unibz.inf.ontop.model.atom.AtomPredicate;
import it.unibz.inf.ontop.model.term.functionsymbol.ExpressionOperation;
import it.unibz.inf.ontop.model.term.ImmutableExpression;
import it.unibz.inf.ontop.model.term.Variable;
import it.unibz.inf.ontop.iq.*;
import it.unibz.inf.ontop.iq.equivalence.IQSyntacticEquivalenceChecker;
import org.junit.Test;

import java.util.Optional;

import static it.unibz.inf.ontop.NoDependencyTestDBMetadata.*;
import static it.unibz.inf.ontop.iq.node.BinaryOrderedOperatorNode.ArgumentPosition.LEFT;
import static it.unibz.inf.ontop.iq.node.BinaryOrderedOperatorNode.ArgumentPosition.RIGHT;
import static junit.framework.TestCase.assertTrue;

import static it.unibz.inf.ontop.OptimizationTestingTools.*;


public class PushDownBooleanExpressionOptimizerTest {
    
    private final static AtomPredicate ANS1_PREDICATE1 = ATOM_FACTORY.getRDFAnswerPredicate( 4);
    private final static AtomPredicate ANS1_PREDICATE2 = ATOM_FACTORY.getRDFAnswerPredicate( 3);
    private final static AtomPredicate ANS1_PREDICATE3 = ATOM_FACTORY.getRDFAnswerPredicate( 5);
    private final static AtomPredicate ANS2_PREDICATE1 = ATOM_FACTORY.getRDFAnswerPredicate(2);
    private final static Variable X = TERM_FACTORY.getVariable("X");
    private final static Variable Y = TERM_FACTORY.getVariable("Y");
    private final static Variable Z = TERM_FACTORY.getVariable("Z");
    private final static Variable W = TERM_FACTORY.getVariable("W");
    private final static Variable A = TERM_FACTORY.getVariable("A");

    private final static ImmutableExpression EXPRESSION1 = TERM_FACTORY.getImmutableExpression(
            ExpressionOperation.EQ, X, Z);
    private final static ImmutableExpression EXPRESSION2 = TERM_FACTORY.getImmutableExpression(
            ExpressionOperation.NEQ, Y, Z);
    private final static ImmutableExpression EXPRESSION3 = TERM_FACTORY.getImmutableExpression(
            ExpressionOperation.LT, Z, W);
    private final static ImmutableExpression EXPRESSION4 = TERM_FACTORY.getImmutableExpression(
            ExpressionOperation.EQ, Y, Z);
    private final static ImmutableExpression EXPRESSION5 = TERM_FACTORY.getImmutableExpression(
            ExpressionOperation.NEQ, Z, W);
    private final static ImmutableExpression EXPRESSION6 = TERM_FACTORY.getImmutableExpression(
            ExpressionOperation.EQ, X, W);
    private final static ImmutableExpression EXPRESSION7 = TERM_FACTORY.getImmutableExpression(
            ExpressionOperation.EQ, X, Y);

    @Test
    public void testJoiningCondition1() throws EmptyQueryException {

        IntermediateQueryBuilder queryBuilder1 = createQueryBuilder(DB_METADATA);
        DistinctVariableOnlyDataAtom projectionAtom = ATOM_FACTORY.getDistinctVariableOnlyDataAtom(ANS1_PREDICATE1, X, Y, Z, W);
        ConstructionNode constructionNode = IQ_FACTORY.createConstructionNode(projectionAtom.getVariables());
        queryBuilder1.init(projectionAtom, constructionNode);
        InnerJoinNode joinNode1 = IQ_FACTORY.createInnerJoinNode(IMMUTABILITY_TOOLS.foldBooleanExpressions(EXPRESSION1,EXPRESSION2,EXPRESSION3));
        queryBuilder1.addChild(constructionNode, joinNode1);
        ExtensionalDataNode dataNode1 =  IQ_FACTORY.createExtensionalDataNode(ATOM_FACTORY.getDataAtom(TABLE1_AR2, X, Y));
        InnerJoinNode joinNode2 = IQ_FACTORY.createInnerJoinNode();
        ExtensionalDataNode dataNode2 = IQ_FACTORY.createExtensionalDataNode(ATOM_FACTORY.getDataAtom(TABLE2_AR2, X, Z));
        ExtensionalDataNode dataNode3 = IQ_FACTORY.createExtensionalDataNode(ATOM_FACTORY.getDataAtom(TABLE3_AR2, Z, W));
        queryBuilder1.addChild(joinNode1, dataNode1);
        queryBuilder1.addChild(joinNode1, joinNode2);
        queryBuilder1.addChild(joinNode2, dataNode2);
        queryBuilder1.addChild(joinNode2, dataNode3);
        IntermediateQuery query1 = queryBuilder1.build();

        System.out.println("\nBefore optimization: \n" +  query1);

        IntermediateQuery optimizedQuery = PUSH_DOWN_BOOLEAN_EXPRESSION_OPTIMIZER.optimize(query1);

        System.out.println("\nAfter optimization: \n" +  optimizedQuery);

        IntermediateQueryBuilder queryBuilder2 = createQueryBuilder(DB_METADATA);
        DistinctVariableOnlyDataAtom projectionAtom2 = ATOM_FACTORY.getDistinctVariableOnlyDataAtom(ANS1_PREDICATE1, X, Y, Z, W);
        ConstructionNode constructionNode2 = IQ_FACTORY.createConstructionNode(projectionAtom.getVariables());
        queryBuilder2.init(projectionAtom2, constructionNode2);
        InnerJoinNode joinNode3 = IQ_FACTORY.createInnerJoinNode(Optional.of(EXPRESSION2));
        queryBuilder2.addChild(constructionNode2, joinNode3);
        InnerJoinNode joinNode4 = IQ_FACTORY.createInnerJoinNode(IMMUTABILITY_TOOLS.foldBooleanExpressions(EXPRESSION1,EXPRESSION3));
        queryBuilder2.addChild(joinNode3, dataNode1);
        queryBuilder2.addChild(joinNode3, joinNode4);
        queryBuilder2.addChild(joinNode4, dataNode2);
        queryBuilder2.addChild(joinNode4, dataNode3);
        IntermediateQuery query2 = queryBuilder2.build();

        System.out.println("\nExpected: \n" +  query2);

        assertTrue(IQSyntacticEquivalenceChecker.areEquivalent(optimizedQuery, query2));
    }

    @Test
    public void testJoiningCondition2 () throws EmptyQueryException {

        IntermediateQueryBuilder queryBuilder1 = createQueryBuilder(DB_METADATA);
        DistinctVariableOnlyDataAtom projectionAtom1 = ATOM_FACTORY.getDistinctVariableOnlyDataAtom(ANS1_PREDICATE2, X, Y, Z);
        DistinctVariableOnlyDataAtom projectionAtom2 = ATOM_FACTORY.getDistinctVariableOnlyDataAtom(ANS2_PREDICATE1, X, Z);

        ConstructionNode constructionNode1 = IQ_FACTORY.createConstructionNode(projectionAtom1.getVariables());
        InnerJoinNode joinNode1 = IQ_FACTORY.createInnerJoinNode(Optional.of(EXPRESSION1));
        ConstructionNode constructionNode2 = IQ_FACTORY.createConstructionNode(projectionAtom2.getVariables());
        UnionNode unionNode1 = IQ_FACTORY.createUnionNode(projectionAtom2.getVariables());
        ConstructionNode constructionNode3 = IQ_FACTORY.createConstructionNode(projectionAtom2.getVariables());
        ConstructionNode constructionNode4 = IQ_FACTORY.createConstructionNode(projectionAtom2.getVariables());

        ExtensionalDataNode dataNode1 =  IQ_FACTORY.createExtensionalDataNode(ATOM_FACTORY.getDataAtom(TABLE1_AR2, X, Y));
        ExtensionalDataNode dataNode2 = IQ_FACTORY.createExtensionalDataNode(ATOM_FACTORY.getDataAtom(TABLE2_AR2, X, Z));
        ExtensionalDataNode dataNode3 = IQ_FACTORY.createExtensionalDataNode(ATOM_FACTORY.getDataAtom(TABLE3_AR2, X, Z));

        queryBuilder1.init(projectionAtom1, constructionNode1);
        queryBuilder1.addChild(constructionNode1, joinNode1);
        queryBuilder1.addChild(joinNode1, dataNode1);
        queryBuilder1.addChild(joinNode1, constructionNode2);
        queryBuilder1.addChild(constructionNode2, unionNode1);
        queryBuilder1.addChild(unionNode1, constructionNode3);
        queryBuilder1.addChild(unionNode1, constructionNode4);
        queryBuilder1.addChild(constructionNode3, dataNode2);
        queryBuilder1.addChild(constructionNode4, dataNode3);

        IntermediateQuery query1 = queryBuilder1.build();

        System.out.println("\nBefore optimization: \n" +  query1);

        IntermediateQuery optimizedQuery = PUSH_DOWN_BOOLEAN_EXPRESSION_OPTIMIZER.optimize(query1);

        System.out.println("\nAfter optimization: \n" +  optimizedQuery);

        IntermediateQueryBuilder queryBuilder2 = createQueryBuilder(DB_METADATA);

        ConstructionNode constructionNode5 = IQ_FACTORY.createConstructionNode(projectionAtom1.getVariables());
        InnerJoinNode joinNode2 = IQ_FACTORY.createInnerJoinNode();
        ConstructionNode constructionNode6 = IQ_FACTORY.createConstructionNode(projectionAtom2.getVariables());
        UnionNode unionNode2 = IQ_FACTORY.createUnionNode(projectionAtom2.getVariables());
        ConstructionNode constructionNode7 = IQ_FACTORY.createConstructionNode(projectionAtom2.getVariables());
        ConstructionNode constructionNode8 = IQ_FACTORY.createConstructionNode(projectionAtom2.getVariables());
        FilterNode filterNode1 = IQ_FACTORY.createFilterNode(EXPRESSION1);
        FilterNode filterNode2 = IQ_FACTORY.createFilterNode(EXPRESSION1);

        queryBuilder2.init(projectionAtom1, constructionNode5);
        queryBuilder2.addChild(constructionNode5, joinNode2);
        queryBuilder2.addChild(joinNode2, dataNode1);
        queryBuilder2.addChild(joinNode2, constructionNode6);
        queryBuilder2.addChild(constructionNode6, unionNode2);
        queryBuilder2.addChild(unionNode2, constructionNode7);
        queryBuilder2.addChild(unionNode2, constructionNode8);
        queryBuilder2.addChild(constructionNode7, filterNode1);
        queryBuilder2.addChild(constructionNode8, filterNode2);
        queryBuilder2.addChild(filterNode1, dataNode2);
        queryBuilder2.addChild(filterNode2, dataNode3);

        IntermediateQuery query2 = queryBuilder2.build();

        System.out.println("\nExpected: \n" +  query2);

        assertTrue(IQSyntacticEquivalenceChecker.areEquivalent(optimizedQuery, query2));
    }

    @Test
    public void testJoiningCondition3 () throws EmptyQueryException {

        IntermediateQueryBuilder queryBuilder1 = createQueryBuilder(DB_METADATA);
        DistinctVariableOnlyDataAtom projectionAtom1 = ATOM_FACTORY.getDistinctVariableOnlyDataAtom(ANS1_PREDICATE1, X, Y, Z, W);
        ConstructionNode constructionNode1 = IQ_FACTORY.createConstructionNode(projectionAtom1.getVariables());
        FilterNode filterNode = IQ_FACTORY.createFilterNode(IMMUTABILITY_TOOLS.foldBooleanExpressions(EXPRESSION4, EXPRESSION5).get());
        LeftJoinNode leftJoinNode = IQ_FACTORY.createLeftJoinNode();
        ExtensionalDataNode dataNode1 = IQ_FACTORY.createExtensionalDataNode(ATOM_FACTORY.getDataAtom(TABLE1_AR2, X, Y));
        ExtensionalDataNode dataNode2 = IQ_FACTORY.createExtensionalDataNode(ATOM_FACTORY.getDataAtom(TABLE4_AR3, Y, Z, W));

        queryBuilder1.init(projectionAtom1, constructionNode1);
        queryBuilder1.addChild(constructionNode1, filterNode);
        queryBuilder1.addChild(filterNode, leftJoinNode);
        queryBuilder1.addChild(leftJoinNode, dataNode1, LEFT);
        queryBuilder1.addChild(leftJoinNode, dataNode2, RIGHT);

        IntermediateQuery query1 = queryBuilder1.build();
        IntermediateQuery initialQuery = query1.createSnapshot();

        System.out.println("\nBefore optimization: \n" +  query1);

        IntermediateQuery optimizedQuery = PUSH_DOWN_BOOLEAN_EXPRESSION_OPTIMIZER.optimize(query1);

        System.out.println("\nAfter optimization: \n" +  optimizedQuery);

        System.out.println("\nExpected: \n" +  initialQuery);

        assertTrue(IQSyntacticEquivalenceChecker.areEquivalent(optimizedQuery, initialQuery));
    }

    @Test
    public void testJoiningCondition4 () throws EmptyQueryException {

        IntermediateQueryBuilder queryBuilder1 = createQueryBuilder(DB_METADATA);
        DistinctVariableOnlyDataAtom projectionAtom1 = ATOM_FACTORY.getDistinctVariableOnlyDataAtom(ANS1_PREDICATE3, X, Y, Z, W, A);
        ConstructionNode constructionNode1 = IQ_FACTORY.createConstructionNode(projectionAtom1.getVariables());
        InnerJoinNode joinNode1 = IQ_FACTORY.createInnerJoinNode(Optional.of(EXPRESSION1));
        LeftJoinNode leftJoinNode1 = IQ_FACTORY.createLeftJoinNode();
        LeftJoinNode leftJoinNode2 = IQ_FACTORY.createLeftJoinNode();
        ExtensionalDataNode dataNode1 = IQ_FACTORY.createExtensionalDataNode(ATOM_FACTORY.getDataAtom(TABLE1_AR2, X, Y));
        ExtensionalDataNode dataNode2 = IQ_FACTORY.createExtensionalDataNode(ATOM_FACTORY.getDataAtom(TABLE2_AR2, X, Z));
        ExtensionalDataNode dataNode3 = IQ_FACTORY.createExtensionalDataNode(ATOM_FACTORY.getDataAtom(TABLE2_AR2, X, A));
        ExtensionalDataNode dataNode4 = IQ_FACTORY.createExtensionalDataNode(ATOM_FACTORY.getDataAtom(TABLE5_AR2, X, W));

        queryBuilder1.init(projectionAtom1, constructionNode1);
        queryBuilder1.addChild(constructionNode1, joinNode1);
        queryBuilder1.addChild(joinNode1, dataNode1);
        queryBuilder1.addChild(joinNode1, leftJoinNode1);
        queryBuilder1.addChild(leftJoinNode1, leftJoinNode2, LEFT);
        queryBuilder1.addChild(leftJoinNode1, dataNode4, RIGHT);
        queryBuilder1.addChild(leftJoinNode2, dataNode2, LEFT);
        queryBuilder1.addChild(leftJoinNode2, dataNode3, RIGHT);

        IntermediateQuery query1 = queryBuilder1.build();

        System.out.println("\nBefore optimization: \n" +  query1);

        IntermediateQuery optimizedQuery = PUSH_DOWN_BOOLEAN_EXPRESSION_OPTIMIZER.optimize(query1);

        System.out.println("\nAfter optimization: \n" +  optimizedQuery);


        IntermediateQueryBuilder queryBuilder2 = createQueryBuilder(DB_METADATA);
        DistinctVariableOnlyDataAtom projectionAtom2 = ATOM_FACTORY.getDistinctVariableOnlyDataAtom(ANS1_PREDICATE3, X, Y, Z, W, A);
        ConstructionNode constructionNode2 = IQ_FACTORY.createConstructionNode(projectionAtom1.getVariables());
        InnerJoinNode joinNode2 = IQ_FACTORY.createInnerJoinNode();
        LeftJoinNode leftJoinNode3 = IQ_FACTORY.createLeftJoinNode();
        LeftJoinNode leftJoinNode4 = IQ_FACTORY.createLeftJoinNode();
        FilterNode filterNode1 = IQ_FACTORY.createFilterNode(EXPRESSION1);

        queryBuilder2.init(projectionAtom2, constructionNode2);
        queryBuilder2.addChild(constructionNode2, joinNode2);
        queryBuilder2.addChild(joinNode2, dataNode1);
        queryBuilder2.addChild(joinNode2, leftJoinNode3);
        queryBuilder2.addChild(leftJoinNode3, leftJoinNode4, LEFT);
        queryBuilder2.addChild(leftJoinNode3, dataNode4, RIGHT);
        queryBuilder2.addChild(leftJoinNode4, filterNode1, LEFT);
        queryBuilder2.addChild(leftJoinNode4, dataNode3, RIGHT);
        queryBuilder2.addChild(filterNode1, dataNode2);

        IntermediateQuery query2 = queryBuilder2.build();

        System.out.println("\nExpected: \n" +  query2);

        assertTrue(IQSyntacticEquivalenceChecker.areEquivalent(optimizedQuery, query2));
    }

    @Test
    public void testJoiningCondition5 () throws EmptyQueryException {

        IntermediateQueryBuilder queryBuilder1 = createQueryBuilder(DB_METADATA);
        DistinctVariableOnlyDataAtom projectionAtom1 = ATOM_FACTORY.getDistinctVariableOnlyDataAtom(ANS1_PREDICATE1, X, Y, Z, W);
        ConstructionNode constructionNode1 = IQ_FACTORY.createConstructionNode(projectionAtom1.getVariables());
        InnerJoinNode joinNode1 = IQ_FACTORY.createInnerJoinNode(EXPRESSION6);
        LeftJoinNode leftJoinNode1 = IQ_FACTORY.createLeftJoinNode();
        ExtensionalDataNode dataNode1 = IQ_FACTORY.createExtensionalDataNode(ATOM_FACTORY.getDataAtom(TABLE1_AR2, X, Y));
        ExtensionalDataNode dataNode2 = IQ_FACTORY.createExtensionalDataNode(ATOM_FACTORY.getDataAtom(TABLE2_AR2, X, Z));
        ExtensionalDataNode dataNode3 = IQ_FACTORY.createExtensionalDataNode(ATOM_FACTORY.getDataAtom(TABLE5_AR2, X, W));

        queryBuilder1.init(projectionAtom1, constructionNode1);
        queryBuilder1.addChild(constructionNode1, joinNode1);
        queryBuilder1.addChild(joinNode1, dataNode1);
        queryBuilder1.addChild(joinNode1, leftJoinNode1);
        queryBuilder1.addChild(leftJoinNode1, dataNode2, LEFT);
        queryBuilder1.addChild(leftJoinNode1, dataNode3, RIGHT);

        IntermediateQuery query1 = queryBuilder1.build();
        IntermediateQuery initialQuery = query1.createSnapshot();

        System.out.println("\nBefore optimization: \n" +  query1);

        IntermediateQuery optimizedQuery = PUSH_DOWN_BOOLEAN_EXPRESSION_OPTIMIZER.optimize(query1);

        System.out.println("\nAfter optimization: \n" +  optimizedQuery);

        assertTrue(IQSyntacticEquivalenceChecker.areEquivalent(optimizedQuery, initialQuery));

    }

    @Test
    public void testLeftJoinCondition1 () throws EmptyQueryException {

        IntermediateQueryBuilder queryBuilder1 = createQueryBuilder(DB_METADATA);
        DistinctVariableOnlyDataAtom projectionAtom1 = ATOM_FACTORY.getDistinctVariableOnlyDataAtom(ANS1_PREDICATE1, W, X, Y, Z);
        ConstructionNode constructionNode1 = IQ_FACTORY.createConstructionNode(projectionAtom1.getVariables());
        LeftJoinNode leftJoinNode1 = IQ_FACTORY.createLeftJoinNode(EXPRESSION7);
        InnerJoinNode innerJoinNode1 = IQ_FACTORY.createInnerJoinNode();
        ExtensionalDataNode dataNode1 = IQ_FACTORY.createExtensionalDataNode(ATOM_FACTORY.getDataAtom(TABLE1_AR2, X, Y));
        ExtensionalDataNode dataNode2 = IQ_FACTORY.createExtensionalDataNode(ATOM_FACTORY.getDataAtom(TABLE2_AR2, X, W));
        ExtensionalDataNode dataNode3 = IQ_FACTORY.createExtensionalDataNode(ATOM_FACTORY.getDataAtom(TABLE3_AR2, X, Z));

        queryBuilder1.init(projectionAtom1, constructionNode1);
        queryBuilder1.addChild(constructionNode1, leftJoinNode1);
        queryBuilder1.addChild(leftJoinNode1, innerJoinNode1, LEFT);
        queryBuilder1.addChild(leftJoinNode1, dataNode3, RIGHT);
        queryBuilder1.addChild(innerJoinNode1, dataNode1);
        queryBuilder1.addChild(innerJoinNode1, dataNode2);


        IntermediateQuery query1 = queryBuilder1.build();

        System.out.println("\nBefore optimization: \n" +  query1);

        IntermediateQuery optimizedQuery = PUSH_DOWN_BOOLEAN_EXPRESSION_OPTIMIZER.optimize(query1);

        System.out.println("\nAfter optimization: \n" +  optimizedQuery);


        IntermediateQueryBuilder queryBuilder2 = createQueryBuilder(DB_METADATA);
        DistinctVariableOnlyDataAtom projectionAtom2 = ATOM_FACTORY.getDistinctVariableOnlyDataAtom(ANS1_PREDICATE1, W, X, Y, Z);
        ConstructionNode constructionNode2 = IQ_FACTORY.createConstructionNode(projectionAtom1.getVariables());
        LeftJoinNode leftJoinNode2 = IQ_FACTORY.createLeftJoinNode(EXPRESSION7);
        InnerJoinNode innerJoinNode2 = IQ_FACTORY.createInnerJoinNode();

        queryBuilder2.init(projectionAtom2, constructionNode2);
        queryBuilder2.addChild(constructionNode2, leftJoinNode2);
        queryBuilder2.addChild(leftJoinNode2, innerJoinNode2, LEFT);
        queryBuilder2.addChild(leftJoinNode2, dataNode3, RIGHT);
        queryBuilder2.addChild(innerJoinNode2, dataNode1);
        queryBuilder2.addChild(innerJoinNode2, dataNode2);


        IntermediateQuery query2 = queryBuilder2.build();

        System.out.println("\nExpected: \n" +  query2);

        assertTrue(IQSyntacticEquivalenceChecker.areEquivalent(optimizedQuery, query2));

    }

    @Test
    public void testLeftJoinCondition2 () throws EmptyQueryException {

        IntermediateQueryBuilder queryBuilder1 = createQueryBuilder(DB_METADATA);
        DistinctVariableOnlyDataAtom projectionAtom1 = ATOM_FACTORY.getDistinctVariableOnlyDataAtom(ANS1_PREDICATE1, W, X, Y, Z);
        ConstructionNode constructionNode1 = IQ_FACTORY.createConstructionNode(projectionAtom1.getVariables());
        LeftJoinNode leftJoinNode1 = IQ_FACTORY.createLeftJoinNode(EXPRESSION7);
        InnerJoinNode innerJoinNode1 = IQ_FACTORY.createInnerJoinNode();
        ExtensionalDataNode dataNode1 = IQ_FACTORY.createExtensionalDataNode(ATOM_FACTORY.getDataAtom(TABLE1_AR2, X, Y));
        ExtensionalDataNode dataNode2 = IQ_FACTORY.createExtensionalDataNode(ATOM_FACTORY.getDataAtom(TABLE2_AR2, X, W));
        ExtensionalDataNode dataNode3 = IQ_FACTORY.createExtensionalDataNode(ATOM_FACTORY.getDataAtom(TABLE3_AR2, X, Z));

        queryBuilder1.init(projectionAtom1, constructionNode1);
        queryBuilder1.addChild(constructionNode1, leftJoinNode1);
        queryBuilder1.addChild(leftJoinNode1, dataNode2, LEFT);
        queryBuilder1.addChild(leftJoinNode1, innerJoinNode1, RIGHT);
        queryBuilder1.addChild(innerJoinNode1, dataNode1);
        queryBuilder1.addChild(innerJoinNode1, dataNode3);


        IntermediateQuery query1 = queryBuilder1.build();

        System.out.println("\nBefore optimization: \n" +  query1);

        IntermediateQuery optimizedQuery = PUSH_DOWN_BOOLEAN_EXPRESSION_OPTIMIZER.optimize(query1);

        System.out.println("\nAfter optimization: \n" +  optimizedQuery);


        IntermediateQueryBuilder queryBuilder2 = createQueryBuilder(DB_METADATA);
        DistinctVariableOnlyDataAtom projectionAtom2 = ATOM_FACTORY.getDistinctVariableOnlyDataAtom(ANS1_PREDICATE1, W, X, Y, Z);
        ConstructionNode constructionNode2 = IQ_FACTORY.createConstructionNode(projectionAtom1.getVariables());
        LeftJoinNode leftJoinNode2 = IQ_FACTORY.createLeftJoinNode();
        InnerJoinNode innerJoinNode2 = IQ_FACTORY.createInnerJoinNode(EXPRESSION7);

        queryBuilder2.init(projectionAtom2, constructionNode2);
        queryBuilder2.addChild(constructionNode2, leftJoinNode2);
        queryBuilder2.addChild(leftJoinNode2, dataNode2, LEFT);
        queryBuilder2.addChild(leftJoinNode2, innerJoinNode2, RIGHT);
        queryBuilder2.addChild(innerJoinNode2, dataNode1);
        queryBuilder2.addChild(innerJoinNode2, dataNode3);


        IntermediateQuery query2 = queryBuilder2.build();

        System.out.println("\nExpected: \n" +  query2);

        assertTrue(IQSyntacticEquivalenceChecker.areEquivalent(optimizedQuery, query2));

    }

    @Test
    public void testLeftJoinAndFilterCondition1 () throws EmptyQueryException {

        IntermediateQueryBuilder queryBuilder1 = createQueryBuilder(DB_METADATA);
        DistinctVariableOnlyDataAtom projectionAtom1 = ATOM_FACTORY.getDistinctVariableOnlyDataAtom(ANS1_PREDICATE1, W, X, Y, Z);
        ConstructionNode constructionNode1 = IQ_FACTORY.createConstructionNode(projectionAtom1.getVariables());
        FilterNode filterNode1 = IQ_FACTORY.createFilterNode(EXPRESSION6);
        LeftJoinNode leftJoinNode1 = IQ_FACTORY.createLeftJoinNode(EXPRESSION7);
        InnerJoinNode innerJoinNode1 = IQ_FACTORY.createInnerJoinNode();
        ExtensionalDataNode dataNode1 = IQ_FACTORY.createExtensionalDataNode(ATOM_FACTORY.getDataAtom(TABLE1_AR2, X, Y));
        ExtensionalDataNode dataNode2 = IQ_FACTORY.createExtensionalDataNode(ATOM_FACTORY.getDataAtom(TABLE2_AR2, X, W));
        ExtensionalDataNode dataNode3 = IQ_FACTORY.createExtensionalDataNode(ATOM_FACTORY.getDataAtom(TABLE2_AR2, X, Z));

        queryBuilder1.init(projectionAtom1, constructionNode1);
        queryBuilder1.addChild(constructionNode1, filterNode1);
        queryBuilder1.addChild(filterNode1, leftJoinNode1);
        queryBuilder1.addChild(leftJoinNode1, innerJoinNode1, LEFT);
        queryBuilder1.addChild(leftJoinNode1, dataNode3, RIGHT);
        queryBuilder1.addChild(innerJoinNode1, dataNode1);
        queryBuilder1.addChild(innerJoinNode1, dataNode2);


        IntermediateQuery query1 = queryBuilder1.build();

        System.out.println("\nBefore optimization: \n" +  query1);

        IntermediateQuery optimizedQuery = PUSH_DOWN_BOOLEAN_EXPRESSION_OPTIMIZER.optimize(query1);

        System.out.println("\nAfter optimization: \n" +  optimizedQuery);


        IntermediateQueryBuilder queryBuilder2 = createQueryBuilder(DB_METADATA);
        DistinctVariableOnlyDataAtom projectionAtom2 = ATOM_FACTORY.getDistinctVariableOnlyDataAtom(ANS1_PREDICATE1, W, X, Y, Z);
        ConstructionNode constructionNode2 = IQ_FACTORY.createConstructionNode(projectionAtom1.getVariables());
        LeftJoinNode leftJoinNode2 = IQ_FACTORY.createLeftJoinNode(EXPRESSION7);
        InnerJoinNode innerJoinNode2 = IQ_FACTORY.createInnerJoinNode(EXPRESSION6);

        queryBuilder2.init(projectionAtom2, constructionNode2);
        queryBuilder2.addChild(constructionNode2, leftJoinNode2);
        queryBuilder2.addChild(leftJoinNode2, innerJoinNode2, LEFT);
        queryBuilder2.addChild(leftJoinNode2, dataNode3, RIGHT);
        queryBuilder2.addChild(innerJoinNode2, dataNode1);
        queryBuilder2.addChild(innerJoinNode2, dataNode2);


        IntermediateQuery query2 = queryBuilder2.build();

        System.out.println("\nExpected: \n" +  query2);

        assertTrue(IQSyntacticEquivalenceChecker.areEquivalent(optimizedQuery, query2));

    }

    @Test
    public void testLeftJoinAndFilterCondition2 () throws EmptyQueryException {

        IntermediateQueryBuilder queryBuilder1 = createQueryBuilder(DB_METADATA);
        DistinctVariableOnlyDataAtom projectionAtom1 = ATOM_FACTORY.getDistinctVariableOnlyDataAtom(ANS1_PREDICATE1, W, X, Y, Z);
        ConstructionNode constructionNode1 = IQ_FACTORY.createConstructionNode(projectionAtom1.getVariables());
        FilterNode filterNode1 = IQ_FACTORY.createFilterNode(EXPRESSION1);
        LeftJoinNode leftJoinNode1 = IQ_FACTORY.createLeftJoinNode(EXPRESSION7);
        InnerJoinNode innerJoinNode1 = IQ_FACTORY.createInnerJoinNode();
        ExtensionalDataNode dataNode1 = IQ_FACTORY.createExtensionalDataNode(ATOM_FACTORY.getDataAtom(TABLE1_AR2, X, Y));
        ExtensionalDataNode dataNode2 = IQ_FACTORY.createExtensionalDataNode(ATOM_FACTORY.getDataAtom(TABLE2_AR2, X, W));
        ExtensionalDataNode dataNode3 = IQ_FACTORY.createExtensionalDataNode(ATOM_FACTORY.getDataAtom(TABLE2_AR2, X, Z));

        queryBuilder1.init(projectionAtom1, constructionNode1);
        queryBuilder1.addChild(constructionNode1, filterNode1);
        queryBuilder1.addChild(filterNode1, leftJoinNode1);
        queryBuilder1.addChild(leftJoinNode1, innerJoinNode1, LEFT);
        queryBuilder1.addChild(leftJoinNode1, dataNode3, RIGHT);
        queryBuilder1.addChild(innerJoinNode1, dataNode1);
        queryBuilder1.addChild(innerJoinNode1, dataNode2);


        IntermediateQuery query1 = queryBuilder1.build();

        System.out.println("\nBefore optimization: \n" +  query1);

        IntermediateQuery initialQuery = query1.createSnapshot();
        IntermediateQuery optimizedQuery = PUSH_DOWN_BOOLEAN_EXPRESSION_OPTIMIZER.optimize(query1);

        System.out.println("\nAfter optimization: \n" +  optimizedQuery);

        System.out.println("\nExpected: \n" +  initialQuery);

        assertTrue(IQSyntacticEquivalenceChecker.areEquivalent(optimizedQuery, initialQuery));
    }
}
