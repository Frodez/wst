package info.wst.utils;

import java.io.PrintWriter;
import java.io.StringWriter;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TextArea;

public class ErrorUtil {

    private static final double GLOBAL_ERROR_ALERT_HEIGHT = 480;

    public static void reportError(Throwable error) {
        String trace = null;
        for (Throwable throwable = error; throwable != null; throwable = throwable.getCause()) {
            if (throwable instanceof CommonException) {
                trace = throwable.getMessage();
                break;
            }
        }
        if (trace == null) {
            StringWriter writer = new StringWriter();
            if (error != null) {
                error.printStackTrace(new PrintWriter(writer));
            }
            trace = writer.toString();
        }
        reportError(trace);
    }

    public static void reportError(String error) {
        LogUtil.error(error);
        Platform.runLater(() -> {
            TextArea textArea = new TextArea(error);
            textArea.setEditable(false);

            Alert alert = new Alert(AlertType.ERROR);
            alert.setHeight(GLOBAL_ERROR_ALERT_HEIGHT);

            DialogPane dialogPane = alert.getDialogPane();
            dialogPane.setExpandableContent(textArea);
            dialogPane.setExpanded(true);

            alert.showAndWait();
        });
    }

}
