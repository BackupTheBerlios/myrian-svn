/*
 * Copyright (C) 2001 ArsDigita Corporation. All Rights Reserved.
 *
 * The contents of this file are subject to the ArsDigita Public 
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of
 * the License at http://www.arsdigita.com/ADPL.txt
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */

package com.arsdigita.persistence.pdl.ast;

import com.arsdigita.persistence.metadata.MetadataRoot;
import com.arsdigita.persistence.metadata.Model;

import com.arsdigita.persistence.Utilities;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Collection;

/**
 * Defines the Model of a particular object type.
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #2 $ $Date: 2002/05/21 $
 */

public class ModelDef extends Element {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/pdl/ast/ModelDef.java#2 $ by $Author: rhs $, $DateTime: 2002/05/21 20:57:49 $";

    // the model name
    private String m_name;

    // the objects contained within the model
    private Map m_objs = new HashMap();

    // the associations contained within the model
    private Set m_assns = new HashSet();

    // the queries contained within the model
    private Map m_queries = new HashMap();

    private Model m_model = null;

    /**
     * Create a new ModelDef with the given name
     *
     * @param name the name of the new modeldef
     */
    ModelDef(String name) {
        m_name = name;
    }

    /**
     * Returns the ModelDef's name
     *
     * @return the ModelDef's name
     */
    public String getName() {
        return m_name;
    }

    /**
     * Returns the ObjectDef within this Model with the given name
     *
     * @param name the name of the desired ObjectDef
     * @return the ObjectDef within this Model with the given name
     */
    public ObjectDef getObjectDef(String name) {
        return (ObjectDef) m_objs.get(name);
    }

    /**
     * Returns a collection of the objects defined in this ModelDef
     *
     * @return a collection of the objects defined in this ModelDef
     */
    public Collection getObjectDefs() {
        return m_objs.values();
    }

    /**
     * Returns an iterator over the associations defined in this ModelDef
     *
     * @return an iterator over the associations defined in this ModelDef
     */
    public Iterator getAssociationDefs() {
        return m_assns.iterator();
    }

    /**
     * Add an ObjectDef to this ModelDef
     *
     * @param od the ObjectDef to add
     */
    public void add(ObjectDef od) {
        if (m_objs.containsKey(od.getName())) {
            od.error("Duplicate object type in model " + getName() +
                     ": " + od.getName());
        }
        m_objs.put(od.getName(), od);
        super.add(od);
    }

    /**
     * Add an AssociationDef to this ModelDef
     *
     * @param ad the AssociationDef to add
     */
    public void add(AssociationDef ad) {
        m_assns.add(ad);
        super.add(ad);
    }

    /**
     * Add a NamedSQLDef to this ModelDef
     *
     * @param qd the NamedSQLDef to add
     */
    public void add(NamedSQLDef qd) {
        if (m_queries.containsKey(qd.getName())) {
            qd.error("Duplicate query definition in model " + getName() +
                     ": " + qd.getName());
        }
        m_queries.put(qd.getName(), qd);
        super.add(qd);
    }

    /**
     * Confirm that this ModelDef is in a valid state by validating each of
     * its sub-elements.
     */
    void validate() {
        validate(m_objs);
        validate(m_queries);
        validate(m_assns);
    }

    void validateMappings() {
        for (Iterator it = m_objs.values().iterator(); it.hasNext(); ) {
            ObjectDef od = (ObjectDef) it.next();
            od.validateMappings();
        }

        for (Iterator it = m_assns.iterator(); it.hasNext(); ) {
            AssociationDef ad = (AssociationDef) it.next();
            ad.validateMappings();
        }
    }

    Model getModel() {
        return m_model;
    }

    /**
     * Creates in metadata the Model. 
     *
     * @param root the metadata root
     */
    Model createModel() {
        MetadataRoot root = MetadataRoot.getMetadataRoot();
        if (root.getModel(m_name) == null) {
            m_model = new Model(m_name);
            initLineInfo(m_model);
            root.addModel(m_model);
        } else {
            m_model = root.getModel(m_name);
        }

        return m_model;
    }

    /**
     * Creates in metadata for the associations contained by this ModelDef
     * 
     * @param root the metadata root
     */
    void generateLogicalModel() {
        for (Iterator it = m_objs.values().iterator(); it.hasNext(); ) {
            ObjectDef od = (ObjectDef) it.next();
            od.generateLogicalModel();
        }

        for (Iterator it = m_queries.values().iterator(); it.hasNext(); ) {
            NamedSQLDef qd = (NamedSQLDef) it.next();

            if (qd instanceof QueryDef) {
                m_model.addDataType(((QueryDef)qd).generateEvents());
            } else {
                m_model.addDataOperationType(((DMLDef)qd).generateEvents());
            }
        }
    }

    /**
     * Returns a string representation of this object.
     *
     * @return a string representation of this object
     */
    public String toString() {
        StringBuffer result = new StringBuffer("model " + m_name + ";" +
                                               Utilities.LINE_BREAK);

        for (Iterator it = m_objs.values().iterator(); it.hasNext(); ) {
            result.append(Utilities.LINE_BREAK + it.next() + Utilities.LINE_BREAK);
        }

        for (Iterator it = m_assns.iterator(); it.hasNext(); ) {
            result.append(Utilities.LINE_BREAK + it.next() + 
                          Utilities.LINE_BREAK);
        }

        return result.toString();
    }

}
