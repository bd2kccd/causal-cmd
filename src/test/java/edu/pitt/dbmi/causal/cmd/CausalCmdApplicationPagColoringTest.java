/*
 * Copyright (C) 2017 University of Pittsburgh.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
package edu.pitt.dbmi.causal.cmd;

import edu.cmu.tetrad.graph.EdgeListGraph;
import edu.cmu.tetrad.graph.Graph;
import edu.cmu.tetrad.graph.GraphNode;
import edu.cmu.tetrad.graph.GraphUtils;
import edu.cmu.tetrad.graph.Node;
import edu.cmu.tetrad.graph.NodeType;
import org.junit.Test;

/**
 *
 * Mar 20, 2017 10:53:59 AM
 *
 * @author Kevin V. Bui (kvb2@pitt.edu)
 */
public class CausalCmdApplicationPagColoringTest {

    @Test
    public void testGFCIc() {
        Node[] nodes = {
            new GraphNode("A"),
            new GraphNode("B"),
            new GraphNode("L1"),
            new GraphNode("D"),
            new GraphNode("E"),
            new GraphNode("L2")
        };

        // set node L1 and L2 to be latent variables.
        nodes[2].setNodeType(NodeType.LATENT);
        nodes[5].setNodeType(NodeType.LATENT);

        Graph graph = new EdgeListGraph();
        for (Node node : nodes) {
            graph.addNode(node);
        }

        graph.addDirectedEdge(nodes[0], nodes[1]);
        graph.addDirectedEdge(nodes[3], nodes[2]);
        graph.addDirectedEdge(nodes[2], nodes[1]);
        graph.addNondirectedEdge(nodes[4], nodes[0]);
        graph.addBidirectedEdge(nodes[4], nodes[5]);

        GraphUtils.addPagColoring(graph);

        System.out.println(GraphUtils.graphToText(graph).trim());
    }

}
