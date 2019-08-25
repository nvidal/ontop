package it.unibz.inf.ontop;

import it.unibz.inf.ontop.dbschema.*;
import it.unibz.inf.ontop.model.atom.RelationPredicate;

import java.sql.Types;

import static it.unibz.inf.ontop.OptimizationTestingTools.createDummyMetadata;

public class NoDependencyTestDBMetadata {

    public static final RelationPredicate TABLE1_AR1;
    public static final RelationPredicate TABLE2_AR1;
    public static final RelationPredicate TABLE3_AR1;
    public static final RelationPredicate TABLE4_AR1;
    public static final RelationPredicate TABLE5_AR1;

    public static final RelationPredicate TABLE1_AR2;
    public static final RelationPredicate TABLE2_AR2;
    public static final RelationPredicate TABLE3_AR2;
    public static final RelationPredicate TABLE4_AR2;
    public static final RelationPredicate TABLE5_AR2;
    public static final RelationPredicate TABLE6_AR2;

    public static final RelationPredicate TABLE1_AR3;
    public static final RelationPredicate TABLE2_AR3;
    public static final RelationPredicate TABLE3_AR3;
    public static final RelationPredicate TABLE4_AR3;
    public static final RelationPredicate TABLE5_AR3;
    public static final RelationPredicate TABLE6_AR3;

    public static final RelationPredicate TABLE7_AR4;

    public static final BasicDBMetadata DB_METADATA;

    private static RelationPredicate createRelationPredicate(BasicDBMetadata dbMetadata, QuotedIDFactory idFactory,
                                                             int tableNumber, int arity) {
        DatabaseRelationDefinition tableDef = dbMetadata.createDatabaseRelation(idFactory.createRelationID(null,
                "TABLE" + tableNumber + "AR" + arity));
        for (int i=1 ; i <= arity; i++) {
            tableDef.addAttribute(idFactory.createAttributeID("col" + i), Types.VARCHAR, null, false);
        }
        return tableDef.getAtomPredicate();
    }

    static {
        BasicDBMetadata dbMetadata = createDummyMetadata();
        QuotedIDFactory idFactory = dbMetadata.getQuotedIDFactory();

        TABLE1_AR1 = createRelationPredicate(dbMetadata, idFactory, 1, 1);
        TABLE2_AR1 = createRelationPredicate(dbMetadata, idFactory, 2, 1);
        TABLE3_AR1 = createRelationPredicate(dbMetadata, idFactory, 3, 1);
        TABLE4_AR1 = createRelationPredicate(dbMetadata, idFactory, 4, 1);
        TABLE5_AR1 = createRelationPredicate(dbMetadata, idFactory, 5, 1);

        TABLE1_AR2 = createRelationPredicate(dbMetadata, idFactory, 1, 2);
        TABLE2_AR2 = createRelationPredicate(dbMetadata, idFactory, 2, 2);
        TABLE3_AR2 = createRelationPredicate(dbMetadata, idFactory, 3, 2);
        TABLE4_AR2 = createRelationPredicate(dbMetadata, idFactory, 4, 2);
        TABLE5_AR2 = createRelationPredicate(dbMetadata, idFactory, 5, 2);
        TABLE6_AR2 = createRelationPredicate(dbMetadata, idFactory, 6, 2);

        TABLE1_AR3 = createRelationPredicate(dbMetadata, idFactory, 1, 3);
        TABLE2_AR3 = createRelationPredicate(dbMetadata, idFactory, 2, 3);
        TABLE3_AR3 = createRelationPredicate(dbMetadata, idFactory, 3, 3);
        TABLE4_AR3 = createRelationPredicate(dbMetadata, idFactory, 4, 3);
        TABLE5_AR3 = createRelationPredicate(dbMetadata, idFactory, 5, 3);
        TABLE6_AR3 = createRelationPredicate(dbMetadata, idFactory, 6, 3);

        TABLE7_AR4 = createRelationPredicate(dbMetadata, idFactory, 7, 4);

        dbMetadata.freeze();
        DB_METADATA = dbMetadata;
    }

}
