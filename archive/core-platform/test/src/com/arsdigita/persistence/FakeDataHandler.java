/*
 * Copyright (C) 2001, 2002 Red Hat Inc. All Rights Reserved.
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

package com.arsdigita.persistence;

import java.util.*;

/**
 * FakeDataHandler
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #2 $ $Date: 2003/01/07 $
 **/

public class FakeDataHandler extends DataHandler {

    public final static String versionId = "$Id: //core-platform/dev/test/src/com/arsdigita/persistence/FakeDataHandler.java#2 $ by $Author: dennis $, $DateTime: 2003/01/07 14:51:38 $";

    public static final Set DELETED = new HashSet();

    public void doDelete(DataObject data) {
        DELETED.add(data);
        super.doDelete(data);
    }

}
