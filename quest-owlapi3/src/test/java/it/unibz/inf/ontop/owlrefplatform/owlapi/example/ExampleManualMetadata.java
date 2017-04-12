package it.unibz.inf.ontop.owlrefplatform.owlapi.example;

import com.google.inject.Injector;
import it.unibz.inf.ontop.answering.OntopQueryEngine;
import it.unibz.inf.ontop.answering.input.InputQueryFactory;
import it.unibz.inf.ontop.injection.OntopEngineFactory;
import it.unibz.inf.ontop.injection.OntopSQLOWLAPIConfiguration;
import it.unibz.inf.ontop.owlrefplatform.core.OntopConnection;
import it.unibz.inf.ontop.owlrefplatform.owlapi.OntopOWLConnection;
import it.unibz.inf.ontop.owlrefplatform.owlapi.OntopOWLStatement;
import it.unibz.inf.ontop.owlrefplatform.owlapi.QuestOWLConnection;
import it.unibz.inf.ontop.sql.DatabaseRelationDefinition;
import it.unibz.inf.ontop.sql.QuotedIDFactory;
import it.unibz.inf.ontop.sql.RDBMetadata;
import it.unibz.inf.ontop.sql.RDBMetadataExtractionTools;

/**
 * This class shows how to create an instance of quest giving the metadata manually 
 * 
 * @author mrezk, Christian 
 *
 */
public class ExampleManualMetadata {
	final String owlfile = "src/main/resources/example/exampleSensor.owl";
	final String obdafile = "src/main/resources/example/UseCaseExampleMini.obda";
	final String propertyfile = "src/main/resources/example/UseCaseExampleMini.properties";
	private OntopOWLStatement qst = null;
	private OntopQueryEngine queryEngine;

	/*
     * 	prepare ontop for rewriting and unfolding steps
     */
private void setup()  throws Exception {

	OntopSQLOWLAPIConfiguration configuration = OntopSQLOWLAPIConfiguration.defaultBuilder()
			.nativeOntopMappingFile(obdafile)
			.ontologyFile(owlfile)
			.propertyFile(propertyfile)
			.dbMetadata(getMeta())
			.enableTestMode()
			.build();
	Injector injector = configuration.getInjector();
	OntopEngineFactory engineFactory = injector.getInstance(OntopEngineFactory.class);

	queryEngine = engineFactory.create(configuration.loadProvidedSpecification(),
			configuration.getExecutorRegistry());
	queryEngine.connect();
	
	/*
	 * Prepare the data connection for querying.
	 */
	
	OntopConnection conn = queryEngine.getConnection();
	OntopOWLConnection connOWL = new QuestOWLConnection(conn, injector.getInstance(InputQueryFactory.class));
	qst = connOWL.createStatement();
}

private void defMeasTable(RDBMetadata dbMetadata, String name) {
	QuotedIDFactory idfac = dbMetadata.getQuotedIDFactory();
	DatabaseRelationDefinition tableDefinition = dbMetadata.createDatabaseRelation(idfac.createRelationID(null, name));
	tableDefinition.addAttribute(idfac.createAttributeID("timestamp"), java.sql.Types.TIMESTAMP, null, false);
	tableDefinition.addAttribute(idfac.createAttributeID("value"), java.sql.Types.NUMERIC, null, false);
	tableDefinition.addAttribute(idfac.createAttributeID("assembly"), java.sql.Types.VARCHAR, null, false);
	tableDefinition.addAttribute(idfac.createAttributeID("sensor"), java.sql.Types.VARCHAR, null, false);
}

private void defMessTable(RDBMetadata dbMetadata, String name) {
	QuotedIDFactory idfac = dbMetadata.getQuotedIDFactory();
	DatabaseRelationDefinition tableDefinition = dbMetadata.createDatabaseRelation(idfac.createRelationID(null, name));
	tableDefinition.addAttribute(idfac.createAttributeID("timestamp"), java.sql.Types.TIMESTAMP, null, false);
	tableDefinition.addAttribute(idfac.createAttributeID("eventtext"), java.sql.Types.VARCHAR, null, false);
	tableDefinition.addAttribute(idfac.createAttributeID("assembly"), java.sql.Types.VARCHAR, null, false);
}

private void defStaticTable(RDBMetadata dbMetadata, String name) {
	QuotedIDFactory idfac = dbMetadata.getQuotedIDFactory();
	DatabaseRelationDefinition tableDefinition = dbMetadata.createDatabaseRelation(idfac.createRelationID(null, name));
	tableDefinition.addAttribute(idfac.createAttributeID("domain"), java.sql.Types.VARCHAR, null, false);
	tableDefinition.addAttribute(idfac.createAttributeID("range"), java.sql.Types.VARCHAR, null, false);
}
private RDBMetadata getMeta(){
	RDBMetadata dbMetadata = RDBMetadataExtractionTools.createDummyMetadata();

	defMeasTable(dbMetadata, "burner");
	defMessTable(dbMetadata, "events");
	defStaticTable(dbMetadata, "a_static");
	return dbMetadata;
}

public void runQuery() throws Exception {
	setup();
	System.out.println("Good");
	queryEngine.close();
}

public static void main(String[] args) {
	try {
		ExampleManualMetadata example = new ExampleManualMetadata();
		example.runQuery();
	} catch (Exception e) {
		e.printStackTrace();
	}
}
}