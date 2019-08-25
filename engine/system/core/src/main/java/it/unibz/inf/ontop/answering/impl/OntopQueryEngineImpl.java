package it.unibz.inf.ontop.answering.impl;


import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import it.unibz.inf.ontop.answering.OntopQueryEngine;
import it.unibz.inf.ontop.answering.reformulation.QueryReformulator;
import it.unibz.inf.ontop.exception.OntopConnectionException;
import it.unibz.inf.ontop.injection.OntopSystemFactory;
import it.unibz.inf.ontop.injection.ReformulationFactory;
import it.unibz.inf.ontop.answering.connection.DBConnector;
import it.unibz.inf.ontop.answering.connection.OntopConnection;
import it.unibz.inf.ontop.iq.tools.ExecutorRegistry;
import it.unibz.inf.ontop.spec.OBDASpecification;

public class OntopQueryEngineImpl implements OntopQueryEngine {

    private final DBConnector dbConnector;

    @AssistedInject
    private OntopQueryEngineImpl(@Assisted OBDASpecification obdaSpecification,
                                 @Assisted ExecutorRegistry executorRegistry,
                                 ReformulationFactory translationFactory,
                                 OntopSystemFactory systemFactory) {
        QueryReformulator queryReformulator = translationFactory.create(obdaSpecification, executorRegistry);
        dbConnector = systemFactory.create(queryReformulator, obdaSpecification.getDBMetadata());
    }

    @Override
    public boolean connect() throws OntopConnectionException {
        return dbConnector.connect();
    }

    @Override
    public void close() throws OntopConnectionException {
        dbConnector.close();
    }

    @Override
    public OntopConnection getConnection() throws OntopConnectionException {
        return dbConnector.getConnection();
    }
}
