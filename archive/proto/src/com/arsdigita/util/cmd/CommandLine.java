/*
 * Copyright (C) 2001, 2002 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the CCM Public
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of
 * the License at http://www.redhat.com/licenses/ccmpl.html
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */

package com.arsdigita.util.cmd;

import java.util.*;

/**
 * CommandLine
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #2 $ $Date: 2003/04/09 $
 **/

public class CommandLine {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/util/cmd/CommandLine.java#2 $ by $Author: rhs $, $DateTime: 2003/04/09 16:35:55 $";

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
