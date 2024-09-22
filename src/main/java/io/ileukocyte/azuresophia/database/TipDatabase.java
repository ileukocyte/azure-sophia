package io.ileukocyte.azuresophia.database;

import io.ileukocyte.azuresophia.entities.Place;
import io.ileukocyte.azuresophia.entities.Tip;

import javafx.scene.image.Image;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * A class containing methods to operate tip data
 *
 * @see Tip
 * @see AttachmentDatabase
 */
public class TipDatabase {
    private static final String ENDPOINT = "data/tips";
    private static final File ENDPOINT_FILE = new File(ENDPOINT);

    public static final Map<String, Tip> PARSED_TIPS = new LinkedHashMap<>();

    public static Tip parseTip(String id) throws IOException, JSONException {
        return parseTip(new File(ENDPOINT + "/" + id + ".json"));
    }

    public static Tip parseTip(File file) throws IOException, JSONException {
        var content = Files.readString(Paths.get(file.getAbsolutePath()));
        var json = new JSONObject(content);

        Place place = null;

        if (json.has("place")) {
            place = PlaceDatabase.PARSED_PLACES.get(json.getString("place"));
        }

        return new Tip(
                json.getString("title"),
                json.getString("description"),
                json.getString("id"),
                place != null && !json.has("image") ? place.getPictures().get(0) : json.getString("image"),
                place
        );
    }

    public static Set<Tip> parseAllTips() throws IOException, JSONException {
        if (!PARSED_TIPS.isEmpty()) {
            PARSED_TIPS.clear();
        }

        for (var file : Objects.requireNonNull(ENDPOINT_FILE.listFiles())) {
            var tip = parseTip(file);

            PARSED_TIPS.put(tip.getId(), tip);

            // image caching
            AttachmentDatabase.ATTACHMENT_IMAGES.put(tip.getId(), new Image(tip.getImage(), true));
        }

        return new LinkedHashSet<>(PARSED_TIPS.values());
    }
}
