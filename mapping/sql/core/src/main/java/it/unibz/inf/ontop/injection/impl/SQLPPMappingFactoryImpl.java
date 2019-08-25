package it.unibz.inf.ontop.injection.impl;

import com.google.common.collect.ImmutableList;
import it.unibz.inf.ontop.exception.DuplicateMappingException;
import it.unibz.inf.ontop.injection.SQLPPMappingFactory;
import it.unibz.inf.ontop.injection.OntopMappingSettings;
import it.unibz.inf.ontop.spec.mapping.MappingMetadata;
import it.unibz.inf.ontop.spec.mapping.pp.SQLPPTriplesMap;
import it.unibz.inf.ontop.spec.mapping.pp.SQLPPMapping;

import javax.inject.Inject;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class SQLPPMappingFactoryImpl implements SQLPPMappingFactory {

    private final OntopMappingSettings settings;

    @Inject
    private SQLPPMappingFactoryImpl(OntopMappingSettings settings) {
        this.settings = settings;
    }

    private Constructor[] findConstructors(Class genericClass) {
        String implementationName = settings.getProperty(genericClass.getCanonicalName())
                //TODO: find a better exception
                .orElseThrow(() -> new RuntimeException("No implementation declared for " +
                        genericClass.getCanonicalName()));

        try {
            Class implementationClass = Class.forName(implementationName);
            return implementationClass.getConstructors();
        } catch (ClassNotFoundException e) {
            // TODO: find a better exception
            throw new RuntimeException(e.getMessage());
        }
    }

    private Constructor findFirstConstructor(Class genericClass) {
        return findConstructors(genericClass)[0];
    }

    /**
     * SQLPPPMapping creation
     */
    @Override
    public SQLPPMapping createSQLPreProcessedMapping(ImmutableList<SQLPPTriplesMap> ppMappingAxioms,
                                                     MappingMetadata mappingMetadata)
            throws DuplicateMappingException {
        try {
            /**
             * Instantiation
             */
            Constructor constructor = findFirstConstructor(SQLPPMapping.class);
            return (SQLPPMapping) constructor.newInstance(ppMappingAxioms, mappingMetadata);
            /**
             * Exception management
             */
        } catch (InvocationTargetException e) {
            Throwable targetException = e.getTargetException();
            /**
             * Expected exception: rethrown
             */
            if (targetException instanceof DuplicateMappingException) {
                throw (DuplicateMappingException) targetException;
            }
            /**
             * Unexcepted: throw a unexpected RuntimeException.
             */
            throw new RuntimeException(targetException.getMessage());

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
