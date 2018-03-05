package org.tracks.model;

public class TrackSegment {
    private double startX;
    private double startY;
    private double endX;
    private double endY;
    private double centerX;
    private double centerY;

    public double getStartX() {
        return startX;
    }

    public void setStartX(double startX) {
        this.startX = startX;
    }

    public double getStartY() {
        return startY;
    }

    public void setStartY(double startY) {
        this.startY = startY;
    }

    public double getEndX() {
        return endX;
    }

    public void setEndX(double endX) {
        this.endX = endX;
    }

    public double getEndY() {
        return endY;
    }

    public void setEndY(double endY) {
        this.endY = endY;
    }

    public double getCenterX() {
        return centerX;
    }

    public void setCenterX(double centerX) {
        this.centerX = centerX;
    }

    public double getCenterY() {
        return centerY;
    }

    public void setCenterY(double centerY) {
        this.centerY = centerY;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("TrackSegment [startX=");
        builder.append(startX);
        builder.append(", startY=");
        builder.append(startY);
        builder.append(", endX=");
        builder.append(endX);
        builder.append(", endY=");
        builder.append(endY);
        builder.append(", centerX=");
        builder.append(centerX);
        builder.append(", centerY=");
        builder.append(centerY);
        builder.append("]");
        return builder.toString();
    }

}
