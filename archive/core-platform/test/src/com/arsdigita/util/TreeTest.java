/*
 * Copyright (C) 2003 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the CCM Public
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of
 * the License at http://www.redhat.com/licenses/ccmpl.html
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */

package com.arsdigita.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;

/**
 * @author Vadim Nasardinov (vadimn@redhat.com)
 * @version $Date: 2003/01/23 $
 * @since 2003-01-22
 **/
public class TreeTest extends TestCase {
    private static final String A = "A";
    private static final String B = "B";
    private static final String C = "C";
    private static final String D = "D";
    private static final String E = "E";
    private static final String F = "F";

    public void testTree() {
        // here's our simple test tree:
        // A
        //   B
        //     D
        //   C
        //     E
        //     F
        Tree aa = new Tree(A);
        assertEquals("aa's root is A", A, aa.getRoot());
        assertNull("aa's parent is null", aa.getParent());
        assertEquals("aa has no children", 0, aa.getSubtrees().size());
        Tree bb = aa.addChild(B);
        assertEquals("bb's parent is A", aa, bb.getParent());
        assertEquals("aa has one child", 1, aa.getSubtrees().size());
        Tree cc = aa.addChild(C);
        assertEquals("aa has two children", 2, aa.getSubtrees().size());
        bb.addChild(D);
        assertEquals("aa still has two children", 2, aa.getSubtrees().size());        
        cc.addChild(E);
        cc.addChild(F);
        List actual =preorder(aa);
        List expected = Arrays.asList(new String[] {A, B, D, C, E, F});
        assertEquals("preorder traversal", expected, actual);
    }

    private static List preorder(Tree tree) {
        List result = new ArrayList();
        preorderRecurse(tree, result);
        return result;
    }

    private static void preorderRecurse(Tree tree, List accumulator) {
        accumulator.add(tree.getRoot());
        for (Iterator ii=tree.getSubtrees().iterator(); ii.hasNext(); ) {
            Tree.EdgeTreePair pair = (Tree.EdgeTreePair) ii.next();
            preorderRecurse(pair.getTree(), accumulator);
        }
    }

    public void testGetAncestors() {
        Tree aa = new Tree(A);
        Tree dd = aa.addChild(B).addChild(D);
        Tree cc = aa.addChild(C);
        Tree ee = cc.addChild(E);
        Tree ff = cc.addChild(F);

        List actual = Tree.treesToNodes(ff.getAncestors());
        List expected = Arrays.asList(new String[] {C, A});
        assertEquals("F's ancestors", expected, actual);

        actual = Tree.treesToNodes(ee.getAncestors());
        assertEquals("E's ancestors", expected, actual);

        actual = Tree.treesToNodes(dd.getAncestors());
        expected = Arrays.asList(new String[] {B, A});
        assertEquals("D's ancestors", expected, actual);
        assertEquals("Number of A's ancestors", 0, aa.getAncestors().size());
    }
}
