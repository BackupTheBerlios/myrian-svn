/*
 * Copyright (C) 2004 Red Hat Inc. All Rights Reserved.
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

import java.io.*;

/**
 * Indentor
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2004/06/07 $
 **/

class Indentor {

    public final static String versionId = "$Id: //eng/persistence/dev/src/com/redhat/persistence/oql/Indentor.java#1 $ by $Author: rhs $, $DateTime: 2004/06/07 13:49:55 $";

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
