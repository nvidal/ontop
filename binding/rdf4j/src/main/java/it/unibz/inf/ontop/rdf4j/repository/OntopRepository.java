package it.unibz.inf.ontop.rdf4j.repository;

import it.unibz.inf.ontop.injection.OntopSystemConfiguration;
import it.unibz.inf.ontop.rdf4j.repository.impl.OntopVirtualRepository;

/**
 * Ontop RDF4J repository
 */
public interface OntopRepository extends org.eclipse.rdf4j.repository.Repository, AutoCloseable {

    static OntopRepository defaultRepository(OntopSystemConfiguration configuration) {
        return new OntopVirtualRepository(configuration);
    }
}
