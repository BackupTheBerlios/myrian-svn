package com.arsdigita.persistence.sql;

import java.util.*;

/**
 * SQLWriter
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #3 $ $Date: 2002/07/18 $
 **/

public final class SQLWriter {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/sql/SQLWriter.java#3 $ by $Author: dennis $, $DateTime: 2002/07/18 13:18:21 $";

    private StringBuffer m_buffer = new StringBuffer(1024);
    private Stack m_indents = new Stack();
    private int m_column = 0;
    private int m_line = 0;
    private boolean m_postID = false;

    public SQLWriter() {
        m_indents.push(new Integer(0));
    }

    public final int getIndent() {
        return ((Integer) m_indents.peek()).intValue();
    }

    public final void pushIndent(int indent) {
        m_indents.push(new Integer(indent));
    }

    public final int popIndent() {
        int result = getIndent();
        m_indents.pop();
        return result;
    }

    public final int getColumn() {
        return m_column;
    }

    public final int getLine() {
        return m_line;
    }

    public final void print(char c) {
        print("" + c);
    }

    public final void println(char c) {
        print(c);
        println();
    }

    public final void println(String str) {
        print(str);
        println();
    }

    public final void print(String str) {
        if (m_postID) {
            if (str.length() > 0 &&
                Character.isJavaIdentifierPart(str.charAt(0))) {
                m_buffer.append(' ');
                m_column++;
            }
        }

        m_buffer.append(str);
        m_column += str.length();
        m_postID = false;
    }


    public final void println() {
        m_buffer.append('\n');
        int indent = getIndent();
        for (int i = 0; i < indent; i++) {
            m_buffer.append(' ');
        }

        m_column = indent;
        m_line++;
        m_postID = false;
    }

    public final void printID(String str) {
        if (m_buffer.length() > 0) {
            char last = m_buffer.charAt(m_buffer.length() - 1);
            if (Character.isJavaIdentifierPart(last) ||
                last == ')' ||
                last == '\'' ||
                last == '?') {
                print(' ');
            }
        }

        print(str);

        m_postID = true;
    }

    public final String toString() {
        return m_buffer.toString();
    }

}
