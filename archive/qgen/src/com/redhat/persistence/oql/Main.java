package com.redhat.persistence.oql;

import com.redhat.persistence.metadata.*;
import com.redhat.persistence.pdl.*;

import java.io.*;
import java.util.*;

/**
 * Main
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #5 $ $Date: 2004/01/27 $
 **/

public class Main {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/redhat/persistence/oql/Main.java#5 $ by $Author: rhs $, $DateTime: 2004/01/27 09:26:37 $";

    public static final void main(String[] args) throws Throwable {
        PDL pdl = new PDL();
        List expressions = new ArrayList();
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if (arg.endsWith(".pdl")) {
                pdl.load(new FileReader(arg), arg);
            } else {
                OQLParser p = new OQLParser(new FileReader(arg));
                while (true) {
                    Expression e = p.expression();
                    if (e == null) { break; }
                    expressions.add(e);
                }
            }
        }

        Root root = new Root();
        pdl.emit(root);

        Frame frame = Frame.root(root);

        for (Iterator it = expressions.iterator(); it.hasNext(); ) {
            Expression e = (Expression) it.next();
            //frame.graph(e);
            Code code = new Code(root);
            e.frame(code);
            e.emit(code);
            System.out.println(code.getSQL() + ";");
        }

        /*Propogator p = new Propogator();
        p.add(frame.type);
        p.propogate();

        Writer w = new OutputStreamWriter(System.out);
        frame.dump(new Indentor(w, "  "));
        w.flush();*/
    }

}
