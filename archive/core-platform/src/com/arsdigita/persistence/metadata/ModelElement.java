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

package com.arsdigita.persistence.metadata;

/**
 * The ModelElement class represents metadata elements that are components of
 * a Model.
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #2 $ $Date: 2002/07/18 $
 **/

abstract public class ModelElement extends Element {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/metadata/ModelElement.java#2 $ by $Author: dennis $, $DateTime: 2002/07/18 13:18:21 $";

    /**
     * The model that contains this ModelElement.
     **/
    private Model m_model = null;


    /**
     * Used by the Model class to set the model that this ModelElement is
     * contained in.
     *
     * @param model The model that this ModelElement is to be contained in.
     **/

    public void setModel(Model model) {
        m_model = model;
    }


    /**
     * Returns the Model that this ModelElement is contained in. This value
     * may be null if this ModelElement hasn't been added to a model.
     *
     * @return The Model that this ModelElement is contained in or null.
     **/

    public Model getModel() {
        return m_model;
    }

}
