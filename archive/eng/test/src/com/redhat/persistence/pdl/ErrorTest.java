package com.redhat.persistence.pdl;

/**
 * ErrorTest
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #3 $ $Date: 2004/09/22 $
 **/

public class ErrorTest extends PDLTest {

    public final static String versionId = "$Id: //eng/persistence/dev/test/src/com/redhat/persistence/pdl/ErrorTest.java#3 $ by $Author: rhs $, $DateTime: 2004/09/22 15:20:55 $";

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
