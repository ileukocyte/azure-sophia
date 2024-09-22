package io.ileukocyte.azuresophia.database;

import io.ileukocyte.azuresophia.entities.Place;
import io.ileukocyte.azuresophia.entities.PlanAttachment;
import io.ileukocyte.azuresophia.entities.Tip;

import javafx.scene.image.Image;

import java.util.HashMap;
import java.util.Map;

/**
 * A class containing methods to operate plan attachment data
 *
 * @see PlanAttachment
 * @see Place
 * @see PlaceDatabase
 * @see Tip
 * @see TipDatabase
 */
public class AttachmentDatabase {
    public static final Map<String, Image> ATTACHMENT_IMAGES = new HashMap<>();

    /**
     * @param attachment the plan attachment to get an image for
     *
     * @return a cached image of a plan attachment
     */
    public static Image get(PlanAttachment attachment) {
        return ATTACHMENT_IMAGES.get(attachment.getId());
    }
}
