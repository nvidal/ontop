package it.unibz.inf.ontop.protege.gui.treemodels;

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

import it.unibz.inf.ontop.spec.mapping.pp.SQLPPTriplesMap;

public class MappingIDTreeModelFilter extends TreeModelFilter<SQLPPTriplesMap> {

	public MappingIDTreeModelFilter() {
		super.bNegation = false;
	}

	@Override
	public boolean match(SQLPPTriplesMap object) {
		boolean isMatch = false;
		for (String keyword : vecKeyword) {
			isMatch = match(keyword.trim(), object.getId());
			if (isMatch) {
				break; // end loop if a match is found!
			}
		}
		// no match found!
		return (bNegation ? !isMatch : isMatch);
	}

	/** A helper method to check a match */
	public static boolean match(String keyword, String mappingId) {
		if (mappingId.indexOf(keyword) != -1) { // match found!
			return true;
		}
		return false;
	}
}
