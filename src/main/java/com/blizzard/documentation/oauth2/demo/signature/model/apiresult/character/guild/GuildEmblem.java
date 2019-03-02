package com.blizzard.documentation.oauth2.demo.signature.model.apiresult.character.guild;

import lombok.Data;

/**
 * Used to support the {@link com.blizzard.documentation.oauth2.demo.signature.model.apiresult.character.Guild} model.
 */
@Data
public class GuildEmblem {
    private Integer icon;
    private String iconColor;               // hex
    private Integer iconColorId;
    private Integer border;
    private String borderColor;             // hex
    private Integer borderColorId;
    private String backgroundColor;         // hex
    private Integer backgroundColorId;
}
