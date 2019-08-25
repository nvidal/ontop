package it.unibz.inf.ontop.spec.mapping.parser;


import it.unibz.inf.ontop.exception.InvalidMappingException;
import it.unibz.inf.ontop.exception.InvalidMappingSourceQueriesException;
import it.unibz.inf.ontop.exception.OBDASpecificationException;
import it.unibz.inf.ontop.injection.OntopMappingSQLAllConfiguration;
import org.junit.Ignore;
import org.junit.Test;

public class BasicNativeMappingMistakeTest extends AbstractBasicMappingMistakeTest {

    @Test(expected = InvalidMappingSourceQueriesException.class)
    public void testUnboundTargetVariable() throws OBDASpecificationException {
        execute("/mistake/unbound.obda");
    }

    @Test(expected = InvalidMappingSourceQueriesException.class)
    public void testInvalidSQLQuery1() throws OBDASpecificationException {
        execute("/mistake/invalid-sql1.obda");
    }

    @Ignore("TODO: create an option for disabling black-box view creation "
            + "and create a specific exception for it")
    @Test
    public void testInvalidSQLQuery2() throws OBDASpecificationException {
        execute("/mistake/invalid-sql2.obda");
    }

    @Test(expected = InvalidMappingException.class)
    public void testMissingTargetTerm() throws OBDASpecificationException {
        execute("/mistake/missing-target-term.obda");
    }

    @Test(expected = InvalidMappingException.class)
    public void testFQDNInTargetTerm1() throws OBDASpecificationException {
        execute("/mistake/fqdn1.obda");
    }

    @Test(expected = InvalidMappingException.class)
    public void testFQDNInTargetTerm2() throws OBDASpecificationException {
        execute("/mistake/fqdn2.obda");
    }

    @Test(expected = InvalidMappingException.class)
    public void testFQDNInTargetTerm3() throws OBDASpecificationException {
        execute("/mistake/fqdn3.obda");
    }

    @Override
    protected OntopMappingSQLAllConfiguration createConfiguration(String obdaFile) {
        return OntopMappingSQLAllConfiguration.defaultBuilder()
                .dbMetadata(getDBMetadata())
                .nativeOntopMappingFile(getClass().getResource(obdaFile).getPath())
                .jdbcUrl("jdbc:h2://localhost/fake")
                .jdbcUser("fake_user")
                .jdbcPassword("fake_password")
                .enableProvidedDBMetadataCompletion(false)
                .build();
    }

}
