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

import com.arsdigita.persistence.metadata.MetadataRoot;
import java.io.Reader;

/**
 * This class is used to parse object-relational metadata specified in
 * PDL files, and emit it into a MetadataRoot.
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2004/06/07 $
 **/

public class PDLCompiler {

    public final static String versionId = "$Id: //eng/persistence/dev/cap/src/com/arsdigita/persistence/pdl/PDLCompiler.java#1 $ by $Author: rhs $, $DateTime: 2004/06/07 13:49:55 $";

    private final com.redhat.persistence.pdl.PDL m_pdl;

    /**
     * Constructs a new and empty PDLCompiler.
     **/

    public PDLCompiler() {
        m_pdl = new com.redhat.persistence.pdl.PDL();
    }

    /**
     * Parses the text in <i>contents</i> and reports any errors using
     * the <i>location</i> tag.
     *
     * @param contents A reader of the text to be parsed.
     * @param location The location to use when reporting errors.
     **/

    public void parse(Reader contents, String location) {
        m_pdl.load(contents, location);
    }

    /**
     * Compiles the parsed PDL into the specified MetadataRoot.
     *
     * @param root The MetadataRoot to emit to.
     **/

    public void emit(MetadataRoot root) {
        m_pdl.emit(root.getRoot());
        //m_pdl.emitVersioned();
        if (root.equals(MetadataRoot.getMetadataRoot())) {
            MetadataRoot.loadPrimitives();
        }
    }

}
