package com.arsdigita.installer;

import java.util.*;

/**
 * Parameter
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/08/21 $
 **/

public abstract class Parameter {

    public final static String versionId = "$Id: //core-platform/test-packaging/src/com/arsdigita/installer/Parameter.java#1 $ by $Author: rhs $, $DateTime: 2003/08/21 17:45:18 $";

    public static final int OPTIONAL = 0;
    public static final int REQUIRED = 1;
    public static final int SCALAR = 0;
    public static final int COLLECTION = 1;

    private int m_lower;
    private int m_upper;
    private String m_name;
    private String m_description;
    private String m_help;

    public Parameter(int lower, int upper, String name, String description,
                     String help) {
        m_lower = lower;
        m_upper = upper;
        m_name = name;
        m_description = description;
        m_help = help;
    }

    public boolean isOptional() {
        return m_lower == OPTIONAL;
    }

    public boolean isRequired() {
        return m_lower == REQUIRED;
    }

    public boolean isScalar() {
        return m_upper == SCALAR;
    }

    public boolean isCollection() {
        return m_upper == COLLECTION;
    }

    public String getName() {
        return m_name;
    }

    public String getDescription() {
        return m_description;
    }

    public String getHelp() {
        return m_help;
    }

    public abstract List validate(String value);

}
