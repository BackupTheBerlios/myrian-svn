/*
 * Copyright (C) 2002-2004 Red Hat, Inc.  All Rights Reserved.
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
package com.arsdigita.util.cmd;

import com.arsdigita.util.Assert;
import java.util.List;

/**
 * @deprecated Use CLI (http://jakarta.apache.org/commons/cli/index.html) instead.
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #2 $ $Date: 2004/08/30 $
 **/

public abstract class Switch {

    public final static String versionId = "$Id: //eng/persistence/dev/cap/src/com/arsdigita/util/cmd/Switch.java#2 $ by $Author: dennis $, $DateTime: 2004/08/30 14:24:55 $";

    public static abstract class Type {
        abstract String[] parse(CommandLine cmd, List args);
    }

    public static final Type FLAG = new Type() {
            String[] parse(CommandLine cmd, List args) {
                return null;
            }
        };
    public static final Type PARAMETER = new Type() {
            String[] parse(CommandLine cmd, List args) {
                return new String[] { (String) args.remove(0) };
            }
        };

    private String m_name;
    private Type m_type;
    private String m_usage;
    private Object m_default;

    protected Switch(String name, Type type, String usage, Object defValue) {
        m_name = name;
        m_type = type;
        m_usage = usage;
        m_default = defValue;
    }

    public String getName() {
        return m_name;
    }

    public Type getType() {
        return m_type;
    }

    public String getUsage() {
        return m_usage;
    }

    public String usage() {
        String line = "    " + m_name;
        StringBuffer result = new StringBuffer(line);
        for (int i = 0; i < 26 - line.length(); i++) {
            result.append(' ');
        }
        result.append(m_usage);
        return result.toString();
    }

    public Object getDefault() {
        return m_default;
    }

    Object parse(CommandLine cmd, List args) {
        Assert.assertEquals(m_name, args.get(0));
        args.remove(0);

        String[] values = m_type.parse(cmd, args);
        return decode(values);
    }

    protected abstract Object decode(String[] values);

}
