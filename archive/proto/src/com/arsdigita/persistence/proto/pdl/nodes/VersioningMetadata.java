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
import com.arsdigita.util.Assert;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

/**
 * Versioning metadata.
 *
 * @author Vadim Nasardinov (vadimn@redhat.com)
 * @since 2003-02-18
 * @version $Revision: #4 $ $Date: 2003/02/21 $
 */
public class VersioningMetadata {
    private final static Logger LOG =
        Logger.getLogger(VersioningMetadata.class);

    private final Node.Switch m_switch;
    private final Set m_versionedTypes;

    /**
     * The reason for this change listener is to avoid tight coupling between
     * the versioning and persistence packages. The versioning package must have
     * some sort of access to the PDL abstract syntax tree in order to be able
     * to determine whether or not an object type is marked "versioned".
     *
     * <p>The easiest route would be to add a boolean method isMarkedVersioned()
     * to the ObjectType class. However, we want to try to avoid joining
     * versioning and persistence at the hip like that. Therefore, instead of
     * adding said boolean method to the ObjectType class, we use this class -
     * i.e. VersioningMetadata - as a communication medium.</p>
     *
     * <p>The idea is that the PDL parser will indirectly expose its underlying
     * PDL AST to this class via the callback provided by nodeSwitch(). This
     * will enable this class to keep track of object types that are marked
     * versioned. However, the versioning package must be made aware of any
     * changes in the object type metadata. Such changes may occur as new object
     * types are added, e.g. as a result of creating a user-defined content-type
     * at runtime. </p>
     **/
    private final List m_changeListeners;

    private final static VersioningMetadata SINGLETON =
        new VersioningMetadata();

    private VersioningMetadata() {
        m_versionedTypes = new HashSet();
        m_changeListeners = new ArrayList();
        m_switch = new Node.Switch() {
                public void onObjectType(ObjectTypeNd ot) {
                    if ( ot.getVersioned() != null ) {
                        m_versionedTypes.add(ot.getQualifiedName());
                        LOG.info("onObjectType");
                        Iterator ii = m_changeListeners.iterator();
                        while ( ii.hasNext() ) {
                            LOG.info("calling the next change listener");
                            ((ChangeListener) ii.next()).onChange();
                        }
                    }
                }
            };
    }

    public static VersioningMetadata getVersioningMetadata() {
        return SINGLETON;
    }

    public Node.Switch nodeSwitch() {
        return m_switch;
    }

    /**
     * Returns <code>true</code> if the object type named by
     * <code>qualifiedName</code> is marked versioned in the PDL definition.
     * Note that this a weaker test than checking of an object type is
     * versioned. A type is versioned if is marked versioned or if one its
     * ancestor types is marked versioned.
     *
     * @param qualifiedName the fully qualified name of an object type
     **/
    public boolean isMarkedVersioned(String qualifiedName) {
        return m_versionedTypes.contains(qualifiedName);
    }

    /**
     * Adds a listener via which you can receive a callback whenever the
     * versioning metadata changes.
     **/
    public void addChangeListener(ChangeListener listener) {
        Assert.assertNotNull(listener, "listener");
        m_changeListeners.add(listener);
    }

    /**
     * @see #addChangeListener(VersioningMetadata.ChangeListener)
     **/
    public interface ChangeListener {
        void onChange();
    }
}

