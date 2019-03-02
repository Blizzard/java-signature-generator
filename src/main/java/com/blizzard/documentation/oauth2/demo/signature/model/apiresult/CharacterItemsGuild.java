package com.blizzard.documentation.oauth2.demo.signature.model.apiresult;

import com.blizzard.documentation.oauth2.demo.signature.model.apiresult.character.Guild;
import com.blizzard.documentation.oauth2.demo.signature.model.apiresult.character.Items;
import lombok.Data;

/**
 * Expands on the base {@link Character} class and adds support for {@link Items} and {@link Guild} instances.
 */
@Data
public class CharacterItemsGuild extends Character {
    private Items items;
    private Guild guild;
}
