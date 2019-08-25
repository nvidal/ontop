package it.unibz.inf.ontop.spec.mapping.validation;

import it.unibz.inf.ontop.exception.OBDASpecificationException;
import it.unibz.inf.ontop.injection.OntopMappingSQLAllOWLAPIConfiguration;
import it.unibz.inf.ontop.spec.mapping.validation.MappingOntologyMismatchTest;
import it.unibz.inf.ontop.spec.OBDASpecification;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class TestConnectionManager implements Closeable {

    private final String jdbcUrl;
    private final String dbUser;
    private final String dbPassword;
    private final String createScriptFilePath;
    private final String dropScriptFilePath;
    private Connection connection;

    public TestConnectionManager(String jdbcUrl, String dbUser, String dbPassword, String createScriptFilePath,
                                 String dropScriptFilePath) throws IOException, SQLException {
        this.jdbcUrl = jdbcUrl;
        this.dbUser = dbUser;
        this.dbPassword = dbPassword;
        this.createScriptFilePath = createScriptFilePath;
        this.dropScriptFilePath = dropScriptFilePath;
        createTables();
    }

    private void createTables() throws IOException, SQLException {
        		/*
		 * Initializing and H2 database with the stock exchange data
		 */
        connection = DriverManager.getConnection(jdbcUrl, dbUser, dbPassword);
        Statement st = connection.createStatement();

        FileReader reader = new FileReader(MappingOntologyMismatchTest.class.getResource(createScriptFilePath).getFile());
        BufferedReader in = new BufferedReader(reader);
        StringBuilder bf = new StringBuilder();
        String line = in.readLine();
        while (line != null) {
            bf.append(line);
            line = in.readLine();
        }
        in.close();

        st.executeUpdate(bf.toString());
        connection.commit();
    }

    private void dropTables() throws SQLException, IOException {

        Statement st = connection.createStatement();

        FileReader reader = new FileReader(MappingOntologyMismatchTest.class.getResource(dropScriptFilePath).getFile());
        BufferedReader in = new BufferedReader(reader);
        StringBuilder bf = new StringBuilder();
        String line = in.readLine();
        while (line != null) {
            bf.append(line);
            line = in.readLine();
        }
        in.close();

        st.executeUpdate(bf.toString());
        st.close();
        connection.commit();
        connection.close();
    }

    public OBDASpecification extractSpecification(String owlFile, String obdaFile) throws OBDASpecificationException {
        OntopMappingSQLAllOWLAPIConfiguration configuration = generateConfiguration(owlFile, obdaFile);
        return configuration.loadSpecification();
    }

    public OntopMappingSQLAllOWLAPIConfiguration generateConfiguration(String owlFile, String obdaFile) {
        Class klass = this.getClass();

        return OntopMappingSQLAllOWLAPIConfiguration.defaultBuilder()
                .ontologyFile(klass.getResource(owlFile).getFile())
                .nativeOntopMappingFile(klass.getResource(obdaFile).getFile())
                .jdbcUrl(jdbcUrl)
                .jdbcUser(dbUser)
                .jdbcPassword(dbPassword)
                .build();
    }

    @Override
    public void close() throws IOException {
        try {
            dropTables();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
