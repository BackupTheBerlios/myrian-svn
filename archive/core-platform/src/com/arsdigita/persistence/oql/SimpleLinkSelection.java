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
 * @version $Revision: #1 $ $Date: 2002/08/06 $
 **/

class SimpleLinkSelection extends Selection {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/oql/SimpleLinkSelection.java#1 $ by $Author: randyg $, $DateTime: 2002/08/06 18:07:28 $";

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
