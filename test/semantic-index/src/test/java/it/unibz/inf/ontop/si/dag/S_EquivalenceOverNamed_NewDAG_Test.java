package it.unibz.inf.ontop.si.dag;

/*
 * #%L
 * ontop-quest-owlapi
 * %%
 * Copyright (C) 2009 - 2014 Free University of Bozen-Bolzano
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */


import it.unibz.inf.ontop.spec.ontology.*;
import it.unibz.inf.ontop.spec.ontology.impl.ClassifiedTBoxImpl;
import junit.framework.TestCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import static it.unibz.inf.ontop.utils.SITestingTools.loadOntologyFromFileAndClassify;

public class S_EquivalenceOverNamed_NewDAG_Test extends TestCase {


	Logger log = LoggerFactory.getLogger(S_EquivalenceOverNamed_NewDAG_Test.class);

	public S_EquivalenceOverNamed_NewDAG_Test (String name){
		super(name);
	}

	public void testNamedAndEquivalences() throws Exception {
		ArrayList<String> input = new ArrayList<>();
		input.add("src/test/resources/test/stockexchange-unittest.owl");
		input.add("src/test/resources/test/dag/test-equivalence-roles-inverse.owl");
		input.add("src/test/resources/test/dag/test-role-hierarchy.owl");
		/** C = B -> ER -> A*/
		input.add("src/test/resources/test/newDag/equivalents1.owl");
		/** B -> A -> ER=C */
		input.add("src/test/resources/test/newDag/equivalents2.owl");
		/** C->B = ER -> A*/
		input.add("src/test/resources/test/newDag/equivalents3.owl");
		/** ER-> A=B=C */
		input.add("src/test/resources/test/newDag/equivalents4.owl");
		/** C=ER=A->B */
		input.add("src/test/resources/test/newDag/equivalents5.owl");
		/** D-> ER=C=B -> A*/
		input.add("src/test/resources/test/newDag/equivalents6.owl");
		/** P-> ER=B -> A  C=L ->ES-> ER */
		input.add("src/test/resources/test/newDag/equivalents7.owl");
		/** B->A=ET->ER C->ES=D->A*/
		input.add("src/test/resources/test/newDag/equivalents8.owl");

		/** C = B -> ER- -> A*/
		input.add("src/test/resources/test/newDag/inverseEquivalents1.owl");
		/** B -> A -> ER- = C */
		input.add("src/test/resources/test/newDag/inverseEquivalents2.owl");
		/** C->B = ER- -> A*/
		input.add("src/test/resources/test/newDag/inverseEquivalents3.owl");
		/** ER- -> A=B=C */
		input.add("src/test/resources/test/newDag/inverseEquivalents4.owl");
		/** C=ER- =A->B */
		input.add("src/test/resources/test/newDag/inverseEquivalents5.owl");
		/** D-> ER  ER- =C=B -> A*/
		input.add("src/test/resources/test/newDag/inverseEquivalents6.owl");
		/** D->  ER- =C=B -> A*/
		input.add("src/test/resources/test/newDag/inverseEquivalents6b.owl");
		/** P-> ER- =B -> A  C=L ->ES- -> ER- */
		input.add("src/test/resources/test/newDag/inverseEquivalents7.owl");
		/** B->A=ET- ->ER- C->ES- = D->A*/
		input.add("src/test/resources/test/newDag/inverseEquivalents8.owl");

		//for each file in the input
		for (String fileInput : input){
			ClassifiedTBoxImpl reasoner = (ClassifiedTBoxImpl) loadOntologyFromFileAndClassify(fileInput);
			//transform in a named graph
			TestClassifiedTBoxImpl_OnNamedDAG namedDag2 = new TestClassifiedTBoxImpl_OnNamedDAG(reasoner);
			log.debug("Input {}", fileInput);
			log.info("First graph {}", reasoner.getClassGraph());
			log.info("First graph {}", reasoner.getObjectPropertyGraph());
			log.info("Second dag {}", namedDag2);
			TestClassifiedTBoxImpl_Named dag2 = new TestClassifiedTBoxImpl_Named(reasoner);

			assertTrue(testDescendants(dag2.classesDAG(), namedDag2.classesDAG()));
			assertTrue(testDescendants(dag2.objectPropertiesDAG(), namedDag2.objectPropertiesDAG()));
			assertTrue(testDescendants(namedDag2.classesDAG(), dag2.classesDAG()));
			assertTrue(testDescendants(namedDag2.objectPropertiesDAG(), dag2.objectPropertiesDAG()));
			assertTrue(testChildren(dag2.classesDAG(), namedDag2.classesDAG()));
			assertTrue(testChildren(dag2.objectPropertiesDAG(), namedDag2.objectPropertiesDAG()));
			assertTrue(testChildren(namedDag2.classesDAG(), dag2.classesDAG()));
			assertTrue(testChildren(namedDag2.objectPropertiesDAG(), dag2.objectPropertiesDAG()));
			assertTrue(testAncestors(dag2.classesDAG(), namedDag2.classesDAG()));
			assertTrue(testAncestors(dag2.objectPropertiesDAG(), namedDag2.objectPropertiesDAG()));
			assertTrue(testAncestors(namedDag2.classesDAG(), dag2.classesDAG()));
			assertTrue(testAncestors(namedDag2.objectPropertiesDAG(), dag2.objectPropertiesDAG()));
			assertTrue(testParents(dag2.classesDAG(), namedDag2.classesDAG()));
			assertTrue(testParents(dag2.objectPropertiesDAG(), namedDag2.objectPropertiesDAG()));
			assertTrue(testParents(namedDag2.classesDAG(), dag2.classesDAG()));
			assertTrue(testParents(namedDag2.objectPropertiesDAG(), dag2.objectPropertiesDAG()));
//			assertTrue(checkVertexReduction(graph1, namedDag2, true));
			//check only if the number of edges is smaller
			//assertTrue(checkEdgeReduction(graph1, namedDag2, true)); COMMENTED OUT BY ROMAN
			assertTrue(checkforNamedVertexesOnly(namedDag2, reasoner));
		}
	}

	
	private static <T> boolean coincide(Set<Equivalences<T>> setd1, Set<Equivalences<T>> setd2) {
		
		Set<T> set1 = new HashSet<>();
		Iterator<Equivalences<T>> it1 = setd1.iterator();
		while (it1.hasNext()) {
			set1.addAll(it1.next().getMembers());	
		}
		
		Set<T> set2 = new HashSet<>();
		Iterator<Equivalences<T>> it2 = setd2.iterator();
		while (it2.hasNext()) {
			set2.addAll(it2.next().getMembers());	
		}
		return set1.equals(set2);		
	}
	
	private <T> boolean testDescendants(EquivalencesDAG<T> d1, EquivalencesDAG<T> d2) {
		
		for(Equivalences<T> node : d1) {
			Set<Equivalences<T>> setd1 = d1.getSub(node);
			log.info("vertex {}", node);
			log.debug("descendants {} ", setd1);
			Set<Equivalences<T>> setd2 = d2.getSub(node);
			log.debug("descendants {} ", setd2);
			if (!coincide(setd1, setd2))
				return false;
		}
		return true;
	}

	private <T> boolean testChildren(EquivalencesDAG<T> d1, EquivalencesDAG<T> d2){

		for(Equivalences<T> node : d1) {
			Set<Equivalences<T>> setd1	= d1.getDirectSub(node);
			log.info("vertex {}", node);
			log.debug("children {} ", setd1);
			Set<Equivalences<T>> setd2	= d2.getDirectSub(node);
			log.debug("children {} ", setd2);
			if (!coincide(setd1, setd2))
				return false;
		}

		return true;
	}
			
	
	private <T> boolean testAncestors(EquivalencesDAG<T> d1, EquivalencesDAG<T> d2) {
		
		for(Equivalences<T> v: d1){
			Set<Equivalences<T>> setd1 = d1.getSuper(v);
			log.info("vertex {}", v);
			log.debug("ancestors {} ", setd1);
			Set<Equivalences<T>> setd2 = d2.getSuper(v);
			log.debug("ancestors {} ", setd2);
			
			if (!coincide(setd1, setd2))
				return false;
		}
		return true;
	}

	private <T> boolean testParents(EquivalencesDAG<T> d1, EquivalencesDAG<T> d2) {
	
		for(Equivalences<T> node : d1) {
			Set<Equivalences<T>> setd1	= d1.getDirectSuper(node);
			log.info("vertex {}", node);
			log.debug("parents {} ", setd1);
			Set<Equivalences<T>> setd2	= d2.getDirectSuper(node);
			log.debug("parents {} ", setd2);
			if (!coincide(setd1, setd2))  
				return false;
		}
		return true;
	}

/*
			private boolean checkVertexReduction(DefaultDirectedGraph<Description,DefaultEdge> d1, Test_NamedTBoxReasonerImpl d2, boolean named){

				//number of vertexes in the graph
				int numberVertexesD1= 0;
				//
				//if(d2.isaNamedDAG()){
				//	for (Description v: d1.vertexSet()){
				//		if(d1.getRoles().contains(v)| d1.classesDAG().contains(v)){
				//			numberVertexesD1++;
				//			System.out.println(v);
				//		}
				//	}
				//}
				//else			 
					numberVertexesD1= d1.vertexSet().size();
				
				//number of vertexes in the dag
				int numberVertexesD2 = d2.getNodes().size();

				//number of vertexes in the equivalent mapping
				int numberEquivalents=0;


				Set<Equivalences<Description>> nodesd2= d2.getNodes();
				Set<Description> set2 = new HashSet<Description>();
				Iterator<Equivalences<Description>> it1 =nodesd2.iterator();
				while (it1.hasNext()) {
					Equivalences<Description> equivalents=it1.next();
					numberEquivalents += equivalents.size()-1;
					set2.addAll(equivalents.getMembers());	
				}
				
				log.info("vertex graph {}", numberVertexesD1);
				log.info("set {}", set2.size());

				log.info("vertex dag {}", numberVertexesD2);
				log.info("equivalents {} ", numberEquivalents);

				return numberVertexesD1== set2.size() & numberEquivalents== (numberVertexesD1-numberVertexesD2);

			}

			private boolean checkEdgeReduction(DefaultDirectedGraph<Description,DefaultEdge> d1, TestTBoxReasonerImplOnNamedDAG d2, boolean named){
				
				//number of edges in the graph
				int  numberEdgesD1= d1.edgeSet().size();
				System.out.println(numberEdgesD1);
				//number of edges in the dag
				int numberEdgesD2 = d2.getEdgesSize();
				System.out.println(numberEdgesD2);

				//number of edges between the equivalent nodes
				int numberEquivalents=0;
				
				if(named){
					TestTBoxReasonerImplOnGraph reasonerd1 = new TestTBoxReasonerImplOnGraph(d1);
					for(Description vertex: d1.vertexSet()) {
						if(!reasonerd1.isNamed(vertex)) {
							if(d1.inDegreeOf(vertex)>=1 || d1.outDegreeOf(vertex)>=1){
								numberEdgesD1 -=1;
							}	
						}
					}
				}				

				Set<Equivalences<Description>> nodesd2= d2.getNodes();
				Iterator<Equivalences<Description>> it1 =nodesd2.iterator();
				while (it1.hasNext()) {
					Equivalences<Description> equivalents=it1.next();
					//System.out.println(equivalents);
					//two nodes have two edges, three nodes have three edges...
					if(equivalents.size()>=2){
						numberEquivalents += equivalents.size();
					}
				}
				
				log.info("edges graph {}", numberEdgesD1);
				log.info("edges dag {}", numberEdgesD2);
				log.info("equivalents {} ", numberEquivalents);

				return numberEdgesD1>= (numberEquivalents+ numberEdgesD2);

			}
*/			
	private boolean checkforNamedVertexesOnly(TestClassifiedTBoxImpl_OnNamedDAG dag, ClassifiedTBox reasoner){
		for (Equivalences<ObjectPropertyExpression> node: dag.objectPropertiesDAG()) {
			ObjectPropertyExpression vertex = node.getRepresentative();
			if (!reasoner.objectPropertiesDAG().getVertex(vertex).isIndexed())
				return false;
		}
		for (Equivalences<DataPropertyExpression> node: dag.dataPropertiesDAG()) {
			DataPropertyExpression vertex = node.getRepresentative();
			if(!reasoner.dataPropertiesDAG().getVertex(vertex).isIndexed())
				return false;
		}
		for (Equivalences<ClassExpression> node: dag.classesDAG()) {
			ClassExpression vertex = node.getRepresentative();
			if (!reasoner.classesDAG().getVertex(vertex).isIndexed())
				return false;
		}
		return true;
	}
}


