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

/**
 * Event processor.
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @since 2003-02-20
 * @version $Revision: #1 $ $Date: 2003/02/20 $
 **/
public abstract class EventProcessor {

    public final static String versionId =
        "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/EventProcessor.java#1 $" +
        " by $Author: vadim $, $DateTime: 2003/02/20 16:04:48 $";

    protected abstract void write(Event ev);

    protected abstract void flush();
}
