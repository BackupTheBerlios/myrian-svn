/*
 * Copyright (C) 2001-2004 Red Hat, Inc.  All Rights Reserved.
 *
 * This program is Open Source software; you can redistribute it and/or
 * modify it under the terms of the Open Software License version 2.1 as
 * published by the Open Source Initiative.
 *
 * You should have received a copy of the Open Software License along
 * with this program; if not, you may obtain a copy of the Open Software
 * License version 2.1 from http://www.opensource.org/licenses/osl-2.1.php
 * or by writing the Open Source Initiative c/o Lawrence Rosen, Esq.,
 * 3001 King Ranch Road, Ukiah, CA 95482.
 */
package com.arsdigita.persistence.metadata;

import com.redhat.persistence.metadata.Root;

/**
 * The ModelElement class represents metadata elements that are components of
 * a Model.
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #2 $ $Date: 2004/08/30 $
 **/

abstract public class ModelElement extends Element {

    public final static String versionId = "$Id: //eng/persistence/dev/cap/src/com/arsdigita/persistence/metadata/ModelElement.java#2 $ by $Author: dennis $, $DateTime: 2004/08/30 14:24:55 $";

    private Root m_root;
    private com.redhat.persistence.metadata.Model m_model;

    ModelElement(Root root,
		 com.redhat.persistence.metadata.Model model,
		 Object obj) {
	super(root, obj);
	m_root = root;
	m_model = model;
    }


    /**
     * Returns the Model that this ModelElement is contained in. This value
     * may be null if this ModelElement hasn't been added to a model.
     *
     * @return The Model that this ModelElement is contained in or null.
     **/

    public Model getModel() {
	return Model.wrap(m_root, m_model);
    }

}
