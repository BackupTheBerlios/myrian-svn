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

import com.arsdigita.persistence.metadata.CompoundType;
import com.arsdigita.persistence.metadata.ObjectType;
import com.arsdigita.persistence.metadata.Property;
import com.arsdigita.persistence.metadata.Operation;

import java.util.Iterator;
import java.sql.ResultSet;

import org.apache.log4j.Logger;


/**
 *  DataAssociationCursor -
 * This is used to allow developers to iterate through the objects
 * within an association and get properties for those objects.
 * This does not implement java.util.Iterator because it is a cursor,
 * not an iterator.  That is, each row has properties but is not
 * actually an object
 *
 * <p>
 * This is typically used when the developer wants to iterator through
 * the objects within an association.  In the sample of code below,
 * the method gets the cursor from the association, filters the cursor
 * so that it only returns the first N articles and then puts those N
 * articles, into a list to be returned.  </p>
 *
 * <pre><code>
 * public Collection getArticles(int numberOfArticles) {
 *     LinkedList articles = new LinkedList();
 *     DataAssociationCursor cursor = ((DataAssociation) get("articles")).cursor();
 *     cursor.addFilter(cursor.getFilterFactory().lessThan("rownum",
 *                                                         numberOfArticles, true));
 *     while (cursor.next()) {
 *         articles.addLast(cursor.getDataObject());
 *     }
 *
 *     cursor.close();
 *     return children;
 * }
 *</code></pre>
 * <p>
 * Note that it is important to close the cursor explicitly to return
 * the proper database resources as soon as possible.
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @author <a href="mailto:randyg@alum.mit.edu">randyg@alum.mit.edu</a>
 * @version $Revision: #7 $ $Date: 2003/03/07 $
 */

class DataAssociationCursorImpl extends DataCollectionImpl
    implements DataAssociationCursor {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/DataAssociationCursorImpl.java#7 $ by $Author: jorris $, $DateTime: 2003/03/07 01:57:54 $";

    private static final Logger s_cat =
        Logger.getLogger(DataAssociationImpl.class);

    private GenericDataObject m_parent;
    private String m_role;

    private DataAssociationImpl m_dataAssociation;

    /**
     *  Create a new DataAssociationImpl
     *
     *  @param parent The parent GenericDataObject for the Association
     *  @param role The role that is being created
     */
    DataAssociationCursorImpl(GenericDataObject parent,
                              String role, DataAssociation data,
                              Operation op) {
        super(queryType(parent.getObjectType(), role), op);

        m_parent = parent;
        m_role = role;
        m_dataAssociation = (DataAssociationImpl) data;

        alias("link.*", "*");
        alias("*", m_role + ".*");
    }

    private static final ObjectType queryType(ObjectType type, String role) {
        Property prop = type.getProperty(role);
        ObjectType linkType = (ObjectType) prop.getLinkType();
        if (linkType != null) {
            return linkType;
        } else {
            return type;
        }
    }

    public CompoundType getType() {
        return getObjectType();
    }

    public ObjectType getObjectType() {
        return (ObjectType) m_parent.getObjectType()
            .getProperty(m_role).getType();
    }

    public boolean hasProperty(String propertyName) {
        return super.hasProperty(propertyName);
    }

    synchronized ResultSet executeQuery(boolean count) {
        Property role = m_parent.getObjectType().getProperty(m_role);
        Property ass = role.getAssociatedProperty();
        if (ass != null) {
            setParameter(ass.getName(), m_parent);
        }

        for (Iterator it = m_parent.getObjectType().getKeyProperties();
             it.hasNext(); ) {
            Property prop = (Property) it.next();
            setParameter(prop.getName(), m_parent.get(prop.getName()));
        }

        return super.executeQuery(count);
    }

    /**
     *  @return The DataAssociation that was used to create this iterator
     */
    public DataAssociation getDataAssociation() {
        return m_dataAssociation;
    }

    /**
     *  This returns the Link that represents the actual association between
     *  the two objects within the association
     */
    public DataObject getLink() {
        Property prop = m_parent.getObjectType().getProperty(m_role);
        Property associated = prop.getAssociatedProperty();
        if (associated == null) {
            return null;
        } else {
            DataObject link = super.getDataObject();
            ((GenericDataObject) link).getDataContainer().initProperty(
                                                                       associated.getName(), m_parent
                                                                       );
            m_dataAssociation.addFetched(link);
            return link;
        }
    }


    /**
     *  This returns the Link Property specified by the passed in parameter.
     *  For instance, if there is a sortKey specifying how to sort
     *  the association, calling getLinkProperty("sortKey") would return
     *  the sortKey for the given Association.
     *
     *  @param name The name of the Link Property to return
     *  @return The Link Property specified by the parameter
     */
    public Object getLinkProperty(String name) {
        return super.get(name);
    }


    /**
     *  Removes the object associated with the current position in the
     *  collection.
     */
    public void remove() {
        getDataAssociation().remove(getDataObject());
    }


    public DataObject getDataObject() {
        Property prop = m_parent.getObjectType().getProperty(m_role);

        DataObject link = getLink();
        DataObject result;

        if (link == null) {
            // This get() is an ugly hack. We need to reconcile the
            // way associations with link attributes and the way
            // associations without link attributes are handled.
            result = (DataObject) super.getDataObject().get(m_role);
            if (prop.isComponent()) {
                m_dataAssociation.addFetched(result);
            }
        } else {
            result = (DataObject) link.get(m_role);
        }
        return result;
    }


    /**
     *  This is a simple string representation of DataAssociationCursorImpl.
     *  For something more involved, you should use
     *  {@link #toString(Set s, int level)}
     *
     *  This is not complex because if it were, it could be possible
     *  to create an infinite loop
     *
     *  <p> Since this is a cross between a DataQuery and a DataAssociation,
     *  it prints out both toStrings
     */
    public String toString() {
        return "DataAssociationCursorImpl: " + Utilities.LINE_BREAK +
            super.toString() + Utilities.LINE_BREAK +
            getDataAssociation().toString();
    }

}
