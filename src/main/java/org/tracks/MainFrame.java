package org.tracks;

import java.io.File;
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
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class MainFrame extends Application {
    private final Logger LOG = LoggerFactory.getLogger(MainFrame.class);
    private static Stage primaryStage;
    private Map<String, Pane> screenMap = new HashMap<>();
    private FileChooser fileChooser = new FileChooser();

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        addView("layoutEditor", "view/LayoutEditView.fxml");
        addView("layoutRunner", "view/RunView.fxml");
        addView("trackEditor", "view/TrackEditView.fxml");
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

    void addView(String viewId, String viewResource) throws Exception {
        Pane pane = Pane.class.cast(FXMLLoader.load(getClass().getResource(viewResource)));
        pane.setUserData(this);
        screenMap.put(viewId, pane);
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

    static Stage getPrimaryStage() {
        return primaryStage;
    }
}
