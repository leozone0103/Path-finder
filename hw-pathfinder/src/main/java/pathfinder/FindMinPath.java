package pathfinder;

import graph.DirectlyLabeledGraph;
import graph.Edge;
import graph.Node;
import pathfinder.datastructures.Path;
import pathfinder.datastructures.Point;

import java.util.*;


public class FindMinPath {

    /** find the shortest path between two nodes with weighted edges
     *
     * @param map the DirectlyLabeledGraph where the two nodes are in
     * @param start the starting nodes in the map
     * @param end the ending nodes in the map
     * @param PNMap the map of data type stored in a node to the node itself
     * @param <N> a Node
     * @return the shortest path between two nodes according to the weighed edges
     */
    public static<N> Path<N> findMinPath(DirectlyLabeledGraph<N, Double> map, Node<N, Double> start,
                                Node<N, Double> end, Map<N, Node<N, Double>> PNMap) {
        PriorityQueue<Path<N>> active = new PriorityQueue<>(new Comparator<Path<N>>() {
            @Override
            public int compare(Path<N> o1, Path<N> o2) {
                if(o1.getCost() != o2.getCost()) {
                    if(o1.getCost() - o2.getCost() > 0) {
                        return 1;
                    } else{
                        return -1;
                    }
                } else {
                    return 1;
                }
            }
        });
        Set<Node<N, Double>> finished = new HashSet<>();
        Path<N> initial = new Path<>(start.getValue());
        active.add(initial);

        while(!active.isEmpty()) {
            Path<N> minPath = active.poll();
            Node<N, Double> minDest = PNMap.get(minPath.getEnd());

            if(minDest.equals(end)) {
                return minPath;
            }
            if(finished.contains(minDest)) {
                continue;
            }
            for(Edge<N, Double> e : minDest.getChildren()) {
                if(!finished.contains(e.getEnd())) {
                    Path<N> newPath = minPath.extend(e.getEnd().getValue(), e.getLabel());
                    active.add(newPath);
                }
            }
            finished.add(minDest);

        }
        return null;


    }
}
