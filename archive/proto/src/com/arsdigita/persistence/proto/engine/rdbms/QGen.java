package com.arsdigita.persistence.proto.engine.rdbms;

import com.arsdigita.persistence.proto.*;
import com.arsdigita.persistence.proto.common.*;
import com.arsdigita.persistence.proto.metadata.*;

import java.util.*;
import java.sql.*;
import java.io.*;


/**
 * QGen
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #23 $ $Date: 2003/04/07 $
 **/

class QGen {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/engine/rdbms/QGen.java#23 $ by $Author: rhs $, $DateTime: 2003/04/07 19:44:54 $";

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
    private SQLBlock m_block;
    private HashMap m_columns = new HashMap();
    private HashMap m_keys = new HashMap();
    private HashMap m_tables = new HashMap();
    private HashMap m_joins = new HashMap();

    public QGen(Query query) {
	this(query, null);
    }

    public QGen(Query query, SQLBlock block) {
        if (query.getSignature().getSources().size() == 0) {
            throw new IllegalArgumentException
                ("no sources");
        }
        m_query = query;
	m_block = block;
    }

    private Path getColumn(Path prefix) {
        Path[] result = getColumns(prefix);
        if (result.length != 1) {
            throw new IllegalStateException
                ("one column requested for multi-column prefix");
        }
        return result[0];
    }

    private Path[] getColumns(Path prefix) {
        return (Path[]) m_columns.get(prefix);
    }

    private void setColumns(Path prefix, Path[] columns) {
        m_columns.put(prefix, columns);
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

    private Path[] getColumns(SQLBlock block, Path[] paths) {
        Path[] result = new Path[paths.length];
        for (int i = 0; i < paths.length; i++) {
            result[i] = block.getMapping(paths[i]);
        }
        return result;
    }

    private boolean isStatic(Source src) {
	if (src.getPath() == null && m_block != null) {
	    return true;
	} else {
	    return hasSQLBlock(src);
	}
    }

    private SQLBlock getBlock(Source src) {
	if (m_block != null && src.getPath() == null) {
	    return m_block;
	} else {
	    return getSQLBlock(src);
	}
    }

    public Select generate() {
        Signature sig = m_query.getSignature();

        Environment env = new Environment();

        for (Iterator it = sig.getSources().iterator(); it.hasNext(); ) {
            Source src = (Source) it.next();

            Join j;

            if (isStatic(src)) {
                SQLBlock block = getBlock(src);
                Path alias;
		if (src.getPath() == null) {
		    alias = Path.get("__static");
		} else {
		    alias = Path.get(src.getPath() + "__static");
		}
                j = new StaticJoin
		    (new StaticOperation(block, env, false), alias);
            } else {
                ObjectMap map =
		    Root.getRoot().getObjectMap(src.getObjectType());

                Table start = null;
		ObjectMap om = map;
		while (om != null) {
		    start = om.getTable();
		    if (start != null) {
			break;
		    }
		    om = om.getSuperMap();
		}

                if (start == null) {
                    throw new IllegalStateException
			("can't find base table for type: " +
			 map.getObjectType());
                }

                Path alias;
		if (src.getPath() == null) {
		    alias = Path.get(start.getName());
		} else {
		    alias = Path.get
			(src.getPath().getPath().replace('.', '_') + "__" +
			 start.getName());
		}
                j = new SimpleJoin(start, alias);

                setColumns(src.getPath(),
                           getPaths(alias, start.getPrimaryKey()));
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
	    if (m_query.isDefaulted(path)) {
		genPath(m_query.getDefault(path));
	    }
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
	    if (m_query.isDefaulted(path)) {
		result.addOrder(getColumn(path), m_query.isAscending(path),
				getColumn(m_query.getDefault(path)));
	    } else {
		result.addOrder(getColumn(path), m_query.isAscending(path));
	    }
        }

        for (Iterator it = sig.getParameters().iterator(); it.hasNext(); ) {
            Parameter param = (Parameter) it.next();
            Path[] paths = RDBMSEngine.getKeyPaths
                (param.getObjectType(), null);
            for (int i = 0; i < paths.length; i++) {
                result.set
                    (Path.add(param.getPath(), paths[i]),
                     RDBMSEngine.get(m_query.get(param), paths[i]),
                     Types.INTEGER);
            }
        }

        result.setLimit(m_query.getLimit());
        result.setOffset(m_query.getOffset());

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
	if (prop == null) {
	    return false;
	} else if (prop.isNullable()) {
            return true;
        } else {
            return isOuter(path.getParent());
        }
    }

    private Path[] getPaths(Path alias, Constraint constraint) {
        Column[] cols = constraint.getColumns();
        Path[] result = new Path[cols.length];
        for (int i = 0; i < cols.length; i++) {
            result[i] = Path.add(alias, cols[i].getName());
        }
        return result;
    }

    private Path addJoin(Path path, Constraint constraint) {
        Table table = constraint.getTable();
	Path parent = path.getParent();

	Path alias;
	if (parent == null) {
	    alias = Path.get(table.getName());
	} else {
	    alias = Path.get(parent.getPath().replace('.', '_') +
			     "__" + table.getName());
	}

        if (!getTables(parent).contains(table)) {
            Join join = getJoin(path);
            Join simple = new SimpleJoin(table, alias);
            Path[] cols = getColumns(parent);
            Condition cond = Condition.equals
                (cols, getPaths(alias, constraint));
            if (isOuter(path)) {
                join = new LeftJoin(join, simple, cond);
            } else {
                join = new InnerJoin(join, simple, cond);
            }
            setJoin(path, join);
            addTable(parent, table);
        }

        return alias;
    }

    private void genPath(Path path) {
	if (!m_query.getSignature().exists(path)) {
	    throw new IllegalArgumentException
		("no such path: " + path);
	}

	if (!genPathRecursive(path)) {
	    throw new IllegalStateException
		("unable to generate sql for path: " + path);
	}
    }

    private boolean genPathRecursive(final Path path) {
	// Make sure we don't generate stuff twice.
        Path[] cols = getColumns(path);
        if (cols != null) {
            return true;
        }

	// Handle parameters
        Parameter p = m_query.getSignature().getParameter(path);
        if (p != null) {
            setColumns(path, RDBMSEngine.getKeyPaths
                       (p.getObjectType(), p.getPath()));
            return true;
        }

	// Handle staticly mapped paths
	Source src = getSource(path);
	if (isStatic(src)) {
	    SQLBlock block = getBlock(src);
	    Path prefix = getPrefix(block);
	    Path[] paths = RDBMSEngine.getKeyPaths
		(m_query.getSignature().getType(path), Path.add(prefix, path));
	    cols = new Path[paths.length];
	    boolean failed = false;
	    for (int i = 0; i < cols.length; i++) {
		cols[i] = block.getMapping(paths[i]);
		if (cols[i] == null) {
		    failed = true;
		    break;
		}
	    }

	    if (!failed) {
		setColumns(path, cols);
		return true;
	    }
	}

	if (path == null) {
	    return false;
	}

        if (!genPathRecursive(path.getParent())) {
	    return false;
	}

        Property prop = m_query.getSignature().getProperty(path);
        ObjectMap map = Root.getRoot().getObjectMap(prop.getContainer());
        Mapping m = map.getMapping(Path.get(prop.getName()));

        m.dispatch(new Mapping.Switch() {
                public void onValue(Value m) {
                    Path alias = addJoin
                        (path, m.getColumn().getTable().getPrimaryKey());
                    setColumns(path, new Path[] {
                        Path.add(alias, m.getColumn().getName())
                    });
                }

                public void onJoinTo(JoinTo m) {
                    Path alias = addJoin(path, m.getKey());
                    setColumns(path, getPaths(alias, m.getKey()));
                }

                public void onJoinFrom(JoinFrom m) {
                    Path alias = addJoin(path, m.getKey());
                    setColumns(path, getPaths
                               (alias, m.getKey().getTable().getPrimaryKey()));
                }

                public void onJoinThrough(JoinThrough m) {
                    Path alias = addJoin(path, m.getFrom());
                    setColumns(path, getPaths(alias, m.getTo()));
                }

                public void onStatic(Static m) {
                    throw new IllegalStateException
                        ("no mapping for: " + m.getPath());
                }
            });

	return true;
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
                    result[0] = Condition.equals(getColumns(f.getLeft()),
                                                 getColumns(f.getRight()));
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
                    result[0] = Condition.equals
                        (getColumns(f.getCollection()),
                         getColumns(f.getElement()));
                }

                public void onPassthrough(final PassthroughFilter f) {
                    SQLParser p = new SQLParser
                        (new StringReader(f.getConditions()),
                         new SQLParser.Mapper() {
                                 public Path map(Path path) {
                                     Root r = Root.getRoot();
                                     if (r.hasObjectType(path.getPath())) {
                                         return path;
                                     } else {
                                         genPath(path);
                                         return getColumn(path);
                                     }
                                 }
                             });

                    try {
                        p.sql();
                    } catch (ParseException e) {
                        throw new Error(e.getMessage());
                    }

                    result[0] = new StaticCondition(p.getSQL());
                }
            });

        return result[0];
    }

}
