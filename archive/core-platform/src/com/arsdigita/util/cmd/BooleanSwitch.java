package com.arsdigita.util.cmd;

/**
 * BooleanSwitch
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #1 $ $Date: 2002/08/27 $
 **/

public class BooleanSwitch extends Switch {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/util/cmd/BooleanSwitch.java#1 $ by $Author: rhs $, $DateTime: 2002/08/27 17:17:22 $";

    public BooleanSwitch(String name, String usage, Boolean defValue) {
        super(name, FLAG, usage, defValue);
    }

    protected Object decode(String[] values) {
        return Boolean.TRUE;
    }

}
