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

import com.arsdigita.util.*;
import java.util.*;

/**
 * Switch
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #4 $ $Date: 2003/08/04 $
 **/

public abstract class Switch {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/util/cmd/Switch.java#4 $ by $Author: dennis $, $DateTime: 2003/08/04 16:15:53 $";

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
