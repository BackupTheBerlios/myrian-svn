/*
 * Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the Open Software License v2.1
 * (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * http://rhea.redhat.com/licenses/osl2.1.html.
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */
package com.arsdigita.util.cmd;

import com.arsdigita.util.StringUtils;
import java.io.File;

/**
 * @deprecated Use CLI (http://jakarta.apache.org/commons/cli/index.html) instead.
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #6 $ $Date: 2004/03/30 $
 **/

public class PathSwitch extends Switch {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/util/cmd/PathSwitch.java#6 $ by $Author: dennis $, $DateTime: 2004/03/30 17:47:27 $";

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
