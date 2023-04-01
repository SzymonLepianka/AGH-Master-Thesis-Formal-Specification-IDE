module sl.fside {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.guice;
    requires org.apache.logging.log4j;
    requires org.apache.logging.log4j.core;
    requires java.xml;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.annotation;
    requires com.fasterxml.jackson.databind;
    requires org.jetbrains.annotations;
    requires io.github.eckig.grapheditor.api;
    requires io.github.eckig.grapheditor.core;
    requires org.eclipse.emf.edit;
    requires org.eclipse.emf.ecore.xmi;
    requires json.simple;
    requires org.json;
    requires org.apache.commons.lang3;
    requires fontawesomefx;
    requires org.fxmisc.richtext;

    opens sl.fside.services to com.google.guice;
    opens sl.fside.persistence to com.google.guice;
    opens sl.fside.persistence.repositories to com.google.guice;
    opens sl.fside.factories to com.google.guice;
    opens sl.fside.model to com.fasterxml.jackson.databind;
    opens sl.fside to com.google.guice, javafx.fxml;
    opens sl.fside.ui.editors.activityDiagramEditor to com.google.guice, javafx.fxml, javafx.graphics;
    opens sl.fside.ui.editors.activityDiagramEditor.resultsEditor to com.google.guice, javafx.fxml, javafx.graphics;
    opens sl.fside.ui.editors.activityDiagramEditor.ownImpl to com.google.guice, javafx.fxml, javafx.graphics;
    opens sl.fside.ui.editors.activityDiagramEditor.customskin to com.google.guice, javafx.fxml, javafx.graphics;
    opens sl.fside.ui.editors.activityDiagramEditor.managers to com.google.guice, javafx.fxml, javafx.graphics;
    opens sl.fside.ui.editors.activityDiagramEditor.utils to com.google.guice, javafx.fxml, javafx.graphics;

    opens sl.fside.ui to javafx.fxml, com.google.guice;
    opens sl.fside.ui.editors.useCaseSelector to javafx.fxml, com.google.guice;
    opens sl.fside.ui.editors.useCaseSelector.controls to javafx.fxml, com.google.guice;
    opens sl.fside.ui.editors.imageViewer to javafx.fxml, com.google.guice;
    opens sl.fside.ui.editors.scenarioSelector to javafx.fxml, com.google.guice;
    opens sl.fside.ui.editors.scenarioSelector.controls to javafx.fxml, com.google.guice;
    opens sl.fside.ui.editors.actionEditor to javafx.fxml, com.google.guice;
    opens sl.fside.ui.editors.actionEditor.controls to javafx.fxml, com.google.guice;
    opens sl.fside.ui.editors.activityDiagramPanel to javafx.fxml, com.google.guice;
    opens sl.fside.ui.editors.resultsPanel to javafx.fxml, com.google.guice;

    exports sl.fside;
}