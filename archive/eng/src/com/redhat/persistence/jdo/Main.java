package com.redhat.persistence.jdo;

import com.redhat.persistence.*;
import com.redhat.persistence.engine.rdbms.*;
import com.redhat.persistence.metadata.*;
import com.redhat.persistence.pdl.*;
import com.redhat.persistence.oql.*;
import com.redhat.persistence.oql.Expression;

import java.sql.*;
import java.util.*;

/**
 * Main
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2004/06/22 $
 **/

public class Main {

    public final static String versionId = "$Id: //eng/persistence/dev/src/com/redhat/persistence/jdo/Main.java#1 $ by $Author: vadim $, $DateTime: 2004/06/22 13:25:03 $";

    public static Cursor cursor(Session ssn, ObjectType type, Expression expr) {
        DataSet ds = new DataSet(ssn, new Signature(type), expr);
        return ds.getCursor();
    }

    public static Cursor cursor(Session ssn, Class klass, Expression expr) {
        Root root = ssn.getRoot();
        ObjectType type = root.getObjectType(klass.getName());
        return cursor(ssn, type, expr);
    }

    public static Cursor all(Session ssn, Class klass) {
        return cursor(ssn, klass, new All(klass.getName()));
    }

    public static Object create(Session ssn, Class klass, Object[] args) {
        return create(ssn, klass.getName(), args);
    }

    public static Object create(Session ssn, String klass, Object[] args) {
        Root root = ssn.getRoot();
        ObjectType type = root.getObjectType(klass);
        Adapter ad = root.getAdapter(type);
        PropertyMap pmap = new PropertyMap(type);
        Collection props = type.getImmediateProperties();
        int index = 0;
        for (Iterator it = props.iterator();
             it.hasNext() && index < args.length; ) {
            Property prop = (Property) it.next();
            pmap.put(prop, args[index++]);
        }
        Object obj = ad.getObject(type.getBasetype(), pmap, ssn);
        ssn.create(obj);
        return obj;
    }

    public static void lock(Session ssn, Expression expr) {
        DataSet ds = new DataSet
            (ssn, new Signature() {
                public Query makeQuery(Expression e) {
                    return new Query(e, true);
                }
            }, expr);
        Cursor c = ds.getCursor();
        try { c.next(); }
        finally { c.close(); }
    }

    public static void lock(Session ssn, Object obj) {
        Root root = ssn.getRoot();
        Adapter ad = root.getAdapter(obj.getClass());
        ObjectType type = ad.getObjectType(obj);
        Expression expr = new Filter
            (new Define(new All(type.getQualifiedName()),"this"),
             new Equals(new Variable("this"), new Literal(obj)));
        lock(ssn, expr);
    }

}
