package prototype;

import java.util.ArrayList;

public class Node {
	
	private int x;
	private int y;

	private boolean start;
	private boolean goal;
	
	//List to keep neighbor nodes
	private ArrayList<Node> neighbours = new ArrayList<>();
	
	
	//Constructor for Node
	
	public Node(int x, int y) {
		this.x = x;
		this.y = y;
		this.start = false;
		this.goal = false;
	}
	
	public Node(int x, int y, boolean start, boolean goal) {
		this.x = x;
		this.y = y;
		this.start = start;
		this.goal = goal;
	}
	
	
	//Add neighbour Node
	public void addNeighbour(Node node){
		this.neighbours.add(node);
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
	
}
