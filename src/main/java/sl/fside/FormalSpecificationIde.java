package sl.fside;

import javafx.stage.Stage;
import sl.fside.services.*;
import com.google.inject.*;
import javafx.application.*;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.stage.*;

public class FormalSpecificationIde extends Application {

    private final Injector injector = Guice.createInjector(new MainModule());

    private final IResourceService resourceService = injector.getInstance(IResourceService.class);
    private FXMLLoader fxmlLoader;

    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle(resourceService.getText("ApplicationTitle"));
        stage.setResizable(false);

        fxmlLoader = new FXMLLoader(getClass().getResource("ui/MainWindow.fxml"));
        fxmlLoader.setControllerFactory(injector::getInstance);
        var root = new AnchorPane();
        fxmlLoader.setRoot(root);
        fxmlLoader.<AnchorPane>load();
        stage.setScene(new Scene(root));

        stage.show();
    }
}