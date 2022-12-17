package com.mygdx.game.world.map;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.ai.pfa.DefaultGraphPath;
import com.badlogic.gdx.ai.pfa.GraphPath;
import com.badlogic.gdx.ai.pfa.Heuristic;
import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder;
import com.badlogic.gdx.ai.pfa.indexed.IndexedGraph;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.MyGdxGame;
import com.mygdx.game.util.CoordinateUtils;

public class PathFinder {
    private static Graph graph;
    private static final DistanceHeuristic dh = new DistanceHeuristic();
    private static IndexedAStarPathFinder<Node> nodeIndexedAStarPathFinder;

    public static void generateGraph(Tile[][] map, boolean allowDiagonals) {
        Array<Node> nodes = new Array<>();
        int index = 0;
        for (int row = map.length - 1; row >= 0; row--) {
            for (int col = map[row].length - 1; col >= 0; col--) {
                nodes.add(new Node(CoordinateUtils.tileToScreen(row, col).cpy(), new Vector2(row, col), index, map[row][col].getCost()));
                index++;
            }
        }

        for (Node node : nodes) {
            for (int i = 0; i < nodes.size; i++) {
                if (isNeighbour(node, nodes.get(i), allowDiagonals) && isPassable(nodes.get(i), map)) {
                    node.connections.add(new NodeConnection(node, nodes.get(i)));
                }
            }
        }

        graph = new Graph(nodes);
        nodeIndexedAStarPathFinder = new IndexedAStarPathFinder<>(graph);
    }

    private static boolean isNeighbour(Node node, Node neighbour, boolean allowDiagonals) {
        int nodeX = (int) node.tilePos.x;
        int nextNodeX = (int) neighbour.tilePos.x;
        int nodeY = (int) node.tilePos.y;
        int nextNodeY = (int) neighbour.tilePos.y;

        boolean sideNeighbour = nodeX + 1 == nextNodeX && nodeY == nextNodeY ||
                nodeX - 1 == nextNodeX && nodeY == nextNodeY ||
                nodeX == nextNodeX && nodeY + 1 == nextNodeY ||
                nodeX == nextNodeX && nodeY - 1 == nextNodeY;

        boolean diagonalNeighbour = nodeX + 1 == nextNodeX && nodeY - 1 == nextNodeY ||
                nodeX + 1 == nextNodeX && nodeY + 1 == nextNodeY ||
                nodeX - 1 == nextNodeX && nodeY - 1 == nextNodeY ||
                nodeX - 1 == nextNodeX && nodeY + 1 == nextNodeY;

        return sideNeighbour || (allowDiagonals && diagonalNeighbour);
    }

    public static GraphPath<Node> getPath(Vector2 start, Vector2 end) {
        Node startNode = null;
        Node endNode = null;

        for (Node node : graph.getNodes().toArray(Node.class)) {
            if (start.dst(node.tilePos) == 0) {
                startNode = node;
            }

            if (end.dst(node.tilePos) == 0) {
                endNode = node;
            }
        }


        GraphPath<Node> path = new DefaultGraphPath<>();
        if (startNode == null || endNode == null) {
            return path;
        }
        boolean found = nodeIndexedAStarPathFinder.searchNodePath(startNode, endNode, dh, path);

/*
        if (!found) {
            for (Connection<Node> connection : endNode.connections) {
                found = nodeIndexedAStarPathFinder.searchNodePath(startNode, connection.getToNode(), dh, path);
                if (found) {
                    return path;
                }
            }
        }
        */

        return path;
    }

    private static boolean isPassable(Node node, Tile[][] map) {
        int i = (int) node.tilePos.x;
        int j = (int) node.tilePos.y;
        return map[i][j].isPassable();
    }

    private static class Graph implements IndexedGraph<Node> {

        private final Array<Node> nodes;

        public Graph(Array<Node> nodes) {
            this.nodes = nodes;
        }

        @Override
        public int getIndex(Node node) {
            return node.index;
        }

        @Override
        public int getNodeCount() {
            return nodes.size;
        }

        @Override
        public Array<Connection<Node>> getConnections(Node fromNode) {
            return fromNode.connections;
        }

        public Array<Node> getNodes() {
            return nodes;
        }
    }

    private static class DistanceHeuristic implements Heuristic<Node> {

        @Override
        public float estimate(Node node, Node endNode) {
            return node.tilePos.dst(endNode.tilePos);
            // manhattan distance
//            return Math.abs(node.tilePos.x - endNode.tilePos.x) + Math.abs(node.tilePos.y - endNode.tilePos.y);
        }
    }

    public static class Node {
        public final Vector2 pos;
        public final Vector2 tilePos;
        private final int index;
        private final Array<Connection<Node>> connections = new Array<>();
        public float cost;

        public Node(Vector2 pos, Vector2 tilePos, int index, int cost) {
            this.pos = pos;
            this.tilePos = tilePos;
            this.index = index;
            this.cost = cost;
        }
    }

    private static class NodeConnection implements Connection<Node> {

        private final Node fromNode;
        private final Node toNode;

        public NodeConnection(Node fromNode, Node toNode) {
            this.fromNode = fromNode;
            this.toNode = toNode;
        }

        @Override
        public float getCost() {
            // check if the node has an enemy on it and if so, increase the cost
            if (MyGdxGame.API().getWorld().containsGameActor((int) toNode.tilePos.x, (int) toNode.tilePos.y) != null) {
                return 9999;
            }
            return toNode.cost;
        }

        @Override
        public Node getFromNode() {
            return fromNode;
        }

        @Override
        public Node getToNode() {
            return toNode;
        }
    }
}
