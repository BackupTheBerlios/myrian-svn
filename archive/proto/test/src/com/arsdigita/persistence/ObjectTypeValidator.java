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

package com.arsdigita.persistence;
import com.arsdigita.util.Assert;
import com.arsdigita.persistence.metadata.*;
import java.math.*;
import java.util.*;
import java.io.*;
import org.apache.log4j.*;

/**
 * ObjectTypeValidator
 *
 * @author <a href="mailto:jorris@arsdigita.com"Jon Orris</a>
 * @version $Revision: #1 $ $Date: 2002/11/27 $
 */

public class ObjectTypeValidator  {

    public final static String versionId = "$Id: //core-platform/proto/test/src/com/arsdigita/persistence/ObjectTypeValidator.java#1 $ by $Author: dennis $, $DateTime: 2002/11/27 19:51:05 $";
    private static final Logger s_log =
        Logger.getLogger(ObjectTypeValidator.class.getName());
    private Session m_session;
    DataObjectManipulator m_manipulator;

    static  {
        s_log.setPriority(Priority.DEBUG);
    }

    public ObjectTypeValidator(Session session) {
        m_session = session;
        m_manipulator = new DataObjectManipulator(session);
    }

    private Session getSession() {
        return m_session;
    }



    public void performCRUDTest(String objectTypeName) throws Exception {
        try {
            s_log.info("CRUDTest on " + objectTypeName);
            DataObject object = getSession().create(objectTypeName);
            reportPropertyTypes(object);

            initializeObject( object, null );
            OID id = object.getOID();
            s_log.info("Initialized object with id: " + id);
            object.save();
            object = getSession().retrieve(id);
            Assert.assertNotNull(object, "Object of type: " + objectTypeName + "and id: " + id + " was not found!");
            checkDefaultValues(object);

            checkUpdates(object, null);
            deleteObject(id);

        } catch (Exception e) {
            s_log.info("END CRUDTest on " + objectTypeName + " With error!" );
            s_log.info(e.getMessage());
            s_log.info("");
            s_log.info("");
            throw e;
        }

        s_log.info("END CRUDTest on " + objectTypeName);
        s_log.info("");
        s_log.info("");



    }

    private void initializeObject(DataObject data, DataObject associatedObject)  throws Exception {
        setDefaultProperties(data, associatedObject);
        s_log.debug("Created " + data.getObjectType().getQualifiedName() + " with OID: " + data.getOID());

        makeChildObjects(data);
        makeAssociations(data, associatedObject);
    }
    private void deleteObject(OID id)  throws Exception {

        // Manipulator for removing associations before delete
        PropertyManipulator.AssociationManipulator assocRemover = new PropertyManipulator.AssociationManipulator() {
                public void manipulate(Property p, DataObject data) throws Exception {
                    s_log.info("Found association: " + p.getName());
                    if( p.isCollection() ) {
                        DataAssociation assoc = (DataAssociation) data.get(p.getName());
                        DataAssociationCursor cursor = assoc.cursor();
                        while(cursor.next()) {
                            s_log.info("Removing from association: " + cursor.getDataObject().getObjectType().getName());
                            cursor.remove();
                            s_log.info("Removed!");
                        }

                    }

                }
            };

        DataObject data = getSession(). retrieve(id);
        s_log.info("");
        String objectName = data.getObjectType().getName();
        s_log.info("Deleting object: " + objectName + " with OID: " + data.getOID());

        PropertyManipulator.manipulateProperties(data, assocRemover);

        s_log.info("daving data!");
        data.save();
        s_log.info("about to delete!");
        data.delete();
        Assert.assertTrue(data.isDeleted());
        data = getSession(). retrieve(id);
        Assert.assertTrue( null == data );
        s_log.info("END Removing object: " + objectName);
        s_log.info("");


    }


    private void setDefaultProperties(DataObject data,
                                      DataObject associatedObject)
        throws Exception {

        final ObjectType type = data.getObjectType();
        s_log.info("");
        s_log.info("Making new object for: " + type.getQualifiedName());
        KeyGenerator.setKeyValues(data);

        PropertyManipulator.NonKeyManipulator manip =
            new PropertyManipulator.NonKeyManipulator(type) {
                public void manipulate(Property p, DataObject dataInner)
                    throws Exception {
                    m_manipulator.setDefaultProperty(p, dataInner);
                }
            };

        PropertyManipulator.manipulateProperties(data, manip);
        s_log.info("END new object.");
        s_log.info("");
        s_log.info("");

    }

    private void setKeyProperties(DataObject data,
                                  final DataObject associatedObject)
        throws Exception {

        s_log.info("setting key properties");
        KeyGenerator.setKeyValues(data);

        final ObjectType type = data.getObjectType();
        PropertyManipulator.AttributeManipulator manip =
            new PropertyManipulator.AttributeManipulator() {
                public boolean obeys(Property p) {
                    return super.obeys(p) && type.isKeyProperty(p)
                        && p.getType().isCompound();
                }

                public void manipulate(Property p, DataObject dataInner)
                    throws Exception {
                    if( associatedObject != null
                        && p.getType().equals(associatedObject.getObjectType())) {

                        dataInner.set(p.getName(), associatedObject);
                    } else {
                        DataObject object =
                            getSession().create(p.getType().getQualifiedName());
                        reportPropertyTypes(object);
                        initializeObject(object, dataInner);
                    }
                }
            };
        PropertyManipulator.manipulateProperties(data, manip);

    }

    private void makeAssociations(DataObject data,
                                  final DataObject associatedObject)
        throws Exception {

        PropertyManipulator.AssociationManipulator manip =
            new PropertyManipulator.AssociationManipulator() {
                public void manipulate(Property p, DataObject dataInner)
                    throws Exception {

                    DataType assocType = p.getType();
                    if( associatedObject == null
                        || !assocType.equals(associatedObject
                                             .getObjectType()) ) {
                        String msg = "Making association for:" +
                            dataInner.getObjectType().getName();
                        if( null == associatedObject ) {
                            msg += " Is not already associated with any object.";
                        } else {
                            msg += " Is not associated with: "
                                + associatedObject.getObjectType().getName();
                            msg += " The association type is: " + assocType.getName();
                        }
                        s_log.info(msg);
                        makeAssociation(p, dataInner);
                    }
                }
            };

        PropertyManipulator.manipulateProperties(data, manip);

    }

    private void makeAssociation(Property p, DataObject data)
        throws Exception {

        String fullTypeName = p.getType().getQualifiedName();
        s_log.info("Making associated object: " + fullTypeName +
                   " for ObjectType: " + data.getObjectType().getQualifiedName());

        DataObject associatedObject = getSession().create(fullTypeName);
        reportPropertyTypes(associatedObject);
        initializeObject(associatedObject, data);
        associatedObject.save();
        reportPropertyTypes(associatedObject);
        s_log.info("Getting association:  " + p.getName());
        if (p.isCollection()) {
            DataAssociation assoc = (DataAssociation) data.get(p.getName());
            assoc.add(associatedObject);
        } else {
            data.set(p.getName(), associatedObject);
        }
    }


    private void makeChildObjects(DataObject data)
        throws Exception {

        PropertyManipulator.ComponentManipulator manip =
            new PropertyManipulator.ComponentManipulator() {
                public void manipulate(Property p, DataObject dataInner)
                    throws Exception {
                    makeChild(p, dataInner);
                }
            };

        PropertyManipulator.manipulateProperties(data, manip);

    }

    private void makeChild(Property p, final DataObject parent)
        throws Exception {

        final String fullTypeName = p.getType().getQualifiedName();
        s_log.info("Making child object: " + fullTypeName +
                   " for ObjectType: " + parent.getObjectType().getQualifiedName());

        DataObject child = getSession().create(fullTypeName);
        reportPropertyTypes(child);

        initializeObject(child, parent);
        PropertyManipulator.AssociationManipulator manip =
            new PropertyManipulator.AssociationManipulator() {
                public boolean obeys(Property pInner) {
                    final boolean isParentRef = super.obeys(pInner)
                        && !pInner.isCollection()
                        && pInner.getType().equals(parent.getObjectType());
                    return isParentRef;
                }
                public void manipulate(Property pInner, DataObject data)
                    throws Exception {
                    s_log.info("Setting parent role reference for: "
                               + fullTypeName + " Property: "
                               + pInner.getName());
                    data.set(pInner.getName(), parent);
                }

            };
        PropertyManipulator.manipulateProperties(child, manip);
        if (p.isCollection()) {
            DataAssociation children = (DataAssociation) parent.get(p.getName());
            children.add(child);

        }
        else {
            parent.set(p.getName(), child);
        }

    }


    private void checkUpdates(DataObject data, final DataObject parent)
        throws Exception {

        final ObjectType type = data.getObjectType();

        try {
            PropertyManipulator.NonKeyManipulator manip =
                new PropertyManipulator.NonKeyManipulator(type) {

                    public void manipulate(Property p,
                                           DataObject dataInner)
                        throws Exception {
                        m_manipulator.updateAllPropertyCombinations(
                                                                    p, dataInner);
                    }
                };

            PropertyManipulator.manipulateProperties(data, manip);

            PropertyManipulator.PredicateManipulator childManip =
                new PropertyManipulator.ComponentManipulator() {
                    public void manipulate(Property p,
                                           DataObject dataInner)
                        throws Exception {

                        if(p.isCollection()) {
                            DataAssociation children =
                                (DataAssociation) dataInner.get(p.getName());
                            DataAssociationCursor cursor = children.cursor();
                            while(cursor.next()) {
                                DataObject child = cursor.getDataObject();
                                s_log.debug("checkUpdates on child: "
                                            + child.getObjectType()
                                            .getQualifiedName());
                                checkUpdates(child, dataInner);
                            }
                        } else {
                            DataObject child =
                                (DataObject) dataInner.get(p.getName());
                            s_log.debug("checkUpdates on child: "
                                        + child.getObjectType()
                                        .getQualifiedName());
                            checkUpdates(child, dataInner);
                        }
                    }

                };

            PropertyManipulator.manipulateProperties(data, childManip);

            PropertyManipulator.AssociationManipulator assocManip =
                new PropertyManipulator.AssociationManipulator() {
                    public void manipulate(Property p,
                                           DataObject dataInner)
                        throws Exception {

                        if(p.isCollection()) {
                            DataAssociation associations =
                                (DataAssociation) dataInner.get(p.getName());
                            DataAssociationCursor cursor = associations.cursor();
                            while(cursor.next()) {
                                DataObject assoc = cursor.getDataObject();
                                if( !assoc.equals(parent) ) {
                                    s_log.debug("checkUpdates on assoc: "
                                                + assoc.getObjectType()
                                                .getQualifiedName());
                                    checkUpdates(assoc, dataInner);
                                }
                            }
                        } else {
                            DataObject assoc =
                                (DataObject) dataInner.get(p.getName());
                            if( null != assoc &&  !assoc.equals(parent) ) {
                                s_log.debug("checkUpdates on assoc: "
                                            + assoc.getObjectType()
                                            .getQualifiedName());
                                checkUpdates(assoc, dataInner);
                            }
                        }
                    }
                };

            PropertyManipulator.manipulateProperties(data, assocManip);
        } catch (UndefinedEventException e) {
            s_log.info("Update event undefined for type: "
                       + type.getQualifiedName()
                       + " Update tests cannot be performed.");
            s_log.info("UndefinedEventException: " + e.getMessage());
        }


    }

    private void checkDefaultValues(DataObject data) throws Exception {
        s_log.debug("Checking default values for : "
                    + data.getObjectType().getName());

        class DefaultValidator extends PropertyManipulator.NonKeyManipulator {
            public DefaultValidator(ObjectType type) {
                super(type);
            }

            public void manipulate(Property p, DataObject dataInner)
                throws Exception {
                s_log.debug("checking property: " + p.getName());
                Object currentVal = dataInner.get(p.getName());
                DataObjectManipulator.SimpleTypeManipulator manip =
                    m_manipulator.getManipulator(p);
                Object defaultVal = manip.getDefaultValue();
                manip.checkEquals( p.getName(), defaultVal, currentVal );
            }
        };

        DefaultValidator validator = new DefaultValidator(data.getObjectType());
        PropertyManipulator.manipulateProperties(data, validator);

        PropertyManipulator.AssociationManipulator assocValidator =
            new PropertyManipulator.AssociationManipulator() {
                public void manipulate(Property p, DataObject dataInner)
                    throws Exception  {

                    s_log.debug("Checking for association: " + p.getName());
                    if (p.isCollection()) {
                        DataAssociation assoc =
                            (DataAssociation) dataInner.get(p.getName());
                        DataAssociationCursor cursor = assoc.cursor();
                        while(cursor.next()) {
                            DataObject role = cursor.getDataObject();
                            checkRole(role);
                        }

                    } else {
                        DataObject role =
                            (DataObject) dataInner.get(p.getName());
                        checkRole(role);
                    }

                }
                void checkRole(DataObject role) throws Exception {
                    reportPropertyTypes(role);
                    DefaultValidator roleValidator =
                        new DefaultValidator(role.getObjectType());
                    PropertyManipulator.manipulateProperties(
                                                             role, roleValidator);
                }
            };

        PropertyManipulator.manipulateProperties(data, assocValidator);
        s_log.debug("END Checking default values");
        s_log.debug("");

    }

    private void reportPropertyTypes(DataObject data) throws Exception {
        ObjectType type = data.getObjectType();
        Iterator keys = type.getProperties();
        s_log.info("Properties for type: " + type.getName());
        while(keys.hasNext()) {
            Property p = (Property) keys.next();
            if( p.isAttribute() ) {
                String msg = "Property " + p.getName() + " is attribute. Class is: " + p.getJavaClass();
                if( type.isKeyProperty(p) )  {
                    msg += " is key property.";
                }
                msg += " value is: " + data.get(p.getName());
                s_log.info(msg);

            }
            else {
                s_log.info("Property " + p.getName() + "  is component: " +
                           p.isComponent() + " is collection: " + p.isCollection() + " is role: " + p.isRole());
                s_log.info("ObjectType is is: " + p.getType().getName());

            }

        }

        s_log.info("END Properties for type: " + type.getName());
    }

    private void setDefaultProperty(Property p, DataObject data) throws Exception {
        // s_log.info("Property " + p.getName() + " class is: " + p.getJavaClass());
        m_manipulator.setDefaultProperty(p, data);

    }

    private void updateAllPropertyCombinations(Property p, DataObject data) throws Exception {
        m_manipulator.updateAllPropertyCombinations(p, data);
    }

    private static boolean isNonKeyAttribute(Property p, ObjectType type) {
        return p.isAttribute() && !type.isKeyProperty(p);
    }
}