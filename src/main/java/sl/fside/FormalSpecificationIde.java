package sl.fside;

import com.google.inject.*;
import javafx.application.*;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import sl.fside.services.*;

public class FormalSpecificationIde extends Application {

    private final Injector injector = Guice.createInjector(new MainModule());

    private final IResourceService resourceService = injector.getInstance(IResourceService.class);

    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle(resourceService.getText("ApplicationTitle"));

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ui/MainWindow.fxml"));
        fxmlLoader.setControllerFactory(injector::getInstance);
        var root = new AnchorPane();
        fxmlLoader.setRoot(root);
        fxmlLoader.<AnchorPane>load();

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(root);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

        stage.setScene(new Scene(scrollPane));
        stage.show();
    }
}