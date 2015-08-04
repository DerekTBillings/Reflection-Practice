package classes;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * This Class is a simple class that creates a message box in JavaFX
 * @author BillingsD
 *
 */
public class Notification {
	
	/**
	 * Creates a message box containing the String value or the provided Object
	 * @param Object
	 */
	public void notify(Object s) {
		Label label = new Label(s.toString());
		VBox vbox = new VBox(10);
		vbox.getChildren().add(label);
		vbox.setAlignment(Pos.CENTER);
		vbox.setPadding(new Insets(15, 15, 15, 15));
		Scene scene = new Scene(vbox);
		Stage stage = new Stage();
		stage.setScene(scene);
		stage.show();
	}
}
