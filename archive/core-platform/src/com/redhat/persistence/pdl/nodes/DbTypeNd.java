package com.redhat.persistence.pdl.nodes;

/**
 * DbType
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/07/08 $
 **/

public class DbTypeNd extends Node {

    public final static String versionId = "$Id: //core-platform/dev/src/com/redhat/persistence/pdl/nodes/DbTypeNd.java#1 $ by $Author: rhs $, $DateTime: 2003/07/08 21:04:28 $";

    public static final Field NAME =
        new Field(DbTypeNd.class, "name", IdentifierNd.class, 1, 1);

    private int m_size = -1;
    private int m_precision = -1;

    public void setSize(int size) {
        m_size = size;
    }

    public void setPrecision(int precision) {
        m_precision = precision;
    }

    public int getSize() {
        return m_size;
    }

    public int getPrecision() {
        return m_precision;
    }

    public int getType() {
        String type = getName().getName();
        try {
            Class types = Class.forName("java.sql.Types");
            java.lang.reflect.Field f = types.getField(type);
            return f.getInt(null);
        } catch (ClassNotFoundException e) {
            throw new Error(e.getMessage());
        } catch (NoSuchFieldException e) {
            throw new Error(e.getMessage());
        } catch (IllegalAccessException e) {
            throw new Error(e.getMessage());
        }
    }

    public IdentifierNd getName() {
        return (IdentifierNd) get(NAME);
    }


    public void dispatch(Switch sw) {
        super.dispatch(sw);
        sw.onDbType(this);
    }

}
