/*
 * Copyright (C) 2001, 2002 Red Hat Inc. All Rights Reserved.
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

import java.math.BigInteger;
import java.util.Arrays;
import org.apache.log4j.Logger;
import com.arsdigita.domain.DataObjectNotFoundException;

/**
 * LinkAttributeTest
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #10 $ $Date: 2003/01/07 $
 **/

public abstract class LinkAttributeTest extends PersistenceTestCase {

    public final static String versionId = "$Id: //core-platform/dev/test/src/com/arsdigita/persistence/LinkAttributeTest.java#10 $ by $Author: dennis $, $DateTime: 2003/01/07 14:51:38 $";


    private static Logger s_log =
        Logger.getLogger(LinkAttributeTest.class.getName());

    public LinkAttributeTest(String name) {
        super(name);
    }

    abstract String getModelName();

    public void testArticle() {
        Session ssn = SessionManager.getSession();
        DataObject article = ssn.create(getModelName() + ".Article");

        article.set("id", BigInteger.ZERO);
        String text = "This is the article text.";
        article.set("text", text);
        article.save();

        OID oid = new OID(getModelName() + ".Article", BigInteger.ZERO);

        article = ssn.retrieve(oid);
        assertEquals("incorrect id", BigInteger.ZERO, article.get("id"));
        assertEquals("incorrect text", text, article.get("text"));

        article.delete();

        assertEquals("article not deleted properly", null, ssn.retrieve(oid));
    }

    public void testImage() {
        Session ssn = SessionManager.getSession();
        DataObject image = ssn.create(getModelName() + ".Image");

        image.set("id", BigInteger.ZERO);
        byte[] bytes = "This is the image.".getBytes();
        image.set("bytes", bytes);
        image.save();

        OID oid = new OID(getModelName() + ".Image", BigInteger.ZERO);

        image = ssn.retrieve(oid);
        assertEquals("incorrect id", BigInteger.ZERO, image.get("id"));
        assertTrue("incorrect image",
                   Arrays.equals(bytes, (byte[])image.get("bytes")));

        image.delete();

        assertEquals("image not deleted properly", null, ssn.retrieve(oid));
    }


    public void testLinkAttributes() throws DataObjectNotFoundException {
        Session ssn = SessionManager.getSession();
        DataObject article = ssn.create(getModelName() + ".Article");
	DataObject user = ssn.create(getModelName() + ".User");
	user.set("id", BigInteger.ZERO);
	user.set("email", "foo@bar.com");
	user.set("firstName", "foo");
	user.set("lastNames", "bar");
	user.save();

        article.set("id", BigInteger.ZERO);
        String text = "This is the article text.";
        article.set("text", text);

        int numItems = 10;

        for (int i = 0; i < numItems; i++) {
            DataObject image = ssn.create(getModelName() + ".Image");
            image.set("id", new BigInteger(Integer.toString(i)));
            byte[] bytes = "This is the image.".getBytes();
            image.set("bytes", bytes);
            image.save();
        }

        String captionPrefix = "This is the caption for ";

        DataAssociation images = (DataAssociation) article.get("images");
        DataCollection samples = ssn.retrieve(getModelName() + ".Image");
        while (samples.next()) {
            DataObject image = samples.getDataObject();
            DataObject link = images.add(image);
            link.set("caption", captionPrefix + image.getOID());

	    link.set("user", user);
        }

        article.save();

        String newCaptionPrefix = "This is the new caption for ";

        DataAssociationCursor cursor = images.cursor();
        while (cursor.next()) {
            DataObject image = cursor.getDataObject();
            DataObject link = cursor.getLink();
            DataObject linkuser = (DataObject)link.get("user");
	    assertNotNull("User is null!", linkuser);
            assertEquals("bad link object",
                         captionPrefix + image.getOID(),
                         link.get("caption"));
            link.set("caption", newCaptionPrefix + image.getOID());
        }

        article.save();

        cursor = images.cursor();
        while (cursor.next()) {
            DataObject image = cursor.getDataObject();
            DataObject link = cursor.getLink();
            assertEquals("bad link object",
                         newCaptionPrefix + image.getOID(),
                         link.get("caption"));
        }

        // Now let's try some filtering. This should probably be split out
        // into a seperate test, but I'm in a hurry now.
        cursor = images.cursor();
        cursor.addFilter("link.caption not like 'This %'");
        assertEquals("filtering on a link attribute didn't work",
                     0,
                     cursor.size());

        cursor = images.cursor();
        cursor.addFilter("link.caption like 'This %'");
        assertEquals("filtering on a link attribute didn't work",
                     numItems,
                     cursor.size());
    }
}
