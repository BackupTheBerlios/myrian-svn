/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
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
package com.redhat.persistence.oql;

import com.redhat.persistence.common.*;
import com.redhat.persistence.metadata.*;
import java.util.*;

/**
 * Variable
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2004/04/05 $
 **/

public class Variable extends Expression {

    public final static String versionId = "$Id: //users/rhs/persistence/src/com/redhat/persistence/oql/Variable.java#1 $ by $Author: rhs $, $DateTime: 2004/04/05 15:33:44 $";

    private String m_name;

    public Variable(String name) {
        m_name = name;
    }

    void frame(Generator gen) {
        QFrame parent = gen.resolve(m_name);
        Get.frame(gen, parent, m_name, this);
    }

    Code emit(Generator gen) {
        return gen.getFrame(this).emit();
    }

    void hash(Generator gen) {
        gen.hash(m_name);
        gen.hash(getClass());
    }

    public String toString() {
        return m_name;
    }

    String summary() { return m_name; }

}
