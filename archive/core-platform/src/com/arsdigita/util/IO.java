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
package com.arsdigita.util;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;

/**
 * A library of methods for dealing with I/O streams.
 */
public class IO {
    
    /**
     * Copies the contents of an input stream to another
     * output stream.
     *
     * @param src the source file to be sent
     * @param dst the destination to send the file to
     */
    public static void copy(InputStream src,
                            OutputStream dst) 
        throws IOException {
        
        byte buf[] = new byte[4096];
        int ret;

        while ((ret = src.read(buf)) != -1) {
            dst.write(buf, 0, ret);
        }
        
        dst.flush();
    }
    
    
    // XXX add a method that, given a InputStream
    // figures out what character set the containing
    // document is, ie reads the XML prolog.
    // Also have similar char set discovery APIs
    // for HttpServletRequest objects.
}
