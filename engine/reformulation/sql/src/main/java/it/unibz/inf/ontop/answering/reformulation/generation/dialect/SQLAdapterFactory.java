package it.unibz.inf.ontop.answering.reformulation.generation.dialect;

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


import it.unibz.inf.ontop.answering.reformulation.generation.dialect.impl.*;
import it.unibz.inf.ontop.injection.OntopReformulationSQLSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SQLAdapterFactory {

	private static Logger log = LoggerFactory.getLogger(SQLAdapterFactory.class);

	public static SQLDialectAdapter getSQLDialectAdapter(String className, String databaseName, OntopReformulationSQLSettings preferences) {

		switch (className) {
			case "org.postgresql.Driver":
				return new PostgreSQLDialectAdapter();
			case "com.mysql.jdbc.Driver":
			case "com.mysql.cj.jdbc.Driver":
				return new Mysql2SQLDialectAdapter();
			case "org.h2.Driver":
				return new H2SQLDialectAdapter();
			case "org.hsqldb.jdbc.JDBCDriver":
				return new HSQLDBDialectAdapter();
			case "com.ibm.db2.jcc.DB2Driver":
				return new DB2SQLDialectAdapter();
			case "oracle.jdbc.driver.OracleDriver":
			case "oracle.jdbc.OracleDriver":
				return new OracleSQLDialectAdapter(databaseName);
			case "org.teiid.jdbc.TeiidDriver":
				return new TeiidSQLDialectAdapter();
			case "net.sourceforge.jtds.jdbc.Driver":
			case "com.microsoft.sqlserver.jdbc.SQLServerDriver":
				return new SQLServerSQLDialectAdapter();
			case "madgik.adp.federatedjdbc.AdpDriver":
				return new AdpSQLDialectAdapter();
            case "nl.cwi.monetdb.jdbc.MonetDriver":
                return new MonetDBSQLDialectAdapter();
            case "com.sap.db.jdbc.Driver":
                return new SAPHANASQLDialectAdapter();
			case "com.dremio.jdbc.Driver":
				return new DremioSQLDialectAdapter();
			case "com.denodo.vdb.jdbcdriver.VDBJDBCDriver":
			case "com.denodo.vdp.jdbc.Driver":
				return new DenodoSQLDialectAdapter();
			default:
				log.warn("WARNING: the specified driver doesn't correspond to any of the drivers officially supported by Ontop.");
				log.warn("WARNING: Contact the authors for further support.");
				String adapterClassName = preferences.getProperty(SQLDialectAdapter.class.getCanonicalName())
						.orElseThrow(() -> new IllegalStateException("No default SQLDialectAdapter provided"));
				try {
					Class adapterClass = Class.forName(adapterClassName);
					return (SQLDialectAdapter) adapterClass.getConstructor().newInstance();
				} catch (Exception e) {
				 throw new RuntimeException("Impossible to initialize the SQL adapter: " + e.getMessage());
            }
		}

	}

}
