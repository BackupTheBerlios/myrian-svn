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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;
import org.apache.log4j.Logger;

/**
 * This takes in a path and makes sure that the resource exists either
 * as a File or an actual resource.  If it does, it returns the
 * InputStream for the given Resource.  If it does not, and if it is
 * required, it logs an error.  Otherwise, it returns null.
 *
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: //core-platform/dev/src/com/arsdigita/util/parameter/ResourceParameter.java#2 $
 */
public class ResourceParameter extends StringParameter {
    public final static String versionId =
        "$Id: //core-platform/dev/src/com/arsdigita/util/parameter/ResourceParameter.java#2 $" +
        "$Author: jorris $" +
        "$DateTime: 2003/10/28 18:36:21 $";

    private static final Logger s_log = Logger.getLogger(ResourceParameter.class);

    public ResourceParameter(final String name) {
        super(name);
    }

    public ResourceParameter(final String name,
                         final int multiplicity,
                         final Object defaalt) {
        super(name, multiplicity, defaalt);
    }

    protected Object unmarshal(String value, final ErrorList errors) {
        File file = new File(value);

        if (!file.exists()) {
            // it is not a standard file so lets try to see if it
            // is a resource
            if (value.startsWith("/")) {
                value = value.substring(1);
            }

            ClassLoader cload = Thread.currentThread().getContextClassLoader();
            URL url = cload.getResource(value);
            InputStream stream = cload.getResourceAsStream(value);
            if (stream == null && isRequired()) {
                s_log.error(value + " is not a valid file and is required");

                final ParameterError error = new ParameterError
                    (this, "Resource not found");
                errors.add(error);
            }
            return stream;
        } else {
            try {
                return new FileInputStream(file);
            } catch (FileNotFoundException ioe) {
                // we know the file exists so this should not
                // be an issue
                s_log.error(value + " is not a valid file and is required", ioe);

                errors.add(new ParameterError(this, ioe));

                return null;
            }
        }
    }
}
