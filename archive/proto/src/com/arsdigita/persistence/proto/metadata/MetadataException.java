package com.arsdigita.persistence.proto.metadata;

/**
 * MetadataException
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/04/04 $
 **/

public class MetadataException extends RuntimeException {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/metadata/MetadataException.java#1 $ by $Author: rhs $, $DateTime: 2003/04/04 17:02:22 $";

    public MetadataException(Object element, String msg) {
	super(message(element, msg));
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

}
