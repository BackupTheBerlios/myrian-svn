/*
 * Copyright (C) 2004 Red Hat Inc. All Rights Reserved.
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
package com.redhat.persistence.oql;

/**
 * Limit
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #3 $ $Date: 2004/04/07 $
 **/

public class Limit extends Range {

    public final static String versionId = "$Id: //core-platform/dev/src/com/redhat/persistence/oql/Limit.java#3 $ by $Author: dennis $, $DateTime: 2004/04/07 16:07:11 $";

    public Limit(Expression query, Expression limit) {
        super(query, limit);
    }

    void frame(Generator gen) {
        super.frame(gen);
        QFrame frame = gen.getFrame(this);
        frame.setLimit(m_operand);
        QFrame query = gen.getFrame(m_query);
        frame.setOffset(query.getOffset());
    }

    String getRangeType() {
        return "limit";
    }

    public String toString() {
        return "limit(" + m_query + ", " + m_operand + ")";
    }

    public String summary() {
        return "limit";
    }

}
