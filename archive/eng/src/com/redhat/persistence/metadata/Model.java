/*
 * Copyright (C) 2003-2004 Red Hat, Inc.  All Rights Reserved.
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
package com.redhat.persistence.metadata;

import java.util.HashMap;

/**
 * Model
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2004/08/30 $
 **/

public class Model {

    public final static String versionId = "$Id: //eng/persistence/dev/src/com/redhat/persistence/metadata/Model.java#2 $ by $Author: dennis $, $DateTime: 2004/08/30 14:24:55 $";

    private static final HashMap MODELS = new HashMap();

    public static final Model getInstance(String model) {
        if (model == null) {
            return null;
        }

        Model result;
        
        if (MODELS.containsKey(model)) {
            result = (Model) MODELS.get(model);
        } else {
            synchronized (MODELS) {
                if (MODELS.containsKey(model)) {
                    result = (Model) MODELS.get(model);
                } else {
                    int dot = model.lastIndexOf('.');
                    Model parent;
                    String name;
                    if (dot > -1) {
                        parent = getInstance(model.substring(0, dot));
                        name = model.substring(dot + 1);
                    } else {
                        parent = null;
                        name = model;
                    }

                    result = new Model(parent, name);
                    MODELS.put(model, result);
                }
            }
        }

        return result;
    }

    private final Model m_parent;
    private final String m_name;
    private final String m_qualifiedName;

    private Model(Model parent, String name) {
        m_parent = parent;
        m_name = name;
        if (m_parent == null) {
            m_qualifiedName = m_name;
        } else {
            m_qualifiedName = m_parent.getQualifiedName() + "." + m_name;
        }

    }

    public Model getParent() {
        return m_parent;
    }

    public String getName() {
        return m_name;
    }

    public String getQualifiedName() {
        return  m_qualifiedName;
    }

}
