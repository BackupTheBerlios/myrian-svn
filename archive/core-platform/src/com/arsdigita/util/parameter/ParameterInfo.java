/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the Open Software License v2.1
 * (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * http://rhea.redhat.com/licenses/osl2.1.html.
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
 * @version $Id: //core-platform/dev/src/com/arsdigita/util/parameter/ParameterInfo.java#4 $
 */
public interface ParameterInfo {
    public final static String versionId =
        "$Id: //core-platform/dev/src/com/arsdigita/util/parameter/ParameterInfo.java#4 $" +
        "$Author: dennis $" +
        "$DateTime: 2004/03/30 17:47:27 $";

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
