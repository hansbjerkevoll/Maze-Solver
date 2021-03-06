package hansbjerkevoll.java_maze_solver;

import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

public class AppController {

	String filepath = null;
	String directoryPath = null;
	Image solutionImage = null;

	@FXML private VBox root;
	@FXML private ImageView imageview;
	@FXML private Button loadButton, solveButton;
	@FXML private MenuItem saveImage, saveRawImage;

	public void initialize() {
		
		saveImage.setDisable(true);
		saveRawImage.setDisable(true);

		loadButton.setOnAction(e -> {
			FileChooser filechooser = new FileChooser();
			filechooser.setTitle("Open Resource File");
			filechooser.getExtensionFilters()
					.addAll(new ExtensionFilter("Image files", "*.png", "*.gif", "*.jpg", "*.jpeg", "*.bmp"));
			if(directoryPath != null) {
				filechooser.setInitialDirectory(new File(directoryPath));
			}

			File selectedFile = filechooser.showOpenDialog((Stage) root.getScene().getWindow());
			if (selectedFile != null) {
				filepath = selectedFile.getAbsolutePath().replace("\\\\", "/");
				directoryPath = selectedFile.getParent();
				imageview.setImage(new Image("file:////" + filepath, 750, 750, true, false));
				saveRawImage.setDisable(false);
				saveImage.setDisable(false);
			}
		});

		solveButton.setOnAction(ae -> {
			if (filepath != null) {
				try {
					Image solveImage = new Image("file:////" + filepath);
					long start = System.currentTimeMillis();
					solutionImage = new MazeSolver().solveMaze(solveImage);
					long end = System.currentTimeMillis();
					imageview.setImage(resample(solutionImage));
					Alerter.info("Maze solved!", "The maze was successfully solved in " + (end-start) + " ms.");

				} catch (Exception e) {
					e.printStackTrace();
					Alerter.exception(null, "Something went wrong!", e);
				}
			}
		});
		
		saveImage.setOnAction(ae -> {
			Image image = imageview.getImage();
			saveImage(image);			
		});
		
		saveRawImage.setOnAction(ae -> {
			saveImage(solutionImage);
		});
		
		
	}
	
	private void saveImage(Image image) {
		FileChooser filechooser = new FileChooser();
		filechooser.setTitle("Save Image");
		ExtensionFilter filter = new ExtensionFilter("Image File", "*.png");
		filechooser.getExtensionFilters().add(filter);
		filechooser.setSelectedExtensionFilter(filter);
		if(directoryPath != null) {
			filechooser.setInitialDirectory(new File(directoryPath));
		}
		File file = filechooser.showSaveDialog((Stage) root.getScene().getWindow());
		if(file != null) {
			try {
				ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
			} catch (IOException e1) {
				Alerter.error("Saving failed", "Could not save image!");
			}
		}
	}

	private Image resample(Image input) {
		final int W = (int) input.getWidth();
		final int H = (int) input.getHeight();
		final double S = Math.min(750d / (double) W, 750d / (double) H);

		WritableImage output = new WritableImage((int) (W * S), (int) (H * S));

		PixelReader reader = input.getPixelReader();
		PixelWriter writer = output.getPixelWriter();

		for (int y = 0; y < H; y++) {
			for (int x = 0; x < W; x++) {
				final int argb = reader.getArgb(x, y);
				for (int dy = 0; dy < S; dy++) {
					for (int dx = 0; dx < S; dx++) {
						writer.setArgb((int) (x * S + dx), (int) (y * S + dy), argb);
					}
				}
			}
		}

		return output;
	}

}
