/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
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
 * @version $Revision: #4 $ $Date: 2004/08/16 $
 **/

public class Main {

    public final static String versionId = "$Id: //core-platform/dev/src/com/redhat/persistence/oql/Main.java#4 $ by $Author: dennis $, $DateTime: 2004/08/16 18:10:38 $";

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
