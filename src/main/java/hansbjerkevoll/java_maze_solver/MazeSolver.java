package hansbjerkevoll.java_maze_solver;

import java.io.IOException;
import java.util.ArrayList;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

public class MazeSolver {

	static int rowSize;
	static int colSize;
	
	public Image solveMaze(Image image) throws IOException {
		
		PixelReader pixelreader = image.getPixelReader();
				
		rowSize = (int) image.getHeight();
		colSize = (int) image.getWidth();
		
		ArrayList<Node> nodes = new ArrayList<>();
		
		//Initialize graph
		long start = System.currentTimeMillis();
		nodes = initialize(pixelreader);
		long end = System.currentTimeMillis();
		System.out.println("Initializing: " + (end-start) + "ms");
		
		//Solve the maze
		start = System.currentTimeMillis();
		SearchAlgorithms.bfs(nodes.get(0));
		end = System.currentTimeMillis();
		System.out.println("Solving maze: " + (end-start) + "ms");
		
		//Get solution to maze
		ArrayList<Node> solution = new ArrayList<>();
		Node node = nodes.get(nodes.size()-1);
		while(node != null){
			solution.add(node);
			node = node.getPredecessor();		
		}
		
		System.out.println("Solution length: " + calculateLength(solution) + "px");
		
		//Print solution
		WritableImage solutionImage = printSolution(solution, image);
				
		return solutionImage;
	}
	
	private  ArrayList<Node> initialize(PixelReader pixelreader){
		
		//Scann the image and create pixelList
		boolean[][] pixelList = new boolean[rowSize][colSize];
		
		for(int y = 0; y < rowSize; y++){
			for(int x = 0; x < colSize; x++){
				Color pixelColor = pixelreader.getColor(x, y);
				if(pixelColor.getRed() == 1.0 && pixelColor.getGreen() == 1.0 && pixelColor.getBlue() == 1.0 ){
					pixelList[y][x] = true;
				}
				else{
					pixelList[y][x] = false;
				}
			}
		}
		
		ArrayList<Node> nodes = new ArrayList<>();
		Node previousNode = null;
		Node[][] nodeList = new Node[rowSize][colSize];
		
		
		//Iterate over the image and create nodes
		for (int i = 0; i < pixelList.length; i++){
			for(int j = 0; j < pixelList[i].length; j++){
				boolean pixel = pixelList[i][j];
				
				//Skipping black pixels
				if(!pixel){
					previousNode = null;
				}
				
				//Creating start and finish node
				else if((i == 0 || i == pixelList.length -1)){
					Node node = new Node(i, j);
					nodeList[i][j] = node;
					if(i == 0){
						node.setStart(true);
						nodes.add(node);
					}
					else {
						node.setGoal(true);
						nodes.add(node);
						//Add node up
						for(int x = i-1; x >= 0; x--){
							if(!pixelList[x][j])continue;
							if(nodeList[x][j] != null){
								Node neighbour = nodeList[x][j];
								node.addNeighbour(neighbour);
								neighbour.addNeighbour(node);
								break;
							}
						}
					}
				}
				
				//Creating node
				else if(!((!pixelList[i-1][j] && !pixelList[i+1][j]) || (!pixelList[i][j-1] && !pixelList[i][j+1]))){
					Node node = new Node(i, j);
					nodeList[i][j] = node;
					if(previousNode != null){
						node.addNeighbour(previousNode);
						previousNode.addNeighbour(node);
					}
					previousNode = node;
					//Check if there is a node up
					for(int x = i-1; x >= 0; x--){
						if(!pixelList[x][j])break;
						if(nodeList[x][j] != null){
							Node neighbour = nodeList[x][j];
							node.addNeighbour(neighbour);
							neighbour.addNeighbour(node);
							break;
						}
					}
					
					nodes.add(node);
				}
			}
			previousNode = null;
		}
		
		return nodes;
	}
		
	private WritableImage printSolution(ArrayList<Node> solution, Image image) {
		
		WritableImage solutionImage = new WritableImage(image.getPixelReader(), rowSize, colSize);
		PixelWriter pixelwriter = solutionImage.getPixelWriter();
		
		// Crimson #DC143c
		Color color = new Color(220d/255, 20d/255, 60d/255, 1.0);
		
		for(int i = 0; i < solution.size()-1; i++){
			Node startnode = solution.get(i);
			Node endnode = solution.get(i+1);
			
			int start_x = startnode.getX();
			int start_y = startnode.getY();
			int end_x = endnode.getX();
			int end_y = endnode.getY();
			
			//Check if printing up
			if(start_y < end_y){
				for(int y = start_y; y <= end_y; y++){
					pixelwriter.setColor(y, start_x, color);
				}
			}
			
			//Check if printing down
			else if(end_y < start_y){
				for(int y = end_y; y <= start_y; y++){
					pixelwriter.setColor(y, start_x, color);
				}
			}
			
			//Check if printing right
			else if(start_x < end_x){
				for(int x = start_x; x <= end_x; x++){
					pixelwriter.setColor(start_y, x, color);
				}
			}
			
			//Check if printing left
			else if(end_x < start_x){
				for(int x = end_x; x <= start_x; x++){
					pixelwriter.setColor(start_y, x, color);
				}
			}		
		}
		
		return solutionImage;
		
	}
	
	private int calculateLength(ArrayList<Node> solution) {
		int length = 0;
		
		for(int i = 0; i < solution.size()-1; i++){
			int x_length = Math.abs(solution.get(i).getX() - solution.get(i+1).getX());
			int y_length = Math.abs(solution.get(i).getY() - solution.get(i+1).getY());
			
			length += (x_length + y_length);

		}
		
		return length;
	}
}
