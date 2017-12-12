import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;


public class Main {
	
	static int rowSize;
	static int colSize;
	
	public static void main(String[] args) throws IOException{
		
		String filename = "C:/Users/Hans Bjerkevoll/Documents/GitHub/Maze-Solver/src/img/maze1.png";
		
		File file = new File(filename);
		BufferedImage image = ImageIO.read(file);
		
		rowSize = image.getHeight();
		colSize = image.getWidth();
		
		
		//Scann the image and get list of pixels
		int[][] pixelList = createPixelList(image);
			
		//Create all the nodes
		ArrayList<Node> nodes = new ArrayList<>();
		nodes = createNodes(pixelList);
		
		
		//Solve the maze
		Search_Algorithms.dfs(nodes.get(0));
		
		
		//Get solution to maze
		ArrayList<Node> solution = new ArrayList<>();
		Node node = nodes.get(nodes.size()-1);
		while(node != null){
			solution.add(node);
			node = node.getPredecessor();
			
		}
		
		//Print solution
		printSolution(solution, image);
		
		//Save solution
		String outputfilename = filename.substring(0, filename.lastIndexOf('.')) + "-solution.png";
		File outputimage = new File(outputfilename);
		ImageIO.write(image, "png", outputimage);
		
	}
	
	private static void printSolution(ArrayList<Node> solution, BufferedImage image){
		
		int colorvalue = -60000;
		
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
					image.setRGB(y, start_x, colorvalue);
				}
			}
			
			//Check if printing down
			else if(end_y < start_y){
				for(int y = end_y; y <= start_y; y++){
					image.setRGB(y, start_x, colorvalue);
				}
			}
			
			//Check if printing right
			else if(start_x < end_x){
				for(int x = start_x; x <= end_x; x++){
					image.setRGB(start_y, x, colorvalue);
				}
			}
			
			//Check if printing left
			else if(end_x < start_x){
				for(int x = end_x; x <= start_x; x++){
					image.setRGB(start_y, x, colorvalue);
				}
			}
			
			colorvalue += 250;
		}
		
		
	}
	
	private static int[][] createPixelList(BufferedImage image){
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
		
		return pixelList;
	}
	
	private static ArrayList<Node> createNodes(int[][] pixelList){
		
		ArrayList<Node> nodes = new ArrayList<>();
		
		for (int i = 0; i < pixelList.length; i++){
			for(int j = 0; j < pixelList[i].length; j++){
				int pixel = pixelList[i][j];
				
				//Skipping black pixels
				if(pixel == 0){
					continue;
				}
				
				//Creating start and finish node
				if((i == 0 || i == pixelList.length -1)){
					if(i == 0)nodes.add(new Node(i, j, true, false));
					else nodes.add(new Node(i, j, false, true));					
					continue;
				}
				
				//Creating node
				if(!((pixelList[i-1][j] == 0 && pixelList[i+1][j] == 0) || (pixelList[i][j-1] == 0 && pixelList[i][j+1] == 0))){
					nodes.add(new Node(i, j));
					continue;
				}
				
			}
		}
		
		//Create edges between nodes
		createEdges(nodes, pixelList);
		
		return nodes;
	}
	
	private static ArrayList<Node> createEdges(ArrayList<Node> nodes, int[][] pixelList){
		
		for(Node node : nodes){
			
			int x = node.getX();
			int y = node.getY();
			
			
			//Check if there is a node up
			for(int i = x; i >= 0; i--){
				boolean skip = false;
				if(pixelList[i][y] == 0) break;
				for(Node checkNode : nodes){
					
					if(node.equals(checkNode)) continue;
					if(checkNode.getX() == i && checkNode.getY() == y){
						node.addNeighbour(checkNode);
						skip = true;
						break;
					}
				}
				if(skip)break;
			}
			
			
			//Check if there is a node down
			for(int i = x; i < rowSize; i++){
				boolean skip = false;
				if(pixelList[i][y] == 0) break;
				for(Node checkNode : nodes){					
					if(node.equals(checkNode)) continue;
					if(checkNode.getX() == i && checkNode.getY() == y){
						node.addNeighbour(checkNode);
						skip = true;
						break;
					}
					
				}
				if(skip)break;
			}
			
			
			//Check if there is a node to the left
			for(int i = y; i > 0; i--){
				boolean skip = false;
				if(pixelList[x][i] == 0) break;
				for(Node checkNode : nodes){					
					if(node.equals(checkNode)) continue;
					if(checkNode.getX() == x && checkNode.getY() == i){
						node.addNeighbour(checkNode);
						skip = true;
						break;
					}
					
				}
				if(skip)break;
			}
			
			
			//Check if there is a node to the right
			for(int i = y; i < colSize; i++){
				boolean skip = false;
				if(pixelList[x][i] == 0) break;
				for(Node checkNode : nodes){					
					if(node.equals(checkNode)) continue;
					if(checkNode.getX() == x && checkNode.getY() == i){
						node.addNeighbour(checkNode);
						skip = true;
						break;
					}
					
				}
				if(skip)break;
			}			
		}
		
		return nodes;
	}

}



