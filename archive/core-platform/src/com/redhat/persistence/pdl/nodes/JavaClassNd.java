/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the CCM Public
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the
 * License at http://www.redhat.com/licenses/ccmpl.html.
 *
 * Software distributed under the License is distributed on an
 * "AS IS" basis, WITHOUT WARRANTY OF ANY KIND, either express
 * or implied. See the License for the specific language
 * governing rights and limitations under the License.
 *
 */
package com.redhat.persistence.pdl.nodes;

/**
 * JavaClassNd
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #4 $ $Date: 2004/04/07 $
 **/

public class JavaClassNd extends Node {

    public final static String versionId = "$Id: //core-platform/dev/src/com/redhat/persistence/pdl/nodes/JavaClassNd.java#4 $ by $Author: dennis $, $DateTime: 2004/04/07 16:07:11 $";

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
