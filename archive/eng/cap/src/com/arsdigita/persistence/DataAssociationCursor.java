/*
 * Copyright (C) 2001-2004 Red Hat, Inc.  All Rights Reserved.
 *
 * This program is Open Source software; you can redistribute it and/or
 * modify it under the terms of the Open Software License version 2.1 as
 * published by the Open Source Initiative.
 *
 * You should have received a copy of the Open Software License along
 * with this program; if not, you may obtain a copy of the Open Software
 * License version 2.1 from http://www.opensource.org/licenses/osl-2.1.php
 * or by writing the Open Source Initiative c/o Lawrence Rosen, Esq.,
 * 3001 King Ranch Road, Ukiah, CA 95482.
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
 * @version $Revision: #2 $ $Date: 2004/08/30 $
 */

public interface DataAssociationCursor extends DataCollection {

    public static final String versionId = "$Id: //eng/persistence/dev/cap/src/com/arsdigita/persistence/DataAssociationCursor.java#2 $ by $Author: dennis $, $DateTime: 2004/08/30 14:24:55 $";

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
