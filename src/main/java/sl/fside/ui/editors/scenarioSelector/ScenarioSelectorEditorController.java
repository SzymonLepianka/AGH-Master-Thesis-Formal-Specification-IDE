package sl.fside.ui.editors.scenarioSelector;

import bgs.formalspecificationide.model.*;
import bgs.formalspecificationide.ui.*;
import com.google.inject.*;

public class ScenarioSelectorEditorController implements IController {
    
    private UseCase useCase;
    
//    public ScenarioSelectorEditorController() {
//        super();
//
//        var fxmlLoader = new FXMLLoader(getClass().getResource(
//                "ScenarioSelectorEditor.fxml"));
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
    public ScenarioSelectorEditorController() {
        
    }
    
    @Override
    public void load(ModelBase object) {
        if (object instanceof UseCase useCase)
            this.useCase = useCase;
        else 
            throw new UnsupportedOperationException("Unsupported type");
    }

    @Override
    public void unload() {
        // TODO
    }

}
