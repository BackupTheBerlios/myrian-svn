package com.arsdigita.util.cmd;

import com.arsdigita.util.*;
import java.util.*;

/**
 * Switch
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #1 $ $Date: 2002/11/27 $
 **/

public abstract class Switch {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/util/cmd/Switch.java#1 $ by $Author: dennis $, $DateTime: 2002/11/27 19:51:05 $";

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
