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
 * Reads an encoded string value for a parameter from storage.  Any
 * errors encountered while reading are added to an error list.
 * This class is counterpart to <code>ParameterWriter</code>.
 *
 * @see Parameter#write(ParameterWriter, Object)
 * @see ErrorList
 * @see ParameterWriter
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: //core-platform/dev/src/com/arsdigita/util/parameter/ParameterReader.java#5 $
 */
public interface ParameterReader {
    public final static String versionId =
        "$Id: //core-platform/dev/src/com/arsdigita/util/parameter/ParameterReader.java#5 $" +
        "$Author: vadim $" +
        "$DateTime: 2003/12/02 13:13:07 $";

    /**
     * Reads an encoded <code>String</code> value for
     * <code>param</code> from storage.  If there are errors, they are
     * added to <code>errors</code>.
     *
     * @param param The <code>Parameter</code> being read; it cannot
     * be null
     * @param errors The <code>ErrorList</code> that will collect any
     * errors; it cannot be null
     * @return The marshaled <code>String</code> value for
     * <code>param</code>; it may be null
     */
    String read(Parameter param, ErrorList errors);
}
