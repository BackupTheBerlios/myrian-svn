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

package com.arsdigita.persistence;

import java.util.Map;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import com.arsdigita.util.Assert;

/**
 * CompoundFilters are used to AND or OR multiple filters together. 
 *
 * @author <a href="mailto:randyg@alum.mit.edu">randyg@alum.mit.edu</a>
 * @version $Revision: #2 $ $Date: 2002/07/18 $
 */

class CompoundFilterImpl extends FilterImpl implements CompoundFilter {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/CompoundFilterImpl.java#2 $ by $Author: dennis $, $DateTime: 2002/07/18 13:18:21 $";

    private static org.apache.log4j.Category m_log = 
      org.apache.log4j.Category.getInstance(CompoundFilterImpl.class.getName());

    private String m_combineWith;
    private Collection m_filters = new ArrayList();

    /**
     *  This creates a new compound filter with the specified join type.
     */
    private CompoundFilterImpl(boolean isAnd) {
        super(null);

        if (isAnd) {
            m_combineWith = "and";
        } else {
            m_combineWith = "or";
        }
        
    }


    /**
     *  Creates a filter that will AND together all filters passed in to it
     *  For instance, if developers want to combine two filters in to one, 
     *  they can write
     *  <pre><code>
     *  CompoundFilter.and().addFilter(Filter.string("lastName", lname))
                            .addFilter(Filter.string("firstName", fname))
     *  </code></pre>
     *  
     */
    public static CompoundFilter and() {
        return new CompoundFilterImpl(true);
    };


    /**
     *  Creates a filter that will AND together all filters passed in to it
     *  For instance, if developers want to combine two filters in to one, 
     *  they can write
     *  <pre><code>
     *  CompountFilter.or().addFilter(Filter.string("lastName", keyword))
                           .addFilter(Filter.string("firstName", keyword))
     *  </code></pre>
     *  
     */
    public static CompoundFilter or() {
        return new CompoundFilterImpl(false);        
    };   

    /**
     *  This provides a mechanism for adding conditions to the existing
     *  filter.  This appends the passed in conditions to the existing
     *  conditions with an "and" statement.
     *  There is no way to remove conditions.
     *
     *  @param conditions The conditions to add to this filter
     */
    public CompoundFilter addFilter(String conditions) {
        addFilter(FilterImpl.simple(conditions));
        return this;
    }


    /**
     *  This adds the passed in filter to this query and adds it 
     *  according to the type of filter this is (if it was created
     *  using Filter.or() then it ORs this filter with the existing
     *  ones; otherwise it ANDs it);
     *
     *  @return this
     */
    public CompoundFilter addFilter(Filter filter) {

        if (m_filters.contains(filter)) {
            // the filter was already added, so do nothing.
            return this;
        }

        Map bindings = filter.getBindings();
        if (bindings != null) {
            int numberBindings = getBindings().size() + bindings.size();
            addBindings(bindings);
            if (getBindings().size() < numberBindings) {
                // there was name overlapping so log a warning
                m_log.warn(
                    "When the filter was added, there was a naming" +
                    " conflict with the variables." + Utilities.LINE_BREAK +
                    "Filter 1: " + filter.toString() + Utilities.LINE_BREAK +
                    "Filter 2: " + toString()
                    );
            }
        }

        m_filters.add(filter);

        return this;
    }

    public boolean removeFilter(Filter filter) {
        return m_filters.remove(filter);
    }

    /**
     *  This returns the string representation of this Filter before
     *  any bindings are applied
     */
    public String getConditions() {
        String conditions = null;
        for (Iterator iter = m_filters.iterator(); iter.hasNext();) {
            Filter f = (Filter) iter.next();
            conditions = combineConditions(conditions, f.getConditions());
        }
        return conditions;
    }

    /**
     * Unsupported.
     **/
    protected void setConditions() {
        Assert.fail("CompoundFilterImpl.setConditions() is unsupported");
    }
    
    private String combineConditions(String currentConditions, 
                                     String newConditions) {
        // do nothing if the conditions passed in are null or the empty string
        if (newConditions == null || newConditions.equals("")) {
            return currentConditions;
        }
        if (currentConditions == null) {
            return newConditions;
        } 
        return 
            "(" + currentConditions + ") " + m_combineWith + 
            " (" + newConditions + ")";
    }

    /**
     *  This outputs a string representation of the CompoundFilter
     */
    public String toString() {
        return super.toString() + Utilities.LINE_BREAK + " Compound Type: " + 
            m_combineWith.toUpperCase();
    }
}






