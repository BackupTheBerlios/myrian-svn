package org.myrian.persistence.pdl;

import org.myrian.persistence.metadata.Root;

import java.io.*;
import java.util.*;

import junit.framework.TestCase;

/**
 * PDLTest
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2004/10/04 $
 **/

abstract class PDLTest extends TestCase {


    private Map m_files = new LinkedHashMap();

    protected void line(String name, String line) {
        String file = (String) m_files.get(name);
        if (file == null) {
            m_files.put(name, line);
        } else {
            m_files.put(name, file + "\n" + line);
        }
    }

    protected void line(String line) {
        line("file", line);
    }

    protected Root parse() {
        PDL pdl = new PDL();

        for (Iterator it = m_files.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry me = (Map.Entry) it.next();
            String name = (String) me.getKey();
            String file = (String) me.getValue();
            pdl.load(new StringReader(file), name);
        }

        Root root = new Root();
        pdl.emit(root);
        return root;
    }

    protected void errorEquals(String error) {
        try {
            parse();
            fail("expected error: " + error);
        } catch (PDLException e) {
            assertEquals(error.trim(), e.getMessage().trim());
        }
    }

    protected String dump(Root root) {
        StringWriter w = new StringWriter();
        PDLWriter pw = new PDLWriter(w);
        pw.write(root);
        return w.toString();
    }

}
