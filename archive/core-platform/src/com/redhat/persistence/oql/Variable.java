package com.redhat.persistence.oql;

import com.redhat.persistence.common.*;
import com.redhat.persistence.metadata.*;
import java.util.*;

/**
 * Variable
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2004/03/28 $
 **/

public class Variable extends Expression {

    public final static String versionId = "$Id: //core-platform/dev/src/com/redhat/persistence/oql/Variable.java#2 $ by $Author: rhs $, $DateTime: 2004/03/28 22:52:45 $";

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
