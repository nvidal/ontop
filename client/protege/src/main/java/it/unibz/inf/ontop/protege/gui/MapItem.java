package it.unibz.inf.ontop.protege.gui;

/*
 * #%L
 * ontop-protege
 * %%
 * Copyright (C) 2009 - 2013 KRDB Research Centre. Free University of Bozen Bolzano.
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

import it.unibz.inf.ontop.model.term.functionsymbol.FunctionSymbol;
import it.unibz.inf.ontop.spec.mapping.PrefixManager;
import it.unibz.inf.ontop.model.term.functionsymbol.Predicate;
import org.apache.commons.rdf.api.IRI;

public class MapItem {

	private PredicateItem predicateItem;
	private String targetMapping = "";
	private FunctionSymbol dataType;

	public MapItem(PredicateItem predicate) {
		this.predicateItem = predicate;
	}

	public PrefixManager getPrefixManager() {
		return predicateItem.getPrefixManager();
	}

	public String getName() {
		if (predicateItem == null) {
			return "";
		} else {
			return predicateItem.getFullName();
		}
	}
	
	public IRI getPredicateIRI() {
		return predicateItem.getSource();
	}

	public void setTargetMapping(String columnOrUriTemplate) {
		targetMapping = columnOrUriTemplate;
	}

	public String getTargetMapping() {
		return targetMapping;
	}
	
	public void setDataType(FunctionSymbol type) {
		dataType = type;
	}

	public FunctionSymbol getDataType() {
		return dataType;
	}

	public boolean isObjectMap() {
		return predicateItem.isDataPropertyPredicate();
	}

	public boolean isRefObjectMap() {
		return predicateItem.isObjectPropertyPredicate();
	}
	
	public boolean isValid() {
		if (predicateItem == null) {
			return false;
		}
		if (targetMapping.isEmpty()) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		return getName().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (obj.getClass() != getClass()) {
			return false;
		}
		MapItem other = (MapItem) obj;
		return this.getName() == other.getName();
	}

	@Override
	public String toString() {
		if (predicateItem == null) {
			return "";
		} else {
			return predicateItem.getQualifiedName();
		}
	}
}
