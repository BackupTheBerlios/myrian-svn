/*
 * Copyright (C) 2003 Red Hat Inc. All Rights Reserved.
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

package com.arsdigita.persistence.proto.pdl.nodes;

import com.arsdigita.persistence.metadata.MetadataRoot;
import com.arsdigita.persistence.metadata.ObjectType;
import com.arsdigita.persistence.metadata.Property;
import com.arsdigita.util.Assert;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

/**
 * Versioning metadata.
 *
 * @author Vadim Nasardinov (vadimn@redhat.com)
 * @since 2003-02-18
 * @version $Revision: #11 $ $Date: 2003/05/09 $
 */
public class VersioningMetadata {
    private final static Logger s_log =
        Logger.getLogger(VersioningMetadata.class);

    private final Node.Switch m_switch;
    private final Set m_versionedTypes;
    private final Set m_unversionedProps;

    /**
     * The reason for this change listener is to avoid tight coupling between
     * the versioning and persistence packages. The versioning package must have
     * some sort of access to the PDL abstract syntax tree in order to be able
     * to determine whether or not an object type is marked "versioned".
     *
     * <p>The easiest route would be to add a boolean method
     * <code>isMarkedVersioned()</code> to the ObjectType class. However, we
     * want to try to avoid joining versioning and persistence at the hip like
     * that. Therefore, instead of adding said boolean method to the ObjectType
     * class, we use this class - i.e. VersioningMetadata - as a communication
     * medium.</p>
     *
     * <p>The idea is that the PDL parser will indirectly expose its underlying
     * PDL AST to this class via the callback provided by nodeSwitch(). This
     * will enable this class to keep track of object types that are marked
     * versioned. However, the versioning package must be made aware of any
     * changes in the object type metadata. Such changes may occur as new object
     * types are added, e.g. as a result of creating a user-defined content-type
     * at runtime. </p>
     **/
    private ChangeListener m_changeListener;

    private final static VersioningMetadata s_singleton =
        new VersioningMetadata();

    private VersioningMetadata() {
        m_versionedTypes = new HashSet();
        m_unversionedProps = new HashSet();

        m_switch = new Node.Switch() {
                public void onObjectType(ObjectTypeNd ot) {
                    final String fqn = ot.getQualifiedName();

                    if ( ot.isVersioned() ) {
                        m_versionedTypes.add(fqn);
                    }
                    s_log.info("onObjectType: " + fqn);
                    if ( m_changeListener != null ) {
                        m_changeListener.onObjectType(fqn, ot.isVersioned());
                    }
                }

                public void onProperty(PropertyNd prop) {
                    Node parent = prop.getParent();
                    String containerName = null;
                    if ( parent instanceof ObjectTypeNd ) {
                        containerName = ((ObjectTypeNd) parent).getQualifiedName();
                    } else if ( parent instanceof AssociationNd) {
                        s_log.error("not implemented");
                    } else {
                        throw new IllegalStateException("can'g get here.");
                    }

                    if ( prop.isUnversioned() ) {
                        Property property = 
                            getProperty(containerName, prop.getName().getName());

                        s_log.info("onProperty: " + property);
                        m_unversionedProps.add(property);

                        if ( m_changeListener != null ) {
                            m_changeListener.onUnversionedProperty(property);
                        }
                    }
                }
            };
    }

    public static VersioningMetadata getVersioningMetadata() {
        return s_singleton;
    }

    public Node.Switch nodeSwitch() {
        return m_switch;
    }

    /**
     * <p>Returns <code>true</code> if the object type named by
     * <code>qualifiedName</code> is marked <code>versioned</code> in the PDL
     * definition.  Note that this a weaker test than checking of an object type
     * is versioned. For example, a type may be versioned if one of its ancestor
     * types is marked versioned.</p>
     *
     * <p>This method is provided for unit testing only.</p>
     *
     * @param qualifiedName the fully qualified name of an object type
     **/
    public boolean isMarkedVersioned(String qualifiedName) {
        return m_versionedTypes.contains(qualifiedName);
    }


    private static Property getProperty(String containerName,
                                        String propertyName) {

        ObjectType objType = MetadataRoot.getMetadataRoot().
            getObjectType(containerName);
        return objType.getProperty(propertyName);
    }

    /**
     * Returns <code>true</code> if the object type property whose name is
     * <code>qualifiedName</code> is marked <code>unversioned</code> in the PDL
     * definition.
     *
     * <p>This method is provided for unit testing only.</p>
     *
     * @param qualifiedName the fully qualified name of an object type property
     **/
    public boolean isMarkedUnversioned(String containerName, String propertyName) {
        return m_unversionedProps.contains
            (getProperty(containerName, propertyName));
    }

    /**
     * Adds a listener via which you can receive a callback whenever the
     * versioning metadata changes.
     **/
    public void registerChangeListener(ChangeListener listener) {
        if ( m_changeListener != null ) {
            throw new IllegalStateException
                ("Already registered " + m_changeListener);
        }
        Assert.exists(listener, ChangeListener.class);
        m_changeListener = listener;
    }

    /**
     * @see #addChangeListener(VersioningMetadata.ChangeListener)
     **/
    public interface ChangeListener {
        /**
         * This method is called whenever an object type node is traversed in
         * the PDL AST.
         *
         * @param objectTypeFQN the fully qualified name of the object
         **/
        void onObjectType(String objectTypeFQN, boolean isMarkedVersioned);

        /**
         * This method is called whenever we traverse a property node of the PDL
         * AST that is marked <code>unversioned</code>.
         **/
        void onUnversionedProperty(Property property);
    }
}

