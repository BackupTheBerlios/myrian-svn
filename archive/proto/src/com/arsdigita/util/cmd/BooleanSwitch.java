package com.arsdigita.util.cmd;

/**
 * BooleanSwitch
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #1 $ $Date: 2002/11/27 $
 **/

public class BooleanSwitch extends Switch {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/util/cmd/BooleanSwitch.java#1 $ by $Author: dennis $, $DateTime: 2002/11/27 19:51:05 $";

    public BooleanSwitch(String name, String usage, Boolean defValue) {
        super(name, FLAG, usage, defValue);
    }

    protected Object decode(String[] values) {
        return Boolean.TRUE;
    }

}
