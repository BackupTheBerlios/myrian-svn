package com.redhat.persistence.jdo;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import javax.jdo.JDOUserException;

final class JDOState {
    private final static String COL_SEP = "|";
    private final static String DELIMS = "|" + " ";
    private final static int ROW_SIZE = 5;

    private final static Map s_actions = new HashMap();

    private final static int A_COMMIT_RETAIN_FALSE =
        action("commit_retainValuesFalse");
    private final static int A_COMMIT_RETAIN_TRUE =
        action("commit_retainValuesTrue");
    private final static int A_DELETE_PERSISTENT =
        action("deletePersistent");
    private final static int A_EVICT =
        action("evict");
    private final static int A_MAKE_NONTRANSACTIONAL =
        action("makeNontransactional");
    private final static int A_MAKE_PERSISTENT =
        action("makePersistent");
    private final static int A_MAKE_TRANSACTIONAL =
        action("makeTransactional");
    private final static int A_MAKE_TRANSIENT =
        action("makeTransient");
    private final static int A_READ_OUTSIDE_TXN =
        action("read_outside_txn");
    private final static int A_READ_WITH_DS_TXN =
        action("read_with_ds_txn");
    private final static int A_READ_WITH_OPT_TXN =
        action("read_with_opt_txn");
    private final static int A_REFRESH_WITH_DS_TXN =
        action("refresh_with_ds_txn");
    private final static int A_REFRESH_WITH_OPT_TXN =
        action("refresh_with_opt_txn");
    private final static int A_RETRIEVE_OUTSIDE_OR_WITH_OPT_TXN =
        action("retrieve_outside_or_with_opt_txn");
    private final static int A_RETRIEVE_WITH_DS_TXN =
        action("retrieve_with_ds_txn");
    private final static int A_ROLLBACK_RESTORE_FALSE =
        action("rollback_restoreValuesFalse");
    private final static int A_ROLLBACK_RESTORE_TRUE =
        action("rollback_restoreValuesTrue");
    private final static int A_WRITE_OR_MAKE_DIRTY_OUTSIDE_TXN =
        action("write_or_makeDirty_outside_txn");
    private final static int A_WRITE_OR_MAKE_DIRTY_WITH_TXN =
        action("write_or_makeDirty_with_txn");

    private final static Map s_states = new HashMap();

    private final static byte S_HOLLOW             = state("Hollow");
    private final static byte S_P_CLEAN            = state("P-clean");
    private final static byte S_P_DELETED          = state("P-del");
    private final static byte S_P_DIRTY            = state("P-dirty");
    private final static byte S_P_NEW              = state("P-new");
    private final static byte S_P_NEW_DELETED      = state("P-new-del");
    private final static byte S_P_NONTRANSACTIONAL = state("P-nontrans");
    private final static byte S_T_CLEAN            = state("T-clean");
    private final static byte S_T_DIRTY            = state("T-dirty");
    private final static byte S_TRANSIENT          = state("Transient");

    // These are sink states.
    private final static byte S_ERROR              = state("error");
    private final static byte S_IMPOSSIBLE         = state("impossible");
    private final static byte S_INAPPLICABLE       = state("n/a");
    private final static byte S_UNCHANGED          = state("unchanged");

    private final static int N_ACTIONS = s_actions.size();
    private final static int N_STATES = ROW_SIZE * 2;

    private final static byte[][] STATE_TRANSITIONS =
        new byte[N_ACTIONS][N_STATES];
    private final static byte UNINITIALIZED = (byte) 255;

    static {
        for (int ii=0; ii<N_ACTIONS; ii++) {
            for (int jj=0; jj<N_STATES; jj++) {
                STATE_TRANSITIONS[ii][jj] = UNINITIALIZED;
            }
        }

        final String dir =
            JDOState.class.getPackage().getName().replace('.', '/');
        loadStates(dir + "/table2a.properties");
        loadStates(dir + "/table2b.properties");

        List undefined = new LinkedList();

        for (int ii=0; ii<N_ACTIONS; ii++) {
            for (byte jj=0; jj<N_STATES; jj++) {
                if (STATE_TRANSITIONS[ii][jj] == UNINITIALIZED ) {
                    undefined.add(transition(ii, jj));
                }
            }
        }

        if ( undefined.size() > 0) {
            throw new IllegalStateException
                ("undefined transitions: " + undefined);
        }
    }

    private static int action(String action) {
        int result = s_actions.size();
        s_actions.put(action, new Integer(result));
        return result;
    }

    private static byte state(String state) {
        byte result = (byte) s_states.size();
        s_states.put(state, new Byte(result));
        return (byte) result;
    }

    private static String transition(int action, byte state) {
        return "action: " + intToAction(action) +
            "; state: " + byteToState(state);
    }


    private static String intToAction(int action) {
        for (Iterator ii=s_actions.entrySet().iterator(); ii.hasNext(); ) {
            Map.Entry entry = (Map.Entry) ii.next();
            if (action == ((Integer) entry.getValue()).intValue()) {
                return (String) entry.getKey();
            }
        }
        throw new IllegalStateException("Unknown action: " + action);
    }

    private static String byteToState(byte state) {
        for (Iterator ii=s_states.entrySet().iterator(); ii.hasNext(); ) {
            Map.Entry entry = (Map.Entry) ii.next();
            if (state == ((Byte) entry.getValue()).byteValue()) {
                return (String) entry.getKey();
            }
        }
        throw new IllegalStateException("Unknown state: " + state);
    }

    private static void loadStates(String properties) {
        Properties props = new Properties();
        try {
            InputStream is = JDOState.class.getClassLoader().
                getResourceAsStream(properties);
            if (is == null) {
                throw new RuntimeException("no " + properties);
            }
            props.load(is);
        } catch (IOException ex) {
            throw (RuntimeException)
                new RuntimeException
                ("error loading " + properties).initCause(ex);
        }

        byte[] sourceStates = null;
        final Map transitions = new HashMap();

        for (Enumeration names=props.propertyNames(); names.hasMoreElements(); ) {
            final String action = (String) names.nextElement();
            if (COL_SEP.equals(action)) {
                sourceStates = getRow(props.getProperty(action));
            } else if (!action.startsWith(COL_SEP)) {
                    throw new IllegalStateException
                        ("Invalid action: " + action);
            } else {
                transitions.put(action.substring(1),
                                getRow(props.getProperty(action)));
            }
        }
        if (sourceStates == null) {
            throw new IllegalStateException("missing source states");
        }

        final Iterator actions=transitions.keySet().iterator();
        while (actions.hasNext()) {
            String action = (String) actions.next();
            Integer actionIdx = (Integer) s_actions.get(action);

            if (actionIdx == null) {
                throw new IllegalStateException("unknown action: " + action);
            }
            final int ii = actionIdx.intValue();

            byte[] targetStates = (byte[]) transitions.get(action);

            for (int col=0; col<ROW_SIZE; col++) {
                STATE_TRANSITIONS[ii][sourceStates[col]] = targetStates[col];
            }
        }
    }

    private static byte[] getRow(String raw) {
        List row = new ArrayList(ROW_SIZE);
        Enumeration tokens = new StringTokenizer(raw.trim(), DELIMS);
        while (tokens.hasMoreElements()) {
            row.add(tokens.nextElement());
        }
        if (row.size() != ROW_SIZE) {
            throw new IllegalStateException
                ("expected " + ROW_SIZE + " elements, but got " + row.size() +
                 ": " + row);
        }
        byte[] result = new byte[ROW_SIZE];
        for (int ii=0; ii<ROW_SIZE; ii++) {
            String state = (String) row.get(ii);
            Byte bite = (Byte) s_states.get(state);
            if (bite == null) {
                throw new IllegalStateException
                    ("unknown state " + state + " in row " + row);
            }
            result[ii] = bite.byteValue();
        }
        return result;
    }

    // Non-static fields and methods begin here.

    private byte m_state = S_HOLLOW;

    private void transition(int action) {
        final byte nextState = STATE_TRANSITIONS[action][m_state];

        if (nextState == S_UNCHANGED) { return; }

        if (nextState == S_INAPPLICABLE) {
            // XXX: see p. 49.  Deal with the case when the instance is an
            // implicit parameter, in which case no exception must be thrown

            throw new JDOUserException
                ("Inapplicable state transition: " +
                 transition(action, m_state));
        }

        if (nextState == S_IMPOSSIBLE) {
            throw new JDOUserException
                ("Implementation error. Impossible state transition: " +
                 transition(action, m_state));
        }

        if (nextState == S_ERROR) {
            throw new JDOUserException
                ("Application error.  Illegal state transition: " +
                 transition(action, m_state));
        }

        m_state = nextState;
    }

    public void commit(boolean retainValues) {
        transition(retainValues ?
                   A_COMMIT_RETAIN_TRUE : A_COMMIT_RETAIN_FALSE);
    }

    public void deletePersistent() {
        transition(A_DELETE_PERSISTENT);
    }

    public void evict() {
        transition(A_EVICT);
    }

    public void makeNontransactional() {
        transition(A_MAKE_NONTRANSACTIONAL);
    }

    public void makePersistent() {
        transition(A_MAKE_PERSISTENT);
    }

    public void makeTransactional() {
        transition(A_MAKE_TRANSACTIONAL);
    }

    public void makeTransient() {
        transition(A_MAKE_TRANSIENT);
    }

    public void readOutsideTxn() {
        transition(A_READ_OUTSIDE_TXN);
    }

    public void readWithDatastoreTxn() {
        transition(A_READ_WITH_DS_TXN);
    }

    public void readWithOptimisticTxn() {
        transition(A_READ_WITH_OPT_TXN);
    }

    public void refreshWithDatastoreTxn() {
        transition(A_REFRESH_WITH_DS_TXN);
    }

    public void refreshWithOptimisticTxn() {
        transition(A_REFRESH_WITH_OPT_TXN);
    }

    public void retrieveOutsideTxn() {
        transition(A_RETRIEVE_OUTSIDE_OR_WITH_OPT_TXN);
    }

    public void retrieveWithOptimisticTxn() {
        transition(A_RETRIEVE_OUTSIDE_OR_WITH_OPT_TXN);
    }

    public void retrieveWithDatastoreTxn() {
        transition(A_RETRIEVE_WITH_DS_TXN);
    }

    public void rollback(boolean restoreValues) {
        transition(restoreValues ?
                  A_ROLLBACK_RESTORE_TRUE : A_ROLLBACK_RESTORE_FALSE);
    }

    public void writeOutsideTxn() {
        transition(A_WRITE_OR_MAKE_DIRTY_OUTSIDE_TXN);
    }

    public void makeDirtyOutsideTxn() {
        transition(A_WRITE_OR_MAKE_DIRTY_OUTSIDE_TXN);
    }

    public void writeWithTxn() {
        transition(A_WRITE_OR_MAKE_DIRTY_WITH_TXN);
    }

    public void makeDirtyWithTxn() {
        transition(A_WRITE_OR_MAKE_DIRTY_WITH_TXN);
    }

    // Interrogatives supported by PersistenceManager

    public boolean isDeleted() {
        return m_state == S_P_DELETED || m_state == S_P_NEW_DELETED;
    }

    public boolean isDirty() {
        return m_state == S_P_DIRTY || m_state == S_T_DIRTY;
    }

    public boolean isNew() {
        return m_state == S_P_NEW || m_state == S_P_NEW_DELETED;
    }

    public boolean isPersistent() {
        return m_state != S_TRANSIENT && m_state != S_T_CLEAN
            && m_state != S_T_DIRTY && m_state != S_HOLLOW;
    }

    public boolean isTransactional() {
        return m_state != S_HOLLOW && m_state != S_P_NONTRANSACTIONAL
            && m_state != S_TRANSIENT;
    }


    // Interrogatives available to the implementation but invisible to the
    // application


    boolean isHollow() {
        return m_state == S_HOLLOW;
    }
}
