package info.wst.controllers;

import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import info.wst.jna.API;
import info.wst.models.AppContainer;
import info.wst.utils.CommonException;
import info.wst.utils.LogUtil;
import info.wst.utils.locale.LocalizedFXMLController;
import info.wst.utils.locale.LocalizedHelper;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;

@LocalizedFXMLController(fxmlResource = "index.fxml", localeResource = "i18n/controller/index")
public class IndexController implements Initializable {

    @FXML
    private TextField appNameFilter;

    @FXML
    private TableView<AppContainer> appStatusView;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        appStatusView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        getAppContainersFromSystem();
    }

    @FXML
    private void switchLanguage(ActionEvent event) {
        MenuItem menuItem = (MenuItem) event.getSource();
        String locale = menuItem.getId();
        if ("zh-CN".equals(locale)) {
            LocalizedHelper.switchLocale(Locale.SIMPLIFIED_CHINESE);
        } else if ("en-US".equals(locale)) {
            LocalizedHelper.switchLocale(Locale.US);
        } else {
            String message = LogUtil.formatLogMessage("error.unknown_locale", locale);
            throw new CommonException(message);
        }
    }

    @FXML
    private void filterAppNames(KeyEvent event) {
        var filteredList = (FilteredList<AppContainer>) appStatusView.getItems();

        setappStatusViewItems(filteredList);
    }

    @FXML
    private void enableIsolation(ActionEvent event) {
        List<String> selectedSids = getSelectedSids();

        API.enableIsolation(selectedSids);

        getAppContainersFromSystem();
    }

    @FXML
    private void disableIsolation(ActionEvent event) {
        List<String> selectedSids = getSelectedSids();
        
        API.disableIsolation(selectedSids);

        getAppContainersFromSystem();
    }

    @FXML
    private void refreshAppContainer(ActionEvent event) {
        getAppContainersFromSystem();
    }

    private List<String> getSelectedSids() {
        return appStatusView.getSelectionModel().getSelectedItems().stream()
                .map(AppContainer::getSid).collect(Collectors.toList());
    }

    private void getAppContainersFromSystem() {
        var filteredList = new FilteredList<>(FXCollections.observableArrayList(API.getAppContainers()));
        setappStatusViewItems(filteredList);
    }

    private void setappStatusViewItems(FilteredList<AppContainer> filteredList) {
        String filter = this.appNameFilter.textProperty().get();
        if (filter != null && !filter.isBlank()) {
            filteredList.setPredicate((appContainer) -> {
                return appContainer.getDisplayName().contains(filter);
            });
        }
        appStatusView.setItems(filteredList);
    }

}
