package org.myrian.persistence.pdl;

import org.myrian.persistence.metadata.*;

/**
 * ParameterTest
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2004/10/01 $
 **/

public class ParameterTest extends PDLTest {

    public final static String versionId = "$Id: //eng/persistence/dev/test/src/org/myrian/persistence/pdl/ParameterTest.java#1 $ by $Author: vadim $, $DateTime: 2004/10/01 18:41:18 $";

    private void foo() {
        line("model test;");
        line("object type Foo<A, B, C> {");
        line("  A a;");
        line("  B b;");
        line("  C c;");
        line("}");
    }

    public void testDefine() {
        foo();
        Root root = parse();
        ObjectType ot = root.getObjectType("test.Foo");
        assertEquals("test.Foo", ot.getQualifiedName());
        assertEquals("Foo", ot.getName());
    }

    public void testUse() {
        foo();
        line("object type Bar {");
        line("    Foo<Integer, String, Long> foo;");
        line("}");
        Root root = parse();
        ObjectType integer = root.getObjectType("global.Integer");
        ObjectType str = root.getObjectType("global.String");
        ObjectType lng = root.getObjectType("global.Long");
        ObjectType foo = root.getObjectType("test.Bar")
            .getProperty("foo").getType();
        assertEquals(integer, foo.getProperty("a").getType());
        assertEquals(str, foo.getProperty("b").getType());
        assertEquals(lng, foo.getProperty("c").getType());
    }

    public void testCycle() {
        line("model test;");
        line("object type Node<T> {");
        line("    Integer id;");
        line("    Node<T>[0..n] children;");
        line("    T value;");
        line("}");
        line("object type Foo {");
        line("    Node<String> nodes;");
        line("}");
        Root root = parse();
        ObjectType str = root.getObjectType("global.String");
        ObjectType nodeStr = root.getObjectType("test.Foo")
            .getProperty("nodes").getType();
        assertEquals(nodeStr, nodeStr.getProperty("children").getType());
        assertEquals(str, nodeStr.getProperty("value").getType());
    }

    public void testIndirectInstantiation() {
        line("model test;");
        line("object type Foo<A> {");
        line("  A a;");
        line("}");
        line("object type Bar<B> {");
        line("  Foo<B> foo;");
        line("}");
        line("object type Baz {");
        line("  Bar<Integer> bar;");
        line("}");
        Root root = parse();
        ObjectType integer = root.getObjectType("global.Integer");
        ObjectType baz = root.getObjectType("test.Baz");
        ObjectType barInt = baz.getProperty("bar").getType();
        ObjectType fooInt = barInt.getProperty("foo").getType();
        assertEquals(integer, fooInt.getProperty("a").getType());
    }

    public void testMutualRecursion() {
        line("model test;");
        line("object type Foo<A> {");
        line("  Bar<A> a;");
        line("}");
        line("object type Bar<B>{");
        line("  Foo<B> b;");
        line("}");
        line("object type Baz {");
        line("  Foo<Integer> foo;");
        line("  Bar<String> bar;");
        line("}");
        Root root = parse();
        ObjectType baz = root.getObjectType("test.Baz");
        ObjectType fooInt = baz.getProperty("foo").getType();
        ObjectType barStr = baz.getProperty("bar").getType();
        ObjectType fooStr = barStr.getProperty("b").getType();
        ObjectType barInt = fooInt.getProperty("a").getType();
        assertEquals(barInt, fooInt.getProperty("a").getType());
        assertEquals(barStr, fooStr.getProperty("a").getType());
        assertEquals(fooInt, barInt.getProperty("b").getType());
        assertEquals(fooStr, barStr.getProperty("b").getType());
    }

    public void testQualifiedReference() {
        line("file1", "model test;");
        line("file1", "object type Foo<A> {");
        line("file1", "  A a;");
        line("file1", "}");
        line("file2", "model bar;");
        line("file2", "object type Bar {");
        line("file2", "  test.Foo<Integer> foo;");
        line("file2", "}");
        Root root = parse();
        ObjectType integer = root.getObjectType("global.Integer");
        ObjectType bar = root.getObjectType("bar.Bar");
        assertEquals
            (integer, bar.getProperty("foo").getType()
             .getProperty("a").getType());
    }

    public void testSubclassing() {
        line("model test;");
        line("object type Foo<A> extends A {");
        line("  Integer foo;");
        line("}");
        line("object type Bar {");
        line("  Integer id;");
        line("}");
        line("object type Baz {");
        line("  Foo<Bar> foobar;");
        line("}");
        Root root = parse();
        ObjectType bar = root.getObjectType("test.Bar");
        ObjectType baz = root.getObjectType("test.Baz");
        assertEquals(bar, baz.getProperty("foobar").getType().getSupertype());
    }

}
