/*
 * Copyright (C) 2001, 2002, 2003 Red Hat Inc. All Rights Reserved.
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

import com.redhat.persistence.metadata.Root;

import java.util.*;

/**
 * A Model provides a logical namespace for a related set of ObjectTypes and
 * Associations.
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #2 $ $Date: 2003/08/19 $
 */

public class Model extends Element {

    public final static String versionId = "$Id: //core-platform/test-packaging/src/com/arsdigita/persistence/metadata/Model.java#2 $ by $Author: rhs $, $DateTime: 2003/08/19 22:28:24 $";

    static Model wrap(Root root,
		      com.redhat.persistence.metadata.Model model) {
	if (model == null) {
	    return null;
	} else {
	    return new Model(root, model);
	}
    }

    private Root m_root;
    private com.redhat.persistence.metadata.Model m_model;

    /**
     * Constructs a new model with the given name.
     **/

    private Model(Root root,
		  com.redhat.persistence.metadata.Model model) {
        super(root, model);
	m_root = root;
        m_model = model;
    }


    /**
     * Returns the name of this Model.
     *
     * @return The name of this Model.
     **/

    public String getName() {
        return m_model.getQualifiedName();
    }


    /**
     * Returns the DataType with the given name.
     *
     * @param name The name of the datatype to get.
     *
     * @return The DataType with the given name.
     **/

    public DataType getDataType(String name) {
	return ObjectType.wrap
	    (m_root.getObjectType(m_model.getName() + "." + name));
    }


    /**
     * Returns true if this Model contains a DataType with the given name.
     *
     * @return True if this Model contains a DataType with the given name.
     **/

    public boolean hasDataType(String name) {
        return getDataType(name) != null;
    }


    /**
     * Returns the ObjectType with the given name.
     *
     * @param name The name of the ObjectType to get.
     *
     * @return The ObjectType with the given name.
     **/

    public ObjectType getObjectType(String name) {
	return (ObjectType) getDataType(name);
    }

    /**
     * Returns a collection of ObjectTypes that this Model contains
     *
     * @return a collection of ObjectTypes that this Model contains
     */
    public Collection getObjectTypes() {
        ArrayList result = new ArrayList();

	for (Iterator it = m_root.getObjectTypes().iterator();
	     it.hasNext(); ) {
	    com.redhat.persistence.metadata.ObjectType ot =
		(com.redhat.persistence.metadata.ObjectType)
		it.next();
	    if (ot.getModel().equals(m_model)) {
		result.add(ot);
	    }
	}

	return ObjectType.wrap(result);
    }

    public Set getAssociations() {
        throw new Error("not implemented");
    }

}
