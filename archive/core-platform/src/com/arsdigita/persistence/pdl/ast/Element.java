/*
 * Copyright (C) 2001 ArsDigita Corporation. All Rights Reserved.
 *
 * The contents of this file are subject to the ArsDigita Public 
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of
 * the License at http://www.arsdigita.com/ADPL.txt
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */

package com.arsdigita.persistence.pdl.ast;

import com.arsdigita.persistence.pdl.*;
import java.util.*;
import org.apache.log4j.Category;

/**
 * The root class for all nodes in the abstract syntax tree (AST).
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #1 $ $Date: 2002/05/12 $
 */
public abstract class Element extends Node {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/pdl/ast/Element.java#1 $ by $Author: dennis $, $DateTime: 2002/05/12 18:23:13 $";

    private static Category s_log = 
        Category.getInstance(Element.class);

    /**
     * Set the line information of this element to match that of another.
     * 
     * @param e the element to take line information from
     */
    public void setLineInfo(Element e) {
        setLineNumber(e.getLineNumber());
        setColumnNumber(e.getColumnNumber());
    }

    /**
     * Set the line information of this element from a JavaCC Token
     *
     * @param t the Token to draw line information from
     */
    public void setLineInfo(Token t) {
        setLineInfo(t.beginLine, t.beginColumn);
    }

    /**
     * Set the line information of this element.
     *
     * @param line the line this element is declared
     * @param column the column where the declaration begins
     */
    public void setLineInfo(int line, int column) {
        setLineNumber(line);
        setColumnNumber(column);
    }

    /**
     * Returns the message string prefixed by filename and line number
     * information.
     **/
    private String formatError(String message) {
        return System.getProperty("line.separator") + getFilename()
	    + ":" + getLineNumber() + ": column " + getColumnNumber() +
            ": " + message;
    }

    /**
     * Throw a new error message
     */
    protected void error(String message) {
        throw new Error(formatError(message));
    }

    /**
     * Displays a warning to the user.
     **/
    protected void warn(String message) {
        s_log.warn("Warning: " + formatError(message));
    }

    /**
     * Find a the parent of this element that is of a particular class
     *
     * @param cls the class to search for
     */
    private Node getParentByType(Class cls) {
        Node elt = this;

        while (!cls.isInstance(elt)) {
            elt = elt.getParent();
            if (elt == null)
                break;
        }

        return elt;
    }

    /**
     * Get the AST root node that this element is defined in
     *
     * @return the AST root node that this element is defined in
     */
    public AST getAST() {
        return (AST) getParentByType(AST.class);
    }

    /**
     * Returns the ModelDef that contains this element
     *
     * @return the ModelDef that contains this element
     */
    public ModelDef getModelDef() {
        return (ModelDef) getParentByType(ModelDef.class);
    }

    /**
     * Returns the ObjectDef that contains this element
     * 
     * @return the ObjectDef that contains this element
     */
    public ObjectDef getObjectDef() {
        return (ObjectDef) getParentByType(ObjectDef.class);
    }

    /**
     * Returns the AssociationDef that contains this element
     *
     * @return the AssociationDef that contains this element
     */
    public AssociationDef getAssociationDef() {
        return (AssociationDef) getParentByType(AssociationDef.class);
    }

    /** 
     * Ensure that this element is in a valid state
     */
    void validate() {
        // Do nothing by default.
    }

    /**
     * Validate a particular element 
     *
     * @param el the element to validate
     */
    void validate(Element el) {
        if (el != null)
            el.validate();
    }

    /** 
     * Validate a set of elements
     *
     * @param m a Map of elements to validate
     */
    void validate(Map m) {
        for (Iterator it = m.values().iterator(); it.hasNext(); ) {
            Element el = (Element) it.next();
            validate(el);
        }
    }

    /**
     * Validate a Collection of elements
     *
     * @param c the collection of Elements to validate
     */
    void validate(Collection c) {
        for (Iterator it = c.iterator(); it.hasNext(); ) {
            Element el = (Element) it.next();
            validate(el);
        }
    }

}
