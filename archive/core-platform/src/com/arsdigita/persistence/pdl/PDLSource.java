/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the CCM Public
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the
 * License at http://www.redhat.com/licenses/ccmpl.html.
 *
 * Software distributed under the License is distributed on an
 * "AS IS" basis, WITHOUT WARRANTY OF ANY KIND, either express
 * or implied. See the License for the specific language
 * governing rights and limitations under the License.
 *
 */
package com.arsdigita.persistence.pdl;

/**
 * The PDLSource interface provides a means for loading PDL from a
 * variety of different locations.
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #4 $ $Date: 2004/04/07 $
 **/

public interface PDLSource {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/pdl/PDLSource.java#4 $ by $Author: dennis $, $DateTime: 2004/04/07 16:07:11 $";

    /**
     * Parses the contents of this PDLSource using the given
     * PDLCompiler.
     *
     * @param compiler the PDLCompiler used to parse the contents of
     * this PDLSource
     **/

    void parse(PDLCompiler compiler);

}
