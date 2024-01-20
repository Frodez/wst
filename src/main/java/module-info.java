module info.wst {
    requires java.logging;
    requires transitive javafx.fxml;
    requires transitive javafx.controls;
    requires transitive javafx.graphics;
    requires transitive com.sun.jna;
    requires transitive com.sun.jna.platform;

    opens info.wst.utils.locale to javafx.fxml;
    opens info.wst.controllers to javafx.fxml;
    opens info.wst.models to javafx.base;
    opens info.wst.executable to javafx.graphics;
    
    exports info.wst.utils to info.wst;
    exports info.wst.models to info.wst;
    exports info.wst.jna.libraries to com.sun.jna;
    exports info.wst.jna.structs to com.sun.jna;
    exports info.wst.jna to com.sun.jna;
    exports info.wst.controllers to javafx.fxml;

    exports info.wst.executable;
}
