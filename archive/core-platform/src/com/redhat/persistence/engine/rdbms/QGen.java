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

import com.redhat.persistence.*;
import com.redhat.persistence.common.*;
import com.redhat.persistence.metadata.*;

import java.util.*;
import java.sql.*;
import java.io.*;

import org.apache.log4j.Logger;


/**
 * QGen
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #3 $ $Date: 2003/08/15 $
 **/

class QGen {

    public final static String versionId = "$Id: //core-platform/dev/src/com/redhat/persistence/engine/rdbms/QGen.java#3 $ by $Author: dennis $, $DateTime: 2003/08/15 13:46:34 $";

    private static final Logger LOG = Logger.getLogger(QGen.class);

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
    private HashMap m_aliases = new HashMap();
    private HashMap m_joins = new HashMap();
    private Analyzer m_anal;
    private Condition m_extra = null;

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
        m_anal = new Analyzer(m_query.getFilter());
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

    private static final HashSet RESERVED = new HashSet();

    static {
        // TODO: expand this list
        RESERVED.add("as");
        RESERVED.add("in");
        RESERVED.add("to");
        RESERVED.add("c_");
    }

    private String abbreviateHard(String name) {
        StringBuffer buf = new StringBuffer(name.length());
        int index = 0;
        while (true) {
            buf.append(name.charAt(index));
            index = name.indexOf('_', index);
            if (index < 0 || index >= name.length()) {
                break;
            }
            index++;
        }

        if (buf.length() == 0) {
            throw new IllegalStateException
                ("empty hard abbreviation produced for " + name);
        }

        String candidate = buf.toString();
        if (RESERVED.contains(candidate)) {
            return candidate + "_";
        } else {
            return candidate;
        }
    }

    private String abbreviate(String name) {
        final int limit = 30;

        String candidate;

        if (name.length() <= limit) {
            candidate = name;
        } else {
            StringBuffer buf = new StringBuffer(name.length());
            boolean preserve = true;
            for (int i = 0; i < name.length(); i++) {
                char c = name.charAt(i);
                if (preserve) {
                    preserve = false;
                    buf.append(c);
                    continue;
                }

                switch (Character.toLowerCase(c)) {
                case 'a':
                case 'e':
                case 'i':
                case 'o':
                case 'u':
                    break;
                case '_':
                    preserve = true;
                default:
                    buf.append(c);
                    break;
                }
            }

            candidate = buf.toString();
        }

        String result = candidate;
        for (int attempt = 1;
             m_aliases.values().contains(Path.get(result));
             attempt++) {
            String id = "" + attempt;
            result = candidate.substring
                (0, Math.min(candidate.length(), limit - id.length())) + id;
        }

        if ("".equals(result)) {
            throw new IllegalStateException
                ("empty abbreviation produced for " + name);
        }

        return result;
    }

    private Path makeAlias(Path prefix, String name) {
	if (prefix == null) {
	    return Path.get(abbreviate(name));
	} else {
	    return Path.get
                (abbreviate(prefix.getPath().replace('.', '_') + "__" + name));
	}
    }

    private Path makeAlias(Path prefix, Table table) {
        return makeAlias(prefix, abbreviateHard(table.getName()));
    }

    private void setAlias(Path prefix, Table table, Path alias) {
        m_aliases.put(new CompoundKey(prefix, table), alias);
    }

    private Path getAlias(Path prefix, Table table) {
        return (Path) m_aliases.get(new CompoundKey(prefix, table));
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

        Environment env = new Environment
            (Root.getRoot().getObjectMap(sig.getObjectType()));

        for (Iterator it = sig.getSources().iterator(); it.hasNext(); ) {
            Source src = (Source) it.next();

            Join j;

            if (isStatic(src)) {
                SQLBlock block = getBlock(src);
                Path alias = makeAlias(src.getPath(), "st_");
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

                Path alias = makeAlias(src.getPath(), start);
                j = new SimpleJoin(start, alias);

                setColumns(src.getPath(),
                           getPaths(alias, start.getPrimaryKey()));
                setAlias(src.getPath(), start, alias);
            }

            setJoin(src, j);
        }

        for (Iterator it = sig.getPaths().iterator(); it.hasNext(); ) {
            Path path = (Path) it.next();
            genPath(path);
        }

        for (Iterator it = m_query.getOrder().iterator(); it.hasNext(); ) {
            Expression e = (Expression) it.next();
            generate(e);
        }

        generate(m_query.getFilter());
        Expression filter;
        if (m_extra == null) {
            filter = m_query.getFilter();
        } else if (m_query.getFilter() == null) {
            filter = m_extra;
        } else {
            filter = Condition.and(m_extra, m_query.getFilter());
        }

        Join join = null;
        for (Iterator it = sig.getSources().iterator(); it.hasNext(); ) {
            Source src = (Source) it.next();
            if (join == null) {
                join = getJoin(src);
            } else {
                join = new CrossJoin(join, getJoin(src));
            }
        }

        Select result = new Select(join, filter, env);
        result.setMappings(m_columns);

        int col = 1;
        for (Iterator it = sig.getPaths().iterator(); it.hasNext(); ) {
            Path path = (Path) it.next();
            result.addSelection(getColumn(path), "c_" + (col++));
        }

        for (Iterator it = m_query.getOrder().iterator(); it.hasNext(); ) {
            Expression e = (Expression) it.next();
            result.addOrder(e, m_query.isAscending(e));
        }

        for (Iterator it = sig.getParameters().iterator(); it.hasNext(); ) {
            Parameter param = (Parameter) it.next();
            Path[] paths = RDBMSEngine.getKeyPaths
                (param.getObjectType(), null);
            for (int i = 0; i < paths.length; i++) {
                result.set
                    (Path.add(param.getPath(), paths[i]),
                     RDBMSEngine.get(m_query.get(param), paths[i]),
                     RDBMSEngine.getType
                     (param.getObjectType().getType(paths[i]).getJavaClass()));
            }
        }

        result.setLimit(m_query.getLimit());
        result.setOffset(m_query.getOffset());

        // For profiling
        result.setQuery(m_query);

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

        if (m_anal.isDuplicate(path)) {
            Path canon = m_anal.getCanonical(path);
            if (!isOuter(canon)) {
                return false;
            }
        }

        Signature sig = m_query.getSignature();
        if (sig.isParameter(path)) {
            return false;
        } else if (sig.isSource(path)) {
            return false;
        } else {
            Property prop = m_query.getSignature().getProperty(path);
            if (prop == null) {
                return false;
            } else if (prop.getType().isKeyed() && prop.isNullable()) {
                return true;
            } else {
                return isOuter(path.getParent());
            }
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

    private SimpleJoin makeSimpleJoin(Table table, Path path) {
        return new SimpleJoin(table, makeAlias(path, table));
    }

    private Path addJoin(Path path, Constraint constraint) {
        Table table = constraint.getTable();
	Path parent = path.getParent();

	Path alias = getAlias(parent, table);
        if (alias != null) {
            return alias;
        }

        SimpleJoin simple = null;

        if (m_anal.isDuplicate(path) &&
            !constraint.equals(table.getPrimaryKey())) {
            Path canon = m_anal.getCanonical(path);
            genPathRecursive(canon);
            alias = getAlias(canon, table);
            if (alias == null) {
                simple = makeSimpleJoin(table, canon);
            }
        } else {
            simple = makeSimpleJoin(table, parent);
        }

        if (simple != null) {
            alias = simple.getAlias();
        }

        Path tmp = Path.add
            (alias, path.getPath().replace('.', '_') + "__tmp__");
        setColumns(tmp, getPaths(alias, constraint));
        Condition cond = Condition.equals(tmp, parent);

        if (simple == null) {
            if (m_extra == null) {
                m_extra = cond;
            } else {
                m_extra = Condition.and(m_extra, cond);
            }
        } else {
            Join join = getJoin(path);
            if (isOuter(path)) {
                join = new LeftJoin(join, simple, cond);
            } else {
                join = new InnerJoin(join, simple, cond);
            }
            setJoin(path, join);
        }

        setAlias(parent, table, alias);
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

        final Property prop = m_query.getSignature().getProperty(path);
        ObjectMap map = Root.getRoot().getObjectMap(prop.getContainer());
        Mapping m = map.getMapping(Path.get(prop.getName()));

        m.dispatch(new Mapping.Switch() {
                public void onValue(Value m) {
                    if (prop.isKeyProperty()) {
                        Path[] keyCols = getColumns(path.getParent());
                        List keys =
                            prop.getContainer().getKeyProperties();
                        setColumns(path, new Path[] {
                            keyCols[keys.indexOf(prop)]
                        });
                    } else {
                        Path alias = addJoin
                            (path, m.getColumn().getTable().getPrimaryKey());
                        setColumns(path, new Path[] {
                            Path.add(alias, m.getColumn().getName())
                        });
                    }
                }

                public void onJoinTo(JoinTo m) {
                    Path alias =
			addJoin(path, m.getKey().getTable().getPrimaryKey());
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

    private final Expression.Switch m_esw = new Expression.Switch() {
        public void onQuery(Query q) {
            throw new Error("not implemented");
        }

        public void onCondition(Condition c) {
            c.dispatch(m_csw);
        }

        public void onVariable(Expression.Variable v) {
            genPath(v.getPath());
        }

        public void onValue(Expression.Value v) {
            // do nothing
        }

        public void onPassthrough(final Expression.Passthrough e) {
            SQLParser p = new SQLParser
                (new StringReader(e.getExpression()),
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
            } catch (ParseException pe) {
                throw new Error(pe.getMessage());
            }
        }
    };

    private final Condition.Switch m_csw = new Condition.Switch() {
        public void onAnd(Condition.And e) {
            generate(e.getLeft());
            generate(e.getRight());
        }

        public void onOr(Condition.Or e) {
            generate(e.getLeft());
            generate(e.getRight());
        }

        public void onNot(Condition.Not e) {
            generate(e.getExpression());
        }

        public void onEquals(Condition.Equals e) {
            generate(e.getLeft());
            generate(e.getRight());
        }

        public void onIn(Condition.In e) {
            generate(e.getLeft());
            generate(e.getRight());
        }

        public void onContains(Condition.Contains e) {
            generate(e.getLeft());
            generate(e.getRight());
        }
    };

    private void generate(Expression e) {
        if (e != null) {
            e.dispatch(m_esw);
        }
    }

}
