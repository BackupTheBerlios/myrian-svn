/*
 * Copyright (C) 2004 Red Hat, Inc.  All Rights Reserved.
 *
 * This program is Open Source software; you can redistribute it and/or
 * modify it under the terms of the Open Software License version 2.1 as
 * published by the Open Source Initiative.
 *
 * You should have received a copy of the Open Software License along
 * with this program; if not, you may obtain a copy of the Open Software
 * License version 2.1 from http://www.opensource.org/licenses/osl-2.1.php
 * or by writing the Open Source Initiative c/o Lawrence Rosen, Esq.,
 * 3001 King Ranch Road, Ukiah, CA 95482.
 */
package com.redhat.persistence.oql;

import java.io.*;

/**
 * Indentor
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2004/08/30 $
 **/

class Indentor {

    public final static String versionId = "$Id: //eng/persistence/dev/src/com/redhat/persistence/oql/Indentor.java#2 $ by $Author: dennis $, $DateTime: 2004/08/30 14:24:55 $";

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
