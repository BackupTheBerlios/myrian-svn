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

package com.arsdigita.persistence.pdl.ast;

import com.arsdigita.persistence.metadata.MetadataRoot;
import com.arsdigita.persistence.metadata.Model;
import com.arsdigita.persistence.metadata.ObjectType;

import java.util.*;

/**
 * A fully-qualified identifier (ie it includes a name, and potentially a
 * a scope).  If no scope is provided, the default scope is assumed.
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #3 $ $Date: 2002/08/14 $
 */

public class Identifier extends Element {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/pdl/ast/Identifier.java#3 $ by $Author: dennis $, $DateTime: 2002/08/14 23:39:40 $";

    // the identifier name
    private String m_name;

    // the path to the identifier, ie the scope
    private List m_path;

    /**
     * Create a new Identifier with the given name and path
     *
     * @param name the name of the identifier
     * @param path the path/scope to the identifier
     * @pre name != null && path != null
     */
    public Identifier(String name, List path) {
        m_name = name;
        m_path = path;
    }

    /**
     * Returns the name of the identifier
     *
     * @return the name of the identifier
     */
    public String getName() {
        return m_name;
    }

    // the Model this identifier belongs to
    private Model m_model = null;

    // the ObjectType this identifier belongs to
    private ObjectType m_type = null;

    /**
     * Returns the Model containing the object to which this Identifier
     * refers, as deduced from the path.
     *
     * @return The Model containing the object to which this Identifier
     *         refers, as deduced from the path.
     **/
    Model getResolvedModel() {
        return m_model;
    }

    /**
     * Returns the path-deduced ObjectType that this identifier refers to.
     *
     * @return The path-deduced ObjectType for this identifier.
     **/
    ObjectType getResolvedObjectType() {
        return m_type;
    }

    /**
     * Returns true if the object that this Identifier refers to exists in the
     * metadata.
     **/

    boolean exists() {
        MetadataRoot root = MetadataRoot.getMetadataRoot();
        for (int i = 0; i < m_path.size(); i++) {
            String elem = (String)m_path.get(i);
            ObjectType type = null;

            // check if we have an object type or model
            if (elem.endsWith(".*")) {
                elem = elem.substring(0, elem.length() - 2);

                type = root.getObjectType(elem + "." + m_name);
            } else if (elem.endsWith(m_name)) {
                type = root.getObjectType(elem);
            }

            if (type != null) {
                return true;
            }
        }

        return false;
    }

    /**
     * Deduces the ModelDef and ObjectDef for this identifier from the path,
     * also checking for some errors.
     */
    void resolve() {
        if (m_model != null)
            return;

        MetadataRoot root = MetadataRoot.getMetadataRoot();

        ObjectType result = null;

        List models = new ArrayList();

        for (int i = 0; i < m_path.size(); i++) {
            String elem = (String)m_path.get(i);
            ObjectType type;

            // check if we have an object type or model
            if (elem.endsWith(".*")) {
                elem = elem.substring(0, elem.length() - 2);

                Model m = root.getModel(elem);

                if (m == null) {
                    continue;
                }

                type = m.getObjectType(m_name);

                if (result == null) {
                    result = type;
                    m_model = m;
                }

                if (type != null) {
                    models.add(m.getName());
                }
            } else if (elem.endsWith(m_name)) {
                type = root.getObjectType(elem);

                if (type == null) {
                    continue;
                }

                if (result == null) {
                    result = type;
                    m_model = type.getModel();
                }

                if (type != null) {
                    models.add(m_model.getName());
                }
            }
        }

        if (result == null)
            error("No such type: " + m_name);

        if (models.size() > 1)
            error("Ambiguous type(" + m_name +
                  ") found in the following models: " + models);

        m_type = result;
    }

    /**
     * Returns a string representation of this object.
     *
     * @return a string representation of this object
     */
    public String toString() {
        StringBuffer sb = new StringBuffer();
        Iterator iter = m_path.iterator();

        while (iter.hasNext()) {
            sb.append((String)iter.next()).append(".");
        }

        sb.append(m_name);

        return sb.toString();
    }

}
