module sl.fside {
    requires javafx.controls;
    requires javafx.fxml;


    opens sl.fside to javafx.fxml;
    exports sl.fside;
}