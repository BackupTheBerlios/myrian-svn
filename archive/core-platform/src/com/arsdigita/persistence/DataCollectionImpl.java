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

import com.arsdigita.persistence.metadata.ObjectType;
import com.arsdigita.persistence.metadata.Property;
import com.arsdigita.persistence.metadata.Operation;

import java.util.Iterator;
import java.util.Map;

/**
 * Title:       DataCollectionImpl class
 * Description: This class is similar to a cursor except that it
 *              conceptually represents a list of GenericDataObjects.
 *
 * Copyright:    Copyright (c) 2001
 * Company:      ArsDigita
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #4 $ $Date: 2002/08/14 $
 */

class DataCollectionImpl extends DataQueryImpl
    implements DataCollection {

    public static final String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/DataCollectionImpl.java#4 $ by $Author: dennis $, $DateTime: 2002/08/14 23:39:40 $";


    /**
     * Create a new DataCollectionImpl
     *
     * @param type The Object Type for this DataCollection.
     * @param op The operation used to execute this DataCollection.
     **/

    DataCollectionImpl(ObjectType type, Operation op) {
        super(type, op);
    }


    /**
     *  This returns the next DataObject in the "collection"
     **/

    public DataObject getDataObject() {
        Session ssn = SessionManager.getSession();
        DataObject data =
            GenericDataObjectFactory.createObject((ObjectType) m_type, ssn,
                                                  false);

        DataContainer dc = ((GenericDataObject) data).getDataContainer();
        Map props = getDataContainer().getProperties();
        for (Iterator it = props.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry me = (Map.Entry) it.next();
            dc.initProperty((String) me.getKey(), me.getValue());
        }

        return data;
    }


    /**
     *  This returns the ObjectType for the DataObjects returned by
     *  this "collection"
     **/

    public ObjectType getObjectType() {
        return (ObjectType) m_type;
    }


    /**
     * Allows a user to bind a parameter within a named query.
     *
     * @deprecated.  There is a raging debate about whether or not
     *  this should be deprecated.  One side says it should stay
     *  because it makes sense, the other side says it should go
     *  because Collections are types of Associations and that Associations
     *  should be pure "retrieve" calls without embedded parameters
     *  and the like.  If you find yourself using a parameter on
     *  a DataCollection, they you should rethink the design and maybe
     *  make it a DataQuery that returns DataObjects instead of
     *  a DataCollection.
     *
     * @param parameterName The name of the parameter to bind
     * @param value The value to assign to the parameter
     **/

    public void setParameter(String parameterName, Object value) {
        super.setParameter(parameterName, value);
    }


    /**
     * Allows a caller to get a parameter value for a parameter that
     * has already been set
     *
     * @deprecated.  There is a raging debate about whether or not
     *  this should be deprecated.  One side says it should stay
     *  because it makes sense, the other side says it should go
     *  because Collections are types of Associations and that Associations
     *  should be pure "retrieve" calls without embedded parameters
     *  and the like.  If you find yourself using a parameter on
     *  a DataCollection, they you should rethink the design and maybe
     *  make it a DataQuery that returns DataObjects instead of
     *  a DataCollection.
     *
     * @param parameterName The name of the parameter to retrieve
     * @return This returns the object representing the value of the
     * parameter specified by the name or "null" if the parameter value
     * has not yet been set.
     **/

    public Object getParameter(String parameterName) {
        return super.getParameter(parameterName);
    }

    public boolean contains(OID oid) {
        ObjectType myBase = getObjectType().getBasetype();
        ObjectType hisBase = oid.getObjectType().getBasetype();
        if (!myBase.equals(hisBase)) { return false; }

        for (Iterator it = myBase.getKeyProperties(); it.hasNext(); ) {
            Property prop = (Property) it.next();
            addEqualsFilter(prop.getName(), oid.get(prop.getName()));
        }

        try {
            return next();
        } finally {
            close();
        }
    }

    public boolean contains(DataObject data) {
        return contains(data.getOID());
    }

}
