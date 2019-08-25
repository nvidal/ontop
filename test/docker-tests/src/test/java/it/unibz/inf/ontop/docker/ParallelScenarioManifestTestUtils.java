package it.unibz.inf.ontop.docker;

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


import it.unibz.inf.ontop.docker.testsuite.ParallelScenarioTest;
import junit.framework.TestResult;
import junit.framework.TestSuite;
import org.eclipse.rdf4j.OpenRDFUtil;
import org.eclipse.rdf4j.common.io.FileUtil;
import org.eclipse.rdf4j.common.io.ZipUtil;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.repository.util.RDFInserter;
import org.eclipse.rdf4j.rio.*;
import org.eclipse.rdf4j.rio.helpers.BasicParserSettings;
import org.eclipse.rdf4j.sail.memory.MemoryStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.jar.JarFile;

public class ParallelScenarioManifestTestUtils {

	static final Logger logger = LoggerFactory.getLogger(ParallelScenarioManifestTestUtils.class);

	public static TestSuite suite(ParallelScenarioTest.ParallelFactory factory) throws Exception {
		final String manifestFile;
		final File tmpDir;

		URL url = ParallelScenarioManifestTestUtils.class.getResource(factory.getMainManifestFile());

		if ("jar".equals(url.getProtocol())) {
			// Extract manifest files to a temporary directory
			try {
				tmpDir = FileUtil.createTempDir("scenario-evaluation");

				JarURLConnection con = (JarURLConnection) url.openConnection();
				JarFile jar = con.getJarFile();

				ZipUtil.extract(jar, tmpDir);

				File localFile = new File(tmpDir, con.getEntryName());
				manifestFile = localFile.toURI().toURL().toString();
			} catch (IOException e) {
				throw new AssertionError(e);
			}
		} else {
			manifestFile = url.toString();
			tmpDir = null;
		}

		TestSuite suite = new TestSuite(factory.getClass().getName()) {
			@Override
			public void run(TestResult result) {
				try {
					super.run(result);
				} finally {
					if (tmpDir != null) {
						try {
							FileUtil.deleteDir(tmpDir);
						} catch (IOException e) {
							System.err.println("Unable to clean up temporary directory '"
											+ tmpDir + "': " + e.getMessage());
						}
					}
				}
			}
		};

		Repository manifestRep = new SailRepository(new MemoryStore());
		manifestRep.initialize();
		RepositoryConnection con = manifestRep.getConnection();

		addTurtle(con, new URL(manifestFile), manifestFile);

		String query = "SELECT DISTINCT manifestFile FROM {x} rdf:first {manifestFile} "
				+ "USING NAMESPACE mf = <http://obda.org/quest/tests/test-manifest#>, "
				+ "  qt = <http://obda.org/quest/tests/test-query#>";

		TupleQueryResult manifestResults = con.prepareTupleQuery(QueryLanguage.SERQL, query, manifestFile).evaluate();

		while (manifestResults.hasNext()) {
			BindingSet bindingSet = manifestResults.next();
			String subManifestFile = bindingSet.getValue("manifestFile").toString();
			suite.addTest(ParallelScenarioTest.extractTest(subManifestFile, factory));
		}

		manifestResults.close();
		con.close();
		manifestRep.shutDown();

		logger.info("Created aggregated test extractTest with " + suite.countTestCases() + " test cases.");
		return suite;
	}

	static void addTurtle(RepositoryConnection con, URL url, String baseURI, Resource... contexts)
			throws IOException, RepositoryException, RDFParseException {
		if (baseURI == null) {
			baseURI = url.toExternalForm();
		}
		InputStream in = url.openStream();

		try {
			OpenRDFUtil.verifyContextNotNull(contexts);
			final ValueFactory vf = con.getRepository().getValueFactory();
			RDFParser rdfParser = Rio.createParser(RDFFormat.TURTLE, vf);
			
			ParserConfig config = rdfParser.getParserConfig();
			// To emulate DatatypeHandling.IGNORE 
			config.addNonFatalError(BasicParserSettings.FAIL_ON_UNKNOWN_DATATYPES);
			config.addNonFatalError(BasicParserSettings.VERIFY_DATATYPE_VALUES);
			config.addNonFatalError(BasicParserSettings.NORMALIZE_DATATYPE_VALUES);

			RDFInserter rdfInserter = new RDFInserter(con);
			rdfInserter.enforceContext(contexts);
			rdfParser.setRDFHandler(rdfInserter);

			con.begin();

			try {
				rdfParser.parse(in, baseURI);
			} catch (RDFHandlerException e) {
					con.rollback();
				// RDFInserter only throws wrapped RepositoryExceptions
				throw (RepositoryException) e.getCause();
			} catch (RuntimeException e) {
					con.rollback();
				throw e;
			} finally {
				con.commit();
			}
		} finally {
			in.close();
		}
	}
}
