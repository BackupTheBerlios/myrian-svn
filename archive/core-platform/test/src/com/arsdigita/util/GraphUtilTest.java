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

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;

/**
 * @author Vadim Nasardinov (vadimn@redhat.com)
 * @version $Date: 2003/01/22 $
 * @since 2003-01-22
 **/
public class GraphUtilTest extends TestCase {
    private static final String NODE_A = "A";
    private static final String NODE_B = "B";
    private static final String NODE_C = "C";
    private static final String NODE_D = "D";
    private static final String NODE_E = "E";

    private Graph m_graph;

    public void setUp() {
        m_graph = new GraphSet();
        m_graph.addEdge(NODE_A, NODE_C, "a -> c");
        m_graph.addEdge(NODE_B, NODE_C, "b -> c");
        m_graph.addEdge(NODE_C, NODE_D, "c -> d");
        m_graph.addEdge(NODE_D, NODE_E, "d -> e");
        m_graph.addEdge(NODE_E, NODE_A, "e -> a");
    }

    public void testFindPath() {
        List expectedPath =
            Arrays.asList(new String[] {NODE_A, NODE_C, NODE_D, NODE_E});
        List computedPath = GraphUtil.edgePathToNodePath
            (GraphUtil.findPath(m_graph, NODE_A, NODE_E));
        assertEquals("path from A to E", expectedPath, computedPath);

        expectedPath =
            Arrays.asList(new String[] {NODE_D, NODE_E, NODE_A, NODE_C});
        computedPath = GraphUtil.edgePathToNodePath
            (GraphUtil.findPath(m_graph, NODE_D, NODE_C));
        assertEquals("path from D to C", expectedPath, computedPath);
    }

    public void testNodesReachableFrom() {
        List computedResult =
            GraphUtil.nodesReachableFrom(m_graph, NODE_A).getNodes();
        Collections.sort(computedResult);
        List expectedResult = Arrays.asList
            (new String[] {NODE_A, NODE_C, NODE_D, NODE_E});
        Collections.sort(expectedResult);
        assertEquals("nodes reachable from A", expectedResult, computedResult);

        computedResult =
            GraphUtil.nodesReachableFrom(m_graph, NODE_B).getNodes();
        Collections.sort(computedResult);
        expectedResult = Arrays.asList
            (new String[] {NODE_B, NODE_C, NODE_D, NODE_E, NODE_A});
        Collections.sort(expectedResult);
        assertEquals("nodes reachable from B", expectedResult, computedResult);

        Graph simpleGraph = new GraphSet();
        simpleGraph.setLabel("simple_graph");
        simpleGraph.addEdge(NODE_A, NODE_B, "a -> b");
        Graph result = GraphUtil.nodesReachableFrom
            (simpleGraph, NODE_B);
        result.setLabel("reachable_from_b");
        assertTrue("b is reachable from b",
                   result.nodeCount() == 1 &&
                   NODE_B.equals(result.getNodes().get(0)));
    }

    public void testGetSinkNodes() {
        List sinkNodes = GraphUtil.getSinkNodes(m_graph);
        assertEquals("sink node count in m_graph", 0, sinkNodes.size());

        Graph gg = new GraphSet();
        gg.addEdge(NODE_A, NODE_B, "a -> b");
        gg.addEdge(NODE_A, NODE_C, "a -> c");
        gg.addEdge(NODE_B, NODE_D, "b -> d");
        sinkNodes = GraphUtil.getSinkNodes(gg);
        assertEquals("sink node count in gg", 2, sinkNodes.size());
    }
}
