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

import com.arsdigita.persistence.Utilities;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

/**
 * AST
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #2 $ $Date: 2002/07/18 $
 */

public class AST extends Element {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/pdl/ast/AST.java#2 $ by $Author: dennis $, $DateTime: 2002/07/18 13:18:21 $";

    // the various models that make up this AST/Metadata
    private Map m_models = new HashMap();

    public AST() {}

    /**
     * Returns the ModelDef named "name".
     * 
     * @param name the name of the ModelDef to retrieve
     * @return the ModelDef named "name", null if it doesn't exist
     */
    public ModelDef getModelDef(String name) {
        return getModelDef(name, false);
    }

    /**
     * Returns the ModelDef named "name".  If the ModelDef does not exist,
     * and "create" is true, the ModelDef is created.
     * 
     * @param name the name of the ModelDef to retrieve
     * @param create true to create when not found, false otherwise
     * @return the ModelDef named "name", null if not found and create is false
     */
    public ModelDef getModelDef(String name, boolean create) {
        ModelDef result = (ModelDef) m_models.get(name);

        if (result == null && create) {
            result = new ModelDef(name);
            m_models.put(name, result);
            add(result);
        }

        return result;
    }

    /**
     * Creates metadata in memory, using "root" as the root of the metadata
     * hierarchy.  The metadata created corresponds to the abstract syntax 
     * tree's contents, which is drawn from the PDL files.
     * 
     * @param root the metadata root
     */
    public void generateMetadata(MetadataRoot root) {
        for (Iterator it = m_models.values().iterator(); it.hasNext(); ) {
            ModelDef model = (ModelDef) it.next();
            model.createModel();
        }

        // We keep the order in which we created the object types because this
        // is used again for event generation.
        List objectDefs = new ArrayList();

        // Create all object types in the order imposed by the type hierarchy
        Set skipped;
        int lastSkippedSize = -1;
        boolean done;

        do {
            skipped = new HashSet();

            for (Iterator it = m_models.values().iterator(); it.hasNext(); ) {
                ModelDef model = (ModelDef) it.next();

                for (Iterator objects = model.getObjectDefs().iterator();
                     objects.hasNext(); ) {
                    ObjectDef ot = (ObjectDef)objects.next();

                    if (ot.getObjectGenerated()) {
                        continue;
                    }

                    // may do some extra work, but also prevents loops
                    //ot.validate();

                    if (ot.getSuper() == null || ot.getSuper().exists()) {
                        model.getModel().addDataType(ot.createObjectType());
                        objectDefs.add(ot);
                    } else {
                        skipped.add(ot);
                    }
                }
            }

            done = (skipped.size() == 0 || skipped.size() == lastSkippedSize);
            lastSkippedSize = skipped.size();
        } while (!done);

        for (Iterator it = skipped.iterator(); it.hasNext(); ) {
            ObjectDef od = (ObjectDef) it.next();
            Identifier sup = od.getSuper();
            if (sup == null) {
                od.error(
                    "Unable to generate object type, but I have no idea " +
                    "why. Please report this as a persistence bug."
                    );
            } else {
                sup.error("No such object type: " + sup.getName());
            }
        }

        // Now generate the rest of the metadata for the logical model.
        for (Iterator it = m_models.values().iterator(); it.hasNext(); ) {
            ModelDef model = (ModelDef) it.next();
            model.generateLogicalModel();
        }

        // Now generate the mapping metadata.
        for (int i = 0; i < objectDefs.size(); i++) {
            ObjectDef od = (ObjectDef) objectDefs.get(i);
            od.generateMappingMetadata();
        }

        for (Iterator it = m_models.values().iterator(); it.hasNext(); ) {
            ModelDef md = (ModelDef)it.next();

            for (Iterator assns = md.getAssociationDefs(); assns.hasNext(); ) {
                AssociationDef ad = (AssociationDef)assns.next();
                md.getModel().addAssociation(ad.generateLogicalModel());
            }
        }

        // Now validate all the event mappings.
        for (Iterator it = m_models.values().iterator(); it.hasNext(); ) {
            ModelDef model = (ModelDef) it.next();
            model.validateMappings();
        }

        for (int i = 0; i < objectDefs.size(); i++) {
            ObjectDef od = (ObjectDef) objectDefs.get(i);
            od.generateEvents();
        }

        Traversal t = new Traversal() {
                public void act(Node node) {
                    node.generateAssociationEvents();
                }
            };
        t.traverse(this);
    }

    /**
     * Returns a string representation of this object.
     *
     * @return a string representation of this object
     */
    public String toString() {
        StringBuffer result = new StringBuffer();

        for (Iterator it = m_models.values().iterator(); it.hasNext(); ) {
            result.append(it.next());
            if (it.hasNext()) {
                result.append(Utilities.LINE_BREAK + Utilities.LINE_BREAK);
            }
        }

        return result.toString();
    }

}
