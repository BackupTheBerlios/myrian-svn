/*
 * Copyright (C) 2003-2004 Red Hat, Inc.  All Rights Reserved.
 *
 * This program is Open Source software; you can redistribute it and/or
 * modify it under the terms of the Open Software License version 2.1 as
 * published by the Open Source Initiative.
 *
 * You should have received a copy of the Open Software License along
 * with this program; if not, you may obtain a copy of the Open Software
 * License version 2.1 from http://www.opensource.org/licenses/osl-2.1.php
 * or by writing the Open Source Initiative c/o Lawrence Rosen, Esq.,
 * 3001 King Ranch Road, Ukiah, CA 95482.
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
 * @version $Revision: #2 $ $Date: 2004/08/30 $
 **/

public class FileSource implements PDLSource {

    public final static String versionId = "$Id: //eng/persistence/dev/cap/src/com/arsdigita/persistence/pdl/FileSource.java#2 $ by $Author: dennis $, $DateTime: 2004/08/30 14:24:55 $";

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
