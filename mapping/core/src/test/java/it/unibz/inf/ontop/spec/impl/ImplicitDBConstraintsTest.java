package it.unibz.inf.ontop.spec.impl;

import com.google.common.collect.ImmutableList;
import com.google.inject.Guice;
import it.unibz.inf.ontop.dbschema.*;
import it.unibz.inf.ontop.exception.ImplicitDBContraintException;
import it.unibz.inf.ontop.spec.dbschema.PreProcessedImplicitRelationalDBConstraintExtractor;
import it.unibz.inf.ontop.spec.dbschema.PreProcessedImplicitRelationalDBConstraintSet;
import it.unibz.inf.ontop.spec.dbschema.impl.BasicPreProcessedImplicitRelationalDBConstraintExtractor;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.Set;

import static it.unibz.inf.ontop.utils.MappingTestingTools.*;
import static org.junit.Assert.*;

public class ImplicitDBConstraintsTest {

	private BasicDBMetadata md;
	private QuotedIDFactory idfac;

	private static final PreProcessedImplicitRelationalDBConstraintExtractor CONSTRAINT_EXTRACTOR = Guice.createInjector()
			.getInstance(BasicPreProcessedImplicitRelationalDBConstraintExtractor.class);
	
	@Before
	public void setupMetadata(){
		this.md = createDummyMetadata();
		this.idfac = md.getQuotedIDFactory();

		DatabaseRelationDefinition td = md.createDatabaseRelation(idfac.createRelationID(null, "TABLENAME"));
		td.addAttribute(idfac.createAttributeID("KEYNAME"), 0, null, false); // from 1

		DatabaseRelationDefinition td2 = md.createDatabaseRelation(idfac.createRelationID(null, "TABLE2"));
		td2.addAttribute(idfac.createAttributeID("KEY1"), 0, null, false);  // from 1
		td2.addAttribute(idfac.createAttributeID("KEY2"), 0, null, false);
	}
	
	@Test
	public void testEmptyUserConstraints() throws ImplicitDBContraintException {
		PreProcessedImplicitRelationalDBConstraintSet uc = CONSTRAINT_EXTRACTOR.extract(
				new File("src/test/resources/userconstraints/empty_constraints.lst"));

		Set<RelationID> refs = uc.getReferredTables(idfac);
		assertEquals(0, refs.size());
	}

	@Test
	public void testUserPKeys() throws ImplicitDBContraintException {
		PreProcessedImplicitRelationalDBConstraintSet uc = CONSTRAINT_EXTRACTOR.extract(
				new File("src/test/resources/userconstraints/pkeys.lst"));
		Set<RelationID> refs = uc.getReferredTables(idfac);
		assertEquals(0, refs.size());
	}

	@Test
	public void testAddPrimaryKeys() throws ImplicitDBContraintException {
		PreProcessedImplicitRelationalDBConstraintSet uc = CONSTRAINT_EXTRACTOR.extract(
				new File("src/test/resources/userconstraints/pkeys.lst"));
		uc.insertUniqueConstraints(this.md);
		DatabaseRelationDefinition dd = this.md.getDatabaseRelation(idfac.createRelationID(null, "TABLENAME"));
		Attribute attr = dd.getAttribute(idfac.createAttributeID("KEYNAME"));
		assertEquals(ImmutableList.of(attr), dd.getUniqueConstraints().get(0).getAttributes());
	}


	@Test
	public void testGetReferredTables() throws ImplicitDBContraintException {
		PreProcessedImplicitRelationalDBConstraintSet uc = CONSTRAINT_EXTRACTOR.extract(
				new File("src/test/resources/userconstraints/fkeys.lst"));
		Set<RelationID> refs = uc.getReferredTables(idfac);
		assertEquals(1, refs.size());
		assertTrue(refs.contains(idfac.createRelationID(null, "TABLE2")));
	}

	@Test
	public void testAddForeignKeys() throws ImplicitDBContraintException {
		PreProcessedImplicitRelationalDBConstraintSet uc = CONSTRAINT_EXTRACTOR.extract(
				new File("src/test/resources/userconstraints/fkeys.lst"));
		uc.insertForeignKeyConstraints(this.md);
		DatabaseRelationDefinition dd = this.md.getDatabaseRelation(idfac.createRelationID(null, "TABLENAME"));
		ForeignKeyConstraint fk = dd.getForeignKeys().get(0);
		assertNotNull(fk);
		Attribute ref = fk.getComponents().get(0).getReference();
		assertEquals(ref.getRelation().getID(), idfac.createRelationID(null, "TABLE2"));
		assertEquals(ref.getID(), idfac.createAttributeID("KEY1"));
	}

	@Test
	public void testAddKeys() throws ImplicitDBContraintException {
		PreProcessedImplicitRelationalDBConstraintSet uc = CONSTRAINT_EXTRACTOR.extract(
				new File("src/test/resources/userconstraints/keys.lst"));
		uc.insertUniqueConstraints(this.md);
		uc.insertForeignKeyConstraints(this.md);
		DatabaseRelationDefinition dd = this.md.getDatabaseRelation(idfac.createRelationID(null, "TABLENAME"));
		ForeignKeyConstraint fk = dd.getForeignKeys().get(0);
		assertNotNull(fk);
		Attribute ref = fk.getComponents().get(0).getReference();
		assertEquals(ref.getRelation().getID(), idfac.createRelationID(null, "TABLE2"));
		assertEquals(ref.getID(), idfac.createAttributeID("KEY1"));
		assertEquals(ImmutableList.of(dd.getAttribute(idfac.createAttributeID("KEYNAME"))),
						dd.getUniqueConstraints().get(0).getAttributes());
	}

	
}
