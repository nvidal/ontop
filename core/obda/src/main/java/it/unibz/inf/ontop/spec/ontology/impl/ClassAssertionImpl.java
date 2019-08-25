package it.unibz.inf.ontop.spec.ontology.impl;

/*
 * #%L
 * ontop-obdalib-core
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

import it.unibz.inf.ontop.model.term.ObjectConstant;
import it.unibz.inf.ontop.spec.ontology.ClassAssertion;
import it.unibz.inf.ontop.spec.ontology.OClass;

/**
 * Represents ClassAssertion from OWL 2 QL Specification
 * 
 * ClassAssertion := 'ClassAssertion' '(' axiomAnnotations Class Individual ')'
 * 
 * @author Roman Kontchakov
 *
 */

public class ClassAssertionImpl implements ClassAssertion {

	private final ObjectConstant object;
	private final OClass concept;

	ClassAssertionImpl(OClass concept, ObjectConstant object) {
		this.object = object;
		this.concept = concept;
	}
 
	@Override
	public ObjectConstant getIndividual() {
		return object;
	}

	@Override
	public OClass getConcept() {
		return concept;
	}
	
	@Override 
	public boolean equals(Object obj) {
		if (obj instanceof ClassAssertionImpl) {
			ClassAssertionImpl other = (ClassAssertionImpl)obj;
			return concept.equals(other.concept) && object.equals(other.object);
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return concept.hashCode() + object.hashCode();
	}
	
	@Override
	public String toString() {
		return concept.toString() + "(" + object.toString() + ")";
	}

}
