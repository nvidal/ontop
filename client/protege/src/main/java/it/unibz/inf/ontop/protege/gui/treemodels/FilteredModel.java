package it.unibz.inf.ontop.protege.gui.treemodels;

/*
 * #%L
 * ontop-protege4
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

import java.util.List;

/**
 * Interface that implements a set of functions to add and remove filters of the
 * TreeModel
 */
public interface FilteredModel {

	/**
	 * @param filter
	 *            Adds a new filter
	 */
	public void addFilter(TreeModelFilter<SQLPPTriplesMap> filter);

	/**
	 * @param filters
	 *            Adds a list of filters
	 */
	public void addFilters(List<TreeModelFilter<SQLPPTriplesMap>> filters);

	/**
	 * @param filter
	 *            Remove a filter of the list of filters
	 */
	public void removeFilter(TreeModelFilter<SQLPPTriplesMap> filter);

	/**
	 * @param filters
	 *            Remove a list of filters
	 */
	public void removeFilter(List<TreeModelFilter<SQLPPTriplesMap>> filters);

	/**
	 * Remove all the current filters
	 */
	public void removeAllFilters();
}
