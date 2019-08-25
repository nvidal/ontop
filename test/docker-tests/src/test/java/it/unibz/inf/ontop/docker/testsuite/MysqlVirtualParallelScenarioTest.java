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



import it.unibz.inf.ontop.docker.ParallelScenarioManifestTestUtils;
import it.unibz.inf.ontop.docker.QuestParallelScenario;
import it.unibz.inf.ontop.docker.QuestVirtualParallelScenario;
import junit.framework.Test;

import java.util.List;

public class MysqlVirtualParallelScenarioTest extends ParallelScenarioTest {

	public MysqlVirtualParallelScenarioTest(){
	}

	public static Test suite() throws Exception {
		return ParallelScenarioManifestTestUtils.suite(new ParallelFactory() {
			@Override
            public QuestParallelScenario createQuestParallelScenarioTest(String suiteName, List<String> testURIs, List<String> names, List<String> queryFileURLs,
																		 List<String> resultFileURLs, String owlFileURL, String obdaFileURL,
																		 String parameterFileURL) {
				return new QuestVirtualParallelScenario(suiteName, testURIs, names, queryFileURLs, resultFileURLs, owlFileURL,
                        obdaFileURL, parameterFileURL) {
                };
			}
			@Override
			public String getMainManifestFile() {
				return "/testcases-docker/manifest-scenario-mysql.ttl";
			}
		});
	}
}
