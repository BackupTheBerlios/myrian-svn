/*
 * Copyright (C) 2004 Red Hat, Inc. All Rights Reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package org.myrian.persistence.jdo;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * ListTest
 *
 * @since 2004-07-13
 * @version $Revision: #1 $ $Date: 2004/10/01 $
 **/
public class ListTest extends WithTxnCase {
    private final static Logger s_log = Logger.getLogger(ListTest.class);

    private final static List EMAILS = Collections.unmodifiableList
        (Arrays.asList(new String[]
            {"asdf@example.com", "fdsa@example.com", "rhs@lhs.example.org"}));
    private final static String NO_SUCH_EMAIL = "no such email@nowhere.com";

    private Group m_group;

    public ListTest() {}

    public ListTest(String name) {
        super(name);
    }

    protected void setUp() throws Exception {
        super.setUp();
        m_group = new Group(0);
        m_group.setEmail("java-project@redhat.com");
        m_group.setName("Java Hackers");
        m_pm.makePersistent(m_group);
    }

    private List addEmails() {
        final List auxEmails = m_group.getAuxiliaryEmails();
        for (Iterator it=EMAILS.iterator(); it.hasNext(); ) {
            auxEmails.add(it.next());
        }
        return auxEmails;
    }

    public void testAdd() {
        List auxEmails = addEmails();

        for (Iterator it=auxEmails.iterator(); it.hasNext(); ) {
            assertTrue("has email", EMAILS.contains((String) it.next()));
        }

        for (Iterator it=EMAILS.iterator(); it.hasNext(); ) {
            assertTrue("has email", auxEmails.contains((String) it.next()));
        }

    }

    public void testClear() {
        assertEquals("size", 0, m_group.getAuxiliaryEmails().size());
        addEmails();
        assertEquals("size", 3, m_group.getAuxiliaryEmails().size());
        m_group.getAuxiliaryEmails().clear();
        assertEquals("size", 0, m_group.getAuxiliaryEmails().size());
    }

    public void testIsEmpty() {
        assertTrue("empty", m_group.getAuxiliaryEmails().isEmpty());
        m_group.getAuxiliaryEmails().add(EMAILS.get(0));
        assertFalse("empty", m_group.getAuxiliaryEmails().isEmpty());
    }

    public void testToArray() {
        addEmails();
        assertEquals("as arrays",
                    Arrays.asList(EMAILS.toArray()),
                    Arrays.asList(m_group.getAuxiliaryEmails().toArray()));
    }

    public void testToPreallocatedArray() {
        List auxEmails = addEmails();

        String[] actual =
            (String[]) auxEmails.toArray(new String[EMAILS.size()]);

        assertEquals("as arrays",
                    Arrays.asList(EMAILS.toArray()),
                    Arrays.asList(actual));
    }

    public void testGet() {
        List auxEmails = addEmails();

        try {
            auxEmails.get(-1);
            fail("get(-1)");
        } catch (IndexOutOfBoundsException _) {
            ;  // expected
        }

        try {
            auxEmails.get(EMAILS.size());
            fail("get(" + EMAILS.size() + ")");
        } catch (IndexOutOfBoundsException _) {
            ;  // expected
        }

        for (int ii=0; ii<EMAILS.size(); ii++) {
            assertEquals("at index=" + ii, EMAILS.get(ii), auxEmails.get(ii));
        }
    }

    public void testIndexOf() {
        List auxEmails = addEmails();

        for (int ii=0; ii<EMAILS.size(); ii++) {
            String email = (String) EMAILS.get(ii);
            assertEquals("index of " + email, ii, auxEmails.indexOf(email));
        }
    }

    public void testRemoveIndex() {
        List auxEmails = addEmails();

        String zerothElement = (String) auxEmails.remove(0);
        assertEquals("zeroth element", EMAILS.get(0), zerothElement);
        assertFalse("still contains zeroth element",
                    auxEmails.contains(EMAILS.get(0)));
        assertEquals("size", 2, auxEmails.size());
        try {
            auxEmails.remove(EMAILS.size());
            fail("index out of bound: " + EMAILS.size());
        } catch (IndexOutOfBoundsException _) {
            ; // expected
        }
    }


    public void testRemoveElement() {
        List auxEmails = addEmails();

        boolean hadZeroth = auxEmails.remove(EMAILS.get(0));

        assertTrue("had zeroth element", hadZeroth);
        assertFalse("still contains zeroth element",
                    auxEmails.contains(EMAILS.get(0)));
        assertFalse("had no such email", auxEmails.remove(NO_SUCH_EMAIL));
    }

    public void testSet() {
        List auxEmails = addEmails();
        final String newEmail = "new@email.example.com";
        String oldEmail = (String) auxEmails.set(1, newEmail);
        assertEquals("old email", EMAILS.get(1), oldEmail);
        assertEquals("new email", newEmail, auxEmails.get(1));

        try {
            auxEmails.set(EMAILS.size(), "this should fail");
            fail("index of out of bounds");
        } catch (IndexOutOfBoundsException _) {
            ; // expected
        }
    }

    public void testIterator() {
        List auxEmails = addEmails();
        Iterator expected = EMAILS.iterator();

        for (Iterator it=auxEmails.iterator(); it.hasNext(); ) {
            String actual = (String) it.next();
            assertEquals("email", (String) expected.next(), actual);
        }
    }

    public void testEquals() {
        List auxEmails = addEmails();
        assertEquals("x equals y", EMAILS, auxEmails);
        assertEquals("y equals x", auxEmails, EMAILS);
        assertEquals("hash codes", EMAILS.hashCode(), auxEmails.hashCode());
    }

    public void testContainsAll() {
        List auxEmails = addEmails();
        final int size = EMAILS.size();
        for (int ii=0; ii<size; ii++) {
            assertTrue("contains sublist",
                       auxEmails.containsAll(EMAILS.subList(ii, size)));
        }

        List notASubset = new LinkedList(EMAILS);
        notASubset.add(NO_SUCH_EMAIL);
        assertFalse("contains sublist", auxEmails.containsAll(notASubset));
    }

    public void testRemoveAll() {
        List auxEmails = addEmails();
        assertFalse("removed at least one",
                    auxEmails.removeAll(Collections.EMPTY_LIST));

        final Collection coll1 = new LinkedList();
        coll1.add(NO_SUCH_EMAIL);
        assertFalse("removed at least one",
                    auxEmails.removeAll(coll1));

        final Collection coll2 =
            new LinkedList(EMAILS.subList(1, EMAILS.size()));
        coll2.add(NO_SUCH_EMAIL);
        assertTrue("removed at least one",
                    auxEmails.removeAll(coll2));

        assertEquals("remaining size", 1, auxEmails.size());
        assertEquals("remaining element", EMAILS.get(0), auxEmails.get(0));
    }

    public void testRetainAll() {
        List auxEmails = addEmails();
        assertFalse("removed at least one", auxEmails.retainAll(EMAILS));

        final Collection coll =
            new LinkedList(EMAILS.subList(1, EMAILS.size()));
        coll.add(NO_SUCH_EMAIL);

        assertTrue("removed some", auxEmails.retainAll(coll));
        assertEquals("remaining size", 2, auxEmails.size());
        assertFalse("contains EMAILS[0]", auxEmails.contains(EMAILS.get(0)));
    }
}
