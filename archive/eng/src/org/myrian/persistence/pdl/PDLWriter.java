/*
 * Copyright (C) 2003-2004 Red Hat, Inc. All Rights Reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package org.myrian.persistence.pdl;

import org.myrian.persistence.common.*;
import org.myrian.persistence.metadata.*;
import java.io.*;
import java.util.*;

/**
 * PDLWriter
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2004/10/01 $
 **/

public class PDLWriter {

    public final static String versionId = "$Id: //eng/persistence/dev/src/org/myrian/persistence/pdl/PDLWriter.java#1 $ by $Author: vadim $, $DateTime: 2004/10/01 18:41:18 $";

    private Writer m_out;

    public PDLWriter(Writer out) {
	m_out = out;
    }

    private void write(String str) {
	try {
	    m_out.write(str);
	} catch (IOException e) {
	    throw new RuntimeException(e);
	}
    }

    public void write(Root root) {
        Set written = new HashSet();
        for (Iterator it = root.getObjectTypes().iterator(); it.hasNext(); ) {
            ObjectType ot = (ObjectType) it.next();
            write(ot);
            write("\n");
            for (Iterator iter = ot.getProperties().iterator();
                 iter.hasNext(); ) {
                Object o = (Object) iter.next();
                if (!(o instanceof Role) || written.contains(o)) {
                    continue;
                }
                Role p = (Role) o;
                if (p.isReversable()) {
                    write("\n");
                    writeAssociation(p);
                    write("\n");
                }
                written.add(p);
                written.add(p.getReverse());
            }
            if (it.hasNext()) {
                write("\n");
            }
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

	ObjectMap om = null;
        Root root = type.getRoot();
        if (root != null) {
            om = root.getObjectMap(type);
        }

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
	} else if (om != null && om.getTable() != null) {
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
        if (type == null) {
            write("null");
        } else if (type.getModel() == null ||
            type.getModel().getName().equals("global")) {
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

	ObjectMap om = null;
        Root root = prop.getRoot();
        if (root != null) {
            om = root.getObjectMap(prop.getContainer());
        }

	Mapping m = null;
        if (om != null) {
            m = om.getMapping(Path.get(prop.getName()));
        }
        if (m == null) {
            write("<no mapping for " + prop.getName() + " in " +
                  prop.getContainer().getName() + ">");
        } else {
            m.dispatch(new Mapping.Switch() {
		public void onValue(Value v) {
                    Column col = v.getColumn();
		    write(col);
                    write(" ");
                    if (col.getType() == Integer.MIN_VALUE) {
                        write("UNKNOWN");
                    } else {
                        write(Column.getTypeName(col.getType()));
                    }
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

                public void onQualias(Qualias q) {
                    write("qualias {" + q.getQuery() + "}");
                }

                public void onNested(Nested n) {
                    write("<nested>");
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
