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
        List<Track> tracks = new ArrayList<>();
        Track t = new Track();
        t.setStartX(1.0);
        t.setStartY(2.0);
        t.setEndX(3.0);
        t.setEndY(4.0);
        t.setCenterX(5.0);
        t.setCenterY(6.0);
        tracks.add(t);
        t = new Track();
        t.setStartX(1.1);
        t.setStartY(2.1);
        t.setEndX(3.1);
        t.setEndY(4.1);
        t.setCenterX(5.1);
        t.setCenterY(6.1);
        tracks.add(t);
        t = new Track();
        t.setStartX(1.2);
        t.setStartY(2.2);
        t.setEndX(3.2);
        t.setEndY(4.2);
        t.setCenterX(5.2);
        t.setCenterY(6.2);
        tracks.add(t);

        Representer representer = new Representer();
        representer.addClassTag(Track.class, new Tag("!track"));
        //representer.addClassTag(Track.class, Tag.MAP);
        DumperOptions options = new DumperOptions();
        //options.setCanonical(true);

        Yaml yaml = new Yaml(representer, options);
        StringWriter writer = new StringWriter();

        yaml.dump(tracks, writer);
        System.out.println(writer.toString());
    }

    @Test
    public void testLoadTracks() throws Exception {
        Constructor constructor = new Constructor(List.class);
        TypeDescription trackDescription = new TypeDescription(Track.class);
        trackDescription.setTag("!track");
        constructor.addTypeDescription(trackDescription);
        Yaml yaml = new Yaml(constructor);

        InputStream input = new FileInputStream(new File("src/test/resources/track_list.yaml"));
        Object data = yaml.load(input);
        System.out.println(data);
    }
}
