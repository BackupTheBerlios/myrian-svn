package com.redhat.persistence.oql;

import java.io.*;

/**
 * Indentor
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2004/01/16 $
 **/

class Indentor {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/redhat/persistence/oql/Indentor.java#1 $ by $Author: rhs $, $DateTime: 2004/01/16 16:27:01 $";

    int level = 0;

    private Writer m_out;
    private String m_indent;
    private boolean m_start;

    Indentor(Writer out, String indent) {
        m_out = out;
        m_indent = indent;
        m_start = true;
    }

    private void write(String str) {
        try { m_out.write(str); }
        catch (IOException e) {
            throw new IllegalStateException(e.getMessage());
        }
    }

    void print(String str) {
        if (m_start) {
            for (int i = 0; i < level; i++) {
                write(m_indent);
            }
            m_start = false;
        }
        write(str);
    }

    void println(String str) {
        print(str);
        println();
    }

    void println() {
        write(System.getProperty("line.separator"));
        m_start = true;
    }

}
