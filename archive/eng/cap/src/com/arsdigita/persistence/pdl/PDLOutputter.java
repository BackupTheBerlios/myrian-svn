/*
 * Copyright (C) 2002-2004 Red Hat, Inc.  All Rights Reserved.
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

import com.arsdigita.persistence.metadata.MetadataRoot;
import com.arsdigita.persistence.metadata.Model;
import com.arsdigita.persistence.metadata.ObjectType;
import com.redhat.persistence.metadata.Root;
import com.redhat.persistence.pdl.PDLWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;


/**
 * An class that outputs the PDL associated with a particular
 * metadata set to files.  Each file will contain the metadata for a single
 * model.  The output will go to the a given directory, with each PDL
 * file being named after the fully qualified model name.
 *
 * @author Patrick McNeill
 * @version $Id: //eng/persistence/dev/cap/src/com/arsdigita/persistence/pdl/PDLOutputter.java#2 $
 */
public class PDLOutputter {
    /**
     * Output the contents of an entire metadata hierarchy as PDL files.
     *
     * @param root the metadata root of the hierarchy to output
     * @param directory the directory to output to
     */
    public static void writePDL(MetadataRoot root, File directory)
        throws IOException {
        Iterator models = root.getModels();

        Root rt = root.getRoot();

        while (models.hasNext()) {
            Model model = (Model) models.next();
            FileWriter writer =
                new FileWriter(new File(directory, model.getName() + ".pdl"));
            PDLWriter out = new PDLWriter(writer);
            for (Iterator it = model.getObjectTypes().iterator();
                 it.hasNext(); ) {
                ObjectType ot = (ObjectType) it.next();
                out.write(rt.getObjectType(ot.getQualifiedName()));
                writer.write("\n\n");
            }
            writer.close();
        }
    }

}
