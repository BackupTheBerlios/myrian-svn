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
 * Writes encoded parameter values to storage.  Implementors define
 * the exact nature of the storage.
 *
 * @see Parameter#write(ParameterWriter,Object)
 * @see ParameterReader
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: //core-platform/dev/src/com/arsdigita/util/parameter/ParameterWriter.java#4 $
 */
public interface ParameterWriter {
    public final static String versionId =
        "$Id: //core-platform/dev/src/com/arsdigita/util/parameter/ParameterWriter.java#4 $" +
        "$Author: dennis $" +
        "$DateTime: 2004/03/30 17:47:27 $";

    /**
     * Writes the marshaled <code>value</code> for parameter
     * <code>param</code> to storage.
     *
     * @param param The <code>Parameter</code> that is being written;
     * it cannot be null
     * @param value The encoded <code>String</code> value to store for
     * <code>param</code>; it may be null
     */
    void write(Parameter param, String value);
}
