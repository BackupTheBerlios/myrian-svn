package com.redhat.persistence.pdl;

import com.redhat.persistence.metadata.Root;

import java.io.*;
import java.util.*;

import junit.framework.TestCase;

/**
 * ErrorTest
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2004/09/14 $
 **/

public class ErrorTest extends TestCase {

    public final static String versionId = "$Id: //eng/persistence/dev/test/src/com/redhat/persistence/pdl/ErrorTest.java#2 $ by $Author: rhs $, $DateTime: 2004/09/14 17:42:52 $";

    private Map m_files = new LinkedHashMap();

    private void line(String name, String line) {
        String file = (String) m_files.get(name);
        if (file == null) {
            m_files.put(name, line);
        } else {
            m_files.put(name, file + "\n" + line);
        }
    }

    private void line(String line) {
        line("file", line);
    }

    private void errorEquals(String error) {
        PDL pdl = new PDL();

        for (Iterator it = m_files.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry me = (Map.Entry) it.next();
            String name = (String) me.getKey();
            String file = (String) me.getValue();
            pdl.load(new StringReader(file), name);
        }

        Root root = new Root();
        try {
            pdl.emit(root);
            fail("expected error: " + error);
        } catch (PDLException e) {
            assertEquals(error.trim(), e.getMessage().trim());
        }
    }

    public void testNestedMapWithNonNestedType() {
        line("model test;");
        line("object type Test {");
        line("    Integer id = tests.id;");
        line("    Foo foo { a = test.a; b = test.b; };");
        line("}");
        line("object type Foo {");
        line("    String a;");
        line("    String b;");
        line("}");
        errorEquals
            ("file: line 4, column 13 [error]: can't nest a non nested type");
    }

}
