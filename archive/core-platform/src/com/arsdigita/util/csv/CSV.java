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

package com.arsdigita.util.csv;

import com.arsdigita.util.*;
import com.arsdigita.util.parameter.*;
import java.io.*;
import java.util.*;
import javax.mail.internet.*;

/**
 * Subject to change.
 *
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: //core-platform/dev/src/com/arsdigita/util/csv/CSV.java#2 $
 */
public final class CSV {
    public final static String versionId =
        "$Id: //core-platform/dev/src/com/arsdigita/util/csv/CSV.java#2 $" +
        "$Author: justin $" +
        "$DateTime: 2003/09/26 15:31:04 $";

    public static final Object[][] load(final Reader reader,
                                        final Parameter[] params) {
        final CSVParameterLoader loader = new CSVParameterLoader
            (reader, params);

        final ArrayList rows = new ArrayList();
        Object[] row;
        ParameterValue value;

        while (loader.next()) {
            row = new Object[params.length];

            for (int i = 0; i < params.length; i++) {
                value = loader.load(params[i]);

                params[i].check(value);

//                 params[i].validate(value);
//
//                 if (!value.getErrors().isEmpty()) {
//                     throw new IllegalArgumentException
//                         (value.getErrors().toString());
//                 }

                row[i] = value.getObject();
            }

            rows.add(row);
        }

        return (Object[][]) rows.toArray(new Object[rows.size()][]);
    }

    private void example() {
        final String csv =
            "\"Justin Ross\",8,jross@redhat.com\n" +
            "Rafi,999,\"rafaels@redhat.com\"\n" +
            "Archit,-80,ashah@redhat.com";

        final Parameter[] params = new Parameter[] {
            new StringParameter("name"),
            new IntegerParameter("number"),
            new EmailParameter("email")
        };

        final Object[][] rows = CSV.load(new StringReader(csv), params);
        Object[] row;

        for (int i = 0; i < rows.length; i++) {
            row = rows[i];

            System.out.print((String) row[0]);
            System.out.print(" ");
            System.out.print((Integer) row[1]);
            System.out.print(" ");
            System.out.print((InternetAddress) row[2]);
            System.out.print("\n");
        }
    }
}
