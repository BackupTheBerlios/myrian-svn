package com.arsdigita.persistence.proto.engine.rdbms;

import com.arsdigita.persistence.proto.*;
import com.arsdigita.persistence.proto.Query;
import com.arsdigita.persistence.proto.common.*;
import com.arsdigita.persistence.proto.metadata.*;
import com.arsdigita.persistence.proto.pdl.SQLParser;
import com.arsdigita.persistence.proto.pdl.ParseException;

import java.util.*;
import java.sql.*;
import java.io.*;


/**
 * QGen
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #11 $ $Date: 2003/02/28 $
 **/

class QGen {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/engine/rdbms/QGen.java#11 $ by $Author: rhs $, $DateTime: 2003/02/28 19:58:14 $";

    private static final HashMap SOURCES = new HashMap();
    private static final HashMap BLOCKS = new HashMap();
    private static final HashMap PREFIXES = new HashMap();

    static final boolean hasSource(SQLBlock block) {
        return SOURCES.containsKey(block);
    }

    static final Source getSource(SQLBlock block) {
        return (Source) SOURCES.get(block);
    }

    static final boolean hasSQLBlock(Source src) {
        return BLOCKS.containsKey(src);
    }

    static final SQLBlock getSQLBlock(Source src) {
        return (SQLBlock) BLOCKS.get(src);
    }

    static final void addSource(Source src, SQLBlock block, Path prefix) {
        SOURCES.put(block, src);
        BLOCKS.put(src, block);
        PREFIXES.put(block, prefix);
    }

    static final Path getPrefix(SQLBlock block) {
        return (Path) PREFIXES.get(block);
    }


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

        Environment env = new Environment();

        for (Iterator it = sig.getSources().iterator(); it.hasNext(); ) {
            Source src = (Source) it.next();

            Join j;

            if (hasSQLBlock(src)) {
                SQLBlock block = getSQLBlock(src);
                Path prefix = getPrefix(block);
                for (Iterator iter = block.getPaths().iterator();
                     iter.hasNext(); ) {
                    Path path = (Path) iter.next();
                    Path column = block.getMapping(path);
                    if (prefix != null) {
                        path = prefix.getRelative(path);
                    }
                    setColumn(path, column);
                    if (src.getObjectType().isKey(path)) {
                        setColumn(path.getParent(), column);
                    }
                }
                Path alias = Path.get(src.getPath() + "__static");
                j = new StaticJoin(new StaticOperation(block, env), alias);
            } else {
                ObjectMap map =
                    Root.getRoot().getObjectMap(src.getObjectType());
                Table start = map.getTable();
                if (start == null) {
                    throw new Error("no metadata");
                }
                Path alias = Path.get(src.getPath() + "__" + start.getName());
                j = new SimpleJoin(start, alias);
                setColumn(src.getPath(),
                          Path.get(src.getPath() + "__" + getKey(start)));
                addTable(src.getPath(), start);
            }

            setJoin(src, j);
        }

        for (Iterator it = sig.getPaths().iterator(); it.hasNext(); ) {
            Path path = (Path) it.next();
            genPath(path);
        }

        for (Iterator it = m_query.getOrder().iterator(); it.hasNext(); ) {
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

        Select result = new Select(join, condition, env);

        int col = 0;
        for (Iterator it = sig.getPaths().iterator(); it.hasNext(); ) {
            Path path = (Path) it.next();
            result.addSelection(getColumn(path), "column" + (col++));
        }

        for (Iterator it = m_query.getOrder().iterator(); it.hasNext(); ) {
            Path path = (Path) it.next();
            result.addOrder(getColumn(path), m_query.isAscending(path));
        }

        for (Iterator it = sig.getParameters().iterator(); it.hasNext(); ) {
            Parameter param = (Parameter) it.next();
            if (param.getObjectType().hasKey()) {
                result.set(param.getPath(),
                           RDBMSEngine.getKeyValue(m_query.get(param)),
                           Types.INTEGER);
            } else {
                result.set(param.getPath(), m_query.get(param),
                           Types.INTEGER);
            }
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

    private boolean isOuter(Path path) {
        if (path == null) {
            return false;
        }

        Property prop = m_query.getSignature().getProperty(path);
        if (prop.isNullable()) {
            return true;
        } else {
            return isOuter(path.getParent());
        }
    }

    private void addJoin(Path path, Column to) {
        Path parentColumn = getColumn(path.getParent());
        Table table = to.getTable();
        if (!getTables(path.getParent()).contains(table)) {
            Join join = getJoin(path);
            Join simple = new SimpleJoin
                (table, Path.get(path.getParent() + "__" + table.getName()));
            Condition cond = new EqualsCondition
                (parentColumn, Path.get(path.getParent() + "__" + to));
            if (isOuter(path)) {
                join = new LeftJoin(join, simple, cond);
            } else {
                join = new InnerJoin(join, simple, cond);
            }
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

        if (m == null) {
            throw new Error("no metadata for path: " + path);
        }

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
                                  (path.getParent() + "__" +
                                   getKey(to.getTable())));
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

                public void onStatic(StaticMapping sm) {
                    // do nothing
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

                public void onPassthrough(final PassthroughFilter f) {
                    SQLParser p = new SQLParser
                        (new StringReader(f.getConditions()),
                         new SQLParser.Mapper() {
                                 public Path map(Path path) {
                                     genPath(path);
                                     return getColumn(path);
                                 }
                             });

                    try {
                        p.sql();
                    } catch (ParseException e) {
                        throw new Error(e.getMessage());
                    }

                    StaticCondition sc = new StaticCondition(p.getSQL());
                    for (Iterator it = p.getBindings().iterator();
                         it.hasNext(); ) {
                        Path path = (Path) it.next();
                        sc.addBinding(f.getParameter(path));
                    }
                    result[0] = sc;
                }
            });

        return result[0];
    }

}
