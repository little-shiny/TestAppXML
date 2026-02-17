module com.testpsp {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires java.xml;

    opens com.testpsp to javafx.fxml;
    exports com.testpsp;
}