package com.arsdigita.persistence.metadata;

import junit.framework.*;
import java.util.ArrayList;

public class ConstraintTest extends TestCase {

    private Table m_table;
    private Column[] columns;

    public ConstraintTest(String name) {
        super(name);
    }

    public static void main(String args[]) {
        junit.textui.TestRunner.run(ConstraintTest.class);
    }

    protected void setUp() {
        // generate the table
        m_table = new Table("tableTest");

        // generate some columns
        columns = new Column[5];
        columns[0] = new Column(m_table, "test_my_long_column_name");
        columns[1] = new Column(m_table, "s");
        columns[2] = new Column(m_table, "the_longest_table_name_we_have_even_seen");
        columns[3] = new Column(m_table, "another_name");
        columns[4] = new Column(m_table, "yet_anotherName");

    }

    public void testUniqueKeyConstraint() {
        ArrayList names = new ArrayList();

        // test to make sure there is an exception for adding the same 
        // constraint twice
        UniqueKey key = new UniqueKey(m_table, null, columns);
        names.add(key.getName());
        try {
            key = new UniqueKey(m_table, null, columns);
            fail("Adding the same key twice should cause an exception");
        } catch (IllegalArgumentException e) {
            // it should be here
        }

        Column[] cols = key.getColumns();
        for (int i = 0; i < columns.length; i++) {
            assertTrue("Incorrect columns returned", 
                       columns[i].equals(cols[i]));
        }

        // test name generation
        names.add(columns[0].getName());
        String name = (new UniqueKey(null, columns[1])).getName();
        assertTrue("name: " + name + " was generated twice", 
                   !names.contains(name));
        names.add(name);

        name = (new UniqueKey(null, columns[2])).getName();
        assertTrue("name: " + name + " was generated twice", 
                   !names.contains(name));
        names.add(name);

        name = (new UniqueKey(null, columns[3])).getName();
        assertTrue("name: " + name + " was generated twice", 
                   !names.contains(name));
        names.add(name);

        name = (new UniqueKey(null, columns[4])).getName();
        assertTrue("name: " + name + " was generated twice", 
                   !names.contains(name));
        names.add(name);
        // now, make sure that we get the same name for the same item
        Column column1 = new Column(m_table, "my_name");
        Constraint con1 = (new UniqueKey(null, column1));

        assertTrue("The same constraint returned different names: " +
                   con1.getName() + "; " + con1.getName(),
                   con1.getName().equals(con1.getName()));
        Column column3 = new Column(m_table, "my_name1");
        Constraint con3 = (new UniqueKey(null, column3));
        assertTrue("A similar name for the column returned the same " +
                   "constraint name: " + con3.getName(), 
                   !con3.getName().equals(con1.getName()));

        Table table = new Table("table_with_relatively_long_name");
        Column column4 = new Column(table, "my_na");
        Constraint con4 = (new UniqueKey(null, column4));
        Column column5 = new Column(table, "my_na1");
        Constraint con5 = (new UniqueKey(null, column5));
        assertTrue("Two different constraints had the same name",
                   !con4.getName().equals(con5.getName()));
    }


}
