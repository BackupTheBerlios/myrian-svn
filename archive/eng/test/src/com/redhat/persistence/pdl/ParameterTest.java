package com.redhat.persistence.pdl;

import com.redhat.persistence.metadata.*;

/**
 * ParameterTest
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2004/09/22 $
 **/

public class ParameterTest extends PDLTest {

    public final static String versionId = "$Id: //eng/persistence/dev/test/src/com/redhat/persistence/pdl/ParameterTest.java#1 $ by $Author: rhs $, $DateTime: 2004/09/22 15:20:55 $";

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
        System.out.println(dump(root));
    }

    public void testCycle() {
        line("model test;");
        line("object type Node<T> {");
        line("    Integer id;");
        line("    Node<T>[0..n] children;");
        line("}");
        line("object type Foo {");
        line("    Node<String> nodes;");
        line("}");
        Root root = parse();
        System.out.println(dump(root));
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
        System.out.println(dump(root));
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
        System.out.println(dump(root));
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
        System.out.println(dump(root));
    }

}
