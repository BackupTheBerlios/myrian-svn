package com.redhat.persistence.oql;

import com.redhat.persistence.metadata.*;
import com.redhat.persistence.pdl.*;

import java.io.*;
import java.util.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.tree.*;
import javax.swing.event.*;

/**
 * Viewer
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2004/01/16 $
 **/

public class Viewer {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/redhat/persistence/oql/Viewer.java#1 $ by $Author: rhs $, $DateTime: 2004/01/16 16:27:01 $";

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
        Container content = frame.getContentPane();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        JTree tree = new JTree
            (new DefaultTreeModel(new DefaultMutableTreeNode
                                  (new UserNode(null))));
        content.add(new JScrollPane(tree));

        JTextArea errors = new JTextArea(5, 40);
        errors.setEditable(false);
        content.add(new JScrollPane(errors));

        JPanel input = new JPanel();
        input.setLayout(new BoxLayout(input, BoxLayout.X_AXIS));
        content.add(input);

        JTextArea text = new JTextArea(8, 40);
        input.add(new JScrollPane(text));

        JPanel buttons = new JPanel();
        buttons.setLayout(new BoxLayout(buttons, BoxLayout.Y_AXIS));
        input.add(buttons);

        JButton parse = new JButton("Parse");
        parse.addActionListener(new ParseListener(root, text, tree, errors));
        buttons.add(parse);

        JButton load = new JButton("Load...");
        load.addActionListener(new LoadListener(frame, root, errors));
        buttons.add(load);

        tree.addTreeSelectionListener(new NodeSelectionListener(errors));

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

        private Root m_root;
        private JTextArea m_text;
        private JTree m_tree;
        private JTextArea m_errors;

        public ParseListener(Root root, JTextArea text, JTree tree,
                             JTextArea errors) {
            m_root = root;
            m_text = text;
            m_tree = tree;
            m_errors = errors;
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
                m_errors.append(t.getMessage().trim() + "\n");
            }
        }
    }

    private static class LoadListener implements ActionListener {

        private Container m_container;
        private Root m_root;
        private JTextArea m_errors;
        private JFileChooser m_chooser;

        public LoadListener(Container container, Root root, JTextArea errors) {
            m_container = container;
            m_root = root;
            m_errors = errors;
            m_chooser = new JFileChooser();
            m_chooser.setMultiSelectionEnabled(true);
        }

        public void actionPerformed(ActionEvent evt) {
            int result = m_chooser.showOpenDialog(m_container);
            if (result == JFileChooser.APPROVE_OPTION) {
                File[] files = m_chooser.getSelectedFiles();
                try {
                    PDL pdl = new PDL();
                    for (int i = 0; i < files.length; i++) {
                        pdl.load(new FileReader(files[i]), "" + files[i]);
                    }
                    pdl.emit(m_root);
                } catch (Throwable t) {
                    m_errors.append(t.getMessage().trim() + "\n");
                }
            }
        }

    }

    private static class NodeSelectionListener
        implements TreeSelectionListener {

        private JTextArea m_console;

        public NodeSelectionListener(JTextArea console) {
            m_console = console;
        }

        public void valueChanged(TreeSelectionEvent evt) {
            TreePath[] paths = evt.getPaths();
            for (int i = 0; i < paths.length; i++) {
                if (!evt.isAddedPath(i)) { continue; }
                DefaultMutableTreeNode node =
                    (DefaultMutableTreeNode) paths[i].getLastPathComponent();
                UserNode un = (UserNode) node.getUserObject();
                Pane p = un.pane;
                if (p == null) { return; }
                m_console.append
                    ("T = " + p.type + ", V = " + p.variables + ", I = " +
                     p.injection + ", C = " + p.constrained + ", K = " +
                     p.keys + "\n");
            }
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

}
