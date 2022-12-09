package sl.fside.ui;

import bgs.formalspecificationide.model.*;
import bgs.formalspecificationide.ui.editors.useCaseSelector.controls.*;
import javafx.scene.layout.*;
import javafx.util.*;

public interface IUIElementFactory {

    Pair<AnchorPane, UseCaseController> CreateUseCase(UseCase useCase);

    Pair<AnchorPane, UseCaseController> CreateUseCase(); 
    
}
