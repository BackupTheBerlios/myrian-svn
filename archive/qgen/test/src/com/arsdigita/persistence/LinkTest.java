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

import java.math.*;
import java.util.Arrays;
import org.apache.log4j.Logger;

/**
 * LinkTest
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #1 $ $Date: 2003/12/10 $
 **/

public abstract class LinkTest extends PersistenceTestCase {

    public final static String versionId = "$Id: //core-platform/test-qgen/test/src/com/arsdigita/persistence/LinkTest.java#1 $ by $Author: dennis $, $DateTime: 2003/12/10 16:59:20 $";

    private static Logger s_log =
        Logger.getLogger(LinkTest.class.getName());

    public LinkTest(String name) {
        super(name);
    }

    abstract String getModel();

    public void testArticle() {
        Session ssn = SessionManager.getSession();
        DataObject article = ssn.create(getModel() + ".Article");
        article.set("id", BigInteger.ZERO);
        String text = "This is the article text.";
        article.set("text", text);
        article.save();

        OID oid = new OID(getModel() + ".Article", BigInteger.ZERO);

        article = ssn.retrieve(oid);
        assertEquals("incorrect id", BigInteger.ZERO, article.get("id"));
        assertEquals("incorrect text", text, article.get("text"));

        article.delete();

        assertEquals("article not deleted properly", null, ssn.retrieve(oid));
    }

    public void testImage() {
        Session ssn = SessionManager.getSession();
        DataObject image = ssn.create(getModel() + ".Image");
        image.set("id", BigInteger.ZERO);
        byte[] bytes = "This is the image.".getBytes();
        image.set("bytes", bytes);
        image.save();

        OID oid = new OID(getModel() + ".Image", BigInteger.ZERO);

        image = ssn.retrieve(oid);
        assertEquals("incorrect id", BigInteger.ZERO, image.get("id"));
        assertTrue("incorrect image",
                   Arrays.equals(bytes, (byte[])image.get("bytes")));

        image.delete();

        assertEquals("image not deleted properly", null, ssn.retrieve(oid));
    }

    public void testArticleImageLink() {
        Session ssn = SessionManager.getSession();
        DataObject article = ssn.create(getModel() + ".Article");
        article.set("id", BigInteger.ZERO);
        String text = "This is the article text.";
        article.set("text", text);

        for (int i = 0; i < 10; i++) {
            DataObject image = ssn.create(getModel() + ".Image");
            image.set("id", new BigInteger(Integer.toString(i)));
            byte[] bytes = "This is the image.".getBytes();
            image.set("bytes", bytes);
            image.save();
        }

        DataAssociation links = (DataAssociation) article.get("images");
        DataCollection images = ssn.retrieve(getModel() + ".Image");
        while (images.next()) {
            DataObject image = images.getDataObject();
            DataObject link = ssn.create(getModel() + ".ArticleImageLink");
            link.set("article", article);
            link.set("image", image);
            link.set("caption", "The caption for: " + image.getOID());
            links.add(link);
        }

        article.save();

        DataAssociationCursor cursor = links.cursor();
        assertEquals(10, cursor.size());

        DataCollection aiLinks = ssn.retrieve(getModel()+".ArticleImageLink");
        aiLinks.addEqualsFilter("image.id", new BigDecimal(5));
        if (aiLinks.next()) {
            DataObject linkArticle = (DataObject) aiLinks.get("article");
            DataObject linkImage = (DataObject) aiLinks.get("image");
            String caption = (String) aiLinks.get("caption");
            assertEquals(BigInteger.valueOf(0), linkArticle.get("id"));
            assertEquals(BigInteger.valueOf(5), linkImage.get("id"));

            if (aiLinks.next()) { fail("too many rows"); }
        } else {
            fail("no rows returned");
        }

        article.delete();
    }
}
