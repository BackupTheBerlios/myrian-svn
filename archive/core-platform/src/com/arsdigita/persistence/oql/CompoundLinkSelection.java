package com.arsdigita.persistence.oql;

import com.arsdigita.persistence.metadata.Mapping;
import com.arsdigita.persistence.metadata.Property;
import com.arsdigita.persistence.metadata.Column;
import com.arsdigita.persistence.metadata.JoinElement;
import com.arsdigita.persistence.metadata.ObjectType;

import org.apache.log4j.Logger;
import java.util.Iterator;

/**
 * CompoundLinkSelection extends Selection.  The only difference between
 * the two classes is the mapping.  Specifically, a CompoundLinkSelection
 * mapping typically will not have a full path but will simply have
 * the name.  This is used for selections such as simple link attributes
 * (link attriubtes of type Integer, String, etc).
 *
 * @author <a href="mailto:randyg@alum.mit.edu">randyg@alum.mit.edu</a>
 * @version $Revision: #2 $ $Date: 2002/08/13 $
 **/

class CompoundLinkSelection extends Selection {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/oql/CompoundLinkSelection.java#2 $ by $Author: dennis $, $DateTime: 2002/08/13 11:53:00 $";

    private static final Logger s_log =
        Logger.getLogger(CompoundLinkSelection.class);

    private String m_path[];

    CompoundLinkSelection(Node node, Property property) {
        super(node, property);
        if (!property.isAttribute()) {

            Iterator i = ((ObjectType)property.getType()).getKeyProperties();
            String keyUsed = null;
            if (i.hasNext()) {
                Property keyProperty = (Property)i.next();
                keyUsed = keyProperty.getName();
                Iterator joinPath = property.getJoinPath()
                    .getJoinElements();
                // we want the second of the first 
                // element of the path
                Column from = ((JoinElement)joinPath.next()).getFrom();
                setProperty(new Property(property.getName() + "." +
                                     keyProperty.getName(), property.getType(),
                                     property.getMultiplicity(),
                                     property.isComponent()));
                getProperty().setColumn(from);
                m_path = new String[2];
                m_path[0] = property.getName();
                m_path[1] = keyProperty.getName();
            }

            if (i.hasNext()) {
                // if there is a second key then the code
                // may not work so we warn.
                s_log.warn
                    ("There are multiple keys for " +
                     "object type " + 
                     ((ObjectType)property.getType()).getName() +
                     ".  We are using key " + keyUsed + "." +
                     " This may or may not be appropriate. " +
                     "Please check to make sure that the " +
                     "generated SQL is correct.");
            }
        } else {
            // if there is not join path then there is
            // no metadata
            s_log.warn("No table/column definition for " +
                       "link attribute " + property.getName());
        }
    }


    /**
     * This returns the standard mapping with a slight twist.  That is,
     * instead of having a fully qualified path (e.g. "articles.caption")
     * it simply uses the attribute name (e.g. "caption");
     */
    public Mapping getMapping() {
        Mapping mapping = new Mapping(m_path,
                                      getColumn().getTable().getAlias(),
                                      getAlias());
        mapping.setLineInfo(getProperty());
        return mapping;
    }
}
