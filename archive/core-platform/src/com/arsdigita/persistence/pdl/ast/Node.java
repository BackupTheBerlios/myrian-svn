/*
 * Copyright (C) 2001, 2002 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the CCM Public
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of
 * the License at http://www.redhat.com/licenses/ccmpl.html
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */

package com.arsdigita.persistence.pdl.ast;

import java.util.*;

/**
 * The root class for all nodes in the abstract syntax tree (AST).
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #4 $ $Date: 2002/08/14 $
 **/

public abstract class Node {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/pdl/ast/Node.java#4 $ by $Author: dennis $, $DateTime: 2002/08/14 23:39:40 $";

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

    void initLineInfo(com.arsdigita.persistence.metadata.Element element) {
        element.setFilename(getFilename());
        element.setLineInfo(getLineNumber(), getColumnNumber());
    }

}
