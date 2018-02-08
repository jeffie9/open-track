package org.tracks;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class MainFrame extends Application {
    private final Logger LOG = LoggerFactory.getLogger(MainFrame.class);
    private Stage primaryStage;
    private Map<String, Pane> screenMap = new HashMap<>();

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        screenMap.put("layoutEditor", FXMLLoader.load(getClass().getResource("view/LayoutEditView.fxml")));
        screenMap.put("layoutRunner", FXMLLoader.load(getClass().getResource("view/RunView.fxml")));
        screenMap.put("trackEditor",  FXMLLoader.load(getClass().getResource("view/TrackEditView.fxml")));
        addViewMenu("layoutEditor", "Layout Editor");
        addViewMenu("layoutRunner", "Runner");
        addViewMenu("trackEditor", "Track Editor");

        Scene scene = new Scene(screenMap.get("trackEditor"));
        primaryStage.setScene(scene);
        primaryStage.setTitle("Layout App");
        primaryStage.show();
        LOG.info("Started MainFrame");
    }

    public static void main(String[] args) {
        Application.launch(args);
    }

    void addViewMenu(String id, String label) throws Exception {
        for (Pane pane : screenMap.values()) {
            // assuming menu bar is first child
            MenuBar mb = (MenuBar) pane.getChildren().get(0);
            Menu menuView = mb.getMenus().filtered(m -> m.getText().equals("View")).get(0);
            MenuItem mi = new MenuItem(label);
            mi.setId(id);
            mi.setOnAction(this::handleMenuView);
            menuView.getItems().add(mi);
        }
    }

    void handleMenuView(ActionEvent event) {
        if (event.getSource() instanceof MenuItem) {
            primaryStage.getScene().setRoot(screenMap.get(((MenuItem)event.getSource()).getId()));
        }
    }
}
