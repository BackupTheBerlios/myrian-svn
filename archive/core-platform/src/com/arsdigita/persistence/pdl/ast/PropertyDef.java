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
import com.arsdigita.persistence.metadata.Property;
import com.arsdigita.persistence.metadata.DataType;
import com.arsdigita.persistence.metadata.SimpleType;

/**
 * Defines a property of an ObjectType, which contains a name and type, a
 * multiplicity, and whether or not the property is composite.
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #2 $ $Date: 2002/05/21 $
 */

public class PropertyDef extends Element {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/pdl/ast/PropertyDef.java#2 $ by $Author: rhs $, $DateTime: 2002/05/21 20:57:49 $";

    // property name
    private String m_name;

    // the type of the property
    private Identifier m_type;

    // the multiplicity of the property
    private MultiplicityDef m_mult;

    // if it's composite or not
    private boolean m_isComposite;

    // the column in the database that stores this property
    private ColumnDef m_column = null;

    // the joinpath that retrieves this association
    private JoinPathDef m_joinPath = null;

    /**
     * Create a new PropertyDef of a given name and type, with a certain
     * multiplicity (can be null) and compositeness.
     *
     * @param name the name of the property
     * @param type the type of the property
     * @param mult the multiplicity, or null for none
     * @param isComposite true for a composite property, false for regular
     * @pre type != null
     */
    public PropertyDef(String name, Identifier type, MultiplicityDef mult,
                       boolean isComposite) {
        m_name = name;
        m_type = type;
        m_mult = mult;
        m_isComposite = isComposite;

        super.add(m_type);
        if (m_mult != null) {
            try {
                m_mult.validateBounds();
            } catch (Error e) {
                throw new Error("Error with " + m_name + " in " + e.toString());
            }
            super.add(m_mult);
        }
    }

    /**
     * Specifies the column used to store this property
     *
     * @param column the database column that stores this property, or null
     *               for not specified
     */
    public void setColumn(ColumnDef column) {
        m_column = column;
    }

    /**
     * Specifies the join path used to retrieve this role reference.
     * 
     * @param jp the joinpathdef
     */
    public void setJoinPath(JoinPathDef jp) {
        m_joinPath = jp;
    }

    /**
     * Returns the name of this property.
     *
     * @return the name of this property
     */
    public String getName() {
        return m_name;
    }

    /** 
     * Returns the ColumnDef of this property.
     *
     * @return the ColumnDef of this property.
     */
    public ColumnDef getColumn() {
        return m_column;
    }

    /**
     * Returns the type of this property.
     *
     * @return the type of this property
     */
    public Identifier getType() {
        return m_type;
    }

    /**
     * Returns true if this is a simple type, false otherwise
     *
     * @return true if this is a simple type, false otherwise
     */
    public boolean isAttribute() {
        return (MetadataRoot.getMetadataRoot()
                            .getPrimitiveType(m_type.getName()) != null);
    }

    /**
     * Creates the metadata for this Property.
     */
    Property generateLogicalModel() {
        MetadataRoot root = MetadataRoot.getMetadataRoot();
        Property prop;
        int mult;

        if (m_mult == null) {
            mult = Property.NULLABLE;
        } else {
            mult = m_mult.getMultiplicityX();
        }

        DataType datatype;

        datatype = root.getPrimitiveType(m_type.getName());
        int defaultJDBCType = Integer.MIN_VALUE;

        if (datatype == null) {
            m_type.resolve();
            datatype = m_type.getResolvedObjectType();
        } else {
            defaultJDBCType = ((SimpleType) datatype).getJDBCtype();
        }

        prop = new Property(m_name, datatype, mult, m_isComposite);
        initLineInfo(prop);

        if (m_column != null) {
            if (defaultJDBCType > Integer.MIN_VALUE) {
                prop.setColumn(m_column.generateLogicalModel(defaultJDBCType));
            } else {
                prop.setColumn(m_column.generateLogicalModel());
            }
        } else if (m_joinPath != null) {
            prop.setJoinPath(m_joinPath.generateLogicalModel());
        }

        return prop;
    }

    /**
     * Returns a string representation of this object.
     *
     * @return a string representation of this object
     */
    public String toString() {
        if (m_mult == null)
            return m_type + " " + m_name;
        else
            return m_type.toString() + m_mult + " " + m_name;
    }

}
