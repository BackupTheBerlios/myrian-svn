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
import com.redhat.persistence.Parameter;
import com.redhat.persistence.Query;
import com.redhat.persistence.Signature;
import com.redhat.persistence.Source;
import com.redhat.persistence.common.CompoundKey;
import com.redhat.persistence.common.ParseException;
import com.redhat.persistence.common.Path;
import com.redhat.persistence.common.SQLParser;
import com.redhat.persistence.metadata.Column;
import com.redhat.persistence.metadata.Constraint;
import com.redhat.persistence.metadata.JoinFrom;
import com.redhat.persistence.metadata.JoinThrough;
import com.redhat.persistence.metadata.JoinTo;
import com.redhat.persistence.metadata.Mapping;
import com.redhat.persistence.metadata.ObjectMap;
import com.redhat.persistence.metadata.Property;
import com.redhat.persistence.metadata.Root;
import com.redhat.persistence.metadata.SQLBlock;
import com.redhat.persistence.metadata.Static;
import com.redhat.persistence.metadata.Table;
import com.redhat.persistence.metadata.Value;
import java.io.StringReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;


/**
 * QGen
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #11 $ $Date: 2003/10/28 $
 **/

class QGen {

    public final static String versionId = "$Id: //core-platform/dev/src/com/redhat/persistence/engine/rdbms/QGen.java#11 $ by $Author: jorris $, $DateTime: 2003/10/28 18:36:21 $";

    private static final Logger LOG = Logger.getLogger(QGen.class);

    private static final Collection s_functions = new HashSet();
    static {
        String[] functions = {
            /* sql standard functions supported by both oracle and postgres.
             * there is an added caveat that the function uses normal function
             * syntax and not keywords as arguments (e.g. trim)
             */
            "current_date", "current_timestamp",
            "upper", "lower",
            // postgres supported oracle-isms
            "substr", "length", "nvl"
        };
        for (int i = 0; i < functions.length; i++) {
            s_functions.add(Path.get(functions[i]));
        }
    }

    private static final boolean isAllowedFunction(Path p) {
        return s_functions.contains(p);
    }

    private static final HashMap SOURCES = new HashMap();
    private static final HashMap BLOCKS = new HashMap();
    private static final HashMap PREFIXES = new HashMap();

    static synchronized final boolean hasSource(SQLBlock block) {
        return SOURCES.containsKey(block);
    }

    static synchronized final Source getSource(SQLBlock block) {
        return (Source) SOURCES.get(block);
    }

    static synchronized final boolean hasSQLBlock(Source src) {
        return BLOCKS.containsKey(src);
    }

    static synchronized final SQLBlock getSQLBlock(Source src) {
        return (SQLBlock) BLOCKS.get(src);
    }

    static synchronized final void addSource(Source src, SQLBlock block,
                                             Path prefix) {
        SOURCES.put(block, src);
        BLOCKS.put(src, block);
        PREFIXES.put(block, prefix);
    }

    static synchronized final Path getPrefix(SQLBlock block) {
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
    private RDBMSEngine m_engine;
    private Root m_root;

    public QGen(RDBMSEngine engine, Query query) {
	this(engine, query, null);
    }

    public QGen(RDBMSEngine engine, Query query, SQLBlock block) {
        if (query.getSignature().getSources().size() == 0) {
            throw new IllegalArgumentException
                ("no sources");
        }
        m_engine = engine;
        m_query = query;
	m_block = block;
        m_anal = new Analyzer(m_query.getFilter());
        m_root = m_engine.getSession().getRoot();
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
        String[] keywords = {
            // postgres keywords (non-reserved and reserved) from 7.3 docs
            "abort", "abs", "absolute", "access", "action", "ada", "add", "admin", "after", "aggregate", "", "alias", "all", "allocate", "alter", "analyse", "analyze", "and", "any", "are", "array", "as", "asc", "asensitive", "assertion", "assignment", "asymmetric", "at", "atomic", "authorization", "avg", "backward", "before", "begin", "between", "bigint", "binary", "bit", "bitvar", "bit_length", "blob", "boolean", "both", "breadth", "by", "c", "cache", "call", "called", "cardinality", "cascade", "cascaded", "case", "cast", "catalog", "catalog_name", "chain", "char", "character", "characteristics", "character_length", "character_set_catalog", "character_set_name", "character_set_schema", "char_length", "check", "checked", "checkpoint", "class", "class_origin", "clob", "close", "cluster", "coalesce", "cobol", "collate", "collation", "collation_catalog", "collation_name", "collation_schema", "column", "column_name", "command_function", "command_function_code", "comment", "commit", "committed", "completion", "condition_number", "connect", "connection", "connection_name", "constraint", "constraints", "constraint_catalog", "constraint_name", "constraint_schema", "constructor", "contains", "continue", "conversion", "convert", "copy", "corresponding", "count", "create", "createdb", "createuser", "cross", "cube", "current", "current_date", "current_path", "current_role", "current_time", "current_timestamp", "current_user", "cursor", "cursor_name", "cycle", "data", "database", "date", "datetime_interval_code", "datetime_interval_precision", "day", "deallocate", "dec", "decimal", "declare", "default", "deferrable", "deferred", "defined", "definer", "delete", "delimiter", "delimiters", "depth", "deref", "desc", "describe", "descriptor", "destroy", "destructor", "deterministic", "diagnostics", "dictionary", "disconnect", "dispatch", "distinct", "do", "domain", "double", "drop", "dynamic", "dynamic_function", "dynamic_function_code", "each", "else", "encoding", "encrypted", "end", "end-exec", "equals", "escape", "every", "except", "exception", "exclusive", "exec", "execute", "existing", "exists", "explain", "external", "extract", "false", "fetch", "final", "first", "float", "for", "force", "foreign", "fortran", "forward", "found", "free", "freeze", "from", "full", "function", "g", "general", "generated", "get", "global", "go", "goto", "grant", "granted", "group", "grouping", "handler", "having", "hierarchy", "hold", "host", "hour", "identity", "ignore", "ilike", "immediate", "immutable", "implementation", "implicit", "in", "increment", "index", "indicator", "infix", "inherits", "initialize", "initially", "inner", "inout", "input", "insensitive", "insert", "instance", "instantiable", "instead", "int", "integer", "intersect", "interval", "into", "invoker", "is", "isnull", "isolation", "iterate", "join", "k", "key", "key_member", "key_type", "lancompiler", "language", "large", "last", "lateral", "leading", "left", "length", "less", "level", "like", "limit", "listen", "load", "local", "localtime", "localtimestamp", "location", "locator", "lock", "lower", "m", "map", "match", "max", "maxvalue", "message_length", "message_octet_length", "message_text", "method", "min", "minute", "minvalue", "mod", "mode", "modifies", "modify", "module", "month", "more", "move", "mumps", "name", "names", "national", "natural", "nchar", "nclob", "new", "next", "no", "nocreatedb", "nocreateuser", "none", "not", "nothing", "notify", "notnull", "null", "nullable", "nullif", "number", "numeric", "object", "octet_length", "of", "off", "offset", "oids", "old", "on", "only", "open", "operation", "operator", "option", "options", "or", "order", "ordinality", "out", "outer", "output", "overlaps", "overlay", "overriding", "owner", "pad", "parameter", "parameters", "parameter_mode", "parameter_name", "parameter_ordinal_position", "parameter_specific_catalog", "parameter_specific_name", "parameter_specific_schema", "partial", "pascal", "password", "path", "pendant", "placing", "pli", "position", "postfix", "precision", "prefix", "preorder", "prepare", "preserve", "primary", "prior", "privileges", "procedural", "procedure", "public", "read", "reads", "real", "recheck", "recursive", "ref", "references", "referencing", "reindex", "relative", "rename", "repeatable", "replace", "reset", "restrict", "result", "return", "returned_length", "returned_octet_length", "returned_sqlstate", "returns", "revoke", "right", "role", "rollback", "rollup", "routine", "routine_catalog", "routine_name", "routine_schema", "row", "rows", "row_count", "rule", "savepoint", "scale", "schema", "schema_name", "scope", "scroll", "search", "second", "section", "security", "select", "self", "sensitive", "sequence", "serializable", "server_name", "session", "session_user", "set", "setof", "sets", "share", "show", "similar", "simple", "size", "smallint", "some", "source", "space", "specific", "specifictype", "specific_name", "sql", "sqlcode", "sqlerror", "sqlexception", "sqlstate", "sqlwarning", "stable", "start", "state", "statement", "static", "statistics", "stdin", "stdout", "storage", "strict", "structure", "style", "subclass_origin", "sublist", "substring", "sum", "symmetric", "sysid", "system", "system_user", "table", "table_name", "temp", "template", "temporary", "terminate", "than", "then", "time", "timestamp", "timezone_hour", "timezone_minute", "to", "toast", "trailing", "transaction", "transactions_committed", "transactions_rolled_back", "transaction_active", "transform", "transforms", "translate", "translation", "treat", "trigger", "trigger_catalog", "trigger_name", "trigger_schema", "trim", "true", "truncate", "trusted", "type", "uncommitted", "under", "unencrypted", "union", "unique", "unknown", "unlisten", "unnamed", "unnest", "until", "update", "upper", "usage", "user", "user_defined_type_catalog", "user_defined_type_name", "user_defined_type_schema", "using", "vacuum", "valid", "validator", "value", "values", "varchar", "variable", "varying", "verbose", "version", "view", "volatile", "when", "whenever", "where", "with", "without", "work", "write", "year", "zone",

            // oracle keywords from 8.1.7 docs
            "access", "add", "all", "alter", "and", "any", "as", "asc", "audit", "between", "by", "char", "check", "cluster", "column", "comment", "compress", "connect", "create", "current", "date", "decimal", "default", "delete", "desc", "distinct", "drop", "else", "exclusive", "exists", "file", "float", "for", "from", "grant", "group", "having", "identified", "immediate", "in", "increment", "index", "initial", "insert", "integer", "intersect", "into", "is", "level", "like", "lock", "long", "maxextents", "minus", "mlslabel", "mode", "modify", "noaudit", "nocompress", "not", "nowait", "null", "number", "of", "offline", "on", "online", "option", "or", "order", "pctfree", "prior", "privileges", "public", "raw", "rename", "resource", "revoke", "row", "rowid", "rownum", "rows", "select", "session", "set", "share", "size", "smallint", "start", "successful", "synonym", "sysdate", "table", "then", "to", "trigger", "uid", "union", "unique", "update", "user", "validate", "values", "varchar", "varchar2", "view", "whenever", "where", "with"
        };

        for (int i = 0; i < keywords.length; i++) {
            RESERVED.add(keywords[i].toLowerCase());
        }

        // used internally
        RESERVED.add("c_");
    }

    private String abbreviateHard(String name) {
        StringBuffer buf = new StringBuffer(name.length());
        int index = 0;
        while (true) {
            buf.append(Character.toLowerCase(name.charAt(index)));
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
        while (RESERVED.contains(candidate)) {
            candidate = candidate + "_";
        }
        return candidate;
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
            (m_engine, m_root.getObjectMap(sig.getObjectType()));

        for (Iterator it = sig.getSources().iterator(); it.hasNext(); ) {
            Source src = (Source) it.next();

            Join j;

            if (isStatic(src)) {
                SQLBlock block = getBlock(src);
                Path alias = makeAlias(src.getPath(), "st_");
                j = new StaticJoin
		    (new StaticOperation(m_engine, block, env, false), alias);
            } else {
                ObjectMap map = m_root.getObjectMap(src.getObjectType());
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

        Select result = new Select(m_engine, join, filter, env);
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
            Path[] paths = m_engine.getKeyPaths
                (param.getObjectType(), null);
            for (int i = 0; i < paths.length; i++) {
                result.set
                    (Path.add(param.getPath(), paths[i]),
                     m_engine.get(m_query.get(param), paths[i]),
                     m_engine.getType
                     (m_root,
                      param.getObjectType().getType(paths[i]).getJavaClass()));
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
            setColumns(path, m_engine.getKeyPaths
                       (p.getObjectType(), p.getPath()));
            return true;
        }

	// Handle staticly mapped paths
	Source src = getSource(path);
	if (isStatic(src)) {
	    SQLBlock block = getBlock(src);
	    Path prefix = getPrefix(block);
	    Path[] paths = m_engine.getKeyPaths
		(m_query.getSignature().getType(path), Path.add(prefix, path));
	    cols = new Path[paths.length];
	    boolean failed = false;
	    for (int i = 0; i < cols.length; i++) {
                Path mapping = block.getMapping(paths[i]);
		if (mapping == null) {
		    failed = true;
		    break;
		}
                cols[i] =
                    Path.add((((StaticJoin) getJoin(src))).getAlias(), mapping);
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
        ObjectMap map = m_root.getObjectMap(prop.getContainer());
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
                 new SQLParser.IdentityMapper() {
                     public Path map(Path path) {
                         if (m_root.hasObjectType(path.getPath())) {
                             return path;
                         } else if (m_query.getSignature().exists(path)) {
                             genPath(path);
                             return getColumn(path);
                         } else if (isAllowedFunction(path)) {
                             return path;
                         } else {
                             StringBuffer msg = new StringBuffer();
                             msg.append("unknown value in expression: ");
                             msg.append(path);
                             msg.append("\nquery signature: ");
                             msg.append(m_query.getSignature());
                             throw new RDBMSException(msg.toString()) {};
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
