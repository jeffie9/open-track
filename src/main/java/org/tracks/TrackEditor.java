package org.tracks;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tracks.model.TrackDefinition;
import org.tracks.model.TrackSegment;
import org.yaml.snakeyaml.Yaml;

import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.InputMethodEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Border;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.shape.StrokeLineCap;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.util.converter.NumberStringConverter;

public class TrackEditor extends VBox {
    private final Logger LOG = LoggerFactory.getLogger(TrackEditor.class);

    @FXML
    TilePane trackList;
    @FXML
    Group trackGroup;
    @FXML
    TextField tfLength;
    @FXML
    TextField tfRadius;
    @FXML
    Label lblRadius;

    private NumberStringConverter converter = new NumberStringConverter();
    private Group selectedSegment;
    private Shape referenceShape;
    private Point2D anchor;
    private Pane selectedTrackPane;
    private List<TrackDefinition> library = new ArrayList<>();
    private File activeFile = null;
    private FileChooser fileChooser;

    @FXML
    public void initialize() throws Exception {
        LOG.debug("initialize");
        tfLength.textProperty().addListener(this::lengthTextChanged);
        tfRadius.textProperty().addListener(this::radiusTextChanged);
        fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(
                new File(System.getProperty("user.home")));
    }

    @FXML
    protected void onMenuFileSave(ActionEvent event) {
        if (activeFile == null) {
            fileChooser.setTitle("Save Track Library As");
            fileChooser.getExtensionFilters().addAll(
                    new ExtensionFilter("YAML Files", "*.yaml", "*.yml"),
                    new ExtensionFilter("All Files", "*.*"));
            activeFile = fileChooser.showSaveDialog(MainFrame.getPrimaryStage());
            LOG.debug("activeFile: " + activeFile);
        }
        saveObjectToFile(library, activeFile);
    }

    @FXML
    protected void onNewButton(ActionEvent event) {
        Pane pane = new StackPane();
        //pane.setMinSize(50.0, 50.0);
        pane.setStyle("-fx-background-color: #87CEFA;");
        pane.setOnMouseClicked(this::onMouseClickedTrackPane);
        pane.setScaleX(0.1);
        pane.setScaleY(0.1);
        trackList.getChildren().add(pane);
        selectedTrackPane = pane;
        TrackDefinition td = new TrackDefinition();
        pane.setUserData(td);
        library.add(td);
        trackGroup.getChildren().clear();
    }

    @FXML
    protected void onAddStraight(ActionEvent event) {
        Group group = makeStraightGroup(null);
        trackGroup.getChildren().add(group);
        onTrackGroupChanged();
    }

    @FXML
    protected void onAddCurve(ActionEvent event) {
        Group group = makeCurveGroup(null);
        trackGroup.getChildren().add(group);
        onTrackGroupChanged();
    }

    @FXML
    protected void onAddEndpoint(ActionEvent event) {
        Group group = makeEndpointGroup();
        trackGroup.getChildren().add(group);
        onTrackGroupChanged();
    }

    @FXML
    public void onMousePressed(MouseEvent event) {
        LOG.debug("onMousePressed " + event);
        anchor = new Point2D(event.getX(), event.getY());
        selectedSegment = null;
        referenceShape = null;
        if (Shape.class.isInstance(event.getTarget())) {
            selectedSegment = (Group) ((Shape) event.getTarget()).getParent();
            // make sure first shape in group is the reference
            referenceShape = (Shape) selectedSegment.getChildren().get(0);
            if (referenceShape instanceof Arc) {
                Arc arc = (Arc) referenceShape;
                tfLength.setText(converter.toString(Double.valueOf(arc.getLength())));
                tfRadius.setText(converter.toString(Double.valueOf(arc.getRadiusX())));
            } else {
                Line line = (Line) referenceShape;
                Point2D pt = new Point2D(line.getStartX(), line.getStartY());
                tfLength.setText(converter.toString((int)(pt.distance(line.getEndX(), line.getEndY()))));
                tfRadius.setText("");
            }
        }
    }

    @FXML
    public void onMouseDragged(MouseEvent event) {
        if (selectedSegment != null) {
            selectedSegment.setTranslateX(event.getX() - anchor.getX());
            selectedSegment.setTranslateY(event.getY() - anchor.getY());
        }
    }

    @FXML
    public void onMouseReleased(MouseEvent event) {
        if (selectedSegment != null) {
            selectedSegment.setLayoutX(selectedSegment.getLayoutX() + event.getX() - anchor.getX());
            selectedSegment.setLayoutY(selectedSegment.getLayoutY() + event.getY() - anchor.getY());
            selectedSegment.setTranslateX(0);
            selectedSegment.setTranslateY(0);
            onTrackGroupChanged();
        }
    }

    @FXML
    public void onMouseClickedTrackPane(MouseEvent event) {
        LOG.debug("onMouseClickedTrackPane " + event);
        selectedTrackPane = (Pane) event.getSource();
        trackGroup.getChildren().clear();
        referenceShape = null;
        if (selectedTrackPane.getChildren().size() == 1) {
            Group tg = (Group) selectedTrackPane.getChildren().get(0);
            tg.getChildren().forEach(c -> {
                if (c instanceof Line) {
                    Group g = makeStraightGroup((Line) c);
                    trackGroup.getChildren().add(g);
                } else if (c instanceof Arc) {
                    Group g = makeCurveGroup((Arc) c);
                    trackGroup.getChildren().add(g);
                }
            });
        }
    }

    @FXML
    protected void onFlipButton(ActionEvent event) {
        if (referenceShape != null && referenceShape instanceof Arc) {
            Arc arc = (Arc) referenceShape;
            LOG.debug("flipping " + arc);
            if (arc.getCenterX() == 0.0) {
            } else {
                //arc.setCenterX(0.0);
            }
            if (arc.getStartAngle() == 0.0) {
                arc.setStartAngle(180.0 - arc.getLength());
                arc.setCenterX(arc.getRadiusX());
            } else {
                arc.setStartAngle(0.0);
                arc.setCenterX(-arc.getRadiusX());
            }
            onTrackGroupChanged();
        }
    }

    public void lengthTextChanged(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        // not enough to compare new and old values
        // property is set when user selects track
        // which fires this listener
        // need to compare values instead
        // unless there is a way to ignore this once
        if (referenceShape != null) {
            double lengthText = converter.fromString(newValue).doubleValue();
            if (referenceShape instanceof Arc) {
                Arc arc = (Arc) referenceShape;
                if (Math.abs(lengthText - arc.getLength()) > 0.5) {
                    arc.setLength(lengthText);
                }
            } else {
                Line line = (Line) referenceShape;
                Point2D pt = new Point2D(line.getEndX(), line.getEndY());
                double lengthLine = pt.distance(line.getStartX(), line.getStartY());
                double lengthDiff = lengthText - lengthLine;
                if (Math.abs(lengthDiff) > 0.5) {
                    // one point should be pinned, so modify "end" point
                    Point2D newEnd = pt.subtract(line.getStartX(), line.getStartY())
                            .normalize().multiply(lengthDiff)
                            .add(line.getEndX(), line.getEndY());
                    line.setEndX(newEnd.getX());
                    line.setEndY(newEnd.getY());
                }
            }
            onTrackGroupChanged();
        }
    }

    public void radiusTextChanged(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        if (referenceShape != null) {
            if (referenceShape instanceof Arc) {
                Arc arc = (Arc) referenceShape;
                double radiusText = converter.fromString(newValue).doubleValue();
                double radiusDiff = radiusText - arc.getRadiusX();
                if (Math.abs(radiusDiff) > 0.5) {
                    arc.setRadiusX(radiusText);
                    arc.setRadiusY(radiusText);
                    arc.setLayoutX(arc.getLayoutX() - radiusDiff);
                }
                // no radius for lines
                onTrackGroupChanged();
            }
        }
    }

    private void onTrackGroupChanged() {
        if (selectedTrackPane == null) {
            LOG.debug("onTrackGroupChanged - nothing selected");
            return;
        }
        LOG.debug("onTrackGroupChanged");
        selectedTrackPane.getChildren().clear();
        Group tg = new Group();
        selectedTrackPane.getChildren().add(tg);
        List<TrackSegment> tracks = new ArrayList<>();
        trackGroup.getChildren().forEach(c -> {
            // top level children are Group
            if (c instanceof Group) {
                Group g = Group.class.cast(c);
                // first child in Group is Shape
                Shape s = (Shape) g.getChildren().get(0);
                tg.getChildren().add(copyShape(s));
                tracks.add(Tracks.trackSegmentFromShape(s));
            }
        });
        TrackDefinition td = (TrackDefinition) selectedTrackPane.getUserData();
        td.setTracks(tracks);
    }

    public Shape copyShape(Shape input) {
        LOG.debug("copyShape " + input);
        if (input instanceof Line) {
            Line ol = (Line) input;
            Line nl = new Line();
            nl.setLayoutX(ol.getLayoutX());
            nl.setLayoutY(ol.getLayoutY());
            nl.setStartX(ol.getStartX());
            nl.setStartY(ol.getStartY());
            nl.setEndX(ol.getEndX());
            nl.setEndY(ol.getEndY());
            nl.setStroke(ol.getStroke());
            nl.setStrokeWidth(ol.getStrokeWidth());
            nl.setStrokeLineCap(ol.getStrokeLineCap());
            return nl;
        } else if (input instanceof Arc) {
            Arc oa = (Arc) input;
            Arc na = new Arc();
            na.setLayoutX(oa.getLayoutX());
            na.setLayoutY(oa.getLayoutY());
            na.setRadiusX(oa.getRadiusX());
            na.setRadiusY(oa.getRadiusY());
            na.setStartAngle(oa.getStartAngle());
            na.setLength(oa.getLength());
            na.setType(oa.getType());
            na.setStroke(oa.getStroke());
            na.setStrokeWidth(oa.getStrokeWidth());
            na.setStrokeLineCap(oa.getStrokeLineCap());
            return na;
        }
        return null;
    }

    private Group makeStraightGroup(Line lineTemplate) {
        LOG.debug("makeStraightGroup " + lineTemplate);
        Group group = new Group();
        group.setUserData("Straight");
        Line line = new Line();
        if (lineTemplate == null) {
            line.setStartX(0.0);
            line.setStartY(0.0);
            line.setEndX(0.0);
            line.setEndY(-200.0);
        } else {
            line.setStartX(lineTemplate.getStartX());
            line.setStartY(lineTemplate.getStartY());
            line.setEndX(lineTemplate.getEndX());
            line.setEndY(lineTemplate.getEndY());
        }
        line.setStrokeWidth(15.0);
        line.setStroke(Color.BLACK);
        line.setStrokeLineCap(StrokeLineCap.BUTT);
        group.getChildren().add(line);
//        line.setStartX(100.0);
//        line.setStartY(100.0);
//        line.setEndX(100.0);
//        line.setEndY(500.0);
//        line.setStrokeWidth(5.0);
//        line.setStroke(Color.BLACK);
//        group.getChildren().add(line);
//        line = new Line();
//        line.setStartX(200.0);
//        line.setStartY(100.0);
//        line.setEndX(200.0);
//        line.setEndY(500.0);
//        line.setStrokeWidth(5.0);
//        line.setStroke(Color.BLACK);
//        group.getChildren().add(line);
//        Rectangle rectangle = new Rectangle();
//        rectangle.setLayoutX(100.0);
//        rectangle.setLayoutY(100.0);
//        rectangle.setHeight(400.0);
//        rectangle.setWidth(100.0);
//        group.getChildren().add(rectangle);
        return group;
    }

    private Group makeCurveGroup(Arc arcTemplate) {
        LOG.debug("makeCurveGroup " + arcTemplate);
        Group group = new Group();
        group.setUserData("Curve");
        Arc arc = new Arc();
        if (arcTemplate == null) {
            //arc.setCenterX(10.0);
            //arc.setCenterY(10.0);
            arc.setLayoutX(-500.0);
            arc.setLayoutY(0.0);
            arc.setRadiusX(500.0);
            arc.setRadiusY(500.0);
            arc.setStartAngle(0);
            arc.setLength(45.0);
        } else {
            //arc.setCenterX(arcTemplate.getCenterX());
            //arc.setCenterY(arcTemplate.getCenterY());
            arc.setLayoutX(arcTemplate.getLayoutX());
            arc.setLayoutY(arcTemplate.getLayoutY());
            arc.setRadiusX(arcTemplate.getRadiusX());
            arc.setRadiusY(arcTemplate.getRadiusY());
            arc.setStartAngle(arcTemplate.getStartAngle());
            arc.setLength(arcTemplate.getLength());
        }
        arc.setType(ArcType.OPEN);
        arc.setStrokeWidth(15.0);
        arc.setStroke(Color.BLACK);
        arc.setFill(Color.TRANSPARENT);
        arc.setStrokeLineCap(StrokeLineCap.BUTT);
        group.getChildren().add(arc);
        return group;
    }

    private Group makeEndpointGroup() {
        Group group = new Group();
        group.setUserData("Endpoint");

        Line line = new Line();
        line.setStartX(0.0);
        line.setStartY(0.0);
        line.setEndX(0.0);
        line.setEndY(20.0);
        line.setStrokeWidth(2.0);
        line.setStroke(Color.BLUE);
        group.getChildren().add(line);
        line = new Line();
        line.setStartX(-10.0);
        line.setStartY(0.0);
        line.setEndX(10.0);
        line.setEndY(0.0);
        line.setStrokeWidth(2.0);
        line.setStroke(Color.BLUE);
        group.getChildren().add(line);

        return group;
    }

    private void saveObjectToFile(Object data, File file) {
        if (file != null && data != null) {
            try {
                Yaml yaml = new Yaml();
                FileWriter writer = new FileWriter(file);
                yaml.dump(data, writer);
            } catch (Exception e) {
                LOG.error("Error saving " + file, e);
                showError("Error saving " + file, e);
            }
        }
    }

    private void showError(String message, Exception exception) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Exception Dialog");
        alert.setHeaderText(message);
        alert.setContentText(exception.getLocalizedMessage());

        // Create expandable Exception.
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        exception.printStackTrace(pw);
        String exceptionText = sw.toString();

        Label label = new Label("The exception stacktrace was:");

        TextArea textArea = new TextArea(exceptionText);
        textArea.setEditable(false);
        textArea.setWrapText(true);

        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(label, 0, 0);
        expContent.add(textArea, 0, 1);

        // Set expandable Exception into the dialog pane.
        alert.getDialogPane().setExpandableContent(expContent);

        alert.showAndWait();
    }
}
