package com.arsdigita.persistence.proto;

import com.arsdigita.persistence.proto.metadata.*;
import com.arsdigita.persistence.proto.pdl.PDL;
import com.arsdigita.tools.junit.extensions.BaseTestSetup;
import com.arsdigita.tools.junit.framework.PackageTestSuite;
import java.util.*;
import junit.framework.Test;
import junit.framework.TestResult;
import org.apache.log4j.Logger;

/**
 * SessionSuite
 *
 * @author <a href="mailto:ashah@redhat.com">ashah@redhat.com</a>
 * @version $Revision: #2 $ $Date: 2003/02/12 $
 **/

public class SessionSuite extends PackageTestSuite {

    public final static String versionId = "$Id: //core-platform/proto/test/src/com/arsdigita/persistence/proto/SessionSuite.java#2 $";

    private static final Logger s_log = Logger.getLogger(SessionSuite.class);

    private static final class Generic {

        private ObjectType m_type;
        private Integer m_id;

        public Generic(ObjectType type, Integer id) {
            m_type = type;
            m_id = id;
        }

        public ObjectType getObjectType() {
            return m_type;
        }

        public Integer getID() {
            return m_id;
        }

    }

    public SessionSuite() {}

    public SessionSuite(Class theClass) {
        super(theClass);
    }

    public SessionSuite(String name) {
        super(name);
    }

    public static Test suite() {
        final SessionSuite suite = new SessionSuite();

        suite.addTest(new Test() {
            public int countTestCases() { return 2; }
            public void run(TestResult result) { suite.sessionTest(result); }
        });

        BaseTestSetup wrapper = new BaseTestSetup(suite);
        wrapper.setInitScriptTarget("com.arsdigita.logging.Initializer");
        return wrapper;
    }

    public static void main(String[] args) throws Exception {
        junit.textui.TestRunner.run(suite());
    }

    private static String INTE = "global.Integer";

    private Model m_model;
    private ObjectType m_root;
    private ObjectType m_one;
    private ObjectType m_two;
    // m_root -> layer 1, m_one -> layer 2
    private Map m_layers = new HashMap(2);

    private Session m_ssn;
    private Map[] m_objs = new Map[] {
        new HashMap(), new HashMap(), new HashMap() };

    public void sessionTest(TestResult result) {
        result.startTest(this);

        PDL pdl = new PDL();
        pdl.emit(Root.getRoot());

        m_model = Model.getInstance("test");

        m_ssn = new Session();

        initializeModel();

        initializeData();

        m_ssn.commit();

        test(m_root);

        test(m_one);

        result.endTest(this);
    }

    private void test(ObjectType type) {

        // XXX: going through roles
        Collection roles = type.getRoles();
        Collection keys = type.getKeyProperties();

        for (Iterator it = roles.iterator(); it.hasNext(); ) {
            Role role = (Role) it.next();

            if (keys.contains(role)) { continue; }

            if (role.getName().startsWith("-")) { continue; }

            s_log.info("start: " + role.getName());

            if (role.isCollection()) {
                testCollection(role);
            } else if (!role.isNullable()) {
                testRequired(role);
            } else {
                testNullable(role);
            }

            s_log.info("stop: " + role.getName());
        }
    }

    private void testCollection(Role role) {
        ObjectType source = role.getContainer();
        ObjectType target = role.getType();

        Object obj = m_objs[0].get(source);

        resetState();

        s_log.info("test: 0add1" + role.getName());
        m_ssn.add(obj, role, m_objs[0].get(target));
        endTest();

        s_log.info("test: 1rem1" + role.getName());
        m_ssn.remove(obj, role, m_objs[0].get(target));
        endTest();

        resetState();

        s_log.info("test: 0add2" + role.getName());
        m_ssn.add(obj, role, m_objs[0].get(target));
        m_ssn.add(obj, role, m_objs[1].get(target));
        endTest();

        s_log.info("test: 2add1" + role.getName());
        m_ssn.add(obj, role, m_objs[2].get(target));
        endTest();

        s_log.info("test: 3rem1" + role.getName());
        m_ssn.remove(obj, role, m_objs[2].get(target));
        endTest();

        s_log.info("test: 2rem2" + role.getName());
        m_ssn.remove(obj, role, m_objs[0].get(target));
        m_ssn.remove(obj, role, m_objs[1].get(target));
        endTest();
    }

    private void testRequired(Role role) {
        ObjectType source = role.getContainer();
        ObjectType target = role.getType();

        Object obj = m_objs[0].get(source);

        resetState();
        s_log.info("test: set " + role.getName());
        m_ssn.set(obj, role, m_objs[1].get(target));
        endTest();

        // XXX: negative test
        // s_log.info("test: null " + role.getName());
    }

    private void testNullable(Role role) {
        ObjectType source = role.getContainer();
        ObjectType target = role.getType();

        Object obj = m_objs[0].get(source);

        resetState();
        s_log.info("test: set " + role.getName());
        m_ssn.set(obj, role, m_objs[1].get(target));
        endTest();

        s_log.info("test: setnull " + role.getName());
        m_ssn.set(obj, role, null);
        endTest();
    }

    private void resetState() {
        m_ssn.rollback();
    }

    private void endTest() {
        m_ssn.flush();
    }

    private void initializeModel() {
        m_root = createKeyedType(m_model, "Root");
        m_one = createKeyedType(m_model, "One");
        m_two = createKeyedType(m_model, "Two");

        ObjectType inte = Root.getRoot().getObjectType(INTE);

        ObjectType int3 = createUnkeyedType(
            m_model, "Int3", new ObjectType[] { inte, inte, inte });

        ObjectType oneint = createUnkeyedType(
            m_model, "OneInt", new ObjectType[] { m_one, inte });

        ObjectType twoint = createUnkeyedType(
            m_model, "TwoInt", new ObjectType[] { m_two, inte });

        ObjectType rootone = createUnkeyedType(
            m_model, "RootOne", new ObjectType[] { m_root, m_one});
                                            
        ObjectType roottwo = createUnkeyedType(
            m_model, "RootTwo", new ObjectType[] { m_root, m_two });

        ArrayList layer1 = new ArrayList();
        layer1.add(m_root);
        layer1.add(m_one);
        layer1.add(inte);
        // layer1.add(int3);
        // layer1.add(oneint);
        // layer1.add(rootone);

        m_layers.put(m_root, layer1);

        ArrayList layer2 = new ArrayList();
        layer2.add(m_root);
        layer2.add(m_two);
        layer2.add(inte);
        // layer2.add(int3);
        // layer2.add(twoint);
        // layer2.add(roottwo);

        m_layers.put(m_one, layer2);

        for (Iterator it = layer1.iterator(); it.hasNext(); ) {
            addProperties(m_root, (ObjectType) it.next());
        }

        for (Iterator it = layer2.iterator(); it.hasNext(); ) {
            addProperties(m_one, (ObjectType) it.next());
        }
    }

    private void initializeData() {
        fill(m_root, 0);
        fill(m_one, 0);
        fill(m_two, 0);
        fill(m_root, 1);
        fill(m_one, 1);
        fill(m_two, 1);
        fill(m_root, 2);
        fill(m_one, 2);
        fill(m_two, 2);
    }

    private Object fill(ObjectType type, int round) {
        if (m_objs[round].get(type) != null) {
            return m_objs[round].get(type);
        }

        Object obj;

        if (type.hasKey()) {
            if (type.getKeyProperties().size() > 1) {
                throw new IllegalStateException("compound key");
            }
            obj = new Generic(type, new Integer(round));
            m_ssn.create(obj);
        } else {
            throw new IllegalStateException("can't create keyless object");
        }

        m_objs[round].put(type, obj);

        // build up data
        Collection props = type.getProperties();

        for (Iterator it = props.iterator(); it.hasNext(); ) {
            Property prop = (Property) it.next();

            if (prop.isNullable()) { continue; }

            if (prop.isCollection()) {
                throw new IllegalStateException("nonnullable collection");
            }

            if (!(prop instanceof Role)) {
                throw new IllegalStateException("nonnullable nonrole");
            }

            Role role = (Role) prop;
            ObjectType targetType = role.getType();

            Object target = m_objs[round].get(targetType);

            if (target != null) {
                m_ssn.set(obj, role, target);
            } else if (!targetType.isCompound()) {
                // XXX: assuming all simple types are integers for this
                m_ssn.set(obj, role, new Integer(round));
            } else {
                // compound type
                m_ssn.set(obj, role, fill(targetType, round));
            }
        }

        return obj;
    }

    private static final boolean[] B = new boolean[] { false, true };

    private static void addProperties(ObjectType ot, ObjectType target) {
        if (s_log.isDebugEnabled()) {
            s_log.debug("props: " + ot.getName() + " " + target.getName());
        }

        // collection, component, nullable
        if (!target.hasKey()) {
            for (int i = 0; i < 2; i++) {
                for (int j = 0; j < 2; j++) {
                    for (int k = 0; k < 2; k++) {
                        oneWay(ot, target, B[i], B[j], B[k]);
                    }
                }
            }
        } else {
            // One way
            for (int i = 0; i < 2; i++) {
                for (int j = 0; j < 2; j++) {
                    for (int k = 0; k < 2; k++) {
                        oneWay(ot, target, B[i], B[j], B[k]);
                    }
                }
            }

            // Two way
            // no composition (ot is collection x target is collection)

            for (int i = 0; i < 2; i++) {
                for (int j = 0; j < 2; j++) {
                    for (int k = 0; k < 2; k++) {
                        for (int l = 0; l < 2; l++) {
                            for (int m = 0; m < 2; m++) {
                                for (int n = 0; n < 2; n++) {
                                    twoWay(ot, target,
                                           B[i], B[j], B[k], B[l], B[m], B[n]);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private static ObjectType createKeyedType(Model m, String name) {
        ObjectType ot = new ObjectType(m, name, null);

        Role id = new Role(
            "id", Root.getRoot().getObjectType(INTE),
            false, false, false);
        ot.addProperty(id);
        ObjectMap om = new ObjectMap(ot);
        Collection keys = om.getKeyProperties();
        keys.add(id);

        Root.getRoot().addObjectType(ot);
        Root.getRoot().addObjectMap(om);

        return ot;
    }

    private static ObjectType createUnkeyedType(Model m, String name,
                                                ObjectType[] ots) {
        ObjectType ot = new ObjectType(m, name, null);

        Root.getRoot().addObjectType(ot);

        for (int i = 0; i < ots.length; i++) {
            ObjectType prop = ots[i];
            ot.addProperty(new Role(prop.getName() + i, prop,
                                    false, false, false));
        }

        return ot;
    }

    private static void twoWay(ObjectType a, ObjectType b,
                               boolean aCollection, boolean bCollection,
                               boolean aComponent, boolean bComponent,
                               boolean aNullable, boolean bNullable) {
        
        if (// two component ends
            (aComponent == true && bComponent == true) ||
            // the composite side must not be a collection
            (aComponent == true && bCollection == true) ||
            (aCollection == true && bComponent == true) ||
            // nonnullable collections are not supported
            (aCollection == true && aNullable == false) ||
            (bCollection == true && bNullable == false) ||
            // with noncollections on both ends
            // only one end can be nullable
            // however if the a end is the nonnullable one,
            // its role to b can not be updated because the reverse
            // would have to be set to null
            (aCollection == false && bCollection == false
             && aNullable == false)) {
            return;
        }

        if (s_log.isDebugEnabled()) {
            s_log.debug(" role2: " + aCollection + aComponent +
                        bCollection + bComponent);
        }

        String name =
            a.getName() + 
            (aCollection ? "0-n" :
             (aNullable ? "0-1" : "1-1")) +
            (bCollection ? "<->0-n" :
             (bNullable ? "<->0-1" : "<->1-1")) +
            b.getName() +
            (aComponent ? "part,whole" : "") +
            (bComponent ? "whole,part" : "");

        Role arole = new Role(name, b, bComponent, bCollection, bNullable);
        Role brole =
            new Role("-" + name, a, aComponent, aCollection, aNullable);

        try {
            a.addProperty(arole);
        } catch (IllegalArgumentException iae) {
            if (s_log.isDebugEnabled()) {
                s_log.info(a.getName());
                s_log.info(arole.getName());
            }
            throw iae;
        }

        try {
            b.addProperty(brole);
        } catch (IllegalArgumentException iae) {
            if (s_log.isDebugEnabled()) {
                s_log.info(b.getName());
                s_log.info(brole.getName());
            }
            throw iae;
        }

        arole.setReverse(brole);
    }

    private static void oneWay(ObjectType a, ObjectType b,
                               boolean bCollection, boolean bComponent,
                               boolean isNullable) {
        if (!isNullable && bCollection) {
            return;
        }

        if (bComponent && !b.isCompound()) {
            return;
        }

        if (s_log.isDebugEnabled()) {
            s_log.debug(" role: " + bCollection + bComponent);
        }

        a.addProperty(new Role(
                               a.getName() + 
                               (bCollection ? "->0-n" :
                                (isNullable ? "->0-1" : "->1-1")) +
                               // (aCollection ? "->0-n" : "->0-1") +
                               b.getName() +
                               (bComponent ? "whole,part" : ""),
                               // (aComponent ? "whole" : "");
                               b, bComponent, bCollection, isNullable));
    }
}
