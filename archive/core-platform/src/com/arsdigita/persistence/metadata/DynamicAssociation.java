/*
 * Copyright (C) 2001, 2002 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the CCM Public
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of
 * the License at http://www.redhat.com/licenses/ccmpl.html
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */

package com.arsdigita.persistence.metadata;

import com.arsdigita.persistence.PersistenceException;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.Filter;
import com.arsdigita.db.Sequences;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.sql.SQLException;

/**
 * This class provides support to create dynamic two-way associations between
 * any object types.  Link attributes will eventually be supported.
 *
 * @deprecated Use com.arsdigita.metadata.DynamicAssociation instead.
 * @author <a href="mailto:pmcneill@arsdigita.com">Patrick McNeill</a>
 * @version $Revision: #7 $ $Date: 2002/11/01 $
 */

public class DynamicAssociation {
    // the model this association will belong to
    private Model m_model;

    // the two primary roles for this association
    private Property m_prop1;
    private Property m_prop2;

    private MetadataRoot m_root = MetadataRoot.getMetadataRoot();

    // the DataObject containing information about this association
    // null indicates a new association
    private DataObject m_dataObject = null;

    private static final String objectTypeString =
        "com.arsdigita.persistence.DynamicAssociation";

    /**
     * Load a pre-existing DynamicAssociation for editing.  Note this is
     * rather useless right now since we don't support link attributes.
     *
     * @param modelName the fully-qualified model name for this association
     * @param objectType1 the fully-qualified name of the datatype of the
     *                    first property
     * @param property1 the name of the first property
     * @param objectType2 the fully-qualified name of the datatype of the
     *                    second property
     * @param property2 the name of the second property
     * @throws PersistenceException if either ObjectType is null or if they
     *                              do not contain the correct property
     */
    public DynamicAssociation(String modelName,
                              String objectType1,
                              String property1,
                              String objectType2,
                              String property2) {
        Model model = m_root.getModel(modelName);

        if (model == null) {
            model = new Model(modelName);
            m_root.addModel(model);
        }

        ObjectType type1 = m_root.getObjectType(objectType1);
        ObjectType type2 = m_root.getObjectType(objectType2);

        if (type1 == null) {
            throw new PersistenceException(objectType1 + " does not exist");
        }

        if (type2 == null) {
            throw new PersistenceException(objectType2 + " does not exist");
        }

        Property prop1 = type2.getProperty(property1);
        Property prop2 = type1.getProperty(property2);

        if ((prop1 == null) || (prop2 == null)) {
            throw new PersistenceException(
                                           "Either " + property1 + " or " + property2 + " is null");
        }

        if ((prop1.getAssociation() == null) ||
            (prop2.getAssociation() == null)) {
            throw new PersistenceException(
                                           "Either " + property1 + " or " + property2 + " does not " +
                                           "belong to an association");
        }

        if (!prop1.getAssociation().equals(prop2.getAssociation())) {
            throw new PersistenceException("Properties belong to different " +
                                           "associations");
        }

        m_prop1 = prop1;
        m_prop2 = prop2;
        m_model = model;

        DataCollection collection = SessionManager.getSession()
            .retrieve(objectTypeString);
        Filter f1 = collection.addFilter("lower(modelName) = :modelName");
        f1.set("modelName", model.getName().toLowerCase());

        Filter f2 = collection.addFilter("lower(property1) = :property1");
        f2.set("property1", prop1.getName().toLowerCase());

        Filter f3 = collection.addFilter("lower(objectType1) = :objectType1");
        f3.set("objectType1", prop1.getType().getQualifiedName().toLowerCase());

        Filter f4 = collection.addFilter("lower(property2) = :property2");
        f4.set("property2", prop2.getName().toLowerCase());

        Filter f5 = collection.addFilter("lower(objectType2) = :objectType2");
        f5.set("objectType2", prop2.getType().getQualifiedName().toLowerCase());

        try {
            if (!collection.next()) {
                throw new PersistenceException(
                                               "The association you have requsted is static, and thus " +
                                               "cannot be used as a dynamic association");
            }

            m_dataObject = collection.getDataObject();
        } finally {
            collection.close();
        }
    }

    /**
     * Creates a new DynamicAssociation.  The two named properties will
     * be created with the given datatypes and multiplicities.
     *
     * @param modelName the fully-qualified model name for this association
     * @param objectType1 the fully-qualified name of the datatype of the
     *                    first property
     * @param property1 the name of the first property
     * @param objectType2 the fully-qualified name of the datatype of the
     *                    second property
     * @param property2 the name of the second property
     * @throws PersistenceException if either ObjectType is null or if they
     *                              do not contain the correct property
     */
    public DynamicAssociation(String modelName,
                              String objectType1,
                              String property1,
                              int multiplicity1,
                              String objectType2,
                              String property2,
                              int multiplicity2) {
        Model model = m_root.getModel(modelName);

        if (model == null) {
            model = new Model(modelName);
            m_root.addModel(model);
        }

        ObjectType type1 = m_root.getObjectType(objectType1);
        ObjectType type2 = m_root.getObjectType(objectType2);

        if (type1 == null) {
            throw new PersistenceException(objectType1 + " does not exist");
        }

        if (type2 == null) {
            throw new PersistenceException(objectType2 + " does not exist");
        }

        Property prop1 = type1.getProperty(property1);
        Property prop2 = type2.getProperty(property2);

        if ((prop1 != null) || (prop2 != null)) {
            throw new PersistenceException(
                                           "Either " + property1 + " or " + property2 + " is not null");
        }

        prop1 = new Property(property1, type1, multiplicity1);
        prop2 = new Property(property2, type2, multiplicity2);

        m_prop1 = prop1;
        m_prop2 = prop2;
        m_model = model;
    }

    /**
     * Saves this DynamicAssociation.  If it's a new Association, the table
     * will be created.  Editing really makes no sense right now, so it
     * won't do a whole lot but resave the PDL.  The Properties are also
     * added to the object types, facilitating retrieval of the association.
     *
     * @return the newly created Association
     */
    public Association save() {
        DDLGenerator generator = DDLGeneratorFactory.getInstance();

        ObjectType type1 = (ObjectType)m_prop1.getType();
        ObjectType type2 = (ObjectType)m_prop2.getType();
        Association assoc;

        // this is an add, so we need to do stuff
        if (m_dataObject == null) {
            Table mappingTable = new Table(
                                           generator.generateMappingTableName(type1, m_prop2.getName())
                                           );

            Column type1Key = type1.getColumn();
            Column type2Key = type2.getColumn();

            if ((type1Key == null) || (type2Key == null)) {
                throw new PersistenceException("One of the object types does " +
                                               "not have a reference key.");
            }

            String col1Name = generator.generateColumnName(type1,
                                                           m_prop1.getName());
            String col2Name = generator.generateColumnName(type2,
                                                           m_prop2.getName());

            Column type1Map = new Column(mappingTable,
                                         col1Name,
                                         type1Key.getType());
            Column type2Map = new Column(mappingTable,
                                         col2Name,
                                         type2Key.getType());

            JoinPath jp = new JoinPath();
            jp.addJoinElement(type2Key, type2Map);
            jp.addJoinElement(type1Map, type1Key);
            m_prop1.setJoinPath(jp);

            jp = new JoinPath();
            jp.addJoinElement(type1Key, type1Map);
            jp.addJoinElement(type2Map, type2Key);
            m_prop2.setJoinPath(jp);

            type1.addProperty(m_prop2);
            type2.addProperty(m_prop1);

            String ddl =
                generator.generateMappingTable((ObjectType)m_prop1.getType(),
                                               m_prop2);

            try {
                java.sql.Statement statement = SessionManager.getSession()
                    .getConnection()
                    .createStatement();

                statement.executeUpdate(ddl);
            } catch (SQLException e) {
                throw PersistenceException.newInstance(e.getMessage() +
                                                       Utilities.LINE_BREAK +
                                                       "SQL for ADD: " + ddl, e);
            }

            assoc = new Association(m_prop1, m_prop2);
            assoc.setModel(m_model);
        } else {
            assoc = m_prop1.getAssociation();
        }

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        assoc.outputPDL(new PrintStream(stream));
        String pdl = "model " + m_model.getName() + ";" +
            Utilities.LINE_BREAK + stream.toString();

        try {
            if (m_dataObject == null) {
                m_dataObject = SessionManager.getSession()
                    .create(objectTypeString);

                m_dataObject.set("id", Sequences.getNextValue());
                m_dataObject.set("objectType", objectTypeString);
                m_dataObject.set("displayName", objectTypeString);
                m_dataObject.set("modelName", m_model.getName());
                m_dataObject.set("property1", m_prop1.getName());
                m_dataObject.set("objectType1", type1.getQualifiedName());
                m_dataObject.set("property2", m_prop2.getName());
                m_dataObject.set("objectType2", type2.getQualifiedName());
            }

            m_dataObject.set("pdlFile", pdl);
            m_dataObject.save();
        } catch (SQLException e) {
            throw PersistenceException.newInstance("Error saving PDL file", e);
        }

        MDSQLGenerator sqlgenerator = MDSQLGeneratorFactory.getInstance();

        // XXX link attributes
        for (int i = 0; i < Property.NUM_EVENT_TYPES; i++) {
            sqlgenerator.generateEvent((ObjectType)m_prop1.getType(),
                                       m_prop2, i, null);
            sqlgenerator.generateEvent((ObjectType)m_prop2.getType(),
                                       m_prop1, i, null);
        }

        return assoc;
    }
}
