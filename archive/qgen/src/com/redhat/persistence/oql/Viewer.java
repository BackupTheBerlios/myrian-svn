package com.redhat.persistence.oql;

import com.redhat.persistence.metadata.*;
import com.redhat.persistence.pdl.*;

import java.io.*;
import java.util.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.tree.*;
import javax.swing.event.*;

/**
 * Viewer
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2004/01/19 $
 **/

public class Viewer {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/redhat/persistence/oql/Viewer.java#2 $ by $Author: rhs $, $DateTime: 2004/01/19 14:43:24 $";

    public static final void main(String[] args) throws Exception {
        PDL pdl = new PDL();
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            pdl.load(new FileReader(arg), arg);
        }

        Root root = new Root();
        pdl.emit(root);

        JFrame frame = new JFrame("Viewer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JTree tree = new JTree
            (new DefaultTreeModel(new DefaultMutableTreeNode
                                  (new UserNode(null))));
        PaneTableModel panes = new PaneTableModel();

        JSplitPane top = new JSplitPane
            (JSplitPane.HORIZONTAL_SPLIT,
             new JScrollPane(tree), new JScrollPane(new JTable(panes)));
        top.setResizeWeight(0.0);
        JPanel bottom = new JPanel();
        bottom.setLayout(new BoxLayout(bottom, BoxLayout.X_AXIS));

        JSplitPane content = new JSplitPane
            (JSplitPane.VERTICAL_SPLIT, top, bottom);
        content.setResizeWeight(1.0);
        frame.setContentPane(content);

        JTextArea text = new JTextArea(8, 40);
        bottom.add(new JScrollPane(text));

        JPanel buttons = new JPanel();
        buttons.setLayout(new BoxLayout(buttons, BoxLayout.Y_AXIS));
        bottom.add(buttons);

        JButton parse = new JButton("Parse");
        parse.addActionListener
            (new ParseListener(frame, root, text, tree, panes));
        buttons.add(parse);

        JButton load = new JButton("Load...");
        load.addActionListener(new LoadListener(frame, root));
        buttons.add(load);

        tree.addTreeSelectionListener(new NodeSelectionListener(panes));

        frame.pack();
        frame.setVisible(true);
    }

    private static DefaultMutableTreeNode tree(Pane pane) {
        DefaultMutableTreeNode result =
            new DefaultMutableTreeNode(new UserNode(pane));
        for (Iterator it = pane.children().iterator(); it.hasNext(); ) {
            Pane child = (Pane) it.next();
            result.add(tree(child));
        }
        return result;
    }

    private static class ParseListener implements ActionListener {

        private JFrame m_frame;
        private Root m_root;
        private JTextArea m_text;
        private JTree m_tree;
        private PaneTableModel m_panes;

        public ParseListener(JFrame frame, Root root, JTextArea text,
                             JTree tree, PaneTableModel panes) {
            m_frame = frame;
            m_root = root;
            m_text = text;
            m_tree = tree;
            m_panes = panes;
        }

        public void actionPerformed(ActionEvent evt) {
            Frame frame = Frame.root(m_root);
            OQLParser p = new OQLParser(new StringReader(m_text.getText()));
            try {
                Expression expr = p.expression();
                Pane pane = frame.graph(expr);
                ((DefaultTreeModel) m_tree.getModel()).setRoot(tree(pane));
                Node.propogate(Collections.singleton(frame.type));
            } catch (Throwable t) {
                error(m_frame, t);
            }
        }
    }

    private static class LoadListener implements ActionListener {

        private JFrame m_frame;
        private Root m_root;
        private JFileChooser m_chooser;

        public LoadListener(JFrame frame, Root root) {
            m_frame = frame;
            m_root = root;
            m_chooser = new JFileChooser();
            m_chooser.setMultiSelectionEnabled(true);
        }

        public void actionPerformed(ActionEvent evt) {
            int result = m_chooser.showOpenDialog(m_frame);
            if (result == JFileChooser.APPROVE_OPTION) {
                File[] files = m_chooser.getSelectedFiles();
                try {
                    PDL pdl = new PDL();
                    for (int i = 0; i < files.length; i++) {
                        pdl.load(new FileReader(files[i]), "" + files[i]);
                    }
                    pdl.emit(m_root);
                } catch (Throwable t) {
                    error(m_frame, t);
                }
            }
        }

    }

    private static class NodeSelectionListener
        implements TreeSelectionListener {

        private PaneTableModel m_panes;

        public NodeSelectionListener(PaneTableModel panes) {
            m_panes = panes;
        }

        public void valueChanged(TreeSelectionEvent evt) {
            TreePath[] paths = evt.getPaths();
            for (int i = 0; i < paths.length; i++) {
                DefaultMutableTreeNode node =
                    (DefaultMutableTreeNode) paths[i].getLastPathComponent();
                UserNode un = (UserNode) node.getUserObject();
                Pane p = un.pane;
                if (p == null) { continue; }
                if (evt.isAddedPath(i)) {
                    m_panes.add(p);
                } else {
                    m_panes.remove(p);
                }
            }
            m_panes.fireTableDataChanged();
        }

    }

    private static class UserNode {

        Pane pane;

        UserNode(Pane pane) {
            this.pane = pane;
        }

        public String toString() {
            if (pane == null) {
                return "(none)";
            } else {
                return pane.expression.summary();
            }
        }

    }

    private static class PaneTableModel extends AbstractTableModel {
        private static String[] columns = new String[] {
            "Expression", "Type", "Variables", "Injection", "Constrained",
            "Keys"
        };

        public String getColumnName(int col) {
            return columns[col];
        }

        public int getColumnCount() {
            return columns.length;
        }

        private java.util.List m_panes = new ArrayList();

        public void clear() {
            m_panes.clear();
        }

        public void add(Pane pane) {
            m_panes.add(pane);
        }

        public void remove(Pane pane) {
            m_panes.remove(pane);
        }

        public void addAll(Collection panes) {
            m_panes.addAll(panes);
        }

        public int getRowCount() { return m_panes.size(); }

        public Object getValueAt(int row, int column) {
            Pane pane = (Pane) m_panes.get(row);
            switch (column) {
            case 0:
                return "" + pane.expression;
            case 1:
                return "" + pane.type;
            case 2:
                return "" + pane.variables;
            case 3:
                return "" + pane.injection;
            case 4:
                return "" + pane.constrained;
            case 5:
                return "" + pane.keys;
            default:
                throw new IllegalArgumentException();
            }
        }
    }

    private static void error(JFrame frame, Throwable t) {
        t.printStackTrace(System.err);
        JOptionPane.showMessageDialog
            (frame, ("" + t.getMessage()).trim(), "Error",
             JOptionPane.ERROR_MESSAGE);
    }

}
