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
 * Size
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2004/06/07 $
 **/

public class Size extends Expression {

    public final static String versionId = "$Id: //eng/persistence/dev/src/com/redhat/persistence/oql/Size.java#1 $ by $Author: rhs $, $DateTime: 2004/06/07 13:49:55 $";

    private Expression m_query;

    public Size(Expression query) {
        m_query = query;
    }

    void frame(Generator gen) {
        m_query.frame(gen);
        QFrame query = gen.getFrame(m_query);
        QFrame frame = gen.frame(this, query.getType());
        frame.addChild(query);
        frame.setLimit(query.getLimit());
        frame.setOffset(query.getOffset());
        gen.addUses(this, gen.getUses(m_query));
    }

    Code emit(Generator gen) {
        return gen.getFrame(this).emit();
    }

    void hash(Generator gen) {
        m_query.hash(gen);
        gen.hash(getClass());
    }

    public String toString() {
        return "size(" + m_query + ")";
    }

    String summary() {
        return "size";
    }

}
