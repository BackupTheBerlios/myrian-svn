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

import com.arsdigita.persistence.Utilities;


/**
 * Defines the multiplicity of a given association
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #1 $ $Date: 2002/05/12 $
 */

public class MultiplicityDef extends Element {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/pdl/ast/MultiplicityDef.java#1 $ by $Author: dennis $, $DateTime: 2002/05/12 18:23:13 $";

    // the upper and lower bounds of the association's multiplicity
    private String m_upper;
    private String m_lower;

    /**
     * Create a new MulitplicityDef with the given upper and lower bounds
     *
     * @param lower the lower bound
     * @param upper the upper bound 
     * @pre lower != null && upper != null
     */
    public MultiplicityDef(String lower, String upper) {
        m_lower = lower;
        m_upper = upper;
    }

    public void validateBounds() {
        StringBuffer sb = new StringBuffer();
        int lowerBound = (new Integer(m_lower)).intValue();
        int upperBound;
        if (m_upper.equals("n")) {
            // set to something valid
            upperBound = lowerBound + 1;
        } else {
            upperBound = (new Integer(m_upper)).intValue();
        }

        if (lowerBound < 0) {
            sb.append(Utilities.LINE_BREAK + "  Lower bound must be >= ");
            sb.append("0. It is currently ").append(lowerBound);
        }
        if (upperBound < 1) {
            sb.append(Utilities.LINE_BREAK + "  Upper bound must be >= ");
            sb.append("1. It is currently ").append(upperBound);
        }
        if (lowerBound > upperBound) {
            sb.append(Utilities.LINE_BREAK + "  Upper bound must be >= to " +
                      "the lower bound");
            sb.append(". Lower bound is currently: ").append(lowerBound);
            sb.append(". Upper bound is currently: ").append(upperBound);
        }

        if (sb.length() > 0) {
            error(sb.toString());
        }
    }

    /** 
     * Returns an integer representing the multiplicity.
     *
     * @return an integer representing the multiplicity
     */
    int getMultiplicityX() {
        if (m_lower.equals("0")) {
            if (m_upper.equals("1")) {
                return com.arsdigita.persistence.metadata.Property.NULLABLE;
            } else {
                return com.arsdigita.persistence.metadata.Property.COLLECTION;
            }
        } else {
            if (m_upper.equals("1")) {
                return com.arsdigita.persistence.metadata.Property.REQUIRED;
            } else {
                return com.arsdigita.persistence.metadata.Property.COLLECTION;
            }
        }
    }
                
    /**
     * Returns a string representation of this object.
     *
     * @return a string representation of this object
     */
    public String toString() {
        return "[" + m_lower + ".." + m_upper + "]";
    }

}
