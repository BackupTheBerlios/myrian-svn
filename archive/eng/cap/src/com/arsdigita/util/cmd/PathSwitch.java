/*
 * Copyright (C) 2002-2004 Red Hat, Inc.  All Rights Reserved.
 *
 * This program is Open Source software; you can redistribute it and/or
 * modify it under the terms of the Open Software License version 2.1 as
 * published by the Open Source Initiative.
 *
 * You should have received a copy of the Open Software License along
 * with this program; if not, you may obtain a copy of the Open Software
 * License version 2.1 from http://www.opensource.org/licenses/osl-2.1.php
 * or by writing the Open Source Initiative c/o Lawrence Rosen, Esq.,
 * 3001 King Ranch Road, Ukiah, CA 95482.
 */
package com.arsdigita.util.cmd;

import com.arsdigita.util.StringUtils;
import java.io.File;

/**
 * @deprecated Use CLI (http://jakarta.apache.org/commons/cli/index.html) instead.
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #2 $ $Date: 2004/08/30 $
 **/

public class PathSwitch extends Switch {

    public final static String versionId = "$Id: //eng/persistence/dev/cap/src/com/arsdigita/util/cmd/PathSwitch.java#2 $ by $Author: dennis $, $DateTime: 2004/08/30 14:24:55 $";

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
