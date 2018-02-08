package org.tracks;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.InputMethodEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
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
    @FXML
    ToggleGroup trackType;

    private NumberStringConverter converter = new NumberStringConverter();
    private List<Group> groups;
    private Group selectedGroup;
    private Shape referenceShape;
    private Point2D anchor;

    @FXML
    public void initialize() throws Exception {
        tfLength.textProperty().addListener(this::lengthTextChanged);
        tfRadius.textProperty().addListener(this::radiusTextChanged);
    }

    @FXML
    protected void onAddStraight(ActionEvent event) {
        Group group = makeStraightGroup();
        trackGroup.getChildren().add(group);
    }

    @FXML
    protected void onAddCurve(ActionEvent event) {
        Group group = makeCurveGroup();
        trackGroup.getChildren().add(group);
    }

    @FXML
    public void onMousePressed(MouseEvent event) {
        System.out.println(event);
        anchor = new Point2D(event.getX(), event.getY());
        selectedGroup = null;
        referenceShape = null;
        if (Shape.class.isInstance(event.getTarget())) {
            selectedGroup = (Group) ((Shape) event.getTarget()).getParent();
            // make sure first shape in group is the reference
            referenceShape = (Shape) selectedGroup.getChildren().get(0);
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
        if (selectedGroup != null) {
            selectedGroup.setTranslateX(event.getX() - anchor.getX());
            selectedGroup.setTranslateY(event.getY() - anchor.getY());
        }
    }

    @FXML
    public void onMouseReleased(MouseEvent event) {
        if (selectedGroup != null) {
            selectedGroup.setLayoutX(selectedGroup.getLayoutX() + event.getX() - anchor.getX());
            selectedGroup.setLayoutY(selectedGroup.getLayoutY() + event.getY() - anchor.getY());
            selectedGroup.setTranslateX(0);
            selectedGroup.setTranslateY(0);
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
        }
    }

    public void radiusTextChanged(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        if (referenceShape != null) {
            if (referenceShape instanceof Arc) {
                Arc arc = (Arc) referenceShape;
                double radiusText = converter.fromString(newValue).doubleValue();
                if (Math.abs(radiusText - arc.getRadiusX()) > 0.5) {
                    arc.setRadiusX(radiusText);
                    arc.setRadiusY(radiusText);
                }
                // no radius for lines
            }
        }
    }

    @FXML
    protected void trackTypeAction(ActionEvent event) {
        LOG.debug(event.toString());
        RadioButton chk = (RadioButton) trackType.getSelectedToggle();
        LOG.debug(chk.toString());
    }

    private Group makeStraightGroup() {
        Group group = new Group();
        group.setUserData("Straight");
        Line line = new Line();
        line.setStartX(10.0);
        line.setStartY(-10.0);
        line.setEndX(10.0);
        line.setEndY(210.0);
        line.setStrokeWidth(15.0);
        line.setStroke(Color.BLACK);
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

    private Group makeCurveGroup() {
        Group group = new Group();
        group.setUserData("Curve");
        Arc arc = new Arc();
        //arc.setCenterX(10.0);
        //arc.setCenterY(10.0);
        arc.setLayoutX(-500.0);
        arc.setLayoutY(210.0);
        arc.setRadiusX(500.0);
        arc.setRadiusY(500.0);
        arc.setType(ArcType.OPEN);
        arc.setStrokeWidth(15.0);
        arc.setStroke(Color.BLACK);
        arc.setFill(Color.TRANSPARENT);
        arc.setStartAngle(0);
        arc.setLength(45.0);
        group.getChildren().add(arc);
        return group;
    }

}
