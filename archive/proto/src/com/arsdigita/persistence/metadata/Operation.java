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

import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.io.PrintStream;

/**
 * The Operation class represents a database I/O operation.
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #1 $ $Date: 2002/11/27 $
 **/

public class Operation extends Element {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/metadata/Operation.java#1 $ by $Author: dennis $, $DateTime: 2002/11/27 19:51:05 $";

    /**
     * The sql for executing this operation.
     **/
    private String m_sql;
    private Map m_bindTypes = new HashMap();
    private boolean m_isCallableStatement = false;

    /**
     * The mappings contained by this operation.
     **/
    private List m_mappings = new ArrayList();
    private Map m_mappingsMap = new HashMap();


    /**
     * Constructs a new Operation with the given sql.
     *
     * @param sql The sql for executing the operation.
     **/

    public Operation(String sql) {
        m_sql = sql;
    }


    /**
     * Returns the sql for executing this operation.
     *
     * @return The sql for executing this operation.
     **/

    public String getSQL() {
        return m_sql;
    }

    /**
     * Sets the SQL that this Operation executes.
     *
     * @param sql the new SQL command
     */
    public void setSQL(String sql) {
        m_sql = sql;
    }


    // This is only an instance variable to avoid repeated object creation
    // overhead.
    private final StringBuffer m_key = new StringBuffer();

    private final String makeKey(String[] path) {
        m_key.setLength(0);

        for (int i = 0; i < path.length - 1; i++) {
            m_key.append(path[i]);
            m_key.append('.');
        }

        m_key.append(path[path.length - 1]);

        return m_key.toString();
    }


    /**
     * Returns true if this operation locally overrides the bind type for the
     * given path.
     **/

    public boolean hasBindType(String[] path) {
        return m_bindTypes.containsKey(makeKey(path));
    }


    /**
     * Returns the bind type for the given variable
     */
    public int getBindType(String variableName) {
        return getBindType(new String[] {variableName});
    }


    /**
     * Returns the bind type for the given path.
     **/
    public int getBindType(String[] path) {
        Integer result = (Integer) m_bindTypes.get(makeKey(path));
        if (result != null) {
            return result.intValue();
        } else {
            return Integer.MIN_VALUE;
        }
    }


    /**
     * Sets the bind type for the given path.
     **/

    public void setBindType(String[] path, int jdbcType) {
        m_bindTypes.put(makeKey(path), new Integer(jdbcType));
    }


    /**
     * Adds a Mapping to this Operation.
     *
     * @param mapping The mapping to add.
     **/

    public void addMapping(Mapping mapping) {
        m_mappings.add(mapping);
        m_mappingsMap.put(makeKey(mapping.getPath()), mapping);
    }

    void removeMapping(Mapping mapping) {
        m_mappings.remove(mapping);
        m_mappingsMap.remove(makeKey(mapping.getPath()));
    };


    /**
     * Returns true if this operation contains a mapping for the given path.
     *
     * @param path A string array identifying the given path.
     **/

    public boolean hasMapping(String[] path) {
        return m_mappingsMap.containsKey(makeKey(path));
    }


    /**
     * Returns the mapping for the given path.
     *
     * @return The mapping for the given path.
     **/

    public Mapping getMapping(String[] path) {
        return (Mapping) m_mappingsMap.get(makeKey(path));
    }


    /**
     * Returns an Iterator with all the Mappings that this Operation contains.
     *
     * @return An Iterator of Mapping objects.
     *
     * @see Mapping
     **/

    public Iterator getMappings() {
        return m_mappings.iterator();
    }

    void outputPDL(PrintStream out) {
        if (isCallableStatement()) {
            out.println("        do call {");
        } else {
            out.println("        do {");
        }
        out.print(m_sql);
        out.print("        }");
        // TODO: need to print out the bind types in the mapping
        // section
        if (m_mappings.size() > 0) {
            out.println(" map {");

            for (int i = 0; i < m_mappings.size(); i++) {
                Element el = (Element) m_mappings.get(i);
                out.print("            ");
                el.outputPDL(out);
                out.println(";");
            }

            out.print("        }");
        }
    }


    /**
     *  This indicates whether a CallableStatement or a PreparedStatement
     *  should be used when executing the operation
     */
    public boolean isCallableStatement() {
        return m_isCallableStatement;
    }

    /**
     *  @param isCallable This dictates whether the statement is
     *   CallableStatement or a PreparedStatement should be used when
     *   executing the operation
     */
    public void setCallableStatement(boolean isCallable) {
        m_isCallableStatement = isCallable;
    }
}
