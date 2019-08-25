package it.unibz.inf.ontop.protege.core;

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

import com.google.common.collect.ImmutableMap;
import it.unibz.inf.ontop.spec.mapping.impl.AbstractPrefixManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.semanticweb.owlapi.formats.PrefixDocumentFormat;


/**
 * This PrefixManager is meant to 'wrap' Protege's prefix manager. That way any
 * prefix defined in Protege are transparently passed to all OBDA lib classes.
 */
public class MutablePrefixManager extends AbstractPrefixManager {

    PrefixDocumentFormat owlmapper;

	public MutablePrefixManager(PrefixDocumentFormat owlmapper) {
		this.owlmapper = owlmapper;
	}
	
	@Override
	public String getDefaultPrefix() {
		return super.getDefaultPrefix();
	}

	@Override
	public String getPrefix(String uri) {
		for (String prefix : owlmapper.getPrefixName2PrefixMap().keySet()) {
			if (owlmapper.getPrefixName2PrefixMap().get(prefix).contains(uri)) {
				return prefix;
			}
		}
		return null;
	}

	@Override
	public ImmutableMap<String, String> getPrefixMap() {
		return ImmutableMap.copyOf(owlmapper.getPrefixName2PrefixMap());
	}

	@Override
	public String getURIDefinition(String prefix) {
		return owlmapper.getPrefix(prefix);
	}

	@Override
	public boolean contains(String prefix) {
		return owlmapper.containsPrefixMapping(prefix);
	}

	public void addPrefix(String name, String uri) {
		owlmapper.setPrefix(name, uri);
	}

	public void clear() {
		owlmapper.clear();
	}

	@Override
	public List<String> getNamespaceList() {
		ArrayList<String> namespaceList = new ArrayList<String>();
		for (String uri : getPrefixMap().values()) {
			namespaceList.add(uri);
		}
		Collections.sort(namespaceList, Collections.reverseOrder());
		return namespaceList;
	}

	public void addPrefixes(ImmutableMap<String, String> prefixMap) {
		prefixMap.entrySet()
				.forEach(e -> owlmapper.setPrefix(e.getKey(), e.getValue()));
	}
}
