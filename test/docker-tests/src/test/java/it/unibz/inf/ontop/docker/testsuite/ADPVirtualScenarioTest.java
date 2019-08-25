package it.unibz.inf.ontop.docker.testsuite;

/*
 * #%L
 * ontop-test
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


import it.unibz.inf.ontop.docker.QuestVirtualScenarioParent;
import it.unibz.inf.ontop.docker.ScenarioManifestTestUtils;
import junit.framework.Test;

public class ADPVirtualScenarioTest extends QuestVirtualScenarioParent {

	public ADPVirtualScenarioTest(String testURI, String name, String queryFileURL, String resultFileURL,
                                  String owlFileURL, String obdaFileURL, String parameterFileURL) {
		super(testURI, name, queryFileURL, resultFileURL, owlFileURL, obdaFileURL, parameterFileURL);
	}

	public static Test suite() throws Exception {
		return ScenarioManifestTestUtils.suite(new Factory() {
			@Override
			public QuestVirtualScenarioParent createQuestScenarioTest(String testURI, String name, String queryFileURL, 
					String resultFileURL, String owlFileURL, String obdaFileURL) {
				return new ADPVirtualScenarioTest(testURI, name, queryFileURL, resultFileURL, owlFileURL,
						obdaFileURL, "");
			}
			@Override
			public QuestVirtualScenarioParent createQuestScenarioTest(String testURI, String name, String queryFileURL, 
					String resultFileURL, String owlFileURL, String obdaFileURL, String parameterFileURL) {
				return new ADPVirtualScenarioTest(testURI, name, queryFileURL, resultFileURL, owlFileURL,
						obdaFileURL, parameterFileURL);
			}
			@Override
			public String getMainManifestFile() {
				return "/testcases-docker/manifest-scenario-adp.ttl";
			}
		});
	}
}
