/*
 * Copyright (C) 2003 Red Hat Inc. All Rights Reserved.
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

package com.redhat.persistence.engine.rdbms;

import com.redhat.persistence.Condition;
import com.redhat.persistence.Expression;
import com.redhat.persistence.common.Path;
import com.redhat.persistence.metadata.Column;
import java.util.Collection;
import java.util.Iterator;

/**
 * ANSIWriter
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/11/09 $
 **/


public class ANSIWriter extends SQLWriter {

    protected void writeSelect(Select select) {
        write("select ");

        Collection sels = select.getSelections();

        if (select.isCount()) {
            write("count(*)\nfrom (\nselect 1");
        } else {
            if (sels.size() == 0) {
                write("*");
            } else {
                for (Iterator it = sels.iterator(); it.hasNext(); ) {
                    Path path = (Path) it.next();
                    write(path);
                    write(" as ");
                    write(select.getAlias(path));
                    if (it.hasNext()) {
                        write(",\n       ");
                    }
                }
            }
        }
    }

    protected void writeOrder(Select select) {
        if (select.isCount()) { return; }

        Collection order = select.getOrder();

        if (order.size() > 0) {
            write("\norder by ");
        }

        for (Iterator it = order.iterator(); it.hasNext(); ) {
            Expression e = (Expression) it.next();
            write(e);

            if (!select.isAscending(e)) {
                write(" desc");
            }

            if (it.hasNext()) {
                write(", ");
            }
        }
    }

    public void write(Select select) {
        writeSelect(select);

        Join join = select.getJoin();
        Expression filter = select.getFilter();

        write("\nfrom ");
        write(join);

        if (filter != null) {
            write("\nwhere ");
            write(filter);
        }

        writeOrder(select);

        if (select.getOffset() != null) {
            write("\noffset " + select.getOffset());
        }

        if (select.getLimit() != null) {
            write("\nlimit " + select.getLimit());
        }

        if (select.isCount()) {
            write("\n) count__");
        }
    }

    public void write(Insert insert) {
        Collection cols = insert.getColumns();

        write("insert into ");
        write(insert.getTable().getName());
        write("\n(");

        for (Iterator it = cols.iterator(); it.hasNext(); ) {
            Column col = (Column) it.next();
            write(col.getName());

            if (it.hasNext()) {
                write(", ");
            }
        }

        write(")\nvalues\n(");

        for (Iterator it = cols.iterator(); it.hasNext(); ) {
            Column col = (Column) it.next();
            write(insert.get(col));

            if (it.hasNext()) {
                write(", ");
            }
        }

        write(")");
    }

    public void write(Update update) {
        write("update ");
        write(update.getTable().getName());
        write("\nset ");

        for (Iterator it = update.getColumns().iterator(); it.hasNext(); ) {
            Column col = (Column) it.next();
            write(col.getName());
            write(" = ");
            write(update.get(col));
            if (it.hasNext()) {
                write(",\n    ");
            }
        }

        Condition cond = update.getCondition();
        if (cond != null) {
            write("\nwhere ");
            write(cond);
        }
    }

    public void write(Delete delete) {
        write("delete from ");
        write(delete.getTable().getName());
        Condition cond = delete.getCondition();
        if (cond != null) {
            write("\nwhere ");
            write(cond);
        }
    }


    public void write(StaticJoin join) {
        write("(");
        // XXX: this is a hack, for binding to work properly we need to call
        // the Operation version of write.
        write((Operation) join.getStaticOperation());
        write(") ");
        write(join.getAlias());
    }

    public void write(SimpleJoin join) {
        write(join.getTable().getName());
        write(" ");
        write(join.getAlias());
    }


    void writeCompound(CompoundJoin join) {
        write(join.getLeft());
        write("\n     ");
        write(join.getType().toString());
        write(" ");
        write(join.getRight());
        Condition cond = join.getCondition();
        if (cond != null) {
            write(" on ");
            write(cond);
        }
    }


    public void write(InnerJoin join) {
        writeCompound(join);
    }

    public void write(LeftJoin join) {
        writeCompound(join);
    }

    public void write(RightJoin join) {
        writeCompound(join);
    }

    public void write(CrossJoin join) {
        writeCompound(join);
    }

}

class RetainUpdatesWriter extends ANSIWriter {

    public void write(StaticOperation sop) {
	write(sop.getSQLBlock().getSQL());
    }

}

class UnboundWriter extends ANSIWriter {

    public void write(Path path) {
        write(path.getPath());
    }

}
