package com.redhat.persistence.metadata;

import com.redhat.persistence.ProtoException;

/**
 * MetadataException
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/07/08 $
 **/

public class MetadataException extends ProtoException {

    public final static String versionId = "$Id: //core-platform/dev/src/com/redhat/persistence/metadata/MetadataException.java#1 $ by $Author: rhs $, $DateTime: 2003/07/08 21:04:28 $";

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
