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
package com.arsdigita.util.protocol.resource;

import java.net.*;
import java.io.*;

/**
 * Handler
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2004/05/07 $
 **/

public class Handler extends URLStreamHandler {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/util/protocol/resource/Handler.java#1 $ by $Author: dan $, $DateTime: 2004/05/07 07:12:01 $";

    protected void parseURL(URL url, String spec, int start, int limit) {
        // trim leading slashes
        while (start < spec.length()) {
            char c = spec.charAt(start);
            if (c != '/') { break; }
            start++;
        }

        setURL(url, url.getProtocol(), null, -1, null, null,
               spec.substring(start, limit), null, url.getRef());
    }

    protected URLConnection openConnection(URL url) {
        return new URLConnection(url) {

            public void connect() {
                // do nothing
            }

            public InputStream getInputStream() throws IOException {
                ClassLoader ldr =
                    Thread.currentThread().getContextClassLoader();
                URL url = getURL();
                String resource = url.getPath();
                if (resource == null || resource.equals("")) {
                    throw new FileNotFoundException(url.toExternalForm());
                }
                InputStream is = ldr.getResourceAsStream(resource);
                if (is == null) {
                    throw new FileNotFoundException(url.toExternalForm());
                }
                return is;
            }

        };
    }

}
