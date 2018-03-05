package org.tracks.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;

public class TrackSerializationTest {
    @Test
    public void testDumpTracks() {
        List<TrackDefinition> tracks = new ArrayList<>();
        TrackDefinition td = new TrackDefinition();
        TrackSegment ts = new TrackSegment();
        ts.setStartX(1.0);
        ts.setStartY(2.0);
        ts.setEndX(3.0);
        ts.setEndY(4.0);
        ts.setCenterX(5.0);
        ts.setCenterY(6.0);
        td.getTracks().add(ts);
        ts = new TrackSegment();
        ts.setStartX(1.1);
        ts.setStartY(2.1);
        ts.setEndX(3.1);
        ts.setEndY(4.1);
        ts.setCenterX(5.1);
        ts.setCenterY(6.1);
        td.getTracks().add(ts);
        ts = new TrackSegment();
        ts.setStartX(1.2);
        ts.setStartY(2.2);
        ts.setEndX(3.2);
        ts.setEndY(4.2);
        ts.setCenterX(5.2);
        ts.setCenterY(6.2);
        td.getTracks().add(ts);
        tracks.add(td);

        td = new TrackDefinition();
        ts = new TrackSegment();
        ts.setStartX(11.0);
        ts.setStartY(12.0);
        ts.setEndX(13.0);
        ts.setEndY(14.0);
        ts.setCenterX(15.0);
        ts.setCenterY(16.0);
        td.getTracks().add(ts);
        ts = new TrackSegment();
        ts.setStartX(11.1);
        ts.setStartY(12.1);
        ts.setEndX(13.1);
        ts.setEndY(14.1);
        ts.setCenterX(15.1);
        ts.setCenterY(16.1);
        td.getTracks().add(ts);
        ts = new TrackSegment();
        ts.setStartX(11.2);
        ts.setStartY(12.2);
        ts.setEndX(13.2);
        ts.setEndY(14.2);
        ts.setCenterX(15.2);
        ts.setCenterY(16.2);
        td.getTracks().add(ts);
        tracks.add(td);

        Representer representer = new Representer();
        //representer.addClassTag(TrackSegment.class, new Tag("!track"));
        //representer.addClassTag(Track.class, Tag.MAP);
        DumperOptions options = new DumperOptions();
        //options.setCanonical(true);

        Yaml yaml = new Yaml(/*representer, options*/);
        StringWriter writer = new StringWriter();

        yaml.dump(tracks, writer);
        System.out.println(writer.toString());
    }

    @Test
    public void testLoadTracks() throws Exception {
        Constructor constructor = new Constructor(List.class);
        TypeDescription trackDescription = new TypeDescription(TrackSegment.class);
        trackDescription.setTag("!track");
        constructor.addTypeDescription(trackDescription);
        Yaml yaml = new Yaml(/*constructor*/);

        InputStream input = new FileInputStream(new File("src/test/resources/track_list.yaml"));
        Object data = yaml.load(input);
        System.out.println(data);
    }
}
