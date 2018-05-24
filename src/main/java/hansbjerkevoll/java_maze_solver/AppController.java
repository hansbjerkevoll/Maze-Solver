package hansbjerkevoll.java_maze_solver;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

public class AppController {
	
	static int rowSize;
	static int colSize;
	
	String filepath = null;
	
	
	@FXML private VBox root; 
	@FXML private ImageView image;
	@FXML private Button loadButton, solveButton;
	
	public void initialize() {
		
		loadButton.setOnAction(e -> {
			FileChooser filechooser = new FileChooser();
			filechooser.setTitle("Open Resource File");
			filechooser.getExtensionFilters().addAll(new ExtensionFilter("Image files", "*.png", "*.gif", "*.jpg", "*.jpeg"));
			
			File selectedFile = filechooser.showOpenDialog((Stage) root.getScene().getWindow());
			filepath = selectedFile.getAbsolutePath().replace("\\\\", "/");
			if(selectedFile != null) {
				image.setImage(new Image("file:////"+filepath));
			}
		});
		
		solveButton.setOnAction(ae -> {
			if(filepath != null) {
				try {
					solveMaze(filepath);
					
				} catch (Exception e) {
					Alerter.error(null, "Something went wrong!");
				}	
			}
		});
	}
	
	private void solveMaze(String filename) throws IOException {
		
		File file = new File(filename);
		BufferedImage buffered_image = ImageIO.read(file);
		buffered_image = convertToRGB(buffered_image);
		
		rowSize = buffered_image.getHeight();
		colSize = buffered_image.getWidth();
		
		ArrayList<Node> nodes = new ArrayList<>();
		
		
		//Initialize graph
		long start = System.currentTimeMillis();
		nodes = initialize(buffered_image);
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
		printSolution(solution, buffered_image);
		
		//View new image on GUI
		Image solution_image = SwingFXUtils.toFXImage(buffered_image, null);
		image.setImage(solution_image);
		
		Alerter.info("Maze solved", "The maze was successfully solved in " + (end-start) + " ms.");
	}
	
	private  ArrayList<Node> initialize(BufferedImage image){
		
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
		
	private void printSolution(ArrayList<Node> solution, BufferedImage image) {
		
		// Crimson #DC143c
		Color color = new Color(220, 20, 60);
		
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
			
		}
		
		
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

	public BufferedImage convertToRGB(BufferedImage image) {
	    BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
	    Graphics2D g = newImage.createGraphics();
	    g.drawImage(image, 0, 0, null);
	    g.dispose();
	    return newImage;
	}
	
}
