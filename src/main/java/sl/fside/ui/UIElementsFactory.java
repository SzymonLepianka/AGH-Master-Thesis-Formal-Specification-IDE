//package sl.fside.ui;
//
//import sl.fside.model.*;
//import sl.fside.ui.editors.useCaseSelector.controls.*;
//import com.google.inject.*;
//import javafx.fxml.*;
//import javafx.scene.*;
//import javafx.scene.layout.*;
//import javafx.util.*;
//
//import java.io.*;
//
//class UIElementsFactory implements IUIElementFactory {
//
//    private final Injector injector;
//
////    private final FXMLLoader fxmlLoader;
//
//    private final String editorsPath = "editors/";
//
//    private final String useCaseSelectorControlsPath = editorsPath + "useCaseSelector/controls/";
//
//    @Inject
//    public UIElementsFactory(Injector injector) {
//
//        this.injector = injector;
//    }
//
//    @Override
//    public Pair<AnchorPane, UseCaseController> CreateUseCase(UseCase useCase) {
//        var pair = this.<AnchorPane, UseCaseController>loadFromFxmnl(useCaseSelectorControlsPath + "UseCase.fxml");
//        pair.getValue().load(useCase);
//        return pair;
//    }
//
//    @Override
//    public Pair<AnchorPane, UseCaseController> CreateUseCase() {
//        return loadFromFxmnl(useCaseSelectorControlsPath + "UseCase.fxml");
//    }
//
//    private <TUIElement extends Node, TController extends IController> Pair<TUIElement, TController> loadFromFxmnl(String path) {
//        try {
//            var fxmlLoader = new FXMLLoader(getClass().getResource(path));
//            fxmlLoader.setControllerFactory(injector::getInstance);
//
//            var result = fxmlLoader.<TUIElement>load();
//            var controller = fxmlLoader.<TController>getController();
//            return new Pair<>(result, controller);
//        } catch (IOException e) {
//            throw new RuntimeException(e); // TODO
//        }
//
//    }
//}
