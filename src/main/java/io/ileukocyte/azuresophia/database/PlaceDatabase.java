package io.ileukocyte.azuresophia.database;

import io.ileukocyte.azuresophia.entities.Place;

import javafx.scene.image.Image;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * A class containing methods to operate place data
 *
 * @see Place
 * @see AttachmentDatabase
 */
public class PlaceDatabase {
    private static final String ENDPOINT = "data/places";
    private static final File ENDPOINT_FILE = new File(ENDPOINT);

    public static final Map<String, Place> PARSED_PLACES = new LinkedHashMap<>();

    public static Place parsePlace(String id) throws IOException, JSONException {
        return parsePlace(new File(ENDPOINT + "/" + id + ".json"));
    }

    public static Place parsePlace(File file) throws IOException, JSONException {
        var content = Files.readString(Paths.get(file.getAbsolutePath()));
        var json = new JSONObject(content);

        var locationJson = json.getJSONObject("location");
        var location = new Place.Location(
                locationJson.getString("humanized_address"),
                locationJson.getDouble("latitude"),
                locationJson.getDouble("longitude")
        );

        return new Place(
                json.getString("name"),
                json.getString("id"),
                json.getString("description"),
                location,
                json.getJSONArray("pictures").toList().stream().map(Object::toString).collect(Collectors.toList())
        );
    }

    public static Set<Place> parseAllPlaces() throws IOException, JSONException {
        if (!PARSED_PLACES.isEmpty()) {
            PARSED_PLACES.clear();
        }

        for (var file : Objects.requireNonNull(ENDPOINT_FILE.listFiles())) {
            var place = parsePlace(file);

            PARSED_PLACES.put(place.getId(), place);

            // image caching
            AttachmentDatabase.ATTACHMENT_IMAGES.put(
                    place.getId(),
                    new Image(place.getPictures().get(0), true)
            );
        }

        return new LinkedHashSet<>(PARSED_PLACES.values());
    }
}
