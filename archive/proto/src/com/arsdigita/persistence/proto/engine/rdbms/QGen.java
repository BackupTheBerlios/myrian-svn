package com.arsdigita.persistence.proto.engine.rdbms;

import com.arsdigita.persistence.proto.*;
import com.arsdigita.persistence.proto.Query;
import com.arsdigita.persistence.proto.common.*;
import com.arsdigita.persistence.proto.metadata.*;

import java.util.*;
import java.sql.*;


/**
 * QGen
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #6 $ $Date: 2003/02/17 $
 **/

class QGen {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/engine/rdbms/QGen.java#6 $ by $Author: rhs $, $DateTime: 2003/02/17 13:30:53 $";

    private Query m_query;
    private HashMap m_columns = new HashMap();
    private HashMap m_tables = new HashMap();
    private HashMap m_joins = new HashMap();

    public QGen(Query query) {
        if (query.getSignature().getSources().size() == 0) {
            throw new IllegalArgumentException
                ("no sources");
        }
        m_query = query;
    }

    private Path getColumn(Path prefix) {
        return (Path) m_columns.get(prefix);
    }

    private void setColumn(Path prefix, Path column) {
        m_columns.put(prefix, column);
    }

    private Set getTables(Path prefix) {
        Set result = (Set) m_tables.get(prefix);
        if (result == null) {
            result = new HashSet();
            m_tables.put(prefix, result);
        }
        return result;
    }

    private void addTable(Path prefix, Table table) {
        getTables(prefix).add(table);
    }

    private Join getJoin(Source src) {
        return (Join) m_joins.get(src);
    }

    private void setJoin(Source src, Join join) {
        m_joins.put(src, join);
    }

    private static final Column getKey(Table table) {
        UniqueKey key = table.getPrimaryKey();
        if (key == null) {
            throw new Error("table has no primary key: " + table);
        } else {
            return key.getColumns()[0];
        }
    }

    public Select generate() {
        Signature sig = m_query.getSignature();
        for (Iterator it = sig.getSources().iterator(); it.hasNext(); ) {
            Source src = (Source) it.next();
            ObjectMap map = Root.getRoot().getObjectMap(src.getObjectType());
            Table start = map.getTable();
            if (start == null) {
                return null;
            }
            Join j = new SimpleJoin
                (start, Path.get(src.getPath() + "__" + start.getName()));
            setColumn(src.getPath(), Path.get(src.getPath() + "__" +
                                              getKey(start)));
            setJoin(src, j);
            addTable(src.getPath(), start);
        }

        for (Iterator it = sig.getPaths().iterator(); it.hasNext(); ) {
            Path path = (Path) it.next();
            genPath(path);
        }

        Condition condition = filter(m_query.getFilter());

        Join join = null;
        for (Iterator it = sig.getSources().iterator(); it.hasNext(); ) {
            Source src = (Source) it.next();
            if (join == null) {
                join = getJoin(src);
            } else {
                join = new CrossJoin(join, getJoin(src));
            }
        }

        Select result = new Select(join, condition);

        int col = 0;
        for (Iterator it = sig.getPaths().iterator(); it.hasNext(); ) {
            Path path = (Path) it.next();
            result.addSelection(getColumn(path), "column" + (col++));
        }

        for (Iterator it = sig.getParameters().iterator(); it.hasNext(); ) {
            Parameter param = (Parameter) it.next();
            result.set(param.getPath(), m_query.get(param), Types.INTEGER);
        }

        return result;
    }

    public Map getMappings(Select sel) {
        Map result = new HashMap();
        for (Iterator it = m_query.getSignature().getPaths().iterator();
             it.hasNext(); ) {
            Path path = (Path) it.next();
            result.put(path, sel.getAlias(getColumn(path)));
        }
        return result;
    }

    Source getSource(Path path) {
        Source src = m_query.getSignature().getSource(path);
        if (src == null) {
            return getSource(path.getParent());
        } else {
            return src;
        }
    }

    private Join getJoin(Path path) {
        return getJoin(getSource(path));
    }

    private void setJoin(Path path, Join join) {
        setJoin(getSource(path), join);
    }

    private void addJoin(Path path, Column to) {
        Path parentColumn = getColumn(path.getParent());
        Table table = to.getTable();
        if (!getTables(path.getParent()).contains(table)) {
            Join join = getJoin(path);
            Join simple = new SimpleJoin
                (table, Path.get(path.getParent() + "__" + table.getName()));
            join = new InnerJoin
                (join, simple, new EqualsCondition
                    (parentColumn, Path.get(path.getParent() + "__" + to)));
            setJoin(path, join);
            addTable(path.getParent(), table);
        }
    }

    private void genPath(final Path path) {
        Path column = getColumn(path);
        if (column != null) {
            return;
        }

        if (m_query.getSignature().isParameter(path)) {
            setColumn(path, path);
            return;
        }

        genPath(path.getParent());

        Property prop = m_query.getSignature().getProperty(path);
        ObjectMap map = Root.getRoot().getObjectMap(prop.getContainer());
        Mapping m = map.getMapping(Path.get(prop.getName()));

        // XXX: no metadata
        if (m == null) { return; }

        m.dispatch(new Mapping.Switch() {
                public void onValue(ValueMapping vm) {
                    addJoin(path, getKey(vm.getColumn().getTable()));
                    setColumn(path, Path.get(path.getParent() + "__" +
                                             vm.getColumn()));
                }

                public void onReference(ReferenceMapping rm) {
                    if (rm.isJoinTo()) {
                        Column to = rm.getJoin(0).getFrom();
                        addJoin(path, getKey(to.getTable()));
                        setColumn(path, Path.get(path.getParent() + "__" +
                                                 to));
                    } else if (rm.isJoinFrom()) {
                        Column to = rm.getJoin(0).getTo();
                        addJoin(path, to);
                        setColumn(path, Path.get
                                  (path.getParent() + "__" + to));
                    } else if (rm.isJoinThrough()) {
                        addJoin(path, rm.getJoin(0).getTo());
                        setColumn
                            (path, Path.get
                             (path.getParent() + "__" +
                              rm.getJoin(1).getFrom()));
                    } else {
                        throw new Error("huh?");
                    }
                }
            });
    }

    private Condition filter(Filter filter) {
        if (filter == null) {
            return null;
        }

        final Condition[] result = { null };

        filter.dispatch(new Filter.Switch() {
                public void onAnd(AndFilter f) {
                    result[0] = new AndCondition(filter(f.getLeft()),
                                                 filter(f.getRight()));
                }

                public void onOr(OrFilter f) {
                    result[0] = new OrCondition(filter(f.getLeft()),
                                                filter(f.getRight()));
                }

                public void onNot(NotFilter f) {
                    result[0] = new NotCondition(filter(f.getOperand()));
                }

                public void onEquals(EqualsFilter f) {
                    genPath(f.getLeft());
                    genPath(f.getRight());
                    result[0] = new EqualsCondition(getColumn(f.getLeft()),
                                                    getColumn(f.getRight()));
                }

                public void onIn(InFilter f) {
                    genPath(f.getPath());
                    QGen qgen = new QGen(f.getQuery());
                    result[0] = new InCondition(getColumn(f.getPath()),
                                                qgen.generate());
                }

                public void onContains(ContainsFilter f) {
                    genPath(f.getCollection());
                    genPath(f.getElement());
                    result[0] = new EqualsCondition
                        (getColumn(f.getCollection()),
                         getColumn(f.getElement()));
                }
            });

        return result[0];
    }

}
