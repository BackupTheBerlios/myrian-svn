/*
 * Copyright (C) 2001 ArsDigita Corporation. All Rights Reserved.
 *
 * The contents of this file are subject to the ArsDigita Public 
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of
 * the License at http://www.arsdigita.com/ADPL.txt
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */

package com.arsdigita.util;

import junit.framework.TestCase;

import java.util.HashMap;

public class StringUtilsTest extends TestCase {

    public StringUtilsTest(String name) {
        super(name);
    }

    public void testEmptyString() {

        assertTrue( StringUtils.emptyString(null) );
        assertTrue( StringUtils.emptyString("") );
        assertTrue( StringUtils.emptyString((Object)(new String(""))) );

        assertTrue( ! StringUtils.emptyString("foo") );
        assertTrue( ! StringUtils.emptyString((Object)(new String("foo"))) );
        assertTrue( ! StringUtils.emptyString((Object)(new Integer(1))) );
        
    }

    public void testQuoteHtml() {

        assertEquals( "", StringUtils.quoteHtml(null) );
        assertEquals( "", StringUtils.quoteHtml("") );
        assertEquals( "foo", StringUtils.quoteHtml("foo") );
        assertEquals( "foo&amp;", StringUtils.quoteHtml("foo&") );
        assertEquals( "&amp;foo", StringUtils.quoteHtml("&foo") );
        assertEquals( "&amp;foo&amp;", StringUtils.quoteHtml("&foo&") );
        assertEquals( "&amp;&quot;&lt;&gt;&quot;&amp;", 
                      StringUtils.quoteHtml("&\"<>\"&") );

    }

    public void testGetParameter() throws Exception {

        String plist = "boyspet=play,pet=dog,play=yes,age=34,eopt=,opt=23";
        verifyGet(plist,"boyspet","play");
        verifyGet(plist,"pet","dog");
        verifyGet(plist,"play","yes");
        verifyGet(plist,"age","34");
        verifyGet(plist,"eopt","");
        verifyGet(plist,"opt","23");
        verifyGet(plist,"spet",null);
        verifyGet(plist,"notin",null);
    }

    // helper method for above test.
    private static void verifyGet(String plist, String key, String expected) {
        String found = StringUtils.getParameter(key, plist, ',');
        assertEquals("Expected parameter not found, key=" +key+
                     " expected=" + expected + " found=" +found,
                     expected, found);
    }




    public void testSplit() throws Exception {

        String plist = "cat,hat,,bat,rat";
        String [] ar = StringUtils.split(plist, ',');
        verifySplit("cat", ar[0]);
        verifySplit("hat", ar[1]);
        verifySplit("", ar[2]);
        verifySplit("bat",ar[3]);
        verifySplit("rat",ar[4]);
        assertEquals("expected array length 5, found="
                     + ar.length,ar.length,5);
        plist = ",,dog,fish,,,";
        ar = StringUtils.split(plist, ',');
        verifySplit("",ar[0]);
        verifySplit("",ar[1]);
        verifySplit("dog", ar[2]);
        verifySplit("fish", ar[3]);
        verifySplit("", ar[4]);
        verifySplit("", ar[5]);
        verifySplit("", ar[6]);
        assertEquals("expected array length 7, found="
                     + ar.length,ar.length,7);

    }

    // helper method for above test.
    private void verifySplit(String expected, String found) {
        String errMsg = "Split, expected = " + expected +
            " found = " + found;
        assertEquals(errMsg, expected, found);
    }



    public void testStripWhiteSpace() throws Exception {

        String in = " <   H>   e \t\n ll/>   o  . \n   ";
        String expected_out = "< H> e ll/> o .";
        String actual_out = StringUtils.stripWhiteSpace(in);
        assertEquals("stripWhiteSpace failed.  Expected = '" +
                     expected_out + "', Found = '" + actual_out + "'",
                     expected_out, actual_out);

    }

    public void testAddNewline() throws Exception {
        String in = "*";
        String nl = System.getProperty("line.separator");

        String expected_out = in + nl;
        String actual_out = StringUtils.addNewline(in);
        assertEquals("failed to add newline", expected_out, actual_out);

        in = "* ";
        expected_out = in;
        actual_out = StringUtils.addNewline(in);
        assertEquals("added unecessary newline", expected_out, actual_out);

        in = "*" + nl;
        expected_out = in;
        actual_out = StringUtils.addNewline(in);
        assertEquals("added unecessary newline", expected_out, actual_out);
    }

    public void testHtmlToText() {

        String in = "<p>this is the text<br>newline .</p>one<br><b>two</b><br>";
        String expected_out = "\n\nthis is the text\nnewline .one\ntwo\n";
        String actual_out = StringUtils.htmlToText(in);
        assertEquals("htmlToText invalid", expected_out, actual_out);

        in = "Text with <a <b <c > strange markup";
        expected_out = "Text with  strange markup";
        actual_out = StringUtils.htmlToText(in);
        assertEquals(expected_out, actual_out);
        
    }

    public void testTrimleft() {
        String in = "a";
        String expected_out = "a";
        String actual_out = StringUtils.trimleft(in);
        assertEquals("trimleft invalid", expected_out, actual_out);

        in = " a";
        expected_out = "a";
        actual_out = StringUtils.trimleft(in);
        assertEquals("trimleft invalid", expected_out, actual_out);

        in = " ";
        expected_out = "";
        actual_out = StringUtils.trimleft(in);
        assertEquals("trimleft invalid", expected_out, actual_out);
   }

    public void testRepeat() {
        String in = "a";
        String expected_out = "aaaaa";
        String actual_out = StringUtils.repeat(in,5);
        assertEquals("repeat invalid", expected_out, actual_out);

        actual_out = StringUtils.repeat('a',5);
        assertEquals("repeat invalid", expected_out, actual_out);
    }

    public void testWrap() {

        // Identity test
        String in = "a\n";
        String expected_out = in;
        String actual_out = StringUtils.wrap(in);
        assertEquals("wrap failed identify test",
                     expected_out,
                     actual_out);

        // Identify test with multiple words
        in = "a b c d e\n";
        expected_out = in;
        actual_out = StringUtils.wrap(in);
        assertEquals("wrap failed identify test",
                     expected_out,
                     actual_out);

        // Simple test with short lines
        in = StringUtils.repeat("1234 ",5);
        expected_out = StringUtils.repeat("1234\n",5);
        actual_out = StringUtils.wrap(in,1);
        assertEquals("wrap invalid", 
                     expected_out, 
                     actual_out);

        // Verify preservation of line breaks
        in = StringUtils.repeat("1234\n",5);
        expected_out = in;
        actual_out = StringUtils.wrap(in,100);
        assertEquals("line break preservation failed",
                     expected_out, 
                     actual_out);

        // Verify a "standard" wrapping case
        in = StringUtils.repeat("1234 ",10);
        expected_out = 
            StringUtils.repeat("1234 ",5).trim() + "\n" +
            StringUtils.repeat("1234 ",5).trim() + "\n";
            
        actual_out = StringUtils.wrap(in,25);
        assertEquals("line wrapping failed",
                     expected_out, 
                     actual_out);
    }
    
    public void testPlaceholders() {
	String in = "foo ::bar:: wizz";
	String expected_out = "foo eek wizz";
	String actual_out = StringUtils.interpolate(in, "bar", "eek");
	
	assertEquals("interpolate failed simple placeholder",
		     expected_out,
		     actual_out);
	
	HashMap vars = new HashMap();
	vars.put("bar", "eek");
	vars.put("more", "wibble");
	
	in = "foo ::bar:: wizz ::more:: done";
	expected_out = "foo eek wizz wibble done";
	actual_out = StringUtils.interpolate(in, vars);
	assertEquals("interpolate failed hashmap test",
		     expected_out,
		     actual_out);
	
    }
}
