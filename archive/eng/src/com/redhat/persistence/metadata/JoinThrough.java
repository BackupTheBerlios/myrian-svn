/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the CCM Public
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the
 * License at http://www.redhat.com/licenses/ccmpl.html.
 *
 * Software distributed under the License is distributed on an
 * "AS IS" basis, WITHOUT WARRANTY OF ANY KIND, either express
 * or implied. See the License for the specific language
 * governing rights and limitations under the License.
 *
 */
package com.redhat.persistence.metadata;

import com.redhat.persistence.common.Path;

import java.util.*;

/**
 * JoinThrough
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2004/08/05 $
 **/

public class JoinThrough extends Mapping {

    public final static String versionId = "$Id: //eng/persistence/dev/src/com/redhat/persistence/metadata/JoinThrough.java#2 $ by $Author: rhs $, $DateTime: 2004/08/05 12:04:47 $";

    private ForeignKey m_from;
    private ForeignKey m_to;

    public JoinThrough(Path path, ForeignKey from, ForeignKey to) {
        super(path);
        m_from = from;
        m_to = to;
    }

    public List getColumns() {
        return Arrays.asList(m_to.getColumns());
    }

    public Table getTable() {
        return m_from.getUniqueKey().getTable();
    }

    public ForeignKey getFrom() {
        return m_from;
    }

    public ForeignKey getTo() {
        return m_to;
    }

    public void dispatch(Switch sw) {
        sw.onJoinThrough(this);
    }

}
