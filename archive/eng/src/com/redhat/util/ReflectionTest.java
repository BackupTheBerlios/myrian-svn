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
package com.redhat.util;

import java.util.*;
import junit.framework.*;
import junit.extensions.*;

public class ReflectionTest extends TestCase {

    public static junit.framework.Test suite() {
        TestSuite suite = new TestSuite(ReflectionTest.class);
        TestSetup wrapper = new TestSetup(suite) {
            protected void setUp() throws Exception { }
            protected void tearDown() throws Exception { }
        };

        return wrapper;
    }

    public void testCompatibleLength() {
        assertFalse(Reflection.compatible
                    (new Class[] { String.class, String.class, String.class },
                     new Object[] { "", "" }));

        assertFalse(Reflection.compatible
                    (new Class[] { String.class, String.class },
                     new Object[] { "", "", "" }));

        assertTrue(Reflection.compatible
                    (new Class[] { String.class, String.class },
                     new Object[] { "", "" }));
    }

    public void testCompatibleNull() {
        assertTrue(Reflection.compatible
                   (new Class[] { String.class }, new Object[] { null }));

        assertTrue(Reflection.compatible
                   (new Class[] { Integer.class }, new Object[] { null }));
    }

    public void testCompatiblePrimitive() {
        assertTrue(Reflection.compatible
                   (new Class[] { Integer.TYPE },
                    new Object[] { new Integer(0) }));
    }

    public void testCompatiblePrimitiveCoercion() {
        assertTrue(Reflection.compatible
                   (new Class[] { Long.TYPE },
                    new Object[] { new Integer(0) }));

        assertFalse(Reflection.compatible
                    (new Class[] { Integer.TYPE },
                     new Object[] { new Long(0L) }));
    }

    static class Length {
        public Length(String a, String b) { }
        public Length(String a, String b, String c) { }
        public void m(String a, String b) { }
        public void m(String a, String b, String c) { }
    }

    public void testLengthConstructor() throws Exception {
        assertEquals
            (Length.class.getConstructor
             (new Class[] { String.class, String.class, String.class }),
             Reflection.dispatchConstructor
             (Length.class, new Object[] { "", "", "" }));

        assertEquals
            (Length.class.getConstructor
             (new Class[] { String.class, String.class }),
             Reflection.dispatchConstructor
             (Length.class, new Object[] { "", "" }));

        assertEquals
            (null,
             Reflection.dispatchConstructor
             (Length.class, new Object[] { "" }));
    }

    public void testLengthMethod() throws Exception {
        assertEquals
            (Length.class.getMethod
             ("m", new Class[] { String.class, String.class, String.class }),
             Reflection.dispatch
             (Length.class, "m", new Object[] { "", "", "" }));

        assertEquals
            (Length.class.getMethod
             ("m", new Class[] { String.class, String.class }),
             Reflection.dispatch
             (Length.class, "m", new Object[] { "", "" }));

        assertEquals
            (null,
             Reflection.dispatch
             (Length.class, "m", new Object[] { "" }));
    }

    static class SimpleType {
        public SimpleType(Object a, Object b) { }
        public SimpleType(String a, Object b) { }
        public SimpleType(Object a, String b) { }
        public SimpleType(String a, String b) { }

        public void m(Object a, Object b) { }
        public void m(String a, Object b) { }
        public void m(Object a, String b) { }
        public void m(String a, String b) { }
    }

    public void testSimpleTypeConstructor() throws Exception {
        assertEquals
            (SimpleType.class.getConstructor
             (new Class[] { String.class, String.class }),
             Reflection.dispatchConstructor
             (SimpleType.class, new Object[] { "", "" }));

        assertEquals
            (SimpleType.class.getConstructor
             (new Class[] { String.class, Object.class }),
             Reflection.dispatchConstructor
             (SimpleType.class, new Object[] { "", new Integer(0) }));

        assertEquals
            (SimpleType.class.getConstructor
             (new Class[] { Object.class, String.class }),
             Reflection.dispatchConstructor
             (SimpleType.class, new Object[] { new Integer(0), "" }));
    }

    public void testSimpleTypeMethod() throws Exception {
        assertEquals
            (SimpleType.class.getMethod
             ("m", new Class[] { String.class, String.class }),
             Reflection.dispatch
             (SimpleType.class, "m", new Object[] { "", "" }));

        assertEquals
            (SimpleType.class.getMethod
             ("m", new Class[] { String.class, Object.class }),
             Reflection.dispatch
             (SimpleType.class, "m", new Object[] { "", new Integer(0) }));

        assertEquals
            (SimpleType.class.getMethod
             ("m", new Class[] { Object.class, String.class }),
             Reflection.dispatch
             (SimpleType.class, "m", new Object[] { new Integer(0), "" }));
    }

    public interface Root { }
    public interface A1 extends Root { }
    public interface A2 extends A1 { }
    public interface A3 extends A2 { }
    public interface B1 extends Root { }
    static class C1 implements A2, B1 { }
    static class C2 implements A2, A3 { }
    static class CSub1 extends C1 { }
    static class CSubSub1 extends CSub1 { }

    public void testScoreInterfacesDirect() {
        assertEquals(0, Reflection.score(new Class[] { A2.class },
                                         new Object[] { new C1() }));
        assertEquals(0, Reflection.score(new Class[] { B1.class },
                                         new Object[] { new C1() }));
        assertEquals(0, Reflection.score(new Class[] { A2.class },
                                         new Object[] { new C2() }));
        assertEquals(0, Reflection.score(new Class[] { A3.class },
                                         new Object[] { new C2() }));
    }

    public void testScoreInterfacesPath() {
        assertEquals(1, Reflection.score(new Class[] { A1.class },
                                         new Object[] { new C1() }));
        assertEquals(1, Reflection.score(new Class[] { Root.class },
                                         new Object[] { new C1() }));
        assertEquals(1, Reflection.score(new Class[] { A1.class },
                                         new Object[] { new C2() }));
        assertEquals(2, Reflection.score(new Class[] { Root.class },
                                         new Object[] { new C2() }));
    }
}
