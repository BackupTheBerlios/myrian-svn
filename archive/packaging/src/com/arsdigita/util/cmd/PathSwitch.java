/*
 * Copyright (C) 2001, 2002, 2003 Red Hat Inc. All Rights Reserved.
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
import java.io.*;

/**
 * PathSwitch
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #2 $ $Date: 2003/08/19 $
 **/

public class PathSwitch extends Switch {

    public final static String versionId = "$Id: //core-platform/test-packaging/src/com/arsdigita/util/cmd/PathSwitch.java#2 $ by $Author: rhs $, $DateTime: 2003/08/19 22:28:24 $";

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
