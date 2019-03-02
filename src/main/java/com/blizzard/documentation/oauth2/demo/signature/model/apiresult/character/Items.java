package com.blizzard.documentation.oauth2.demo.signature.model.apiresult.character;

import com.blizzard.documentation.oauth2.demo.signature.model.apiresult.character.item.Item;
import lombok.Data;

/**
 * An optional piece of data that can be retrieved when fetching character data.
 *
 * Contains a hard list of {@link Item} that can be retrieved, as well as some metadata about the item level a character
 * has equipped.
 */
@Data
public class Items {
    private Integer averageItemLevel, averageItemLevelEquipped;
    private Item head, neck, shoulder, back, chest, shirt, tabard, wrist, hands, waist, legs, feet, finger1, finger2,
        trinket1, trinket2, mainHand, offHand;
}
