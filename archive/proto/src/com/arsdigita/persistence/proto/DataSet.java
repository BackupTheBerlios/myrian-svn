package com.arsdigita.persistence.proto;

import com.arsdigita.persistence.proto.metadata.ObjectType;

/**
 * DataSet
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #3 $ $Date: 2003/01/06 $
 **/

public class DataSet {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/DataSet.java#3 $ by $Author: rhs $, $DateTime: 2003/01/06 16:31:02 $";

    private Session m_ssn;
    private Binding m_binding;

    protected DataSet(Session ssn, Binding binding) {
        m_ssn = ssn;
        m_binding = binding;
    }

    public Session getSession() {
        return m_ssn;
    }

    public Binding getBinding() {
        return m_binding;
    }

    public Cursor getCursor() {
        return new Cursor(this);
    }

}
