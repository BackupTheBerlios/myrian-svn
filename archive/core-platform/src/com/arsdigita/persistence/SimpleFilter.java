package com.arsdigita.persistence;

/**
 * SimpleFilter
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2003/05/12 $
 **/

class SimpleFilter extends FilterImpl {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/SimpleFilter.java#2 $ by $Author: ashah $, $DateTime: 2003/05/12 18:19:45 $";

    String m_conditions;

    SimpleFilter(String conditions) {
        // note that it is possible for conditions to be null
        // if we actually want a NO-OP filter
        m_conditions = conditions;
    }

    /**
     *  This returns the SQL that is represented by the Filter. All
     *  values in the filter should have been bound with
     *  set(parameterName, value).  This actually returns the
     *  conditions with a namespace constant inserted after the ":"
     *  so that we know what namespace to use for binding.
     **/

    public String getConditions() {
	return m_conditions;
    }

    /**
     * This prints out a string representation of the filter
     */
    public String toString() {
        return "Filter:" + Utilities.LINE_BREAK +
            " Conditions: " + m_conditions +
            Utilities.LINE_BREAK + "  Values: " + getBindings();
    }

}
