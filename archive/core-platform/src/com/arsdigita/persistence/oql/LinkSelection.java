package com.arsdigita.persistence.oql;

import com.arsdigita.persistence.metadata.Mapping;
import com.arsdigita.persistence.metadata.Property;

/**
 * LinkSelection extends Selection.  The only difference between
 * the two classes is the mapping.  Specifically, a LinkSelection
 * mapping typically will not have a full path but will simply have
 * the name.
 *
 * @author <a href="mailto:randyg@alum.mit.edu">randyg@alum.mit.edu</a>
 * @version $Revision: #1 $ $Date: 2002/08/01 $
 **/

class LinkSelection extends Selection {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/oql/LinkSelection.java#1 $ by $Author: randyg $, $DateTime: 2002/08/01 11:13:21 $";

    LinkSelection(Node node, Property property) {
        super(node, property);
    }

    /**
     * This returns the standard mapping with a slight twist.  That is,
     * instead of having a fully qualified path (e.g. "articles.caption")
     * it simply uses the attribute name (e.g. "caption");
     */
    public Mapping getMapping() {
        com.arsdigita.persistence.metadata.Column col =
            new com.arsdigita.persistence.metadata.Column
            (getColumn().getTable().getAlias(), getAlias());
        col.setLineInfo(getProperty().getColumn());
        String path[] = {getProperty().getName()};
        Mapping mapping = new Mapping(path, col);
        mapping.setLineInfo(getProperty());
        return mapping;
    }
}
