package org.myrian.persistence.pdl;

/**
 * ErrorTest
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 **/

public class ErrorTest extends PDLTest {


    public void testNestedMapWithNonNestedType() {
        line("model test;");
        line("object type Test {");
        line("    Integer id = tests.id;");
        line("    Foo foo { a = test.a; b = test.b; };");
        line("}");
        line("object type Foo {");
        line("    String a;");
        line("    String b;");
        line("}");
        errorEquals
            ("file: line 4, column 13 [error]: can't nest a non nested type");
    }

}
