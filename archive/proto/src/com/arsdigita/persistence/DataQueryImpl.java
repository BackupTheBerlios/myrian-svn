package com.arsdigita.persistence;

import com.arsdigita.persistence.metadata.*;
import com.arsdigita.persistence.proto.PersistentCollection;
import com.arsdigita.persistence.proto.DataSet;
import com.arsdigita.persistence.proto.Cursor;
import com.arsdigita.persistence.proto.Query;
import com.arsdigita.persistence.proto.Signature;
import java.util.*;

/**
 * DataQueryImpl
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #6 $ $Date: 2003/01/10 $
 **/

class DataQueryImpl implements DataQuery {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/DataQueryImpl.java#6 $ by $Author: rhs $, $DateTime: 2003/01/10 17:10:28 $";

    private Session m_ssn;
    private com.arsdigita.persistence.proto.Session m_pssn;
    private PersistentCollection m_pc = null;
    private Query m_query;
    private Cursor m_cursor = null;

    DataQueryImpl(Session ssn,
                  com.arsdigita.persistence.proto.metadata.ObjectType type) {
        m_ssn = ssn;
        m_pssn = ssn.getProtoSession();
        Signature sig = new Signature(type);
        sig.addDefaultProperties();
        m_query = new Query(sig, null);
    }

    Session getSession() {
        return m_ssn;
    }

    public CompoundType getType() {
        throw new Error("not implemented");
    }

    public boolean hasProperty(String propertyName) {
        throw new Error("not implemented");
    }

    public void reset() {
        throw new Error("not implemented");
    }


    public boolean first() {
        throw new Error("not implemented");
    }


    public boolean isEmpty() {
        throw new Error("not implemented");
    }


    public boolean isBeforeFirst() {
        throw new Error("not implemented");
    }

    public boolean isFirst() {
        throw new Error("not implemented");
    }


    public boolean isLast() {
        throw new Error("not implemented");
    }


    public boolean isAfterLast() {
        throw new Error("not implemented");
    }


    public boolean last() {
        throw new Error("not implemented");
    }


    public boolean previous() {
        throw new Error("not implemented");
    }


    public Filter setFilter(String conditions) {
        throw new Error("not implemented");
    }


    public Filter addFilter(String conditions) {
        throw new Error("not implemented");
    }


    public Filter addFilter(Filter filter) {
        throw new Error("not implemented");
    }

    public boolean removeFilter(Filter filter) {
        throw new Error("not implemented");
    }

    public Filter addInSubqueryFilter(String propertyName, String subqueryName) {
        throw new Error("not implemented");
    }


    public Filter addInSubqueryFilter(String propertyName,
                                      String subQueryProperty,
                                      String queryName) {
        throw new Error("not implemented");
    }

    public Filter addNotInSubqueryFilter(String propertyName,
                                         String subqueryName) {
        throw new Error("not implemented");
    }

    public Filter addEqualsFilter(String attribute, Object value) {
        throw new Error("not implemented");
    }


    public Filter addNotEqualsFilter(String attribute, Object value) {
        throw new Error("not implemented");
    }


    public void clearFilter() {
        throw new Error("not implemented");
    }


    public FilterFactory getFilterFactory() {
        throw new Error("not implemented");
    }


    public void setOrder(String order) {
        throw new Error("not implemented");
    }


    public void addOrder(String order) {
        throw new Error("not implemented");
    }


    public void addOrderWithNull(String orderOne, Object orderTwo,
                          boolean isAscending) {
        throw new Error("not implemented");
    }


    public void clearOrder() {
        throw new Error("not implemented");
    }


    public void setParameter(String parameterName, Object value) {
        throw new Error("not implemented");
    }


    public Object getParameter(String parameterName) {
        throw new Error("not implemented");
    }


    public boolean isNoView() {
        throw new Error("not implemented");
    }

    public void setNoView(boolean isNoView) {
        throw new Error("not implemented");
    }


    public void setRange(Integer beginIndex) {
        throw new Error("not implemented");
    }

    public void setRange(Integer beginIndex, Integer endIndex) {
        throw new Error("not implemented");
    }


    public Map getPropertyValues() {
        throw new Error("not implemented");
    }


    public void setReturnsUpperBound(int upperBound) {
        throw new Error("not implemented");
    }


    public void setReturnsLowerBound(int lowerBound) {
        throw new Error("not implemented");
    }

    public void alias(String fromPrefix, String toPrefix) {
        throw new Error("not implemented");
    }

    public void close() {
        throw new Error("not implemented");
    }

    public void rewind() {
        throw new Error("not implemented");
    }


    public Object get(String propertyName) {
        throw new Error("not implemented");
    }


    public int getPosition() {
        throw new Error("not implemented");
    }

    private Cursor getCursor() {
        PersistentCollection pc = m_pssn.retrieve(m_query);
        return pc.getDataSet().getCursor();
    }

    public boolean next() {
        if (m_cursor == null) {
            m_cursor = getCursor();
        }
        return m_cursor.next();
    }

    public long size() {
        throw new Error("not implemented");
    }

}
