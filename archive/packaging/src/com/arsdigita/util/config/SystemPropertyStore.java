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

package com.arsdigita.util.config;

import com.arsdigita.util.*;
import com.arsdigita.util.parameter.*;
import java.util.*;

/**
 * Subject to change.
 *
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: //core-platform/test-packaging/src/com/arsdigita/util/config/SystemPropertyStore.java#1 $
 */
public class SystemPropertyStore implements ParameterStore {
    public final static String versionId =
        "$Id: //core-platform/test-packaging/src/com/arsdigita/util/config/SystemPropertyStore.java#1 $" +
        "$Author: justin $" +
        "$DateTime: 2003/08/26 11:56:51 $";

    public String read(final Parameter param) {
        return System.getProperty(param.getName());
    }

    public List readList(final Parameter param) {
        final String string = read(param);
        final ArrayList list = new ArrayList();

        final int len = string.length();
        int start = 0;

        while (true) {
            int end = string.indexOf(',', start);

            if (end == -1) {
                if (len > start) {
                    list.add(string.substring(start, len).trim());
                }

                break;
            } else {
                list.add(string.substring(start, end).trim());
                start = end + 1;
            }
        }

        return list;
    }
}
