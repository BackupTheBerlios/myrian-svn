package com.arsdigita.persistence.proto.pdl;

import com.arsdigita.persistence.proto.pdl.nodes.*;
import java.util.*;

/**
 * SymbolTable
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2003/01/09 $
 **/

class SymbolTable {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/pdl/SymbolTable.java#2 $ by $Author: rhs $, $DateTime: 2003/01/09 18:20:28 $";

    private HashMap m_types = new HashMap();
    private ArrayList m_order = new ArrayList();
    private HashMap m_resolutions = new HashMap();
    private HashMap m_emitted = new HashMap();
    private ErrorReport m_report;

    public SymbolTable(ErrorReport report) {
        m_report = report;
    }

    public void define(ObjectType type) {
        if (isDefined(type.getQualifiedName())) {
            ObjectType original = getObjectType(type.getQualifiedName());
            m_report.fatal(type,
                           "duplicate type definition, " +
                           "original definition: " + original.getLocation());
        } else {
            m_types.put(type.getQualifiedName(), type);
            m_order.add(type);
        }
    }

    public String resolve(Type type) {
        String result = null;

        if (type.isQualified()) {
            result = type.getQualifiedName();
        } else {
            File file = type.getFile();
            Collection imps = file.getImports();

            ArrayList qnames = new ArrayList();


            // First check imports
            for (Iterator it = imps.iterator(); it.hasNext(); ) {
                Import imp = (Import) it.next();
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
                        break;
                    }
                }
            } else if (qnames.size() > 1) {
                m_report.fatal(type, "ambiguous symbol, resolves to: " +
                               qnames);
                return null;
            } else {
                result = (String) qnames.get(0);
            }
        }

        if (result == null) {
            m_report.fatal(type, "unresolved symbol: " +
                           type.getName());
        } else {
            m_resolutions.put(type, result);
        }

        return result;
    }

    private boolean isDefined(String qualifiedName) {
        return m_types.containsKey(qualifiedName) ||
            m_emitted.containsKey(qualifiedName);
    }

    private ObjectType getObjectType(String qualifiedName) {
        return (ObjectType) m_types.get(qualifiedName);
    }

    public String lookup(Type type) {
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
                ObjectType type = (ObjectType) it.next();
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
            ObjectType ot = (ObjectType) it.next();
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

    private boolean isCircular(ObjectType type) {
        return isCircular(type, type, new HashSet());
    }

    private boolean isCircular(ObjectType type, ObjectType start,
                               HashSet visited) {
        if (visited.contains(type)) {
            return false;
        } else if (type.getExtends() == null) {
            return false;
        } else {
            ObjectType sup = getObjectType(lookup(type.getExtends()));
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

    public void addEmitted(com.arsdigita.persistence.proto.metadata.ObjectType
                           type) {
        m_emitted.put(type.getQualifiedName(), type);
    }

    public com.arsdigita.persistence.proto.metadata.ObjectType getEmitted(String qname) {
        return (com.arsdigita.persistence.proto.metadata.ObjectType)
            m_emitted.get(qname);
    }

    public com.arsdigita.persistence.proto.metadata.ObjectType getEmitted(ObjectType type) {
        return (com.arsdigita.persistence.proto.metadata.ObjectType)
            m_emitted.get(type.getQualifiedName());
    }

    public void emit() {
        for (Iterator it = m_order.iterator(); it.hasNext(); ) {
            ObjectType ot = (ObjectType) it.next();
            com.arsdigita.persistence.proto.metadata.ObjectType sup = null;
            if (ot.getExtends() != null) {
                sup = getEmitted(lookup(ot.getExtends()));
            }
            com.arsdigita.persistence.proto.metadata.ObjectType type =
                new com.arsdigita.persistence.proto.metadata.ObjectType
                    (com.arsdigita.persistence.proto.metadata.Model
                     .getInstance(ot.getFile().getModel().getName()),
                     ot.getName().getName(), sup);
            addEmitted(type);
        }
    }

}
