package com.arsdigita.persistence.proto.pdl;

import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.persistence.proto.common.*;
import com.arsdigita.persistence.proto.metadata.*;

import java.util.*;

import java.io.Writer;
import java.io.IOException;

/**
 * PDLWriter
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/04/18 $
 **/

public class PDLWriter {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/pdl/PDLWriter.java#1 $ by $Author: rhs $, $DateTime: 2003/04/18 15:09:07 $";

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

	write("{");

	for (Iterator it = type.getProperties().iterator(); it.hasNext(); ) {
	    write((Property) it.next());
	}

	ObjectMap om = type.getRoot().getObjectMap(type);

	if (type.getSupertype() == null) {
	    write("    object key(");
	    for (Iterator it = type.getKeyProperties().iterator();
		 it.hasNext(); ) {
		Property prop = (Property) it.next();
		write(prop.getName());
		if (it.hasNext()) {
		    write(", ");
		}
	    }
	    write(");\n");
	} else if (om.getTable() != null) {
	    write("    reference key(");
	    UniqueKey uk = om.getTable().getPrimaryKey();
	    write(uk.getColumns());
	    write(");\n");
	}

	write("}");
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
	write(col.getName());
	write(".");
	write(col.getTable().getName());
    }

    private void write(Property prop) {
	write("    ");

	if (prop.isComponent()) {
	    write("component ");
	}

	write(prop.getType().getQualifiedName());

	if (prop.isCollection()) {
	    write("[0..n] ");
	} else if (prop.isNullable()) {
	    write("[0..1] ");
	} else {
	    write("[1..1] ");
	}

	write(prop.getName());

	ObjectMap om = prop.getRoot().getObjectMap(prop.getContainer());

	Mapping m = om.getMapping(Path.get(prop.getName()));
	m.dispatch(new Mapping.Switch() {
		public void onValue(Value v) {
		    write(v.getColumn());
		}

		public void onJoinTo(JoinTo j) {
		    write("join to ");
		    write(j.getKey().getColumns());
		    write(")");
		}

		public void onJoinFrom(JoinFrom j) {
		    write("join from (");
		    write(j.getKey().getColumns());
		    write(")");
		}

		public void onJoinThrough(JoinThrough j) {
		    write("join through (");
		    write(j.getFrom().getColumns());
		    write("), (");
		    write(j.getTo().getColumns());
		}

		public void onStatic(Static s) {
		    // do nothing
		}
	    });

	write(";\n");
    }

}
