package com.arsdigita.util.cmd;

/**
 * StringSwitch
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #1 $ $Date: 2002/11/27 $
 **/

public class StringSwitch extends Switch {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/util/cmd/StringSwitch.java#1 $ by $Author: dennis $, $DateTime: 2002/11/27 19:51:05 $";

    public StringSwitch(String name, String usage, String defValue) {
        super(name, PARAMETER, usage, defValue);
    }

    protected Object decode(String[] values) {
        return values[0];
    }

}

