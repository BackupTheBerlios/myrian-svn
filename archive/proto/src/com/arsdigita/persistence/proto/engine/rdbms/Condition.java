package com.arsdigita.persistence.proto.engine.rdbms;

import com.arsdigita.persistence.proto.common.*;
import java.util.*;

/**
 * Condition
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #9 $ $Date: 2003/03/14 $
 **/

abstract class Condition {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/engine/rdbms/Condition.java#9 $ by $Author: rhs $, $DateTime: 2003/03/14 13:52:50 $";

    abstract void write(SQLWriter w);

    public String toString() {
        SQLWriter w = new UnboundWriter();
        w.write(this);
        return w.getSQL();
    }

    public static Condition equals(Path[] left, Path[] right) {
        if (left.length != right.length) {
            throw new IllegalArgumentException
                ("left and right must be the same length");
        }

        Condition cond = null;
        for (int i = 0; i < left.length; i++) {
            Condition eq = new EqualsCondition(left[i], right[i]);
            if (cond == null) {
                cond = eq;
            } else {
                cond = new AndCondition(cond, eq);
            }
        }

        return cond;
    }

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

}

class StaticCondition extends Condition {

    private SQL m_sql;

    public StaticCondition(SQL sql) {
        m_sql = sql;
    }

    public SQL getSQL() {
        return m_sql;
    }

    void write(SQLWriter w) {
        w.write(this);
    }

}
