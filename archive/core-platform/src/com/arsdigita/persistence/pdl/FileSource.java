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

import com.arsdigita.util.UncheckedWrapperException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

/**
 * An implementation of PDLSource that loads an individual file.
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #5 $ $Date: 2004/04/07 $
 **/

public class FileSource implements PDLSource {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/pdl/FileSource.java#5 $ by $Author: dennis $, $DateTime: 2004/04/07 16:07:11 $";

    private final File m_file;

    /**
     * Constructs a PDLSource with the contents of the given file.
     *
     * @param file the PDL file
     **/

    public FileSource(File file) {
        m_file = file;
    }

    /**
     * Constructs a PDLSource with the contents of the given file.
     *
     * @param filename the name of the PDL file
     **/

    public FileSource(String filename) {
        this(new File(filename));
    }

    /**
     * Parses the contents of this PDLSource using the given compiler.
     *
     * @param compiler the PDLCompiler used to parse this PDLSource
     **/

    public void parse(PDLCompiler compiler) {
        try {
            compiler.parse(new FileReader(m_file), m_file.getAbsolutePath());
        } catch (FileNotFoundException e) {
            throw new UncheckedWrapperException(e);
        }
    }

}
