package org.myrian.persistence.pdl;

/**
 * ErrorTest
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2004/10/01 $
 **/

public class ErrorTest extends PDLTest {

    public final static String versionId = "$Id: //eng/persistence/dev/test/src/org/myrian/persistence/pdl/ErrorTest.java#1 $ by $Author: vadim $, $DateTime: 2004/10/01 18:41:18 $";

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
