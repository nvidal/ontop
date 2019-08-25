package it.unibz.inf.ontop.spec.mapping.parser.impl;

import com.google.inject.Inject;
import it.unibz.inf.ontop.exception.DuplicateMappingException;
import it.unibz.inf.ontop.exception.InvalidMappingException;
import it.unibz.inf.ontop.spec.mapping.pp.SQLPPMapping;
import it.unibz.inf.ontop.spec.mapping.parser.SQLMappingParser;
import org.apache.commons.rdf.api.Graph;

import java.io.File;
import java.io.Reader;

/**
 * Allows to create a OntopMappingConfiguration instance without having a proper MappingParser
 */
public class FakeSQLMappingParser implements SQLMappingParser {

    @Inject
    private FakeSQLMappingParser() {
    }

    @Override
    public SQLPPMapping parse(File file) throws InvalidMappingException, DuplicateMappingException {
        throw new UnsupportedOperationException("This mapping parser is fake and thus does not parse");
    }

    @Override
    public SQLPPMapping parse(Reader reader) throws InvalidMappingException, DuplicateMappingException {
        throw new UnsupportedOperationException("This mapping parser is fake and thus does not parse");
    }

    @Override
    public SQLPPMapping parse(Graph mappingGraph) throws InvalidMappingException, DuplicateMappingException {
        throw new UnsupportedOperationException("This mapping parser is fake and thus does not parse");
    }
}
