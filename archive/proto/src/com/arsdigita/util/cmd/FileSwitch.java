package com.arsdigita.util.cmd;

import java.io.*;

/**
 * FileSwitch
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #1 $ $Date: 2002/11/27 $
 **/

public class FileSwitch extends Switch {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/util/cmd/FileSwitch.java#1 $ by $Author: dennis $, $DateTime: 2002/11/27 19:51:05 $";

    public FileSwitch(String name, String usage, File defValue) {
        super(name, PARAMETER, usage, defValue);
    }

    protected Object decode(String[] values) {
        return new File(values[0]);
    }

}
