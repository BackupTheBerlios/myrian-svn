/*
 * Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the CCM Public
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the
 * License at http://www.redhat.com/licenses/ccmpl.html.
 *
 * Software distributed under the License is distributed on an
 * "AS IS" basis, WITHOUT WARRANTY OF ANY KIND, either express
 * or implied. See the License for the specific language
 * governing rights and limitations under the License.
 *
 */
package com.arsdigita.persistence.tests.data;

import com.arsdigita.util.*;
import com.arsdigita.persistence.metadata.*;

import java.util.*;

/**
 * ObjectTree
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #1 $ $Date: 2004/05/03 $
 **/

public class ObjectTree {

    public final static String versionId = "$Id: //users/rhs/persistence/cap/test/src/com/arsdigita/persistence/tests/data/ObjectTree.java#1 $ by $Author: rhs $, $DateTime: 2004/05/03 11:48:01 $";

    private ObjectType m_type;
    private ObjectTree m_parent;
    private Map m_children = new HashMap();
    private Set m_attributes = new HashSet();

    private ObjectTree(ObjectTree parent, ObjectType type) {
        m_parent = parent;
        m_type = type;
    }

    public ObjectTree(ObjectType type) {
        this(null, type);
    }

    public ObjectTree(ObjectType type, String[] paths) {
        this(type);

        for (int i = 0; i < paths.length; i++) {
            addPath(paths[i]);
        }
    }

    public ObjectTree getParent() {
        return m_parent;
    }

    public ObjectType getObjectType() {
        return m_type;
    }

    public ObjectTree getRoot() {
        if (m_parent == null) {
            return this;
        } else {
            return m_parent.getRoot();
        }
    }

    public void addPath(String path) {
        Assert.assertNotNull(path, "path");
        String[] parts = StringUtils.split(path, '.');
        addPath(parts);
    }

    public void addPath(String[] path) {
        Assert.assertNotNull(path, "path");
        Assert.assertTrue(path.length > 0);
        Property prop = m_type.getProperty(path[0]);

        if (prop.isAttribute()) {
            Assert.assertTrue(path.length == 1);
            m_attributes.add(prop);
            return;
        }

        ObjectTree subtree = getSubtree(path[0]);
        if (subtree == null) {
            subtree = new ObjectTree(this, (ObjectType) prop.getType());
            m_children.put(path[0], subtree);
        }

        if (path.length > 1) {
            subtree.addPath(subPath(path));
        }
    }

    public ObjectTree getSubtree(String path) {
        Assert.assertNotNull(path, "path");
        String[] parts = StringUtils.split(path, '.');
        return getSubtree(parts);
    }

    public ObjectTree getSubtree(String[] path) {
        Assert.assertNotNull(path, "path");
        Assert.assertTrue(path.length > 0);

        if (path.length == 1) {
            return (ObjectTree) m_children.get(path[0]);
        } else {
            return getSubtree(subPath(path));
        }
    }

    private static final String[] subPath(String[] path) {
        Assert.assertNotNull(path);
        Assert.assertTrue(path.length > 1);

        String[] result = new String[path.length - 1];
        for (int i = 0; i < result.length; i++) {
            result[i] = path[i + 1];
        }

        return result;
    }

    public Property getProperty(String path) {
        Assert.assertNotNull(path);
        return getProperty(StringUtils.split(path, '.'));
    }

    public Property getProperty(String[] path) {
        Assert.assertNotNull(path);
        Assert.assertTrue(path.length > 0);

        if (path.length == 1) {
            return m_type.getProperty(path[0]);
        } else {
            ObjectTree subtree = getSubtree(path[0]);
            if (subtree == null) {
                return null;
            } else {
                return subtree.getProperty(subPath(path));
            }
        }
    }

    private boolean m_nameValid = false;
    private String m_name;

    String getName() {
        if (m_nameValid) {
            return m_name;
        } else {
            if (m_parent == null) {
                m_name = null;
                m_nameValid = true;
                return m_name;
            } else {
                for (Iterator it = m_parent.m_children.entrySet().iterator();
                     it.hasNext(); ) {
                    Map.Entry me = (Map.Entry) it.next();
                    if (me.getValue().equals(this)) {
                        m_name = (String) me.getKey();
                        m_nameValid = true;
                        return m_name;
                    }
                }

                throw new IllegalStateException
                    ("Parent doesn't contain me. :(");
            }
        }
    }

    public String getAbsolutePath(String path) {
        if (m_parent == null) {
            return path;
        } else {
            return getPrefix() + "." + path;
        }
    }

    public String getAbsolutePath() {
        if (m_parent == null) {
            return "";
        } else {
            return m_parent.getAbsolutePath(getName());
        }
    }

    private String getPrefix() {
        if (m_parent == null) {
            return "";
        } else if (m_parent.m_parent == null) {
            return getName();
        } else {
            return  m_parent.getPrefix() + "." + getName();
        }
    }

    public Collection getSubtrees() {
        return m_children.values();
    }

    public Collection getAttributes() {
        return m_attributes;
    }

    private void print(StringBuffer result) {
        if (m_parent == null) {
            result.append(m_type.getQualifiedName() + ":\n");
        } else {
            result.append("  " + m_parent.getAbsolutePath(getName()) + "\n");
        }

        for (Iterator it = getAttributes().iterator(); it.hasNext(); ) {
            Property prop = (Property) it.next();
            result.append("  " + getAbsolutePath(prop.getName()) + "\n");
        }

        for (Iterator it = getSubtrees().iterator(); it.hasNext(); ) {
            ObjectTree subtree = (ObjectTree) it.next();
            subtree.print(result);
        }
    }

    public String toString() {
        StringBuffer result = new StringBuffer();
        print(result);
        return result.toString();
    }

}
