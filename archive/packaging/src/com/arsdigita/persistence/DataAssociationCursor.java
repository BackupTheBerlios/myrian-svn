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

/**
 *  DataAssociationCursor -
 * This is used to allow developers to iterate through the objects
 * within an association and get properties for those objects.
 * This does not implement java.util.Iterator because it is a cursor,
 * not an iterator.  That is, each row has properties but is not
 * actually an object
 * <p>
 *
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
 * @version $Revision: #1 $ $Date: 2003/08/14 $
 */

public interface DataAssociationCursor extends DataCollection {

    public static final String versionId = "$Id: //core-platform/test-packaging/src/com/arsdigita/persistence/DataAssociationCursor.java#1 $ by $Author: dennis $, $DateTime: 2003/08/14 14:53:20 $";

    /**
     * Returns a data association that created this iterator
     **/
    DataAssociation getDataAssociation();


    /**
     * Returns the link associated with the current row.
     *
     * @return The link.
     **/
    DataObject getLink();


    /**
     * Calls get("link." + name).
     *
     * @param name The name of the link property.
     *
     * @return The property value.
     */
    Object getLinkProperty(String name);


    /**
     * Removes the object associated with the current position in the
     * collection. Note that this has NO EFFECT on the underlying
     * DataAssociation until save() is called on the association's parent
     * DataObject
     */
    void remove();

}
