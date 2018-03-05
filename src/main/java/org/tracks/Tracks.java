package org.tracks;

import org.tracks.model.TrackSegment;

import javafx.scene.shape.Arc;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;

public class Tracks {
    public static TrackSegment trackSegmentFromShape(Shape shape) {
        TrackSegment ts = null;
        if (shape instanceof Line) {
            Line l = (Line) shape;
            
        } else if (shape instanceof Arc) {
            Arc a = (Arc) shape;
            
        }
        return ts;
    }
}
