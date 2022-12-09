package sl.fside.ui.editors.scenarioSelector.controls;

import bgs.formalspecificationide.model.*;
import bgs.formalspecificationide.ui.*;
import com.google.inject.*;

public class ScenarioController implements IController {

    private Scenario scenario;

//    public ScenarioController(bgs.formalspecificationide.model.Scenario scenario) {
//        super();
//        this.scenario = scenario;
//
//        var fxmlLoader = new FXMLLoader(getClass().getResource(
//                "Scenario.fxml"));
//        fxmlLoader.setController(this);
//
//        try {
//            var result = fxmlLoader.<AnchorPane>load();
//            this.getChildren().add(result);
//        } catch (IOException exception) {
//            throw new RuntimeException(exception);
//        }
//    }

    @Inject
    public ScenarioController() {
        
    }

    @Override
    public void load(ModelBase object) {
        // TODO
    }

    @Override
    public void unload() {
        // TODO
    }
}
