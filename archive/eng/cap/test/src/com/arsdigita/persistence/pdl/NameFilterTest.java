/*
 * Copyright (C) 2004 Red Hat, Inc.  All Rights Reserved.
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

import java.io.File;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import junit.framework.TestCase;

/**
 * NameFilterTest
 *
 * @author <a href="mailto:ashah@redhat.com">ashah@redhat.com</a>
 * @version $Revision: #2 $ $Date: 2004/08/30 $
 **/

public class NameFilterTest extends TestCase {

    public final static String versionId = "$Id: //eng/persistence/dev/cap/test/src/com/arsdigita/persistence/pdl/NameFilterTest.java#2 $ by $Author: dennis $, $DateTime: 2004/08/30 14:24:55 $";

    public NameFilterTest(String name) {
        super(name);
    }

    public void testNames() {
        HashMap inputs = new HashMap();
        ArrayList input = new ArrayList();

        inputs.put("a.ext", Boolean.TRUE);
        input.add("a.ext");

        inputs.put("b.ext", Boolean.TRUE);
        inputs.put("b.suf", Boolean.FALSE);
        input.add("b.ext");
        input.add("b.suf");

        inputs.put("c.ext", Boolean.FALSE);
        inputs.put("c.suf.ext", Boolean.TRUE);
        inputs.put("c.suf", Boolean.FALSE);
        input.add("c.ext");
        input.add("c.suf.ext");
        input.add("c.suf");

        inputs.put("cc.ext", Boolean.FALSE);
        inputs.put("cc.suf.ext", Boolean.TRUE);
        inputs.put("cc.suf", Boolean.FALSE);
        input.add("cc.suf");
        input.add("cc.suf.ext");
        input.add("cc.ext");

        inputs.put("1.2" + File.separatorChar + "d.ext", Boolean.FALSE);
        inputs.put("1.2" + File.separatorChar + "d.suf.ext", Boolean.TRUE);
        inputs.put("1.2" + File.separatorChar + "d.suf.suf.ext", Boolean.TRUE);
        input.add("1.2" + File.separatorChar + "d.ext");
        input.add("1.2" + File.separatorChar + "d.suf.ext");
        input.add("1.2" + File.separatorChar + "d.suf.suf.ext");

        inputs.put("e.suf.ext", Boolean.TRUE);
        inputs.put("e.ext", Boolean.FALSE);
        inputs.put("e.oth.ext", Boolean.FALSE);
        input.add("e.suf.ext");
        input.add("e.ext");
        input.add("e.oth.ext");

        NameFilter filter = new NameFilter("suf", "ext");

        Collection output = filter.accept(input);

        for (Iterator it = inputs.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry me = (Map.Entry) it.next();
            Boolean expected = (Boolean) me.getValue();
            String s = (String) me.getKey();

            if (expected.booleanValue() ^ output.contains(s)) {
                fail("expected " + me.getValue() + " for " + me.getKey());
            }
        }
    }
}
