import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class project5 {
    public static void main(String[] args) throws IOException {
        FileReader fileReader = new FileReader(args[0]);
        BufferedReader input = new BufferedReader(fileReader);
        FileWriter output = new FileWriter(args[1]);
        int cityNo = Integer.parseInt(input.readLine().split(" ")[0]);      // Reading input from the file and
        String[] maxTroops = input.readLine().split(" ");                   // Putting them in adjacency list
        HashMap<String, HashMap<String, Integer>> adjacency = new HashMap<>();
        while (input.ready()){
            String[] neighbors = input.readLine().split(" ");           // Reading the roads
            String city = neighbors[0];
            adjacency.computeIfAbsent(city, k -> new HashMap<>());
            for (int i = 1; i < neighbors.length; i += 2){
                adjacency.get(city).put(neighbors[i], Integer.parseInt(neighbors[i+1]));
            }
        }
        adjacency.computeIfAbsent("A", k -> new HashMap<>());       // I add a beginning node with name A to connect the regions
        adjacency.get("A").put("r0", Integer.parseInt(maxTroops[0]));   // And I give the capacity of regions as the capacity of roads of A
        adjacency.get("A").put("r1", Integer.parseInt(maxTroops[1]));
        adjacency.get("A").put("r2", Integer.parseInt(maxTroops[2]));
        adjacency.get("A").put("r3", Integer.parseInt(maxTroops[3]));
        adjacency.get("A").put("r4", Integer.parseInt(maxTroops[4]));
        adjacency.get("A").put("r5", Integer.parseInt(maxTroops[5]));

        HashMap<String, String> parents = new HashMap<>();

        maxFlow(adjacency, "A", "KL", output);


    }

    public static boolean bfs(HashMap<String, HashMap<String, Integer>> list, String source, String end, HashMap<String, String> parent){
        HashMap<String, Boolean> visited = new HashMap<>();     // Breath first search method to check there is another way from source to end in the residual graph
        LinkedList<String> queue = new LinkedList<>();
        queue.add(source);
        visited.put(source, true);
        parent.put(source, null);
        while (!queue.isEmpty()){
            String city = queue.poll();
            for (Map.Entry<String, Integer> neighbor : list.get(city).entrySet()){
                visited.putIfAbsent(neighbor.getKey(), false);
                if (!visited.get(neighbor.getKey()) && neighbor.getValue() > 0){
                    parent.put(neighbor.getKey(), city);
                    if (neighbor.getKey().equals(end)){
                        return true;
                    }
                    queue.add(neighbor.getKey());
                    visited.put(neighbor.getKey(), true);
                }
            }
        }
        return false;
    }

    public static int maxFlow(HashMap<String, HashMap<String, Integer>> list, String source, String end, FileWriter output) throws IOException {
        HashMap<String, HashMap<String, Integer>> residual = new HashMap<>(list);       // Max flow method finds the max flow using ford fulkerson algorithm
        HashMap<String, String> parents = new HashMap<>();
        int flow = 0;
        HashMap<String, Boolean> visited = new HashMap<>();


        while (bfs(residual, source, end, parents)){                // While there is a way in residual graph, it checks the way and updates the max flow
            int newFlow = Integer.MAX_VALUE;                        // And updates residual graph, turns the roads back according to the flow
            for (String current = end; !current.equals(source); current = parents.get(current)){
                String parent = parents.get(current);
                newFlow = Math.min(newFlow, residual.get(parent).get(current));
            }
            for (String current = end; !current.equals(source); current = parents.get(current)){
                String parent = parents.get(current);
                HashMap<String, Integer> temp = new HashMap<>(residual.get(parent));
                temp.put(current, residual.get(parent).get(current)-newFlow);
                residual.put(parent, temp);

                residual.computeIfAbsent(current, k -> new HashMap<>());
                residual.get(current).computeIfAbsent(parent, l -> 0);
                HashMap<String, Integer> temp2 = new HashMap<>(residual.get(current));

                temp2.put(parent, residual.get(current).get(parent)+newFlow);
                residual.put(current, temp2);
            }
            flow += newFlow;
        }
                                                        // When there is not another way in residual graph, the flow is maximum
        for (String node : residual.keySet()){          // I reset the visited nodes and use it in the dfs method again
            visited.put(node, false);
        }
        output.write(flow + "\n");
        output.flush();

        dfs(residual, source, visited);

        for (Map.Entry<String, HashMap<String, Integer>> node1 : list.entrySet()) {     // Writing the cutted roads by looking which nodes are in visited
            for (Map.Entry<String, Integer> node2 : node1.getValue().entrySet()) {
                if (node1.getValue().get(node2.getKey()) > 0 && visited.get(node1.getKey()) && !visited.get(node2.getKey())) {
                    if (node1.getKey().equals("A")){
                        output.write(node2.getKey() + "\n");
                    }else {
                        output.write(node1.getKey() + " " + node2.getKey() + "\n");

                    }
                }
            }
        }
        output.close();

        return flow;

    }

    public static void dfs(HashMap<String, HashMap<String, Integer>> residual, String source, HashMap<String, Boolean> visited){
        visited.put(source, true);      // Depth first search method to see which nodes we can reach in residual graph
        for (Map.Entry<String, Integer> neighbor : residual.get(source).entrySet()){
            if (neighbor.getValue() > 0 && !visited.get(neighbor.getKey())){
                if (neighbor.getKey().equals("KL")){
                    continue;
                }
                dfs(residual, neighbor.getKey(), visited);
            }
        }
    }
}
