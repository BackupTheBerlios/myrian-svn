package com.redhat.persistence.pdl.nodes;

/**
 * JavaClassNd
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/08/14 $
 **/

public class JavaClassNd extends Node {

    public final static String versionId = "$Id: //core-platform/test-packaging/src/com/redhat/persistence/pdl/nodes/JavaClassNd.java#1 $ by $Author: dennis $, $DateTime: 2003/08/14 14:53:20 $";

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
