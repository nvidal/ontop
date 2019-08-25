package it.unibz.inf.ontop.dbschema;

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

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.List;

/**
 * Foreign Key constraints<br>
 * 
 * FOREIGN KEY (columnName (, columnName)*)  
 * 			REFERENCES refTableName	(refColumnName (, refColumnName)*)<br>
 * 
 * (a particular case of linear tuple-generating dependencies<br>
 * \forall x (\exists y_1 R_1(x,y_1) \to \exists y_2 R_2(x,y_2))<br>
 * where x, y_1 and y_2 are *tuples* of variables)
 * 
 * @author Roman Kontchakov
 *
 */

public class ForeignKeyConstraint {

    public static final class Component {
		private final Attribute attribute, reference;
		
		private Component(Attribute attribute, Attribute reference) {
			this.attribute = attribute;
			this.reference = reference;
		}
		
		public Attribute getAttribute() {
			return attribute;
		}
		
		public Attribute getReference() {
			return reference;
		}


    }
	
	public static final class Builder {
		private final ImmutableList.Builder<Component> builder = new ImmutableList.Builder<>();
		private final DatabaseRelationDefinition relation, referencedRelation;
		
		/**
		 * creates a FOREIGN KEY builder 
		 * 
		 * @param relation 
		 * @param referencedRelation
		 */
		
		public Builder(DatabaseRelationDefinition relation, DatabaseRelationDefinition referencedRelation) {
			this.relation = relation;
			this.referencedRelation = referencedRelation;
		}
		
		/**
		 * adds a pair (attribute, referenced attribute) to the FK constraint
		 * 
		 * @param attribute
		 * @param referencedAttribute
		 * @return
		 */
		
		public Builder add(Attribute attribute, Attribute referencedAttribute) {
			if (attribute == null) {
				throw new IllegalArgumentException("Missing Foreign Key column for table " + relation.getID() +" referring to primary key "+attribute+"  in table " + referencedRelation.getID());
			}
			if (referencedAttribute == null){
				throw new IllegalArgumentException("Missing Primary Key column for table " + referencedRelation.getID() +" referring to foreign key "+attribute+" in table "+ relation.getID() );
			}
			if (relation != attribute.getRelation())
				throw new IllegalArgumentException("Foreign Key requires the same table in all attributes: " + relation + " -> " + referencedRelation + " (attribute " + attribute.getRelation().getID() + "." + attribute + ")");
			
			if (referencedRelation != referencedAttribute.getRelation())
				throw new IllegalArgumentException("Foreign Key requires the same table in all referenced attributes: "  + relation + " -> " + referencedRelation  + " (attribute " + referencedAttribute.getRelation().getID() + "." + referencedAttribute + ")");
			
			builder.add(new Component(attribute, referencedAttribute));
			return this;
		}

		/**
		 * builds a FOREIGN KEY constraint
		 * 
		 * @param name
		 * @return null if the list of components is empty
		 */
		
		public ForeignKeyConstraint build(String name) {
			ImmutableList<Component> components = builder.build();
			if (components.isEmpty())
				return null;
			
			return new ForeignKeyConstraint(name, components);
		}
	}
	
	/**
	 * creates a FOREIGN KEY builder
	 * 
	 * @param relation
	 * @param referencedRelation
	 * @return
	 */
	
	public static Builder builder(DatabaseRelationDefinition relation, DatabaseRelationDefinition referencedRelation) {
		return new Builder(relation, referencedRelation);
	}
	
	/**
	 * creates a single-attribute foreign key 
	 * 
	 * @param name
	 * @param attribute
	 * @param reference
	 * @return
	 */
	public static ForeignKeyConstraint of(String name, Attribute attribute, Attribute reference) {
		return new Builder((DatabaseRelationDefinition)attribute.getRelation(), 
							(DatabaseRelationDefinition)reference.getRelation())
									.add(attribute, reference).build(name);
	}
	
	private final String name;
	private final ImmutableList<Component> components;
	private final DatabaseRelationDefinition relation, referencedRelation;
	
	/**
	 * private constructor (use Builder instead)
	 * 
	 * @param name
	 * @param components
	 */
	
	private ForeignKeyConstraint(String name, ImmutableList<Component> components) {
		this.name = name;
		this.components = components;
		this.relation = (DatabaseRelationDefinition)components.get(0).getAttribute().getRelation();
		this.referencedRelation = (DatabaseRelationDefinition)components.get(0).getReference().getRelation();
	}
	
	/**
	 * returns the name of the foreign key constraint
	 * 
	 * @return name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * returns the components of the foreign key constraint
	 * each component defines a map from an attribute of the relation 
	 * 			to an attribute of the referenced relation
	 *  
	 * @return
	 */
	public ImmutableList<Component> getComponents() {
		return components;
	}
	
	/**
	 * returns referenced database relation
	 * 
	 * @return referenced relation 
	 */
	public DatabaseRelationDefinition getReferencedRelation() {
		return referencedRelation;
	}

	/**
	 * returns the relation with the foreign key
	 * 
	 * @return relation
	 */
	
	public DatabaseRelationDefinition getRelation() {
		return relation;
	}



    @Override
	public String toString() {
		List<String> columns = new ArrayList<>(components.size());
		List<String> refColumns = new ArrayList<>(components.size());
		for (Component c : components) {
			columns.add(c.getAttribute().getID().toString());
			refColumns.add(c.getReference().getID().toString());
		}
		
		StringBuilder bf = new StringBuilder();

		bf.append("ALTER TABLE ").append(relation.getID().getSQLRendering())
			.append(" ADD CONSTRAINT ").append(name).append(" FOREIGN KEY (");
		Joiner.on(", ").appendTo(bf, columns);	
		bf.append(") REFERENCES ").append(referencedRelation.getID().getSQLRendering())
			.append(" (");
		Joiner.on(", ").appendTo(bf, refColumns);	
		bf.append(")");
		
		return bf.toString();
	}
}
