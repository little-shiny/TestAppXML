module com.testapp {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires java.xml;

    opens com.testapp to javafx.fxml;
    exports com.testapp;
}