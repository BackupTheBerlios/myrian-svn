package com.arsdigita.persistence.proto.metadata;

import com.arsdigita.persistence.proto.ProtoException;

/**
 * MetadataException
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2003/07/02 $
 **/

public class MetadataException extends ProtoException {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/proto/metadata/MetadataException.java#2 $ by $Author: ashah $, $DateTime: 2003/07/02 17:18:32 $";

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
