/*
 * Copyright (C) 2003 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the CCM Public
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of
 * the License at http://www.redhat.com/licenses/ccmpl.html
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */

package com.redhat.persistence.pdl.nodes;

import java.util.*;

/**
 * AST
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2003/08/19 $
 **/

public class AST extends Node {

    public final static String versionId = "$Id: //core-platform/test-packaging/src/com/redhat/persistence/pdl/nodes/AST.java#2 $ by $Author: rhs $, $DateTime: 2003/08/19 22:28:24 $";

    public static final Field FILES =
        new Field(AST.class, "files", FileNd.class);

    public void dispatch(Switch sw) {
        super.dispatch(sw);
        sw.onAST(this);
    }

}
