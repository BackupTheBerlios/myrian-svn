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
package com.arsdigita.util.parameter;

import java.io.File;

import org.apache.log4j.Logger;

/**
 * A Parameter representing a File
 * 
 * @see Parameter
 * @see java.io.File
 * @author bche
 */
public class FileParameter extends AbstractParameter {
    private static final Logger s_log = Logger.getLogger(FileParameter.class);

    public FileParameter(final String name) {
        super(name, File.class);
    }

    public Object unmarshal(final String value, final ErrorList errors) {
        final String sPath = value;
        File file = new File(sPath);
        if (file.exists()) {
            return file;
        } else {
            return null;
        }
    }

    public String marshal(final Object value) {
        final File file = (File) value;
        if (file == null) {
            return null;
        } else {
            return file.getAbsolutePath();
        }
    }

    public void doValidate(final Object value, final ErrorList errors) {
        final File file = (File) value;
        if (!file.exists()) {
            errors.add(
                new ParameterError(
                    this,
                    "File " + file.getAbsolutePath() + " does not exist"));
        }
    }
}
