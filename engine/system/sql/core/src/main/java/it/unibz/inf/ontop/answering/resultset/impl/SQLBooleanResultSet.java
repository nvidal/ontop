package it.unibz.inf.ontop.answering.resultset.impl;

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

import it.unibz.inf.ontop.answering.resultset.BooleanResultSet;
import it.unibz.inf.ontop.exception.OntopConnectionException;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SQLBooleanResultSet implements BooleanResultSet {

    private final ResultSet set;
    private boolean hasRead;

    public SQLBooleanResultSet(ResultSet set) {
        this.set = set;
        this.hasRead = false;
    }

    @Override
    public void close() throws OntopConnectionException {
        if (set == null)
            return;
        try {
            set.close();
        } catch (SQLException e) {
            throw new OntopConnectionException(e);
        }
    }

    /**
     * Returns true if there is at least one result
     */
    @Override
    public boolean getValue() throws OntopConnectionException {
        if (hasRead)
            throw new IllegalStateException("getValue() can only called once!");
        hasRead = true;
        try {
            return set.next();
        } catch (SQLException e) {
            throw new OntopConnectionException(e);
        }
    }
}
