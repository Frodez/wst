package info.wst.executable;

import java.io.IOException;
import java.util.Locale;

import info.wst.controllers.IndexController;
import info.wst.utils.ErrorUtil;
import info.wst.utils.LogUtil;
import info.wst.utils.locale.LocalizedHelper;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    private static final double PRIMARY_STAGE_MIN_WIDTH = 800;
    private static final double PRIMARY_STAGE_MIN_HEIGHT = 480;

    public static void main(String[] args) throws SecurityException, IOException {
        boolean enableLog = false;
        for(String arg : args) {
            if(arg.equals("--enable-log")) {
                enableLog = true;
            }
        }
        LogUtil.setup(enableLog);
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
            ErrorUtil.reportError(e);
        });
        Parent root = LocalizedHelper.fxmlLoad(IndexController.class, Locale.getDefault());
        primaryStage.setScene(new Scene(root));
        primaryStage.setTitle("Windows Small Toolkits");
        primaryStage.setMinWidth(PRIMARY_STAGE_MIN_WIDTH);
        primaryStage.setMinHeight(PRIMARY_STAGE_MIN_HEIGHT);
        primaryStage.show();
    }

}
