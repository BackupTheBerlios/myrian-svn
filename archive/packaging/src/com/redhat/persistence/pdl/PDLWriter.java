package com.redhat.persistence.pdl;

import com.arsdigita.util.UncheckedWrapperException;
import com.redhat.persistence.common.*;
import com.redhat.persistence.metadata.*;

import java.util.*;

import java.io.Writer;
import java.io.IOException;

/**
 * PDLWriter
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/08/14 $
 **/

public class PDLWriter {

    public final static String versionId = "$Id: //core-platform/test-packaging/src/com/redhat/persistence/pdl/PDLWriter.java#1 $ by $Author: dennis $, $DateTime: 2003/08/14 14:53:20 $";

    private Writer m_out;

    public PDLWriter(Writer out) {
	m_out = out;
    }

    private void write(String str) {
	try {
	    m_out.write(str);
	} catch (IOException e) {
	    throw new UncheckedWrapperException(e);
	}
    }

    public void write(ObjectType type) {
	write("object type ");

	write(type.getName());

	if (type.getSupertype() != null) {
	    write(" extends ");
	    write(type.getSupertype().getQualifiedName());
	}

	if (type.getJavaClass() != null) {
	    write(" class ");
	    write(type.getJavaClass().getName());
	}

	// XXX: We don't store adapter names so we have no way of
	// writing them out.

	write(" {");

	for (Iterator it = type.getDeclaredProperties().iterator();
             it.hasNext(); ) {
            Property prop = (Property) it.next();
            if (!(prop instanceof Role)) { continue; }
            Role role = (Role) prop;
            if (role.getName().startsWith("~")) { continue; }
            Role rev = role.getReverse();
            if (rev != null && !rev.getName().startsWith("~")) { continue; }
            write("\n");
            write(prop);
	}

	ObjectMap om = type.getRoot().getObjectMap(type);

	if (type.getSupertype() == null && type.isKeyed()) {
	    write("\n\n    object key (");
	    for (Iterator it = type.getKeyProperties().iterator();
		 it.hasNext(); ) {
		Property prop = (Property) it.next();
		write(prop.getName());
		if (it.hasNext()) {
		    write(", ");
		}
	    }
	    write(");");
	} else if (om.getTable() != null) {
	    write("\n\n    reference key (");
	    UniqueKey uk = om.getTable().getPrimaryKey();
	    write(uk.getColumns());
	    write(");");
	}

	write("\n}");
    }

    private void write(Column[] cols) {
	for (int i = 0; i < cols.length; i++) {
	    write(cols[i]);
	    if (i < cols.length - 1) {
		write(", ");
	    }
	}
    }

    private void write(Column col) {
	write(col.getTable().getName());
	write(".");
	write(col.getName());
    }

    private void write(Property prop) {
	write("    ");

	if (prop.isComponent()) {
	    write("component ");
	} else if (prop.isComposite()) {
            write("composite ");
        }

        ObjectType type = prop.getType();
        if (type.getModel().getName().equals("global")) {
            write(type.getName());
        } else {
            write(type.getQualifiedName());
        }

	if (prop.isCollection()) {
	    write("[0..n] ");
	} else if (prop.isNullable()) {
	    write("[0..1] ");
	} else {
	    write("[1..1] ");
	}

	write(prop.getName());
        write(" = ");

	ObjectMap om = prop.getRoot().getObjectMap(prop.getContainer());

	Mapping m = om.getMapping(Path.get(prop.getName()));
        if (m == null) {
            write("<no mapping for " + prop.getName() + " in " +
                  prop.getContainer().getName() + ">");
        } else {
            m.dispatch(new Mapping.Switch() {
		public void onValue(Value v) {
                    Column col = v.getColumn();
		    write(col);
                    write(" ");
                    write(Column.getTypeName(col.getType()));
                    if (col.getSize() >= 0) {
                        write("(" + col.getSize());
                        if (col.getScale() >= 0) {
                            write(", " + col.getScale());
                        }
                        write(")");
                    }
		}

		public void onJoinTo(JoinTo j) {
		    write("join ");
		    write(j.getKey().getColumns());
                    write(" to ");
                    write(j.getKey().getUniqueKey().getColumns());
		}

		public void onJoinFrom(JoinFrom j) {
		    write("join ");
                    write(j.getKey().getUniqueKey().getColumns());
                    write(" to ");
		    write(j.getKey().getColumns());
		}

		public void onJoinThrough(JoinThrough j) {
		    write("join ");
                    write(j.getFrom().getUniqueKey().getColumns());
                    write(" to ");
		    write(j.getFrom().getColumns());
                    write(", join ");
		    write(j.getTo().getColumns());
                    write(" to ");
                    write(j.getTo().getUniqueKey().getColumns());
		}

		public void onStatic(Static s) {
		    write("<static>");
		}
	    });
        }

	write(";");
    }

    public void writeAssociation(Property prop) {
        Role role = (Role) prop;
        Role rev = role.getReverse();
        if (rev == null) {
            throw new IllegalArgumentException
                ("not a two way");
        }
        write("association {\n");
        write(role);
        write("\n");
        write(rev);
        write("\n}");
    }

}
