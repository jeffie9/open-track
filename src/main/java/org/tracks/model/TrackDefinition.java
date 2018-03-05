package org.tracks.model;

import java.util.ArrayList;
import java.util.List;

public class TrackDefinition {
    private String id;
    private String description;
    private List<TrackSegment> tracks = new ArrayList<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<TrackSegment> getTracks() {
        return tracks;
    }

    public void setTracks(List<TrackSegment> tracks) {
        this.tracks = tracks;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("TrackDefinition [id=");
        builder.append(id);
        builder.append(", description=");
        builder.append(description);
        builder.append(", tracks=");
        builder.append(tracks);
        builder.append("]");
        return builder.toString();
    }

}
