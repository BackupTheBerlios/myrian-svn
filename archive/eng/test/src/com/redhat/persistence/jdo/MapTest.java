package com.redhat.persistence.jdo;

import com.redhat.persistence.Session;
import com.redhat.persistence.jdo.C;
import com.redhat.persistence.jdo.PersistenceManagerImpl;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * MapTest
 *
 * @since 2004-07-13
 * @version $Revision: #4 $ $Date: 2004/07/14 $
 **/
public class MapTest extends WithTxnCase {
    private final static Logger s_log = Logger.getLogger(MapTest.class);

    private final static String TOPIC1 = "Samba";
    private final static String TOPIC2 = "OProfile";

    private final static Integer PAGE1 = new Integer(3);
    private final static Integer PAGE2 = new Integer(15);

    private Session m_ssn;
    private Magazine m_mag;

    public MapTest() {}

    public MapTest(String name) {
        super(name);
    }

    protected void setUp() throws Exception {
        super.setUp();
        PersistenceManagerImpl pm = (PersistenceManagerImpl) m_pm;
        m_ssn = pm.getSession();
        m_mag = new Magazine(0);
        m_mag.setTitle("Wide Open");

        m_mag.getIndex().put(TOPIC1, PAGE1);
        m_mag.getIndex().put(TOPIC2, PAGE2);

        m_pm.makePersistent(m_mag);
    }

    public void testMagazine1() {
        javax.jdo.Query qq = m_pm.newQuery
            ("com.redhat.persistence.OQL",
             "all(com.redhat.persistence.jdo.Magazine)");
        Collection magazines = (Collection) qq.execute();
        Iterator it = magazines.iterator();
        assertTrue("has next", it.hasNext());
        Magazine current = (Magazine) it.next();
        assertEquals("wide open", m_mag, current);
        Map idx = current.getIndex();
        assertTrue("has TOPIC1", idx.containsKey(TOPIC1));
        assertTrue("has TOPIC2", idx.containsKey(TOPIC2));
        assertEquals("TOPIC1 on PAGE 3", PAGE1, idx.get(TOPIC1));
        assertEquals("PAGE 3", PAGE1, idx.remove(TOPIC1));
        assertTrue("has TOPIC1", !idx.containsKey(TOPIC1));
        assertTrue("has TOPIC2", idx.keySet().contains(TOPIC2));
    }

    public void testEntrySet() {
        Map.Entry entry = new Entry(TOPIC2, PAGE2);
        assertTrue("entrySet has (TOPIC2, PAGE2)",
                   m_mag.getIndex().entrySet().contains(entry));
    }

    public void testValues() {
        assertTrue("values has PAGE1",
                   m_mag.getIndex().values().contains(PAGE1));
        assertTrue("values has PAGE1",
                   m_mag.getIndex().values().contains(PAGE2));
        assertEquals("values size", 2, m_mag.getIndex().values().size());
    }

    public void testContainsValue() {
        assertTrue("has PAGE1", m_mag.getIndex().containsValue(PAGE1));
        assertFalse("has Orwell",
                    m_mag.getIndex().containsValue(new Integer(1984)));
    }

    public void testSize() {
        assertEquals("number of elements", 2, m_mag.getIndex().size());
    }

}
