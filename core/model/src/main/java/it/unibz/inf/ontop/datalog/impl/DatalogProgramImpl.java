package it.unibz.inf.ontop.datalog.impl;

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

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import it.unibz.inf.ontop.model.term.Function;
import it.unibz.inf.ontop.datalog.MutableQueryModifiers;
import it.unibz.inf.ontop.model.term.functionsymbol.Predicate;
import it.unibz.inf.ontop.datalog.CQIE;
import it.unibz.inf.ontop.datalog.DatalogProgram;

public class DatalogProgramImpl implements DatalogProgram {

	private static final long serialVersionUID = -1644491423712454150L;

	private List<CQIE> rules = null;

	private Map<Predicate, List<CQIE>> predicateIndex = null;

	private MutableQueryModifiers modifiers;

	@Override
	public DatalogProgram clone() {
		DatalogProgramImpl clone = new DatalogProgramImpl();
		for (CQIE query : rules) {
			clone.appendRule(query.clone());
		}
		clone.modifiers = modifiers.clone();
		return clone;
	}

	protected DatalogProgramImpl() {
		modifiers = new MutableQueryModifiersImpl();
		rules = new LinkedList<>();
		predicateIndex = new HashMap<>();
	}

	protected DatalogProgramImpl(MutableQueryModifiers modifiers) {
		this.modifiers = modifiers.clone();
		rules = new LinkedList<>();
		predicateIndex = new HashMap<>();
	}

	@Override
	public void appendRule(CQIE rule) {
		if (rule == null) {
			throw new IllegalArgumentException("DatalogProgram: Recieved a null rule.");
		}
		if (rules.contains(rule)) {
			return; // Skip if the rule already exists!
		}

		rules.add(rule);

		Function head = rule.getHead();
		if (head != null) {
			Predicate predicate = rule.getHead().getFunctionSymbol();
			List<CQIE> indexedRules = predicateIndex.get(predicate);
			if (indexedRules == null) {
				indexedRules = new LinkedList<CQIE>();
				predicateIndex.put(predicate, indexedRules);
			}
			indexedRules.add(rule);
		}
	}

	@Override
	public void appendRule(Collection<CQIE> rules) {
		for (CQIE rule : rules) {
			appendRule(rule);
		}
	}

	@Override
	public List<CQIE> getRules() {
		return Collections.unmodifiableList(rules);
	}

	@Override
	public String toString() {
		StringBuffer bf = new StringBuffer();
		for (CQIE rule : rules) {
			bf.append(rule.toString());
			bf.append("\n");
		}
		return bf.toString();
	}

	@Override
	public List<CQIE> getRules(Predicate headPredicate) {
		List<CQIE> rules = predicateIndex.get(headPredicate);
		if (rules == null) {
			rules = new LinkedList<>();
		}
		return Collections.unmodifiableList(rules);
	}

	@Override
	public MutableQueryModifiers getQueryModifiers() {
		return modifiers;
	}
	
}
