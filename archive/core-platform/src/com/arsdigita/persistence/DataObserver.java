/*
 * Copyright (C) 2001-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.persistence;

/**
 * DataObserver
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #6 $ $Date: 2004/03/30 $
 **/

public abstract class DataObserver {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/DataObserver.java#6 $ by $Author: dennis $, $DateTime: 2004/03/30 17:47:27 $";

    public void set(DataObject object, String property, Object previous,
                    Object value) {}

    public void add(DataObject object, String property, DataObject value) {}

    public void remove(DataObject object, String property, DataObject value) {}

    public void clear(DataObject object, String property) {}

    public void beforeSave(DataObject object) {}

    public void afterSave(DataObject object) {}

    public void beforeDelete(DataObject object) {}

    public void afterDelete(DataObject object) {}

}
