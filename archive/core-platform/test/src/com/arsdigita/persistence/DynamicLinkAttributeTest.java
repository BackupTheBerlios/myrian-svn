/*
 * Copyright (C) 2001, 2002, 2003, 2003 Red Hat Inc. All Rights Reserved.
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

package com.arsdigita.persistence;

import com.arsdigita.tools.junit.extensions.BaseTestSetup;
import com.arsdigita.persistence.metadata.*;
import java.math.*;
import java.util.*;
import java.io.*;
import junit.framework.TestSuite;
import org.apache.log4j.Logger;

/**
 * LinkAttributeTest
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #7 $ $Date: 2003/09/03 $
 */

public class DynamicLinkAttributeTest extends LinkAttributeTest {

    public final static String versionId = "$Id: //core-platform/dev/test/src/com/arsdigita/persistence/DynamicLinkAttributeTest.java#7 $ by $Author: ashah $, $DateTime: 2003/09/03 11:13:55 $";

    public DynamicLinkAttributeTest(String name) {
        super(name);
    }

    String getModelName() {
        return "mdsql";
    }

    public void testReferenceLinkAttribute() {
        Session ssn = SessionManager.getSession();
        DataObject user = ssn.create(getModelName() + ".User");
        user.set("id", BigInteger.valueOf(0));
        user.set("email", "foo@bar.com");
        user.set("firstName", "foo");
        user.set("lastNames", "bar");
        user.save();

        DataObject[] images = new DataObject[2];
        for (int i = 0; i < images.length; i++) {
            images[i] = ssn.create(getModelName() + ".Image");
            images[i].set("id", BigInteger.valueOf(i));
            byte[] bytes = "This is the image.".getBytes();
            images[i].set("bytes", bytes);
            images[i].save();
        }

        // set image
        user.set("image", images[0]);
        user.save();

        // retrieve and then update caption
        DataAssociationCursor dac =
            ((DataAssociation) images[0].get("users")).cursor();
        dac.next();
        assertNull(dac.getLinkProperty("caption"));
        assertEquals(user, dac.getDataObject());
        DataObject link = dac.getLink();
        link.set("caption", "caption");
        link.save();

        dac = ((DataAssociation) images[0].get("users")).cursor();
        dac.next();
        assertEquals("caption", dac.getLinkProperty("caption"));
        assertEquals(1L, ((DataAssociation) images[0].get("users")).size());

        // set new image as image
        user.set("image", images[1]);
        user.save();


        // check that old image is no longer associated with user
        assertEquals(0L, ((DataAssociation) images[0].get("users")).size());

        // check that new image is associated with user and has no caption
        dac = ((DataAssociation) images[1].get("users")).cursor();
        dac.next();
        assertNull(dac.getLinkProperty("caption"));
        assertEquals(1L, ((DataAssociation) images[1].get("users")).size());
    }
}
