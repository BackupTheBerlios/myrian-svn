package org.myrian.persistence.pdl.nodes;

import java.util.*;

/**
 * PropertyMappingNd
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 **/

public class PropertyMappingNd extends Node {


    public static final Field COLUMNS =
        new Field(PropertyMappingNd.class, "columns", ColumnNd.class, 1);

    public void dispatch(Switch sw) {
        sw.onPropertyMapping(this);
    }

    private boolean m_value = false;
    private boolean m_reference = false;
    private boolean m_inverse = false;
    private boolean m_mapping = false;

    public void setValue() {
        m_value = true;
    }

    public boolean isValue() {
        return m_value;
    }

    public void setReference() {
        m_reference = true;
    }

    public boolean isReference() {
        return m_reference;
    }

    public void setInverse() {
        m_inverse = true;
    }

    public boolean isInverse() {
        return m_inverse;
    }

    public void setMapping() {
        m_mapping = true;
    }

    public boolean isMapping() {
        return m_mapping;
    }

    public List getColumns() {
        return (List) get(COLUMNS);
    }

}
