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

import com.arsdigita.persistence.metadata.MetadataRoot;
import com.arsdigita.persistence.metadata.DataType;
import com.arsdigita.persistence.metadata.ObjectType;
import com.arsdigita.persistence.metadata.Property;
import java.util.Iterator;
import java.util.Collection;


/**
 * AbstractDataOperation provides a base class for both DataOperation
 * and DataQuery.  It provides functionality for binding arbitrary
 * variables as well as standard information such as setting the
 * source and retrieving the query.  The class is abstract because
 * it does not make any sense to instantiate this without other
 * methods (e.g. execute or executeQuery)
 *
 * @author <a href="mailto:randyg@arsdigita.com">randyg@arsdigita.com</a>
 * @version $Revision: #3 $ $Date: 2002/08/14 $
 */

abstract class AbstractDataOperation {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/AbstractDataOperation.java#3 $ by $Author: dennis $, $DateTime: 2002/08/14 23:39:40 $";

    protected ObjectType m_paramType = new ObjectType("<params>");
    protected DataContainer m_source = new DataContainer(m_paramType);


    /**
     * Gets the source data container this query will use.
     **/
    protected DataContainer getSource() {
        return m_source;
    }


    /**
     * Allows a user to bind a parameter within a named query.
     *
     * @param parameterName The name of the parameter to bind
     * @param value The value to assign to the parameter
     */
    public void setParameter(String parameterName, Object value) {
        m_paramType.addProperty(new Property(parameterName,
                                             getDataType(value)));
        m_source.set(parameterName, value);
    }


    /**
     * Allows a caller to get a parameter value for a parameter that
     * has already been set
     *
     * @param parameterName The name of the parameter to retrieve
     * @return This returns the object representing the value of the
     * parameter specified by the name or "null" if the parameter value
     * has not yet been set.
     */
    public Object getParameter(String parameterName) {
        return m_source.get(parameterName);
    }


    private static final DataType getDataType(Object object) {
        if (object instanceof DataObject) {
            return ((DataObject) object).getObjectType();
        } else if (object instanceof java.math.BigDecimal) {
            return MetadataRoot.BIGDECIMAL;
        } else if (object instanceof String || object == null) {
            return MetadataRoot.STRING;
        } else if (object instanceof Integer) {
            return MetadataRoot.INTEGER;
        } else if (object instanceof Boolean) {
            return MetadataRoot.BOOLEAN;
        } else if (object instanceof Byte) {
            return MetadataRoot.BYTE;
        } else if (object instanceof Character) {
            return MetadataRoot.CHARACTER;
        } else if (object instanceof java.util.Date) {
            return MetadataRoot.DATE;
        } else if (object instanceof Double) {
            return MetadataRoot.DOUBLE;
        } else if (object instanceof Float) {
            return MetadataRoot.FLOAT;
        } else if (object instanceof Long) {
            return MetadataRoot.LONG;
        } else if (object instanceof Short) {
            return MetadataRoot.SHORT;
        } else if (object instanceof java.math.BigInteger) {
            return MetadataRoot.BIGINTEGER;
        } else if (object instanceof java.util.Collection) {
            Iterator it = ((Collection) object).iterator();
            if (it.hasNext()) {
                return getDataType(it.next());
            } else {
                return getDataType(null);
            }
        } else {
            throw new PersistenceException("Unsupported Attribute class: " +
                                           object.getClass());
        }
    }
}
