package com.arsdigita.persistence.pdl.ast;

import java.util.*;

/**
 * The root class for all nodes in the abstract syntax tree (AST).
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #1 $ $Date: 2002/05/12 $
 **/

public abstract class Node {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/pdl/ast/Node.java#1 $ by $Author: dennis $, $DateTime: 2002/05/12 18:23:13 $";

    // The filename that this node was created from.
    private String m_filename = "<none>";
    // The line number and column number associated with this node.
    private int m_line = -1, m_column = -1;

    // The parent of this node. Can be null
    private Node m_parent = null;
    private List m_children = new ArrayList();

    protected final void add(Node child) {
        child.setParent(this);
        m_children.add(child);
    }

    protected final List getChildren() {
        return m_children;
    }

    private final void setParent(Node parent) {
        m_parent = parent;
    }

    protected final Node getParent() {
        return m_parent;
    }

    public final void setFilename(String filename) {
        m_filename = filename;
    }

    public String getFilename() {
        return m_filename;
    }

    public final void setLineNumber(int line) {
        m_line = line;
    }

    public final int getLineNumber() {
        return m_line;
    }

    public final void setColumnNumber(int column) {
        m_column = column;
    }

    public final int getColumnNumber() {
        return m_column;
    }

    void generateAssociationEvents() {}

}
