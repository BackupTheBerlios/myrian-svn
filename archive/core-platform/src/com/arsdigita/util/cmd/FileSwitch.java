package com.arsdigita.util.cmd;

import java.io.*;

/**
 * FileSwitch
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #1 $ $Date: 2002/08/27 $
 **/

public class FileSwitch extends Switch {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/util/cmd/FileSwitch.java#1 $ by $Author: rhs $, $DateTime: 2002/08/27 17:17:22 $";

    public FileSwitch(String name, String usage, File defValue) {
        super(name, PARAMETER, usage, defValue);
    }

    protected Object decode(String[] values) {
        return new File(values[0]);
    }

}
