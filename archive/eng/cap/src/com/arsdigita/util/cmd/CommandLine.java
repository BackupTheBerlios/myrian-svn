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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @deprecated Use CLI (http://jakarta.apache.org/commons/cli/index.html) instead.
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #2 $ $Date: 2004/08/30 $
 **/

public class CommandLine {

    public final static String versionId = "$Id: //eng/persistence/dev/cap/src/com/arsdigita/util/cmd/CommandLine.java#2 $ by $Author: dennis $, $DateTime: 2004/08/30 14:24:55 $";

    private String m_name;
    private String m_usage;
    private Map m_switches = new HashMap();

    public CommandLine(String name, String usage) {
        m_name = name;
        m_usage = usage;
    }

    public void addSwitch(Switch s) {
        m_switches.put(s.getName(), s);
    }

    public Switch getSwitch(String name) {
        return (Switch) m_switches.get(name);
    }

    public String[] parse(Map result, String[] argv) {
        for (Iterator it = m_switches.values().iterator(); it.hasNext(); ) {
            Switch s = (Switch) it.next();
            result.put(s.getName(), s.getDefault());
        }

        List args = new ArrayList();
        args.addAll(Arrays.asList(argv));
        List remaining = new ArrayList();
        while (args.size() > 0) {
            String arg = (String) args.get(0);
            if (arg.startsWith("-")) {
                if (m_switches.containsKey(arg)) {
                    Switch s = (Switch) m_switches.get(arg);
                    result.put(s.getName(), s.parse(this, args));
                } else {
                    throw new Error(usage());
                }
            } else {
                args.remove(0);
                remaining.add(arg);
            }
        }

        return (String[]) remaining.toArray(new String[0]);
    }

    public String usage() {
        StringBuffer result = new StringBuffer();

        result.append("Usage: " + m_name);

        if (m_usage != null) {
            result.append(" " + m_usage);
        }

        for (Iterator it = m_switches.values().iterator(); it.hasNext(); ) {
            Switch s = (Switch) it.next();
            result.append('\n');
            result.append(s.usage());
        }

        return result.toString();
    }

    public String usage(String name) {
        return getSwitch(name).usage();
    }

}
