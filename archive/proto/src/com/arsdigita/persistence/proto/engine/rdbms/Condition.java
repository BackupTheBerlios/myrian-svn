package com.arsdigita.persistence.proto.engine.rdbms;

import com.arsdigita.persistence.proto.common.*;

/**
 * Condition
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #5 $ $Date: 2003/02/14 $
 **/

abstract class Condition {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/engine/rdbms/Condition.java#5 $ by $Author: rhs $, $DateTime: 2003/02/14 16:46:06 $";

    abstract void write(SQLWriter w);

}

class AndCondition extends Condition {

    private Condition m_left;
    private Condition m_right;

    public AndCondition(Condition left, Condition right) {
        m_left = left;
        m_right = right;
    }

    public Condition getLeft() {
        return m_left;
    }

    public Condition getRight() {
        return m_right;
    }

    void write(SQLWriter w) {
        w.write(this);
    }

    public String toString() {
        return m_left + "\nand " + m_right;
    }

}

class OrCondition extends Condition {

    private Condition m_left;
    private Condition m_right;

    public OrCondition(Condition left, Condition right) {
        m_left = left;
        m_right = right;
    }

    public Condition getLeft() {
        return m_left;
    }

    public Condition getRight() {
        return m_right;
    }

    void write(SQLWriter w) {
        w.write(this);
    }

    public String toString() {
        return m_left + " or " + m_right;
    }

}

class NotCondition extends Condition {

    private Condition m_operand;

    public NotCondition(Condition operand) {
        m_operand = operand;
    }

    public Condition getOperand() {
        return m_operand;
    }

    void write(SQLWriter w) {
        w.write(this);
    }

    public String toString() {
        return "not " + m_operand;
    }

}

class InCondition extends Condition {

    private Path m_column;
    private Select m_select;

    public InCondition(Path column, Select select) {
        m_column = column;
        m_select = select;
    }

    public Path getColumn() {
        return m_column;
    }

    public Select getSelect() {
        return m_select;
    }

    void write(SQLWriter w) {
        w.write(this);
    }

    public String toString() {
        return m_column + " in (" + m_select + ")";
    }

}

class EqualsCondition extends Condition {

    private Path m_left;
    private Path m_right;

    public EqualsCondition(Path left, Path right) {
        m_left = left;
        m_right = right;
    }

    public Path getLeft() {
        return m_left;
    }

    public Path getRight() {
        return m_right;
    }

    void write(SQLWriter w) {
        w.write(this);
    }

    public String toString() {
        return m_left + " = " + m_right;
    }

}
