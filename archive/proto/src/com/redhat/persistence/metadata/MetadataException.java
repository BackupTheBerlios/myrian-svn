package com.redhat.persistence.metadata;

import com.redhat.persistence.ProtoException;

/**
 * MetadataException
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2003/08/04 $
 **/

public class MetadataException extends ProtoException {

    public final static String versionId = "$Id: //core-platform/proto/src/com/redhat/persistence/metadata/MetadataException.java#2 $ by $Author: dennis $, $DateTime: 2003/08/04 16:15:53 $";

    private final Object m_element;

    public MetadataException(Object element, String msg) {
	super(message(element, msg), false);
        m_element = element;
    }

    private static String message(Object element, String msg) {
	Root root = Root.getRoot();
	if (root.hasLocation(element)) {
	    return root.getFilename(element) + ": line " +
		root.getLine(element) + ", column " + root.getColumn(element) +
		": " + msg;
	} else {
	    return msg;
	}
    }

    public Object getMetadataElement() {
        return m_element;
    }

}
