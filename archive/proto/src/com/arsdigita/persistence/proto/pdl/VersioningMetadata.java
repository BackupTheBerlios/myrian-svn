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

package com.arsdigita.persistence.proto.pdl;

import com.arsdigita.persistence.metadata.MetadataRoot;
import com.arsdigita.persistence.metadata.ObjectType;
import com.arsdigita.persistence.metadata.Property;
import com.arsdigita.persistence.proto.metadata.Role;
import com.arsdigita.persistence.proto.pdl.nodes.AssociationNd;
import com.arsdigita.persistence.proto.pdl.nodes.Node;
import com.arsdigita.persistence.proto.pdl.nodes.ObjectTypeNd;
import com.arsdigita.persistence.proto.pdl.nodes.PropertyNd;
import com.arsdigita.util.Assert;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

/**
 * Versioning metadata.
 *
 * @author Vadim Nasardinov (vadimn@redhat.com)
 * @since 2003-02-18
 * @version $Revision: #6 $ $Date: 2003/05/13 $
 */
public class VersioningMetadata {
    private final static Logger s_log =
        Logger.getLogger(VersioningMetadata.class);

    private final Set m_versionedTypes;
    private final Set m_unversionedProps;
    private NodeVisitor m_nodeVisitor;

    private final static VersioningMetadata s_singleton =
        new VersioningMetadata();

    private VersioningMetadata() {
        m_versionedTypes = new HashSet();
        m_unversionedProps = new HashSet();
    }

    public static VersioningMetadata getVersioningMetadata() {
        return s_singleton;
    }

    Node.Switch nodeSwitch(Map properties) {
        return new NodeSwitch(properties);
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
     * Adds a visitor via which you can receive a callback whenever the
     * versioning metadata changes.
     **/
    public void registerNodeVisitor(NodeVisitor visitor) {
        if ( m_nodeVisitor != null ) {
            throw new IllegalStateException
                ("Already registered " + m_nodeVisitor);
        }
        Assert.exists(visitor, NodeVisitor.class);
        m_nodeVisitor = visitor;
    }

    /**
     * @see #registerNodeVisitor(VersioningMetadata.NodeVisitor)
     **/
    public interface NodeVisitor {
        /**
         * This method is called whenever an object type node is traversed in
         * the PDL AST.  To reiterate, this method is called upon visiting any
         * object type, whereas {@link #onVersionedProperty(Property)} and
         * {@link #onUnversionedProperty(Property)} are only called for a subset
         * of property nodes.
         **/
        void onObjectType(ObjectType objType, boolean isMarkedVersioned);

        /**
         * This method is called whenever we traverse a property node of the PDL
         * AST that is marked <code>versioned</code>.
         **/
        void onVersionedProperty(Property property);

        /**
         * This method is called whenever we traverse a property node of the PDL
         * AST that is marked <code>unversioned</code>.
         **/
        void onUnversionedProperty(Property property);
    }

    private class NodeSwitch extends Node.Switch {
        private Map m_properties;

        public NodeSwitch(Map properties) {
            m_properties = properties;
        }

        public void onObjectType(ObjectTypeNd ot) {
            final String fqn = ot.getQualifiedName();

            if ( ot.isVersioned() ) {
                m_versionedTypes.add(fqn);
            }

            if ( m_nodeVisitor != null ) {
                // This returns null for things like "global.BigDecimal".
                ObjectType objType =
                    MetadataRoot.getMetadataRoot().getObjectType(fqn);

                if ( objType != null ) {
                    m_nodeVisitor.onObjectType(objType, ot.isVersioned());
                }
            }
        }

        public void onProperty(PropertyNd prop) {
            if ( !prop.isUnversioned() && !prop.isVersioned() ) return;

            String containerName = getContainerName(prop);
            Property property = 
                getProperty(containerName, prop.getName().getName());

            if ( property.isKeyProperty() ) {
                throw new IllegalStateException
                    ("Cannot mark a key property 'unversioned': " +
                     property);
            }
            m_unversionedProps.add(property);

            if ( m_nodeVisitor != null ) {
                if ( prop.isUnversioned() ) {
                    m_nodeVisitor.onUnversionedProperty(property);
                } else if ( prop.isVersioned() ) {
                    if ( property.getType().isSimple() ) {
                        throw new IllegalStateException
                            ("Simple properties are versioned by default. " +
                             "They cannot be marked 'versioned'. " + property);
                    }
                    m_nodeVisitor.onVersionedProperty(property);
                } else {
                    throw new IllegalStateException("es impossible");
                }
            }
        }

        private String getContainerName(PropertyNd prop) {
            Node parent = prop.getParent();
            if ( parent instanceof ObjectTypeNd ) {
                return ((ObjectTypeNd) parent).getQualifiedName();
            }

            Assert.truth(parent instanceof AssociationNd,
                         "parent instanceof AssociationNd");
            AssociationNd assoc = (AssociationNd) parent;

            PropertyNd other = null;
            if ( prop.equals(assoc.getRoleOne()) ) {
                other = assoc.getRoleTwo();
            } else if ( prop.equals(assoc.getRoleTwo()) ) {
                other = assoc.getRoleOne();
            } else {
                throw new IllegalStateException("can't get here");
            }

            Role role = (Role) m_properties.get(other);

            if ( role == null ) {
                throw new IllegalStateException
                    ("Failed to look up property node=" + other);
            }
            return role.getType().getQualifiedName();
        }
    }
}

