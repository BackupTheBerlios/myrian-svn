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

import org.myrian.persistence.metadata.*;
import org.myrian.persistence.pdl.nodes.*;
import java.util.*;

/**
 * SymbolTable
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2004/10/04 $
 **/

class SymbolTable {


    private Map m_types = new HashMap();
    private List m_order = new ArrayList();
    private Map m_resolutions = new HashMap();
    private Map m_references = new HashMap();
    private Map m_emitted = new HashMap();
    private ErrorReport m_report;
    private Root m_root;

    public SymbolTable(ErrorReport report, Root root) {
        m_report = report;
        m_root = root;
    }

    public void define(ObjectTypeNd type) {
        if (isDefined(type.getQualifiedName())) {
            ObjectTypeNd original = getObjectType(type.getQualifiedName());
	    if (original == null) {
		m_report.fatal(type, "already loaded");
	    } else {
		m_report.fatal
		    (type, "duplicate type definition for " +
                     type.getQualifiedName() + ", original definition: " +
		     original.getLocation());
	    }
        } else {
            m_types.put(type.getQualifiedName(), type);
            m_order.add(type);
        }
    }

    public String resolve(TypeNd type) {
        if (m_resolutions.containsKey(type)) {
            return (String) m_resolutions.get(type);
        }

        String result = null;
        TypeReference ref = null;

        if (type.isQualified()) {
            String qname = type.getQualifiedName();
            if (isDefined(qname)) {
                result = qname;
                ref = TypeReference.get(m_root, qname);
            }
        } else {
            // check parameters
            ObjectTypeNd container = null;
            for (Node parent = type.getParent(); parent != null;
                 parent = parent.getParent()) {
                if (parent instanceof ObjectTypeNd) {
                    container = (ObjectTypeNd) parent;
                    break;
                }
            }
            if (container != null) {
                List params = container.getParameters();
                for (int i = 0; i < params.size(); i++) {
                    IdentifierNd param = (IdentifierNd) params.get(i);
                    if (param.getName().equals(type.getName())) {
                        result = container.getQualifiedName() + ":" +
                            param.getName();
                        ref = TypeReference.get(param.getName());
                        break;
                    }
                }
            }

            if (result == null) {
                // check imports
                FileNd file = type.getFile();
                Collection imps = file.getImports();

                ArrayList qnames = new ArrayList();

                for (Iterator it = imps.iterator(); it.hasNext(); ) {
                    ImportNd imp = (ImportNd) it.next();
                    String qname = imp.qualify(type);
                    if (qname != null && isDefined(qname)) {
                        qnames.add(qname);
                    }
                }

                if (qnames.size() == 0) {
                    String[] special = new String[] {
                        file.getModel().getName() + "." + type.getName(),
                        "global." + type.getName()
                    };

                    for (int i = 0; i < special.length; i++) {
                        if (isDefined(special[i])) {
                            result = special[i];
                            ref = TypeReference.get(m_root, special[i]);
                            break;
                        }
                    }
                } else if (qnames.size() > 1) {
                    m_report.fatal(type, "ambiguous symbol, resolves to: " +
                                   qnames);
                    return null;
                } else {
                    result = (String) qnames.get(0);
                    ref = TypeReference.get(m_root, result);
                }
            }
        }

        if (result == null) {
            m_report.fatal(type, "unresolved symbol: " +
                           (type.isQualified() ?
                            type.getQualifiedName() : type.getName()));
            return null;
        }

        List arguments = type.getArguments();
        if (!arguments.isEmpty()) {
            List args = new ArrayList();
            for (int i = 0; i < arguments.size(); i++) {
                TypeNd arg = (TypeNd) arguments.get(i);
                resolve(arg);
                TypeReference argref = (TypeReference) m_references.get(arg);
                if (argref == null) {
                    return null;
                }
                args.add(argref);
            }
            ref = TypeReference.get(ref, args);
        }

        m_resolutions.put(type, result);
        m_references.put(type, ref);

        return result;
    }

    public TypeReference getTypeReference(TypeNd type) {
        return (TypeReference) m_references.get(type);
    }

    private boolean isDefined(String qualifiedName) {
        return m_types.containsKey(qualifiedName) ||
            m_emitted.containsKey(qualifiedName);
    }

    private ObjectTypeNd getObjectType(String qualifiedName) {
        return (ObjectTypeNd) m_types.get(qualifiedName);
    }

    public String lookup(TypeNd type) {
        return (String) m_resolutions.get(type);
    }

    public boolean sort() {
        HashSet defined = new HashSet();
        defined.addAll(m_emitted.keySet());

        ArrayList undefined = new ArrayList();
        undefined.addAll(m_order);
        ArrayList nwo = new ArrayList();

        HashSet circular = new HashSet();
        ArrayList circOrd = new ArrayList();

        int before;
        do {
            if (undefined.size() == 0) { break; }
            before = defined.size();
            for (Iterator it = undefined.iterator(); it.hasNext(); ) {
                ObjectTypeNd type = (ObjectTypeNd) it.next();
                if (!circular.contains(type) && isCircular(type)) {
                    circular.add(type);
                    circOrd.add(type);
                }
                if (type.getExtends() == null ||
                    defined.contains(lookup(type.getExtends()))) {
                    defined.add(type.getQualifiedName());
                    nwo.add(type);
                    it.remove();
                }
            }
        } while (defined.size() > before);

        for (Iterator it = circOrd.iterator(); it.hasNext(); ) {
            ObjectTypeNd ot = (ObjectTypeNd) it.next();
            m_report.fatal(ot, "circular type dependency: " +
                           ot.getQualifiedName());
        }

        if (undefined.size() > 0) {
            return false;
        } else {
            m_order = nwo;
            return true;
        }
    }

    private boolean isCircular(ObjectTypeNd type) {
        return isCircular(type, type, new HashSet());
    }

    private boolean isCircular(ObjectTypeNd type, ObjectTypeNd start,
                               HashSet visited) {
        if (visited.contains(type)) {
            return false;
        } else if (type.getExtends() == null) {
            return false;
        } else {
            ObjectTypeNd sup = getObjectType(lookup(type.getExtends()));
            if (sup == null) {
                return false;
            } else if (sup.equals(start)) {
                return true;
            } else {
                visited.add(type);
                return isCircular(sup, start, visited);
            }
        }
    }

    public Collection getObjectTypes() {
        return m_order;
    }

    public void addEmitted(ObjectType type) {
        m_emitted.put(type.getQualifiedName(), type);
    }

    public ObjectType getEmitted(String qname) {
        return (ObjectType) m_emitted.get(qname);
    }

    public ObjectType getEmitted(ObjectTypeNd type) {
        return (ObjectType) m_emitted.get(type.getQualifiedName());
    }

    public ObjectType getEmitted(TypeNd type) {
        return getEmitted(lookup(type));
    }

    public void emit() {
        for (Iterator it = m_order.iterator(); it.hasNext(); ) {
            ObjectTypeNd ot = (ObjectTypeNd) it.next();
            Model model = Model.getInstance(ot.getFile().getModel().getName());
            String name = ot.getName().getName();
            List parameters = new ArrayList();
            List params = ot.getParameters();
            for (int i = 0; i < params.size(); i++) {
                IdentifierNd param = (IdentifierNd) params.get(i);
                parameters.add(param.getName());
            }
            TypeReference sup = null;
            if (ot.getExtends() != null) {
                sup = getTypeReference(ot.getExtends());
            }
            ObjectType type = new ObjectType(model, name, parameters, sup);
            addEmitted(type);
            setLocation(type, ot);
        }
    }

    final void setLocation(Object element, Node nd) {
        m_root.setLocation
            (element, nd.getFile().getName(), nd.getLine(), nd.getColumn());
    }

}
