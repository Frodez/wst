package info.wst.utils.locale;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.WeakHashMap;
import java.util.Map.Entry;
import java.util.Objects;

import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

public class LocalizedHelper {

    public static final String LOCALIZED_EXPRESSION_PREFIX = "i18n";

    private static Map<Object, ObservableMap<String, Object>> controllerNamespaceMap = Collections
            .synchronizedMap(new WeakHashMap<>());

    public static void switchLocale(Locale locale) {
        Objects.requireNonNull(locale);
        
        Locale.setDefault(locale);

        for (Entry<Object, ObservableMap<String, Object>> entry : controllerNamespaceMap.entrySet()) {
            Class<?> controllerClass = entry.getKey().getClass();
            LocalizedFXMLController controllerAnnotation = controllerClass
                    .getAnnotation(LocalizedFXMLController.class);
            if (controllerAnnotation == null) {
                throw new RuntimeException("The controller " + controllerClass.getName() +
                        " must have the annotation " + LocalizedFXMLController.class.getName() + ".");
            }

            String localeResourceName = controllerAnnotation.localeResource();
            ResourceBundle resourceBundle = ResourceBundle.getBundle(localeResourceName, locale);
            if (resourceBundle == null) {
                throw new RuntimeException("The " + locale + " locale resource " + localeResourceName +
                        " for the controller " + controllerClass.getName() + " has not been found.");
            }

            ObservableMap<String, Object> namespace = entry.getValue();

            @SuppressWarnings("unchecked")
            ObservableMap<String, String> oldLocaleResource = (ObservableMap<String, String>) namespace
                    .get(LOCALIZED_EXPRESSION_PREFIX);
            if (oldLocaleResource == null) {
                throw new RuntimeException("The old locale resource not found.");
            }

            HashSet<String> removableKeySet = new HashSet<>();
            removableKeySet.addAll(oldLocaleResource.keySet());
            removableKeySet.removeAll(resourceBundle.keySet());
            for (String removableKey : removableKeySet) {
                oldLocaleResource.remove(removableKey);
            }

            for (String availableKey : resourceBundle.keySet()) {
                oldLocaleResource.put(availableKey, resourceBundle.getString(availableKey));
            }
        }
    }

    public static void bindLocale(Object controller, StringProperty property, String reference) {
        Objects.requireNonNull(property);
        Objects.requireNonNull(reference);

        ObservableMap<String, String> localeResource = getLocaleResource(controller);

        localeResource.addListener(new MapChangeListener<String, String>() {

            @Override
            public void onChanged(Change<? extends String, ? extends String> change) {
                if (change.getKey().equals(reference)) {
                    property.setValue(change.getValueAdded());
                }
            }

        });
    }

    public static ObservableMap<String, String> getLocaleResource(Object controller) {
        Objects.requireNonNull(controller);
        ObservableMap<String, Object> namespace = controllerNamespaceMap.get(controller);
        if (namespace == null) {
            throw new RuntimeException("namespace of local resource not found");
        }

        @SuppressWarnings("unchecked")
        ObservableMap<String, String> localeResource = (ObservableMap<String, String>) namespace
                .get(LOCALIZED_EXPRESSION_PREFIX);
        if (localeResource == null) {
            throw new RuntimeException("The locale resource not found.");
        }

        return localeResource;
    }

    public static Parent fxmlLoad(Class<?> controllerClass, Locale locale) {
        Objects.requireNonNull(controllerClass);
        Objects.requireNonNull(locale);

        LocalizedFXMLController controllerAnnotation = controllerClass.getAnnotation(LocalizedFXMLController.class);
        if (controllerAnnotation == null) {
            throw new RuntimeException("The controller " + controllerClass.getName() +
                    " must have the annotation " + LocalizedFXMLController.class.getName() + ".");
        }

        String fxmlResourceName = controllerAnnotation.fxmlResource();
        URL fxmlResource = ClassLoader.getSystemResource(fxmlResourceName);
        if (fxmlResource == null) {
            throw new RuntimeException("The fxml resource " + fxmlResourceName + " for the controller " +
                    controllerClass.getName() + " has not been found.");
        }

        String localeResourceName = controllerAnnotation.localeResource();
        ResourceBundle resourceBundle = ResourceBundle.getBundle(localeResourceName, locale);
        if (resourceBundle == null) {
            throw new RuntimeException("The " + locale + " locale resource " + localeResourceName +
                    " for the controller " + controllerClass.getName() + " has not been found.");
        }
        ObservableMap<String, String> localeResource = FXCollections
                .observableMap(buildLocaleResourceMap(resourceBundle));

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(fxmlResource);
        ObservableMap<String, Object> namespace = loader.getNamespace();
        namespace.put(LOCALIZED_EXPRESSION_PREFIX, localeResource);

        Parent parent;
        try {
            parent = loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Object controller = loader.getController();
        if (controller == null) {
            throw new RuntimeException("The controller " + controllerClass.getName() +
                    " must be related to the fxml resource " + fxmlResource + ".");
        }
        if (controller.getClass() != controllerClass) {
            throw new RuntimeException("The fxml resource " + fxmlResource +
                    " must not refer to the controller " + controller.getClass().getName() +
                    ", but the controller " + controllerClass.getName() + ".");
        }

        controllerNamespaceMap.put(controller, namespace);

        return parent;
    }

    private static HashMap<String, String> buildLocaleResourceMap(ResourceBundle resourceBundle) {
        HashMap<String, String> messageMap = new HashMap<>();
        for (String key : resourceBundle.keySet()) {
            String value = resourceBundle.getString(key);
            messageMap.put(key, value);
        }
        return messageMap;
    }

}
