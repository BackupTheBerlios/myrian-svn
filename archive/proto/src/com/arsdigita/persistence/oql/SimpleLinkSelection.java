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

package com.arsdigita.persistence.oql;

import com.arsdigita.persistence.metadata.Mapping;
import com.arsdigita.persistence.metadata.Property;

/**
 * SimpleLinkSelection extends Selection.  The only difference between
 * the two classes is the mapping.  Specifically, a SimpleLinkSelection
 * mapping typically will not have a full path but will simply have
 * the name.  This is used for selections such as simple link attributes
 * (link attriubtes of type Integer, String, etc).
 *
 * @author <a href="mailto:randyg@alum.mit.edu">randyg@alum.mit.edu</a>
 * @version $Revision: #1 $ $Date: 2002/11/27 $
 **/

class SimpleLinkSelection extends Selection {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/oql/SimpleLinkSelection.java#1 $ by $Author: dennis $, $DateTime: 2002/11/27 19:51:05 $";

    SimpleLinkSelection(Node node, Property property) {
        super(node, property);
    }

    /**
     * This returns the standard mapping with a slight twist.  That is,
     * instead of having a fully qualified path (e.g. "articles.caption")
     * it simply uses the attribute name (e.g. "caption");
     */
    public Mapping getMapping() {
        String path[] = {getProperty().getName()};
        Mapping mapping = new Mapping(path,
                                      getColumn().getTable().getAlias(),
                                      getAlias());
        mapping.setLineInfo(getProperty());
        return mapping;
    }
}
