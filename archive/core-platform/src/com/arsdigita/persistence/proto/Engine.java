/*
 * Copyright (C) 2003 Red Hat Inc. All Rights Reserved.
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
 */

package com.arsdigita.persistence.proto;

import com.arsdigita.persistence.proto.metadata.Property;

/**
 * Engine
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/05/12 $
 **/

public abstract class Engine extends EventProcessor {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/proto/Engine.java#1 $ by $Author: ashah $, $DateTime: 2003/05/12 18:19:45 $";

    protected abstract RecordSet execute(Query query);
}
