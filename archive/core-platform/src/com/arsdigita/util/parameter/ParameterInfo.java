/*
 * Copyright (C) 2001, 2002, 2003 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the CCM Public
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of
 * the License at http://www.redhat.com/licenses/ccmpl.html
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */

package com.arsdigita.util.parameter;

/**
 * Subject to change.
 *
 * Metadata for a parameter that is of use for building documentation
 * or user interfaces for parameters.  The fields are not required and
 * thus the methods of this class may return null.
 *
 * @see Parameter#setInfo(ParameterInfo)
 * @see Parameter#getInfo()
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: //core-platform/dev/src/com/arsdigita/util/parameter/ParameterInfo.java#3 $
 */
public interface ParameterInfo {
    public final static String versionId =
        "$Id: //core-platform/dev/src/com/arsdigita/util/parameter/ParameterInfo.java#3 $" +
        "$Author: justin $" +
        "$DateTime: 2003/11/10 12:42:42 $";

    /**
     * Gets the pretty name of the parameter.
     *
     * @return The <code>String</code> title of the parameter; it may
     * be null
     */
    String getTitle();

    /**
     * Gets the parameter's reason for being.
     *
     * @return The <code>String</code> purpose of the parameter; it
     * may be null
     */
    String getPurpose();

    /**
     * Gets an example value for the parameter.
     *
     * @return A <code>String</code> example value; it may be null
     */
    String getExample();

    /**
     * Gets a format description.
     *
     * @return A format <code>String</code>; it may be null
     */
    String getFormat();
}
