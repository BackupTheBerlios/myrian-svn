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

/**
 * @deprecated Use CLI (http://jakarta.apache.org/commons/cli/index.html) instead.
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #3 $ $Date: 2003/09/25 $
 **/

public class BooleanSwitch extends Switch {

    public final static String versionId = "$Id: //core-platform/test-packaging/src/com/arsdigita/util/cmd/BooleanSwitch.java#3 $ by $Author: justin $, $DateTime: 2003/09/25 14:54:00 $";

    public BooleanSwitch(String name, String usage, Boolean defValue) {
        super(name, FLAG, usage, defValue);
    }

    protected Object decode(String[] values) {
        return Boolean.TRUE;
    }

}
