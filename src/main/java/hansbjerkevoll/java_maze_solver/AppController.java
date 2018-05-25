package hansbjerkevoll.java_maze_solver;

import java.io.File;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
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

	@FXML
	private VBox root;
	@FXML
	private ImageView imageview;
	@FXML
	private Button loadButton, solveButton;

	public void initialize() {

		loadButton.setOnAction(e -> {
			FileChooser filechooser = new FileChooser();
			filechooser.setTitle("Open Resource File");
			filechooser.getExtensionFilters()
					.addAll(new ExtensionFilter("Image files", "*.png", "*.gif", "*.jpg", "*.jpeg"));

			File selectedFile = filechooser.showOpenDialog((Stage) root.getScene().getWindow());
			if (selectedFile != null) {
				filepath = selectedFile.getAbsolutePath().replace("\\\\", "/");
				imageview.setImage(new Image("file:////" + filepath, 750, 750, true, false));
			}
		});

		solveButton.setOnAction(ae -> {
			if (filepath != null) {
				try {
					Image solveImage = new Image("file:////" + filepath);
					Image solutionImage = new MazeSolver().solveMaze(solveImage);

					imageview.setImage(resample(solutionImage));

				} catch (Exception e) {
					e.printStackTrace();
					Alerter.exception(null, "Something went wrong!", e);
				}
			}
		});
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
