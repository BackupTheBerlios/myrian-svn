/*
 * Copyright (C) 2004 Red Hat, Inc. All Rights Reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package com.arsdigita.persistence.pdl;

import com.arsdigita.persistence.metadata.MetadataRoot;
import java.io.Reader;

/**
 * This class is used to parse object-relational metadata specified in
 * PDL files, and emit it into a MetadataRoot.
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #4 $ $Date: 2004/10/01 $
 **/

public class PDLCompiler {

    public final static String versionId = "$Id: //eng/persistence/dev/cap/src/com/arsdigita/persistence/pdl/PDLCompiler.java#4 $ by $Author: vadim $, $DateTime: 2004/10/01 18:41:18 $";

    private final org.myrian.persistence.pdl.PDL m_pdl;

    /**
     * Constructs a new and empty PDLCompiler.
     **/

    public PDLCompiler() {
        m_pdl = new org.myrian.persistence.pdl.PDL();
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
