/*
 * Copyright (C) 2001 ArsDigita Corporation. All Rights Reserved.
 *
 * The contents of this file are subject to the ArsDigita Public 
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of
 * the License at http://www.arsdigita.com/ADPL.txt
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */

package com.arsdigita.persistence.metadata;

import java.util.Iterator;
import java.util.HashSet;
import java.util.Set;
import java.util.Map;
import java.util.HashMap;
import java.util.Collection;
import java.util.ArrayList;
import java.io.PrintStream;

/**
 * A Model provides a logical namespace for a related set of ObjectTypes and
 * Associations.
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #3 $ $Date: 2002/08/06 $
 */

public class Model extends Element {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/metadata/Model.java#3 $ by $Author: rhs $, $DateTime: 2002/08/06 16:54:58 $";

    /**
     * The name of the model.
     **/
    private String m_name;

    /**
     * The types this model contains.
     **/
    private Map m_types = new HashMap();

    /**
     * The associations this model contains.
     **/
    private Set m_assns = new HashSet();

    /**
     * The data operations this model contains.
     **/
    private Map m_opTypes = new HashMap();


    /**
     * Constructs a new model with the given name.
     **/

    public Model(String name) {
        m_name = name;
    }


    /**
     * Returns the name of this Model.
     *
     * @return The name of this Model.
     **/

    public String getName() {
        return m_name;
    }


    /**
     * Adds the given type to this Model.
     *
     * @param type The type to add.
     **/

    public void addDataType(DataType type) {
        if (hasDataType(type.getName())) {
            throw new IllegalArgumentException(
                "This Model already contains a type named " + type.getName()
                );
        }

        if (type.getModel() != null) {
            throw new IllegalArgumentException(
                "DataType " + type.getName() +
                " already belongs to Model " + type.getModel().getName()
                );
        }

        m_types.put(type.getName(), type);
        type.setModel(this);
    }


    /**
     * Returns the DataType with the given name.
     *
     * @param name The name of the datatype to get.
     *
     * @return The DataType with the given name.
     **/

    public DataType getDataType(String name) {
        Object result = m_types.get(name);
        if (result == null)
            result = caseInsensativeGet(m_types, name);
        return (DataType) result;
    }


    /**
     * Returns true if this Model contains a DataType with the given name.
     *
     * @return True if this Model contains a DataType with the given name.
     **/

    public boolean hasDataType(String name) {
        return m_types.containsKey(name);
    }


    /**
     * Returns the ObjectType with the given name.
     *
     * @param name The name of the ObjectType to get.
     *
     * @return The ObjectType with the given name.
     **/

    public ObjectType getObjectType(String name) {
        DataType result = getDataType(name);
        if (result != null && result instanceof ObjectType) {
            return (ObjectType) result;
        } else {
            return null;
        }
    }

    /**
     * Returns a collection of ObjectTypes that this Model contains
     *
     * @return a collection of ObjectTypes that this Model contains
     */
    public Collection getObjectTypes() {
        Iterator it = m_types.values().iterator();
        Collection retval = new ArrayList();

        while (it.hasNext()) {
            DataType dt = (DataType)it.next();

            if (dt instanceof ObjectType) {
                retval.add(dt);
            }
        }

        return retval;
    }


    /**
     * Returns the QueryType with the given name.
     *
     * @param name The name of the QueryType to get.
     *
     * @return The QueryType with the given name.
     **/

    public QueryType getQueryType(String name) {
        DataType result = getDataType(name);
        if (result != null && result instanceof QueryType) {
            return (QueryType) result;
        } else {
            return null;
        }
    }


    /**
     * Adds the given Association to this Model.
     *
     * @param assn The Association to add.
     **/

    public void addAssociation(Association assn) {
        m_assns.add(assn);
        assn.setModel(this);
    }

    public Set getAssociations() {
        return m_assns;
    }


    /**
     * Adds a DataOperationType to this Model.
     *
     * @param opType The DataOperationType to add.
     **/

    public void addDataOperationType(DataOperationType opType) {
        m_opTypes.put(opType.getName(), opType);
        opType.setModel(this);
    }

    /**
     * Returns the DataOperationType with the specified name.
     *
     * @param name The name of the DataOperationType.
     *
     * @return The DataOperationType with the specified name.
     **/

    public DataOperationType getDataOperationType(String name) {
        Object result = m_opTypes.get(name);
        if (result == null) {
            result = caseInsensativeGet(m_opTypes, name);
        }
        return (DataOperationType) result;
    }


    /**
     * Outputs a serialized representation of this Model on the given
     * PrintStream.
     *
     * The format used:
     *
     * <pre>
     *     "model" &lt;name&gt; ";"
     *
     *     &lt;datatypes&gt; ";"
     *
     *     &lt;associations&gt; ";"
     * </pre>
     *
     * @param out The PrintStream to use for output.
     **/

    public void outputPDL(PrintStream out) {
        out.println("model " + m_name + ";");

        for (Iterator it = m_types.values().iterator(); it.hasNext(); ) {
            out.println();
            Element el = (Element) it.next();
            el.outputPDL(out);
            out.println();
        }

        for (Iterator it = m_assns.iterator(); it.hasNext(); ) {
            out.println();
            Element el = (Element) it.next();
            el.outputPDL(out);
            out.println();
        }
    }

    public String toString() {
        return "Model: " + m_name;
    }
}
