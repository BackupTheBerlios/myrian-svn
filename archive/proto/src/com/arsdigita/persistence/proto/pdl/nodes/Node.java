package com.arsdigita.persistence.proto.pdl.nodes;

import java.util.*;

/**
 * Node
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #4 $ $Date: 2003/02/26 $
 **/

public abstract class Node {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/pdl/nodes/Node.java#4 $ by $Author: rhs $, $DateTime: 2003/02/26 12:01:31 $";


    /**
     * Metadata Structures
     *
     * These store the legal relationships between nodes and are used both to
     * traverse the parse tree and to perform validation.
     **/

    private static final HashMap TYPES = new HashMap();

    private static class Type {

        private Class m_type;
        private ArrayList m_fields = new ArrayList();

        public Type(Class type) {
            m_type = type;
        }

        public void addField(Field field) {
            m_fields.add(field);
        }

        public Collection getFields() {
            return m_fields;
        }

    }

    public static class Field {

        private Class m_parent;
        private String m_name;
        private Class m_type;
        private int m_lower;
        private int m_upper;

        public Field(Class parent, String name, Class type) {
            this(parent, name, type, 0);
        }

        public Field(Class parent, String name, Class type, int lower) {
            this(parent, name, type, lower, -1);
        }

        public Field(Class parent, String name, Class type, int lower,
                     int upper) {
            m_parent = parent;
            m_name = name;
            m_type = type;
            m_lower = lower;
            m_upper = upper;

            Type t = (Type) TYPES.get(parent);
            if (t == null) {
                t = new Type(parent);
                TYPES.put(parent, t);
            }
            t.addField(this);
        }

        public int getLower() {
            return m_lower;
        }

        public int getUpper() {
            return m_upper;
        }

        public String toString() {
            return m_name;
        }

    }

    private class Child {
        private Field m_field;
        private Node m_node;

        public Child(Field field, Node node) {
            m_field = field;
            m_node = node;
        }

        public Field getField() {
            return m_field;
        }

        public Node getNode() {
            return m_node;
        }
    }


    /**
     * Dynamic Dispatch
     *
     * The following code is provided in order to perform dynamic dispatch on
     * node type.
     **/

    public static abstract class Switch {
        public void onNode(Node node) {}

        public void onAST(AST ast) {}

        public void onFile(FileNd file) {}
        public void onModel(ModelNd model) {}
        public void onImport(ImportNd imp) {}
        public void onObjectType(ObjectTypeNd type) {}
        public void onAssociation(AssociationNd assn) {}

        public void onStatement(StatementNd st) {}
        public void onProperty(PropertyNd prop) {}
        public void onAggressiveLoad(AggressiveLoadNd al) {}
        public void onReferenceKey(ReferenceKeyNd key) {}
        public void onObjectKey(ObjectKeyNd key) {}
        public void onUniqueKey(UniqueKeyNd key) {}

        public void onType(TypeNd type) {}
        public void onJavaClass(JavaClassNd jc) {}
        public void onPath(PathNd path) {}
        public void onColumn(ColumnNd col) {}
        public void onDbType(DbTypeNd type) {}
        public void onJoinPath(JoinPathNd jp) {}
        public void onJoin(JoinNd join) {}
        public void onIdentifier(IdentifierNd id) {}

        public void onEvent(EventNd nd) {}
        public void onSQLBlock(SQLBlockNd nd) {}
        public void onMapping(MappingNd nd) {}
        public void onBinding(BindingNd nd) {}
    }


    public void dispatch(Switch sw) {
        sw.onNode(this);
    }


    /**
     * Traversals
     *
     * The following code is used to perform traversals over the parse tree.
     **/

    public static abstract class Filter {
        public abstract boolean accept(Field f);
    }

    public static class IncludeFilter extends Filter {

        private HashSet m_fields = new HashSet();

        public IncludeFilter(Field[] fields) {
            for (int i = 0; i < fields.length; i++) {
                m_fields.add(fields[i]);
            }
        }

        public boolean accept(Field f) {
            return m_fields.contains(f);
        }
    }


    public static final Filter ALL = new Filter() {
            public boolean accept(Field f) { return true; }
        };


    public void traverse(Switch sw) {
        traverse(sw, ALL);
    }


    public void traverse(Switch sw, Filter f) {
        dispatch(sw);
        for (Iterator it = m_children.iterator(); it.hasNext(); ) {
            Child child = (Child) it.next();
            if (f.accept(child.getField())) {
                child.getNode().traverse(sw, f);
            }
        }
    }


    /**
     * Each node stores line number information, a pointer to it's parent and
     * a list of it's children. This list doesn't necessarily need to
     * correspond to the constraints specified by the metadata stored in the
     * Field objects since the grammar allows invalid parse trees to be built
     * up in certain cases.
     **/

    private int m_line = -1;
    private int m_column = -1;

    private Node m_parent = null;
    private ArrayList m_children = new ArrayList();


    /**
     * Setters and Getters for traversing the parse tree.
     **/

    private void check(Field field) {
        if (field == null) {
            throw new IllegalArgumentException
                ("Field is null.");
        }
    }

    public Object get(Field field) {
        check(field);

        ArrayList result = null;
        for (Iterator it = m_children.iterator(); it.hasNext(); ) {
            Child child = (Child) it.next();
            if (child.getField().equals(field)) {
                if (field.getUpper() == 1) {
                    return child.getNode();
                } else {
                    if (result == null) {
                        result = new ArrayList();
                    }
                    result.add(child.getNode());
                }
            }
        }

        if (result == null &&
            (field.getUpper() > 1 || field.getUpper() < field.getLower())) {
            result = new ArrayList();
        }

        return result;
    }

    private void check(Node child) {
        if (child == null) {
            throw new IllegalArgumentException
                ("Child is null.");
        }
        if (child.m_parent != null) {
            throw new IllegalArgumentException
                ("Child belongs to another node: " + child);
        }
    }

    public void add(Field field, Node child) {
        check(field); check(child);

        child.m_parent = this;
        m_children.add(new Child(field, child));
    }


    /**
     * Line info
     **/

    public void setLine(int line) {
        m_line = line;
    }

    public int getLine() {
        return m_line;
    }

    public void setColumn(int column) {
        m_column = column;
    }

    public int getColumn() {
        return m_column;
    }

    public String getLocation() {
        return getFile().getName() + ": line " + getLine() + ", column " +
            getColumn();
    }


    /**
     * Under Construction
     **/

    public Node getParent() {
        return m_parent;
    }

    public FileNd getFile() {
        return getParent().getFile();
    }


    /**
     * Pretty Printing
     **/

    private int getDepth() {
        if (m_parent == null) {
            return 0;
        } else {
            return m_parent.getDepth() + 1;
        }
    }

    private String indent() {
        int depth = getDepth();
        StringBuffer result = new StringBuffer(2*depth);
        for (int i = 0; i < depth; i++) {
            result.append("  ");
        }
        return result.toString();
    }

    private static String getName(Class klass) {
        String full = klass.getName();
        return full.substring(full.lastIndexOf('.') + 1);
    }

    public String toString() {
        StringBuffer result = new StringBuffer();
        result.append("(" + getName(getClass()) + ": " + getLine() +
                      "[" + getColumn() + "]");
        for (Iterator it = m_children.iterator(); it.hasNext(); ) {
            Child child = (Child) it.next();
            result.append("\n  " + indent());
            result.append(child.getField());
            result.append(" = ");
            result.append(child.getNode());
        }
        result.append(")");
        return result.toString();
    }

}
