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
package com.arsdigita.persistence;

import java.math.BigInteger;
import java.util.Arrays;
import org.apache.log4j.Logger;

/**
 * LinkAttributeTest
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #2 $ $Date: 2004/08/30 $
 **/

public abstract class LinkAttributeTest extends PersistenceTestCase {

    public final static String versionId = "$Id: //eng/persistence/dev/cap/test/src/com/arsdigita/persistence/LinkAttributeTest.java#2 $ by $Author: dennis $, $DateTime: 2004/08/30 14:24:55 $";


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

    /**
     * Tests the handling of a path that isn't a property or a link attribute.
     */
    public void testLinkNamespace() {
        Session ssn = SessionManager.getSession();
        DataObject article = ssn.create(getModelName() + ".Article");
        article.set("id", BigInteger.ZERO);
        article.set("text", "text");
        article.save();
        DataAssociation images = (DataAssociation) article.get("images");
        images.addInSubqueryFilter("id", "examples.DataQueryZeroOrOneRow");
        images.size();
    }

    public void testLinkAttributes() {
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
