package com.arsdigita.persistence.proto.pdl.nodes;

/**
 * JavaClassNd
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/05/12 $
 **/

public class JavaClassNd extends Node {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/proto/pdl/nodes/JavaClassNd.java#1 $ by $Author: ashah $, $DateTime: 2003/05/12 18:19:45 $";

    public static final Field IDENTIFIERS =
        new Field(JavaClassNd.class, "identifiers", IdentifierNd.class, 1);

    public void dispatch(Switch sw) {
        super.dispatch(sw);
        sw.onJavaClass(this);
    }

    public String getName() {
	final StringBuffer name = new StringBuffer();
	traverse(new Switch() {
		public void onIdentifier(IdentifierNd id) {
		    if (name.length() > 0) {
			name.append('.');
		    }
		    name.append(id.getName());
		}
	    });
	return name.toString();
    }

}
