import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;


public class Main {
	
	static int rowSize;
	static int colSize;
	
	public static void main(String[] args) throws IOException{
		
		String filename = "C:/Users/Hans Bjerkevoll/Documents/GitHub/Maze-Solver/src/img/giant-maze.png";
		
		File file = new File(filename);
		BufferedImage image = ImageIO.read(file);
		image = convertToARGB(image);
		
		rowSize = image.getHeight();
		colSize = image.getWidth();
		
		ArrayList<Node> nodes = new ArrayList<>();
		
		
		//Initialize graph
		long start = System.currentTimeMillis();
		nodes = initialize(image);
		long end = System.currentTimeMillis();
		System.out.println("Initializing: " + (end-start) + "ms");
		
		
		//Solve the maze
		start = System.currentTimeMillis();
		Search_Algorithms.bfs(nodes.get(0));
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
		printSolution(solution, image);
		
		//Save solution
		String outputfilename = filename.substring(0, filename.lastIndexOf('.')) + "-solution.png";
		File outputimage = new File(outputfilename);
		ImageIO.write(image, "png", outputimage);
		
		
	}
	
	private static ArrayList<Node> initialize(BufferedImage image){
		
		//Scann the image and create pixelList
		int[][] pixelList = new int[rowSize][colSize];
		
		for(int y = 0; y < rowSize; y++){
			for(int x = 0; x < colSize; x++){
				int pixelValue = image.getRGB(x, y);
				if(pixelValue == -1){
					pixelList[y][x] = 1;
				}
				else{
					pixelList[y][x] = 0;
				}
			}
		}
		
		ArrayList<Node> nodes = new ArrayList<>();
		Node previousNode = null;
		Node[][] nodeList = new Node[rowSize][colSize];
		
		
		//Iterate over the image and create nodes
		for (int i = 0; i < pixelList.length; i++){
			for(int j = 0; j < pixelList[i].length; j++){
				int pixel = pixelList[i][j];
				
				//Skipping black pixels
				if(pixel == 0){
					previousNode = null;
				}
				
				//Creating start and finish node
				else if((i == 0 || i == pixelList.length -1)){
					Node node = new Node(i, j);
					nodeList[i][j] = node;
					if(i == 0){
						nodes.add(node);
					}
					else {
						nodes.add(node);
						//Add node up
						for(int x = i-1; x >= 0; x--){
							if(pixelList[x][j] == 0)continue;
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
				else if(!((pixelList[i-1][j] == 0 && pixelList[i+1][j] == 0) || (pixelList[i][j-1] == 0 && pixelList[i][j+1] == 0))){
					Node node = new Node(i, j);
					nodeList[i][j] = node;
					if(previousNode != null){
						node.addNeighbour(previousNode);
						previousNode.addNeighbour(node);
					}
					previousNode = node;
					//Check if there is a node up
					for(int x = i-1; x >= 0; x--){
						if(pixelList[x][j] == 0)break;
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
	
	
	private static void printSolution(ArrayList<Node> solution, BufferedImage image){
		
		Color color = new Color(255, 0, 0);
		boolean red_sinking = true;
		
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
					image.setRGB(y, start_x, color.getRGB());
				}
			}
			
			//Check if printing down
			else if(end_y < start_y){
				for(int y = end_y; y <= start_y; y++){
					image.setRGB(y, start_x, color.getRGB());
				}
			}
			
			//Check if printing right
			else if(start_x < end_x){
				for(int x = start_x; x <= end_x; x++){
					image.setRGB(start_y, x, color.getRGB());
				}
			}
			
			//Check if printing left
			else if(end_x < start_x){
				for(int x = end_x; x <= start_x; x++){
					image.setRGB(start_y, x, color.getRGB());
				}
			}
			
			if(color.getRed() == 255 && color.getBlue() < 255){
				color = new Color(color.getRed(), 0, color.getBlue()+5);
			}
			if(color.getBlue() == 255 && color.getRed() > 0){
				color = new Color(color.getRed()-5, 0, color.getBlue());
			}
		}
		
		
	}
	
	private static int calculateLength(ArrayList<Node> solution){
		int length = 0;
		
		for(int i = 0; i < solution.size()-1; i++){
			int x_length = Math.abs(solution.get(i).getX() - solution.get(i+1).getX());
			int y_length = Math.abs(solution.get(i).getY() - solution.get(i+1).getY());
			
			length += (x_length + y_length);

		}
		
		return length;
	}

	public static BufferedImage convertToARGB(BufferedImage image)
	{
	    BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
	    Graphics2D g = newImage.createGraphics();
	    g.drawImage(image, 0, 0, null);
	    g.dispose();
	    return newImage;
	}
}



