package com.arsdigita.persistence;

/**
 * SimpleFilter
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/03/19 $
 **/

class SimpleFilter extends FilterImpl {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/SimpleFilter.java#1 $ by $Author: rhs $, $DateTime: 2003/03/19 18:16:01 $";

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

    public String getSQL(DataQuery query) {
        // if there are not any conditions then it does not make
        // sense to return any SQL
        if (m_conditions == null) {
            return null;
        } else {
            return mangleBindVars
		(((DataQueryImpl) query).mapSQL(m_conditions));
        }
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
