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
package com.redhat.persistence.oql;

import com.redhat.persistence.metadata.*;
import com.redhat.persistence.pdl.*;

import java.io.*;
import java.util.*;

/**
 * Main
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #3 $ $Date: 2004/04/07 $
 **/

public class Main {

    public final static String versionId = "$Id: //core-platform/dev/src/com/redhat/persistence/oql/Main.java#3 $ by $Author: dennis $, $DateTime: 2004/04/07 16:07:11 $";

    public static final void main(String[] args) throws Throwable {
        PDL pdl = new PDL();
        List queries = new ArrayList();
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if (arg.endsWith(".pdl")) {
                pdl.load(new FileReader(arg), arg);
            } else {
                OQLParser p = new OQLParser(new FileReader(arg));
                while (true) {
                    Query q = p.query();
                    if (q == null) { break; }
                    queries.add(q);
                }
            }
        }

        Root root = new Root();
        pdl.emit(root);

        for (Iterator it = queries.iterator(); it.hasNext(); ) {
            Query q = (Query) it.next();
            System.out.println(q.generate(root) + ";");
        }
    }

}
