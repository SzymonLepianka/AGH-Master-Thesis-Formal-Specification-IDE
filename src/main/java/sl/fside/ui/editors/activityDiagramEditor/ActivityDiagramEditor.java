package sl.fside.ui.editors.activityDiagramEditor;

import io.github.eckig.grapheditor.*;
import javafx.application.*;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.text.*;
import javafx.stage.*;

import java.net.*;

/**
 * A demo application to show uses of the {@link GraphEditor} library.
 */
public class ActivityDiagramEditor extends Application {

    private static final String APPLICATION_TITLE = "Workflow Diagram";
    private static final String DEMO_STYLESHEET = "sl/fside/ui/editors/activityDiagramEditor/demo.css";
    private static final String FONT_AWESOME = "sl/fside/ui/editors/activityDiagramEditor/fontawesome.ttf";

    public static void main(final String[] args) {
        launch(args);
    }

    @Override
    public void start(final Stage stage) throws Exception {

        final URL location = getClass().getClassLoader().getResource("sl/fside/ui/editors/activityDiagramEditor/ActivityDiagramEditor.fxml");
        final FXMLLoader loader = new FXMLLoader();
        final Parent root = loader.load(location.openStream());

        final Scene scene = new Scene(root, 830, 630);

        scene.getStylesheets().add(getClass().getClassLoader().getResource(DEMO_STYLESHEET).toExternalForm());
        Font.loadFont(getClass().getClassLoader().getResource(FONT_AWESOME).toExternalForm(), 12);

        stage.setScene(scene);
        stage.setTitle(APPLICATION_TITLE);

        stage.show();

        final ActivityDiagramEditorController controller = loader.getController();
        controller.panToCenter();
    }
}
