package it.unibz.inf.ontop.owlapi.connection.impl;

/*
 * #%L
 * ontop-quest-owlapi
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

import it.unibz.inf.ontop.answering.reformulation.input.*;
import it.unibz.inf.ontop.exception.*;
import it.unibz.inf.ontop.answering.resultset.BooleanResultSet;
import it.unibz.inf.ontop.answering.resultset.SimpleGraphResultSet;
import it.unibz.inf.ontop.answering.resultset.TupleResultSet;
import it.unibz.inf.ontop.owlapi.exception.OntopOWLException;
import it.unibz.inf.ontop.answering.reformulation.ExecutableQuery;
import it.unibz.inf.ontop.answering.connection.OntopStatement;
import it.unibz.inf.ontop.owlapi.connection.OntopOWLStatement;
import it.unibz.inf.ontop.owlapi.resultset.BooleanOWLResultSet;
import it.unibz.inf.ontop.owlapi.resultset.GraphOWLResultSet;
import it.unibz.inf.ontop.owlapi.resultset.impl.OntopTupleOWLResultSet;
import it.unibz.inf.ontop.owlapi.resultset.TupleOWLResultSet;
import it.unibz.inf.ontop.owlapi.resultset.impl.OntopGraphOWLResultSet;
import it.unibz.inf.ontop.owlapi.resultset.impl.OntopBooleanOWLResultSet;

/***
 * A Statement to execute queries over a QuestOWLConnection. The logic of this
 * statement is equivalent to that of JDBC's Statements.
 * 
 * <p>
 * <strong>Performance</strong> Note that you should not create multiple
 * statements over the same connection to execute parallel queries (see
 * {@link DefaultOntopOWLConnection}). Multiple statements over the same connection are
 * not going to be very useful until we support updates (then statements will
 * allow to implement transactions in the same way as JDBC Statements).
 * 
 * @author Mariano Rodriguez Muro <mariano.muro@gmail.com>
 *
 * Used by the OWLAPI.
 *
 */
public class DefaultOntopOWLStatement implements OntopOWLStatement {
	private OntopStatement st;
	private final InputQueryFactory inputQueryFactory;

	public DefaultOntopOWLStatement(OntopStatement st, InputQueryFactory inputQueryFactory) {
		this.st = st;
		this.inputQueryFactory = inputQueryFactory;
	}

	public void cancel() throws OntopOWLException {
		try {
			st.cancel();
		} catch (OntopConnectionException e) {
			throw new OntopOWLException(e);
		}
	}

	@Override
	public void close() throws OntopOWLException {
		try {
			st.close();
		} catch (OntopConnectionException e) {
			throw new OntopOWLException(e);
		}
	}

	@Override
	public TupleOWLResultSet executeSelectQuery(String inputQuery) throws OntopOWLException {
		try {
			SelectQuery query = inputQueryFactory.createSelectQuery(inputQuery);
			TupleResultSet resultSet = st.execute(query);

			return new OntopTupleOWLResultSet(resultSet);

		} catch (OntopQueryEngineException e) {
			throw new OntopOWLException(e);
		}
	}

	@Override
	public BooleanOWLResultSet executeAskQuery(String inputQuery) throws OntopOWLException {
		try {
			AskQuery query = inputQueryFactory.createAskQuery(inputQuery);
			BooleanResultSet resultSet = st.execute(query);

			return new OntopBooleanOWLResultSet(resultSet);

		} catch (OntopQueryEngineException e) {
			throw new OntopOWLException(e);
		}
	}

	@Override
	public GraphOWLResultSet executeConstructQuery(String inputQuery) throws OntopOWLException {
		try {
			ConstructQuery query = inputQueryFactory.createConstructQuery(inputQuery);
			return executeGraph(query);
		} catch (OntopQueryEngineException e) {
			throw new OntopOWLException(e);
		}
	}

	@Override
	public GraphOWLResultSet executeDescribeQuery(String inputQuery) throws OntopOWLException {
		try {
			DescribeQuery query = inputQueryFactory.createDescribeQuery(inputQuery);
			return executeGraph(query);
		} catch (OntopQueryEngineException e) {
			throw new OntopOWLException(e);
		}
	}

	@Override
	public GraphOWLResultSet executeGraphQuery(String inputQuery) throws OntopOWLException {
		try {
			GraphSPARQLQuery query = inputQueryFactory.createGraphQuery(inputQuery);
			return executeGraph(query);
		} catch (OntopQueryEngineException e) {
			throw new OntopOWLException(e);
		}
	}

	private GraphOWLResultSet executeGraph(GraphSPARQLQuery query)
			throws OntopQueryEvaluationException, OntopConnectionException, OntopReformulationException,
			OntopResultConversionException {

		SimpleGraphResultSet resultSet = st.execute(query);
		return new OntopGraphOWLResultSet(resultSet);
	}

	public int getFetchSize() throws OntopOWLException {
		try {
			return st.getFetchSize();
		} catch (OntopConnectionException e) {
			throw new OntopOWLException(e);
		}
	}

	public int getMaxRows() throws OntopOWLException {
		try {
			return st.getMaxRows();
		} catch (OntopConnectionException e) {
			throw new OntopOWLException(e);
		}
	}

	public void getMoreResults() throws OntopOWLException {
		try {
			st.getMoreResults();
		} catch (OntopConnectionException e) {
			throw new OntopOWLException(e);
		}
	}

	public int getQueryTimeout() throws OntopOWLException {
		try {
			return st.getQueryTimeout();
		} catch (OntopConnectionException e) {
			throw new OntopOWLException(e);
		}
	}

	public void setFetchSize(int rows) throws OntopOWLException {
		try {
			st.setFetchSize(rows);
		} catch (OntopConnectionException e) {
			throw new OntopOWLException(e);
		}
	}

	public void setMaxRows(int max) throws OntopOWLException {
		try {
			st.setMaxRows(max);
		} catch (OntopConnectionException e) {
			throw new OntopOWLException(e);
		}
	}

	public boolean isClosed() throws OntopOWLException {
		try {
			return st.isClosed();
		} catch (OntopConnectionException e) {
			throw new OntopOWLException(e);
		}
	}

	public void setQueryTimeout(int seconds) throws OntopOWLException {
		try {
			st.setQueryTimeout(seconds);
		} catch (OntopConnectionException e) {
			throw new OntopOWLException(e);
		}
	}

	public long getTupleCount(String query) throws OntopOWLException {
		try {
			return st.getTupleCount(parseQueryString(query));
		} catch (Exception e) {
			throw new OntopOWLException(e);
		}
	}

	public String getRewritingRendering(String query) throws OntopOWLException {
		try {
			return st.getRewritingRendering(parseQueryString(query));
		} 
		catch (Exception e) {
			throw new OntopOWLException(e);
		}
	}

	public ExecutableQuery getExecutableQuery(String query) throws OntopOWLException {
		try {
			return st.getExecutableQuery(parseQueryString(query));
		} catch (OntopReformulationException e) {
			throw new OntopOWLException(e);
		}
	}

	/**
	 * In contexts where we don't know the precise type
	 */
	private InputQuery parseQueryString(String queryString) throws OntopOWLException {
		try {
			return inputQueryFactory.createSPARQLQuery(queryString);
		} catch (OntopInvalidInputQueryException | OntopUnsupportedInputQueryException e) {
			throw new OntopOWLException(e);
		}
	}

}
