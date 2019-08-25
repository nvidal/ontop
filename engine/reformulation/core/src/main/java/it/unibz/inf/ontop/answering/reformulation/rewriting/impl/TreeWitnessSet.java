package it.unibz.inf.ontop.answering.reformulation.rewriting.impl;

/*
 * #%L
 * ontop-reformulation-core
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

import com.google.common.collect.ImmutableList;
import it.unibz.inf.ontop.model.atom.RDFAtomPredicate;
import it.unibz.inf.ontop.model.term.*;
import it.unibz.inf.ontop.model.term.impl.ImmutabilityTools;
import it.unibz.inf.ontop.spec.ontology.ClassExpression;
import it.unibz.inf.ontop.spec.ontology.ClassifiedTBox;
import it.unibz.inf.ontop.spec.ontology.ObjectPropertyExpression;
import it.unibz.inf.ontop.answering.reformulation.rewriting.impl.QueryConnectedComponent.Edge;
import it.unibz.inf.ontop.answering.reformulation.rewriting.impl.QueryConnectedComponent.Loop;

import java.util.*;

import it.unibz.inf.ontop.utils.ImmutableCollectors;
import org.apache.commons.rdf.api.IRI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TreeWitnessSet {
	private final List<TreeWitness> tws = new LinkedList<>();
	private final QueryConnectedComponent cc;
	private final Collection<TreeWitnessGenerator> allTWgenerators;
	private final QueryConnectedComponentCache cache; 
	private boolean hasConflicts = false;
	
	// working lists (may all be nulls)
	private List<TreeWitness> mergeable;
	private Queue<TreeWitness> delta;
	private Map<TreeWitness.TermCover, TreeWitness> twsCache;

	private static final Logger log = LoggerFactory.getLogger(TreeWitnessSet.class);

	private TreeWitnessSet(QueryConnectedComponent cc, ClassifiedTBox reasoner, Collection<TreeWitnessGenerator> allTWgenerators,
						   ImmutabilityTools immutabilityTools) {
		this.cc = cc;
		this.cache = new QueryConnectedComponentCache(reasoner, immutabilityTools);
		this.allTWgenerators = allTWgenerators;
	}
	
	public Collection<TreeWitness> getTWs() {
		return tws;
	}
	
	public boolean hasConflicts() {
		return hasConflicts;
	}
	
	public static TreeWitnessSet getTreeWitnesses(QueryConnectedComponent cc, ClassifiedTBox reasoner,
												  Collection<TreeWitnessGenerator> generators, ImmutabilityTools immutabilityTools) {
		TreeWitnessSet treewitnesses = new TreeWitnessSet(cc, reasoner, generators, immutabilityTools);
		
		if (!cc.isDegenerate())
			treewitnesses.computeTreeWitnesses();
				
		return treewitnesses;
	}

	private void computeTreeWitnesses() {		
		QueryFolding qf = new QueryFolding(cache); // in-place query folding, so copying is required when creating a tree witness
		
		for (Loop loop : cc.getQuantifiedVariables()) {
			Term v = loop.getTerm();
			log.debug("QUANTIFIED VARIABLE {}", v); 
			qf.newOneStepFolding(v);
			
			for (Edge edge : cc.getEdges()) { // loop.getAdjacentEdges()
				if (edge.getTerm0().equals(v)) {
					if (!qf.extend(edge.getLoop1(), edge, loop))
						break;
				}
				else if (edge.getTerm1().equals(v)) {
					if (!qf.extend(edge.getLoop0(), edge, loop))
						break;
				}
			}
			
			if (qf.isValid()) {
				// tws cannot contain duplicates by construction, so no caching (even negative)
				Collection<TreeWitnessGenerator> twg = getTreeWitnessGenerators(qf); 
				if (twg != null) { 
					// no need to copy the query folding: it creates all temporary objects anyway (including NewLiterals)
					addTWS(qf.getTreeWitness(twg, cc.getEdges()));
				}
			}
		}		
		
		if (!tws.isEmpty()) {
			mergeable = new ArrayList<>();
			Queue<TreeWitness> working = new LinkedList<TreeWitness>();

			for (TreeWitness tw : tws) 
				if (tw.isMergeable())  {
					working.add(tw);			
					mergeable.add(tw);
				}
			
			delta = new LinkedList<>();
			twsCache = new HashMap<>();

			while (!working.isEmpty()) {
				while (!working.isEmpty()) {
					TreeWitness tw = working.poll(); 
					qf.newQueryFolding(tw);
					saturateTreeWitnesses(qf); 					
				}
				
				while (!delta.isEmpty()) {
					TreeWitness tw = delta.poll();
					addTWS(tw);
					if (tw.isMergeable())  {
						working.add(tw);			
						mergeable.add(tw);
					}
				}
			}				
		}
		
		log.debug("TREE WITNESSES FOUND: {}", tws.size());
	}
	
	private void addTWS(TreeWitness tw1) {
		for (TreeWitness tw0 : tws)
			if (!tw0.getDomain().containsAll(tw1.getDomain()) && !tw1.getDomain().containsAll(tw0.getDomain())) {
				if (!TreeWitness.isCompatible(tw0, tw1)) {
					hasConflicts = true;
					log.debug("CONFLICT: {}  AND {}", tw0, tw1);
				}
			}
		tws.add(tw1);
	}
	
	private void saturateTreeWitnesses(QueryFolding qf) { 
		boolean saturated = true; 
		
		for (Edge edge : cc.getEdges()) { 
			Loop rootLoop, internalLoop;
			if (qf.canBeAttachedToAnInternalRoot(edge.getLoop0(), edge.getLoop1())) {
				rootLoop = edge.getLoop0();
				internalLoop = edge.getLoop1();
			}
			else if (qf.canBeAttachedToAnInternalRoot(edge.getLoop1(), edge.getLoop0())) { 
				rootLoop = edge.getLoop1();
				internalLoop = edge.getLoop0();
			}
			else
				continue;
			
			log.debug("EDGE {} IS ADJACENT TO THE TREE WITNESS {}", edge, qf); 

			if (qf.getRoots().contains(internalLoop)) {
				if (qf.extend(internalLoop, edge, rootLoop)) {
					log.debug("    RE-ATTACHING A HANDLE {}", edge);
					continue;
				}	
				else {
					log.debug("    FAILED TO RE-ATTACH A HANDLE {}", edge);
					return;					
				}
			}

			saturated = false; 

			Term rootNewLiteral = rootLoop.getTerm();
			Term internalNewLiteral = internalLoop.getTerm();
			for (TreeWitness tw : mergeable)  
				if (tw.getRoots().contains(rootNewLiteral) && tw.getDomain().contains(internalNewLiteral)) {
					log.debug("    ATTACHING A TREE WITNESS {}", tw);
					saturateTreeWitnesses(qf.extend(tw)); 
				} 
			
			QueryFolding qf2 = new QueryFolding(qf);
			if (qf2.extend(internalLoop, edge, rootLoop)) {
				log.debug("    ATTACHING A HANDLE {}", edge);
				saturateTreeWitnesses(qf2);  
			}	
		}

		if (saturated && qf.hasRoot())  {
			if (!twsCache.containsKey(qf.getTerms())) {
				Collection<TreeWitnessGenerator> twg = getTreeWitnessGenerators(qf); 
				if (twg != null) {
					TreeWitness tw = qf.getTreeWitness(twg, cc.getEdges()); 
					delta.add(tw);
					twsCache.put(tw.getTerms(), tw);
				}
				else
					twsCache.put(qf.getTerms(), null); // cache negative
			}
			else {
				log.debug("TWS CACHE HIT {}", qf.getTerms());
			}
		}
	}
	
	// can return null if there are no applicable generators!
	
	private Collection<TreeWitnessGenerator> getTreeWitnessGenerators(QueryFolding qf) {
		Collection<TreeWitnessGenerator> twg = null;
		log.debug("CHECKING WHETHER THE FOLDING {} CAN BE GENERATED: ", qf); 
		for (TreeWitnessGenerator g : allTWgenerators) {
			Intersection<ObjectPropertyExpression> subp = qf.getProperties();
			if (!subp.subsumes(g.getProperty())) {
				log.debug("      NEGATIVE PROPERTY CHECK {}", g.getProperty());
				continue;
			}
			else
				log.debug("      POSITIVE PROPERTY CHECK {}", g.getProperty());

			Intersection<ClassExpression> subc = qf.getInternalRootConcepts();
			if (!g.endPointEntailsAnyOf(subc)) {
				 log.debug("        ENDTYPE TOO SPECIFIC: {} FOR {}", subc, g);
				 continue;			
			}
			else
				 log.debug("        ENDTYPE IS FINE: TOP FOR {}", g);

			boolean failed = false;
			for (TreeWitness tw : qf.getInteriorTreeWitnesses()) 
				if (!g.endPointEntailsAnyOf(tw.getGeneratorSubConcepts())) { 
					log.debug("        ENDTYPE TOO SPECIFIC: {} FOR {}", tw, g);
					failed = true;
					break;
				} 
				else
					log.debug("        ENDTYPE IS FINE: {} FOR {}", tw, g);
				
			if (failed)
				continue;
			
			if (twg == null) 
				twg = new LinkedList<>();
			twg.add(g);
			log.debug("        OK");
		}
		return twg;
	}
	
	public CompatibleTreeWitnessSetIterator getIterator() {
		return new CompatibleTreeWitnessSetIterator(tws.size());
	}
	
	public class CompatibleTreeWitnessSetIterator implements Iterator<Collection<TreeWitness>> {
		private boolean isInNext[];
		private boolean atNextPosition = true;
		private boolean finished = false;
		private Collection<TreeWitness> nextSet = new LinkedList<>();

		private CompatibleTreeWitnessSetIterator(int len) {
			isInNext = new boolean[len];
		}

		/**
	     * Returns the next subset of tree witnesses
	     *
	     * @return the next subset of tree witnesses
	     * @exception NoSuchElementException has no more subsets.
	     */
		@Override
		public Collection<TreeWitness> next() {
			if (atNextPosition) {
				atNextPosition = false;
				return nextSet;
			}
			
			while (!isLast()) 
				if (moveToNext()) {
					atNextPosition = false;
					return nextSet;
				}
			finished = true;
			
			throw new NoSuchElementException("The next method was called when no more objects remained.");
	    }

	  	/**
	     * @return <tt>true</tt> if the PowerSet has more subsets.
	     */
		@Override
	  	public boolean hasNext() {
			if (atNextPosition)
				return !finished;
			
			while (!isLast()) 
				if (moveToNext()) {
					atNextPosition = true;
					return true;
				}
			
			return false;
	  	}
		
		private boolean isLast() {
	  		for (int i = 0; i < isInNext.length; i++)
	  			if (!isInNext[i])
	  				return false;
	  		return true;
			
		}
		
		// return true if the next is compatible
		
		private boolean moveToNext() {
		    boolean carry = true;
			for (int i = 0; i < isInNext.length; i++)
				if(!carry)
					break;
				else {
			        carry = isInNext[i];
			        isInNext[i] = !isInNext[i];
				}			

			nextSet.clear();
			int i = 0;
	      	for (TreeWitness tw : tws)
	      		if (isInNext[i++]) {
	      			for (TreeWitness tw0 : nextSet)
	      				if (!TreeWitness.isCompatible(tw0, tw)) 
	      					return false;
	      					
	      			nextSet.add(tw);
	      		}
	      	return true;
		}
		
		/**
	     * @exception UnsupportedOperationException because the <tt>remove</tt>
	     *		  operation is not supported by this Iterator.
	     */
		@Override
		public void remove() {
			throw new UnsupportedOperationException("The PowerSet class does not support the remove method.");
		}
	}
	
	
	static class QueryConnectedComponentCache {
		private final Map<TermOrderedPair, Intersection<ObjectPropertyExpression>> propertiesCache = new HashMap<>();
		private final Map<Term, Intersection<ClassExpression>> conceptsCache = new HashMap<>();

		private final ClassifiedTBox reasoner;
		private final ImmutabilityTools immutabilityTools;

		private QueryConnectedComponentCache(ClassifiedTBox reasoner, ImmutabilityTools immutabilityTools) {
			this.reasoner = reasoner;
			this.immutabilityTools = immutabilityTools;
		}
		
		public Intersection<ClassExpression> getTopClass() {
			return new Intersection<>(reasoner.classesDAG());
		}

		public Intersection<ObjectPropertyExpression> getTopProperty() {
			return new Intersection<>(reasoner.objectPropertiesDAG());
		}
		
		public Intersection<ClassExpression> getSubConcepts(Collection<Function> atoms) {
			Intersection<ClassExpression> subc = new Intersection<>(reasoner.classesDAG());
			for (Function a : atoms) {
				Optional<IRI> optionalClassIRI = extractClassIRI(a);
				if (!optionalClassIRI.isPresent()) {
					subc.setToBottom();   // binary predicates R(x,x) cannot be matched to the anonymous part
					break;
				}
				IRI classIRI = optionalClassIRI.get();

				 if (reasoner.classes().contains(classIRI))
					 subc.intersectWith(reasoner.classes().get(classIRI));
				 else
					 subc.setToBottom();
				 if (subc.isBottom())
					 break;
			}
			return subc;
		}
		
		
		public Intersection<ClassExpression> getLoopConcepts(Loop loop) {
			Term t = loop.getTerm();
			Intersection<ClassExpression> subconcepts = conceptsCache.get(t); 
			if (subconcepts == null) {				
				subconcepts = getSubConcepts(loop.getAtoms());
				conceptsCache.put(t, subconcepts);	
			}
			return subconcepts;
		}
		
		
		public Intersection<ObjectPropertyExpression> getEdgeProperties(Edge edge, Term root, Term nonroot) {
			TermOrderedPair idx = new TermOrderedPair(root, nonroot);
			Intersection<ObjectPropertyExpression> properties = propertiesCache.get(idx);			
			if (properties == null) {
				properties = new Intersection<>(reasoner.objectPropertiesDAG());
				for (Function a : edge.getBAtoms()) {
					if (a.isOperation()) {
						log.debug("EDGE {} HAS PROPERTY {} NO BOOLEAN OPERATION PREDICATES ALLOWED IN PROPERTIES", edge, a);
						properties.setToBottom();
						break;
					}
					else {
						log.debug("EDGE {} HAS PROPERTY {}",  edge, a);
						Optional<IRI> optionalPropertyIRIString = extractPropertyIRI(a);
						if (optionalPropertyIRIString
								.filter(i -> reasoner.objectProperties().contains(i))
								.isPresent()) {
							ObjectPropertyExpression prop = reasoner.objectProperties().get(optionalPropertyIRIString.get());
							if (!root.equals(a.getTerm(0)))
									prop = prop.getInverse();
							properties.intersectWith(prop);
						}
						else
							properties.setToBottom();
						
						if (properties.isBottom())
							break;						
					}
				}
				propertiesCache.put(idx, properties); // edge.getTerms()
			}
			return properties;
		}

		private Optional<IRI> extractPropertyIRI(Function rdfDataAtom) {
			if (rdfDataAtom.getFunctionSymbol() instanceof RDFAtomPredicate) {

				RDFAtomPredicate rdfAtomPredicate = (RDFAtomPredicate) rdfDataAtom.getFunctionSymbol();

				ImmutableList<ImmutableTerm> arguments = rdfDataAtom.getTerms().stream()
						.map(immutabilityTools::convertIntoImmutableTerm)
						.collect(ImmutableCollectors.toList());

				return rdfAtomPredicate.getPropertyIRI(arguments);
			}
			return Optional.empty();
		}

		private Optional<IRI> extractClassIRI(Function rdfDataAtom) {
			if (rdfDataAtom.getFunctionSymbol() instanceof RDFAtomPredicate) {

				RDFAtomPredicate rdfAtomPredicate = (RDFAtomPredicate) rdfDataAtom.getFunctionSymbol();

				ImmutableList<ImmutableTerm> arguments = rdfDataAtom.getTerms().stream()
						.map(immutabilityTools::convertIntoImmutableTerm)
						.collect(ImmutableCollectors.toList());

				return rdfAtomPredicate.getClassIRI(arguments);
			}
			return Optional.empty();
		}
	}
	
	private static class TermOrderedPair {
		private final Term t0, t1;
		private final int hashCode;

		public TermOrderedPair(Term t0, Term t1) {
			this.t0 = t0;
			this.t1 = t1;
			this.hashCode = t0.hashCode() ^ (t1.hashCode() << 4);
		}

		@Override
		public boolean equals(Object o) {
			if (o instanceof TermOrderedPair) {
				TermOrderedPair other = (TermOrderedPair) o;
				return (this.t0.equals(other.t0) && this.t1.equals(other.t1));
			}
			return false;
		}

		@Override
		public String toString() {
			return "term pair: (" + t0 + ", " + t1 + ")";
		}
		
		@Override
		public int hashCode() {
			return hashCode;
		}
	}	

	

	public Set<TreeWitnessGenerator> getGeneratorsOfDetachedCC() {		
		Set<TreeWitnessGenerator> generators = new HashSet<>();
		
		if (cc.isDegenerate()) { // do not remove the curly brackets -- dangling else otherwise
			Intersection<ClassExpression> subc = cache.getSubConcepts(cc.getLoop().getAtoms());
			log.debug("DEGENERATE DETACHED COMPONENT: {}", cc);
			if (!subc.isBottom()) // (subc == null) || 
				for (TreeWitnessGenerator twg : allTWgenerators) {
					if (twg.endPointEntailsAnyOf(subc)) {
						log.debug("        ENDTYPE IS FINE: {} FOR {}", subc, twg);
						generators.add(twg);					
					}
					else 
						 log.debug("        ENDTYPE TOO SPECIFIC: {} FOR {}", subc, twg);
				}
		} 
		else {
			for (TreeWitness tw : tws) 
				if (tw.getDomain().containsAll(cc.getVariables())) {
					log.debug("TREE WITNESS {} COVERS THE QUERY",  tw);
					Intersection<ClassExpression> subc = cache.getSubConcepts(tw.getRootAtoms());
					if (!subc.isBottom())
						for (TreeWitnessGenerator twg : allTWgenerators)
							if (twg.endPointEntailsAnyOf(subc)) {
								log.debug("        ENDTYPE IS FINE: {} FOR {}",  subc, twg);
								if (twg.endPointEntailsAnyOf(tw.getGeneratorSubConcepts())) {
									log.debug("        ENDTYPE IS FINE: {} FOR {}",  tw, twg);
									generators.add(twg);					
								}
								else  
									log.debug("        ENDTYPE TOO SPECIFIC: {} FOR {}", tw, twg);
							}
							else 
								 log.debug("        ENDTYPE TOO SPECIFIC: {} FOR {}", subc, twg);
				}
		}
		
		if (!generators.isEmpty()) {
			boolean saturated = false;
			while (!saturated) {
				saturated = true;
				Set<ClassExpression> subc = new HashSet<>();
				for (TreeWitnessGenerator twg : generators) 
					subc.addAll(twg.getSubConcepts());
				
				for (TreeWitnessGenerator g : allTWgenerators) 
					if (g.endPointEntailsAnyOf(subc)) {
						if (generators.add(g))
							saturated = false;
					}		 		
			} 									
		}	
		return generators;
	}
}
