package com.arsdigita.persistence.proto.engine.rdbms;

import com.arsdigita.persistence.proto.*;
import com.arsdigita.persistence.proto.common.*;
import com.arsdigita.persistence.proto.metadata.*;
import com.arsdigita.persistence.proto.metadata.Table;
import com.arsdigita.persistence.proto.metadata.Column;

import java.util.*;


/**
 * QGen
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/02/05 $
 **/

class QGen {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/engine/rdbms/QGen.java#1 $ by $Author: rhs $, $DateTime: 2003/02/05 18:34:37 $";

    private Signature m_signature;
    private HashMap m_columns = new HashMap();
    private HashMap m_tables = new HashMap();
    private HashMap m_joins = new HashMap();

    public QGen(Signature signature) {
        if (signature.getSources().size() == 0) {
            throw new IllegalArgumentException
                ("no sources");
        }
        m_signature = signature;
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

    private Column getKey(Table table) {
        UniqueKey key = table.getPrimaryKey();
        if (key == null) {
            throw new Error("table has no primary key: " + table);
        } else {
            return key.getColumns()[0];
        }
    }

    public Select generate() {
        for (Iterator it = m_signature.getSources().iterator();
             it.hasNext(); ) {
            Source src = (Source) it.next();
            ObjectMap map = Root.getRoot().getObjectMap(src.getObjectType());
            Table start = map.getTable();
            if (start == null) {
                return null;
            }
            Join j = new SimpleJoin
                (start, Path.get(src.getPath() + ":" + start.getName()));
            setColumn(src.getPath(), Path.get(src.getPath() + ":" +
                                              getKey(start)));
            setJoin(src, j);
            addTable(src.getPath(), start);
        }

        for (Iterator it = m_signature.getPaths().iterator();
             it.hasNext(); ) {
            Path path = (Path) it.next();
            genPath(path);
        }

        Join join = null;

        for (Iterator it = m_signature.getSources().iterator();
             it.hasNext(); ) {
            Source src = (Source) it.next();
            if (join == null) {
                join = getJoin(src);
            } else {
                join = new CrossJoin(join, getJoin(src));
            }
        }

        Select result = new Select(join);

        for (Iterator it = m_signature.getPaths().iterator();
             it.hasNext(); ) {
            Path path = (Path) it.next();
            result.addSelection(getColumn(path));
        }

        return result;
    }

    Source getSource(Path path) {
        Source src = m_signature.getSource(path);
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
                (table, Path.get(path.getParent() + ":" + table.getName()));
            join = new InnerJoin
                (join, simple, new EqualsCondition
                    (parentColumn, Path.get(path.getParent() + ":" + to)));
            setJoin(path, join);
            addTable(path.getParent(), table);
        }
    }

    private void genPath(final Path path) {
        Path column = getColumn(path);
        if (column != null) {
            return;
        }

        genPath(path.getParent());

        Property prop = m_signature.getObjectType().getProperty(path);
        ObjectMap map = Root.getRoot().getObjectMap(prop.getContainer());
        Mapping m = map.getMapping(Path.get(prop.getName()));

        // XXX: no metadata
        if (m == null) { return; }

        m.dispatch(new Mapping.Switch() {
                public void onValue(ValueMapping vm) {
                    addJoin(path, getKey(vm.getColumn().getTable()));
                    setColumn(path, Path.get(path.getParent() + ":" +
                                             vm.getColumn()));
                }

                public void onReference(ReferenceMapping rm) {
                    if (rm.isJoinTo()) {
                        Column to = rm.getJoin(0).getFrom();
                        addJoin(path, getKey(to.getTable()));
                        setColumn(path, Path.get(path.getParent() + ":" + to));
                    } else if (rm.isJoinFrom()) {
                        Column to = rm.getJoin(0).getTo();
                        addJoin(path, to);
                        setColumn(path, Path.get(path + ":" + to));
                    } else if (rm.isJoinThrough()) {
                        addJoin(path, rm.getJoin(0).getTo());
                        setColumn
                            (path, Path.get
                             (path + ":" + rm.getJoin(1).getFrom()));
                    } else {
                        throw new Error("huh?");
                    }
                }
            });
    }

}
