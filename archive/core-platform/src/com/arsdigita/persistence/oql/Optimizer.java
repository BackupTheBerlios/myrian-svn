package com.arsdigita.persistence.oql;

import com.arsdigita.util.*;
import com.arsdigita.persistence.metadata.*;
import java.util.*;

/**
 * Optimizer
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #2 $ $Date: 2002/07/10 $
 **/

class Optimizer extends Actor {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/oql/Optimizer.java#2 $ by $Author: rhs $, $DateTime: 2002/07/10 16:04:39 $";

    public void act(Table table) {
        Query query = table.getQuery();

        if (table.getSelections().size() == 0) {
            if (table.isEliminable()) {
                table.remove();
                query.modify("Removed '" + table + "'.");
            } else if (table.getConditions().size() == 2 &&
                       !table.isBase()) {
                Iterator it = table.getConditions().iterator();
                Condition one = (Condition) it.next();
                Condition two = (Condition) it.next();
                Condition added = null;
                if (one.getHead().getTable().equals(table) &&
                    two.getHead().getTable().equals(table) &&
                    one.getHead().equals(two.getHead())) {
                    if (!one.isOuter() && !two.isOuter()) {
                        added = new Condition(table.getNode(), one.getTail(),
                                              two.getTail());
                    }
                } else if (one.getTail().getTable().equals(table) &&
                           two.getTail().getTable().equals(table) &&
                           one.getTail().equals(two.getTail())) {
                    if (!one.isOuter() && !two.isOuter()) {
                        added = new Condition(table.getNode(), one.getHead(),
                                              two.getHead());
                    }
                } else if (one.getHead().getTable().equals(table) &&
                           one.getHead().equals(two.getTail())){
                    added = new Condition(table.getNode(), one.getTail(),
                                          two.getHead());
                } else if (one.getTail().equals(two.getHead())) {
                    added = new Condition(table.getNode(), two.getTail(),
                                          one.getHead());
                }

                if (added != null) {
                    table.remove();
                    query.modify(
                        "Added join from '" + added.getTail() + "' to '" +
                        added.getHead() + "' and removed '" + table + "'."
                        );
                }
            }
        } else if (canShiftSelections(table)) {
            Table other = shiftSelections(table);
            Assert.assertEquals(0, table.getSelections().size());
            table.remove();
            query.modify(
                "Shifted selections from '" + table + "' to '" + other +
                "' and removed '" + table + "'."
                );
        }
    }

    private static final Table shiftSelections(Table table) {
        Table other = null;
        Map columns = new HashMap();
        for (Iterator it = table.getConditions().iterator(); it.hasNext(); ) {
            Condition cond = (Condition) it.next();
            if (cond.getHead().getTable().equals(table)) {
                columns.put(cond.getHead(), cond.getTail());
                other = cond.getTail().getTable();
            } else {
                columns.put(cond.getTail(), cond.getHead());
                other = cond.getHead().getTable();
            }
        }
        
        for (Iterator it = table.getSelections().iterator(); it.hasNext(); ) {
            Selection sel = (Selection) it.next();
            Column col = (Column) columns.get(sel.getColumn());
            if (col == null) {
                throw new IllegalStateException(
                    "Couldn't map " + sel.getColumn() + " using " + columns +
                    " derived from " + table.getConditions() + ", " +
                    table.getSelections()
                    );
            }
            sel.setColumn(col);
        }

        return other;
    }

    private static final boolean canShiftSelections(Table table) {
        if (!table.isEliminable()) {
            return false;
        }

        Set conditions = table.getConditions();
        Set selections = table.getSelections();

        if (conditions.size() != selections.size() ||
            (table.getEntering().size() != 0 &&
             table.getLeaving().size() != 0)) {
            return false;
        }

        Set columns = new HashSet(conditions.size());

        for (Iterator it = conditions.iterator(); it.hasNext(); ) {
            Condition cond = (Condition) it.next();
            Column head = cond.getHead();
            Column tail = cond.getTail();

            Table from = table;
            Table to = head.getTable().equals(table) ?
                tail.getTable() : head.getTable();

            if (head.getTable().equals(table)) {
                columns.add(head);
            } else {
                columns.add(tail);
            }
        }

        int before = columns.size();

        for (Iterator it = selections.iterator(); it.hasNext(); ) {
            columns.add(((Selection) it.next()).getColumn());
        }

        return columns.size() == before;
    }

}
