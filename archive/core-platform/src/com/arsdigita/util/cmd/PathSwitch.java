package com.arsdigita.util.cmd;

import com.arsdigita.util.*;
import java.io.*;

/**
 * PathSwitch
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #1 $ $Date: 2002/08/27 $
 **/

public class PathSwitch extends Switch {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/util/cmd/PathSwitch.java#1 $ by $Author: rhs $, $DateTime: 2002/08/27 17:17:22 $";

    public PathSwitch(String name, String usage, File[] defValue) {
        super(name, PARAMETER, usage, defValue);
    }

    protected Object decode(String[] values) {
        String[] path = StringUtils.split(values[0], ':');
        File[] result = new File[path.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = new File(path[i]);
        }

        return result;
    }

}
