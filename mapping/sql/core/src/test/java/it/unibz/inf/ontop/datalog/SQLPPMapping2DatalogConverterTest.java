package it.unibz.inf.ontop.datalog;

/*
 * #%L
 * ontop-obdalib-core
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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Injector;
import it.unibz.inf.ontop.dbschema.*;
import it.unibz.inf.ontop.injection.OntopMappingConfiguration;
import it.unibz.inf.ontop.injection.SpecificationFactory;
import it.unibz.inf.ontop.iq.IQ;
import it.unibz.inf.ontop.model.atom.TargetAtom;
import it.unibz.inf.ontop.spec.mapping.PrefixManager;
import it.unibz.inf.ontop.spec.mapping.SQLMappingFactory;
import it.unibz.inf.ontop.spec.mapping.impl.SQLMappingFactoryImpl;
import it.unibz.inf.ontop.spec.mapping.parser.TargetQueryParser;
import it.unibz.inf.ontop.spec.mapping.parser.impl.TurtleOBDASQLParser;
import it.unibz.inf.ontop.spec.mapping.pp.SQLPPTriplesMap;
import it.unibz.inf.ontop.spec.mapping.pp.impl.LegacySQLPPMappingConverter;
import it.unibz.inf.ontop.spec.mapping.pp.impl.OntopNativeSQLPPTriplesMap;
import junit.framework.TestCase;

import java.sql.Types;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static it.unibz.inf.ontop.utils.SQLMappingTestingTools.*;


public class SQLPPMapping2DatalogConverterTest extends TestCase {

	private static final SQLMappingFactory MAPPING_FACTORY = SQLMappingFactoryImpl.getInstance();
	private final SpecificationFactory specificationFactory;
	private final DummyRDBMetadata defaultDummyMetadata;

	private RDBMetadata md;
	private PrefixManager pm;
	private final LegacySQLPPMappingConverter ppMapping2DatalogConverter;

	public SQLPPMapping2DatalogConverterTest() {
		OntopMappingConfiguration defaultConfiguration = OntopMappingConfiguration.defaultBuilder()
				.enableTestMode()
				.build();

		Injector injector = defaultConfiguration.getInjector();
		specificationFactory = injector.getInstance(SpecificationFactory.class);
		ppMapping2DatalogConverter = injector.getInstance(LegacySQLPPMappingConverter.class);
		defaultDummyMetadata = injector.getInstance(DummyRDBMetadata.class);
    }
	
	public void setUp() {
		md = defaultDummyMetadata.clone();
		QuotedIDFactory idfac = md.getQuotedIDFactory();

		// Database schema
		DatabaseRelationDefinition table1 = md.createDatabaseRelation(idfac.createRelationID(null, "Student"));
		table1.addAttribute(idfac.createAttributeID("id"), Types.INTEGER, null, false);
		table1.addAttribute(idfac.createAttributeID("first_name"), Types.VARCHAR, null, false);
		table1.addAttribute(idfac.createAttributeID("last_name"), Types.VARCHAR, null, false);
		table1.addAttribute(idfac.createAttributeID("year"), Types.INTEGER, null, false);
		table1.addAttribute(idfac.createAttributeID("nationality"), Types.VARCHAR, null, false);
		table1.addUniqueConstraint(UniqueConstraint.primaryKeyOf(table1.getAttribute(idfac.createAttributeID("id"))));
		
		DatabaseRelationDefinition table2 = md.createDatabaseRelation(idfac.createRelationID(null, "Course"));
		table2.addAttribute(idfac.createAttributeID("cid"), Types.VARCHAR, null, false);
		table2.addAttribute(idfac.createAttributeID("title"), Types.VARCHAR, null, false);
		table2.addAttribute(idfac.createAttributeID("credits"), Types.INTEGER, null, false);
		table2.addAttribute(idfac.createAttributeID("description"), Types.VARCHAR, null, false);
		table2.addUniqueConstraint(UniqueConstraint.primaryKeyOf(table2.getAttribute(idfac.createAttributeID("cid"))));
		
		DatabaseRelationDefinition table3 = md.createDatabaseRelation(idfac.createRelationID(null, "Enrollment"));
		table3.addAttribute(idfac.createAttributeID("student_id"), Types.INTEGER, null, false);
		table3.addAttribute(idfac.createAttributeID("course_id"), Types.VARCHAR, null, false);
		table3.addUniqueConstraint(UniqueConstraint.primaryKeyOf(table3.getAttribute(idfac.createAttributeID("student_id")),
				table3.getAttribute(idfac.createAttributeID("course_id"))));
		
		// Prefix manager
        Map<String, String> prefixes = new HashMap<>();
		prefixes.put(":", "http://www.example.org/university#");
        pm = specificationFactory.createPrefixManager(ImmutableMap.copyOf(prefixes));
	}
	
	private void runAnalysis(String source, String targetString) throws Exception {
		TargetQueryParser targetParser = new TurtleOBDASQLParser(pm.getPrefixMap(), TERM_FACTORY,
				TARGET_ATOM_FACTORY, RDF_FACTORY);
		ImmutableList<TargetAtom> targetAtoms = targetParser.parse(targetString);

		SQLPPTriplesMap mappingAxiom = new OntopNativeSQLPPTriplesMap(MAPPING_FACTORY.getSQLQuery(source), targetAtoms);
		Set<IQ> dp = ppMapping2DatalogConverter.convert(ImmutableList.of(mappingAxiom), md).keySet();
		
		assertNotNull(dp);
		System.out.println(dp.toString());
	}
	
	public void testAnalysis_1() throws Exception {
		runAnalysis(
				"select id from Student",
				":S_{id} a :Student .");
	}
	
	public void testAnalysis_2() throws Exception {
		runAnalysis(
				"select id, first_name, last_name from Student",
				":S_{id} a :Student ; :fname {first_name} ; :lname {last_name} .");
	}
	
	public void testAnalysis_3() throws Exception {
		runAnalysis(
				"select id, first_name, last_name from Student where year=2010",
				":S_{id} a :Student ; :fname {first_name} ; :lname {last_name} .");
	}
	
	public void testAnalysis_4() throws Exception {
		runAnalysis(
				"select id, first_name, last_name from Student where nationality='it'",
				":S_{id} a :Student ; :fname {first_name} ; :lname {last_name} .");
	}
	
	public void testAnalysis_5() throws Exception {
		runAnalysis(
				"select id, first_name, last_name from Student where year=2010 and nationality='it'",
				":S_{id} a :Student ; :fname {first_name} ; :lname {last_name} .");
	}
	
	public void testAnalysis_6() throws Exception {
		runAnalysis(
				"select cid, title, credits, description from Course",
				":C_{cid} a :Course ; :title {title} ; :creditPoint {credits} ; :hasDescription {description} .");
	}
	
	public void testAnalysis_7() throws Exception {
		runAnalysis(
				"select cid, title from Course where credits>=4",
				":C_{cid} a :Course ; :title {title} .");
	}
	
	public void testAnalysis_8() throws Exception {
		runAnalysis(
				"select student_id, course_id from Enrollment",
				":S_{student_id} :hasCourse :C_{course_id}.");
	}
	
	public void testAnalysis_9() throws Exception {
		runAnalysis(
				"select id, first_name, last_name from Student, Enrollment where Student.id=Enrollment.student_id and Enrollment.course_id='BA002'",
				":S_{id} a :Student ; :fname {first_name} ; :lname {last_name} .");
	}
	
	public void testAnalysis_10() throws Exception {
		runAnalysis(
				"select id, first_name, last_name from Student, Enrollment where Student.id=Enrollment.student_id and Enrollment.course_id='BA002' and Student.year=2010",
				":S_{id} a :Student ; :fname {first_name} ; :lname {last_name} .");
	}
	
	public void testAnalysis_11() throws Exception {
		runAnalysis(
				"select id as StudentNumber, first_name as Name, last_name as FamilyName from Student",
				":S_{StudentNumber} a :Student ; :fname {Name} ; :lname {FamilyName} .");
	}
	
	public void testAnalysis_12() throws Exception {
		runAnalysis(
				"select id as StudentNumber, first_name as Name, last_name as FamilyName from Student as TableStudent where TableStudent.year=2010",
				":S_{StudentNumber} a :Student ; :fname {Name} ; :lname {FamilyName} .");
	}
	
	public void testAnalysis_13() throws Exception {
		runAnalysis(
				"select cid as CourseCode, title as CourseTitle, credits as CreditPoints, description as CourseDescription from Course",
				":C_{CourseCode} a :Course ; :title {CourseTitle} ; :creditPoint {CreditPoints} ; :hasDescription {CourseDescription} .");
	}
	
	public void testAnalysis_14() throws Exception {
		runAnalysis(
				"select cid as CourseCode, title as CourseTitle from Course as TableCourse where TableCourse.credits>=4",
				":C_{CourseCode} a :Course ; :title {CourseTitle} .");
	}
	
	public void testAnalysis_15() throws Exception {
		runAnalysis(
				"select student_id as sid, course_id as cid from Enrollment",
				":S_{sid} :hasCourse :C_{cid} .");
	}

	// ROMAN (28.11.17): StudentNumber cannot be used in the WHERE clause
	public void testAnalysis_16() throws Exception {
		runAnalysis(
				"select id as StudentNumber, first_name as Name, last_name as FamilyName from Student as t1, Enrollment as t2 where id=student_id and t2.course_id='BA002'",
				":S_{StudentNumber} a :Student ; :fname {Name} ; :lname {FamilyName} .");
	}

	public void testAnalysis_17() throws Exception {
		runAnalysis(
				"select id, first_name, last_name from Student where last_name like '%lli'",
				":S_{id} a :Student ; :fname {first_name} ; :lname {last_name} .");
	}
//	public void testAnalysis_17() throws Exception {
//		runAnalysis(
	//RENAME STUDENT.ID TO STUDENT.STUDENT_ID SO THE COLUMN NAMES ARE THE SAME
//				"select Student.student_id as StudentId from Student JOIN Enrollment USING (student_id) ",
//				":S_{StudentId} a :Student .");
//	}

	// ROMAN (28.11.17): StudentId cannot be used in the WHERE clause
	// ROMAN (28.11.17): year was not visible outside the subsquery
	public void testAnalysis_18() throws Exception {
		runAnalysis(
				"select id as StudentId from (select id, year from Student) as Sub JOIN Enrollment ON student_id = id where year> 2010 ",
				":S_{StudentId} a :Student .");
	}

	// ROMAN (28.11.17): StudentId cannot be used in the WHERE clause
	// ROMAN (28.11.17): first_name was not visible outside the subsquery
	public void testAnalysis_19() throws Exception {
		runAnalysis(
				"select id as StudentId from (select id, first_name from Student) as Sub JOIN Enrollment ON student_id = id where first_name !~ 'foo' ",
				":S_{StudentId} a :Student .");
	}

	// ROMAN (28.11.17): StudentId cannot be used in the WHERE clause
	// ROMAN (28.11.17): first_name was not visible outside the subsquery
	public void testAnalysis_20() throws Exception {
		runAnalysis(
				"select id as StudentId from (select id, first_name from Student) as Sub JOIN Enrollment ON student_id = id where regexp_like(first_name,'foo') ",
				":S_{StudentId} a :Student .");
	}

	// ROMAN (28.11.17): StudentId cannot be used in the WHERE clause
	// ROMAN (28.11.17): first_name was not visible outside the subsquery
	public void testAnalysis_21() throws Exception {
		runAnalysis(
				"select id as StudentId from (select id, first_name from Student) as Sub JOIN Enrollment ON student_id = id where first_name regexp 'foo' ",
				":S_{StudentId} a :Student .");
	}

    public void testAnalysis_22() throws Exception{
        runAnalysis("select id, first_name, last_name from Student where year in (2000, 2014)",
                ":S_{id} a :RecentStudent ; :fname {first_name} ; :lname {last_name} .");
    }

    public void testAnalysis_23() throws Exception{
        runAnalysis("select id, first_name, last_name from Student where  (year between 2000 and 2014) and nationality='it'",
                ":S_{id} a :RecentStudent ; :fname {first_name} ; :lname {last_name} .");
    }

	// ROMAN (28.11.17): first_name was not visible outside the subsquery
    public void testAnalysis_24() throws Exception {
        runAnalysis(
                "select id from (select id, first_name from Student) as Sub JOIN Enrollment ON student_id = id where regexp_like(first_name,'foo') ",
                ":S_{id} a :Student .");
    }

	public void testAnalysis_25() throws Exception {
		runAnalysis(
				"select \"QINVESTIGACIONPUARTTMP0\".id \"t1_1\" from Student \"QINVESTIGACIONPUARTTMP0\"  where \"QINVESTIGACIONPUARTTMP0\".first_name IS NOT NULL ",
				":S_{t1_1} a :Student .");
	}

	public void testAnalysis_26() throws Exception {
		runAnalysis(
				"SELECT * FROM Student  WHERE (id,  year) IN (SELECT i_id, MAX(year) FROM Student GROUP BY id)",
				":S_{id} a :Student ; :year \"{year}\" .");
	}

	public void testAnalysis_27() throws Exception {
		runAnalysis(
				"SELECT id FROM Student  WHERE (id,  year) IN (SELECT i_id, MAX(year) FROM Student GROUP BY id) INTERSECT SELECT student_id FROM Enrollment",
				":S_{id} a :Student .");
	}

    public void testAnalysis_28() throws Exception {
        runAnalysis(
                "select lower(id) as lid from Student",
                ":S_{lid} a :Student .");
    }


}
