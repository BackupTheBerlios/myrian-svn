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

/**
 * Defines a bind variable that is associated with a particular query,
 * including the Java type of that bind variable.
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #1 $ $Date: 2002/11/27 $
 */

public class BindingDef extends MapStatement {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/pdl/ast/BindingDef.java#1 $ by $Author: dennis $, $DateTime: 2002/11/27 19:51:05 $";

    // the variable name
    private String[] m_path;

    // the variable type
    private DataTypeDef m_type;

    /**
     * Create a new binding definition, with the given variable and type.
     *
     * @param var the variable name Identifier
     * @param type the type Identifier
     */
    public BindingDef(String[] path, DataTypeDef type) {
        m_path = path;
        m_type = type;
    }

    /**
     * Returns a string representation of this object.
     *
     * @return a string representation of this object
     */
    public String toString() {
        return m_path + ": " + m_type;
    }

    public String[] getPath() {
        return m_path;
    }

    public DataTypeDef getType() {
        return m_type;
    }

}
