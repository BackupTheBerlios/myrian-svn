package com.redhat.persistence.oql;

import com.redhat.persistence.metadata.*;
import com.redhat.persistence.pdl.*;

import java.io.*;
import java.util.*;

/**
 * Main
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2004/03/11 $
 **/

public class Main {

    public final static String versionId = "$Id: //core-platform/dev/src/com/redhat/persistence/oql/Main.java#1 $ by $Author: vadim $, $DateTime: 2004/03/11 18:13:02 $";

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
