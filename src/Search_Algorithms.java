import java.util.ArrayList;
import java.util.Stack;

public class Search_Algorithms {

	
	public static int bfs(Node source){
		
		ArrayList<Node> queue = new ArrayList<>();
	
		
		if(source.getGoal()){
			return source.getDepth();
		}
		
		source.setDepth(0);
		source.color = "grey";
		
		queue.add(source);
		
		while(!queue.isEmpty()){
			Node currentNode = queue.remove(0);
			for(Node neighbour : currentNode.getNeighbours()){
				
				if(neighbour.color.equals("white")){
					neighbour.color = "grey";
					neighbour.setDepth(currentNode.getDepth() + 1);
					neighbour.setPredecessor(currentNode);
					queue.add(neighbour);
					
					if(neighbour.getGoal()){
						neighbour.setPredecessor(currentNode);
						return currentNode.getDepth() + 1;
					}
				}
				
			}
			currentNode.color = "black";
		}
		
		return -1;
	}
	
	
	public static int dfs(Node source){
		Stack<Node> stack = new Stack<>();
	
		
		if(source.getGoal()){
			return source.getDepth();
		}
		
		source.setDepth(0);
		source.color = "grey";
		
		stack.push(source);
		
		while(!stack.isEmpty()){
			Node currentNode = stack.pop();
			for(Node neighbour : currentNode.getNeighbours()){
				
				if(neighbour.color.equals("white")){
					neighbour.color = "grey";
					neighbour.setDepth(currentNode.getDepth() + 1);
					neighbour.setPredecessor(currentNode);
					stack.add(neighbour);
					
					if(neighbour.getGoal()){
						neighbour.setPredecessor(currentNode);
						return currentNode.getDepth() + 1;
					}
				}
				
			}
			currentNode.color = "black";
		}
		
		return -1;
	}
	
	
	public static int dijkstra(int[][] matrix, int source, int goal){
		
		// Initialize single source
		int n = matrix.length;
		int[] costs = new int[n];
		for(int i = 0; i < costs.length; i++){
			costs[i] = Integer.MAX_VALUE;
		}
		costs[source] = 0;
		
		
		//List of all the vertices
		ArrayList<Integer> Q = new ArrayList<>();
		for(int i = 0; i < costs.length; i++){
			Q.add(i);
		}
		
		
		while(Q.size() > 0){
			System.out.println(Q.toString());
			//get the smallest element from Q and removing it
			int min_value = Integer.MAX_VALUE;
			Object u = null;
			
			for(int vertice : Q){
				if(costs[vertice] < min_value){
					min_value = costs[vertice];
					u = vertice;
				}
			}
			
			if(u == null){
				break;
			}
			
			Q.remove(u);	
			
			for(int i = 0; i < n; i++){
				if(costs[i] > costs[(int) u] + matrix[(int) u][i]){
					costs[i] = costs[(int) u] + matrix[(int) u][i];
				}
			}
			
		}
		
		return costs[goal];
	}
	
}
