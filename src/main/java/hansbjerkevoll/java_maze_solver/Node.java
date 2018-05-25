package hansbjerkevoll.java_maze_solver;

import java.util.ArrayList;

public class Node {
	
	private int x;
	private int y;
	
	//Fields for BFS search
	int depth;
	Node predecessor;
	String color;
	
	private boolean start;
	private boolean goal;

	
	//List to keep neighbor nodes
	private ArrayList<Node> neighbours = new ArrayList<>();
	
	
	//Constructor for Node
	
	public Node(int x, int y) {
		this.x = x;
		this.y = y;
		this.depth = -1;
		this.start = false;
		this.goal = false;
		this.predecessor = null;
		this.color = "white";
	}
	
	public Node(int x, int y, boolean start, boolean goal) {
		this.x = x;
		this.y = y;
		this.depth = -1;
		this.start = start;
		this.goal = goal;
		this.predecessor = null;
		this.color = "white";
	}
	
	
	//Add neighbour Node
	public void addNeighbour(Node node){
		this.neighbours.add(node);
	}
	
	
	//Set depth of Node
	public void setDepth(int depth){
		this.depth = depth;
	}
	
	//Set predecessor
	public void setPredecessor(Node node){
		this.predecessor = node;
	}
	
	
	// Getters
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public boolean getStart(){
		return this.start;
	}
	
	public boolean getGoal() {
		return this.goal;
	}
	
	public ArrayList<Node> getNeighbours() {
		return this.neighbours;
	}
	
	public int getDepth(){
		return this.depth;
	}
	
	public Node getPredecessor(){
		return this.predecessor;
	}

}
