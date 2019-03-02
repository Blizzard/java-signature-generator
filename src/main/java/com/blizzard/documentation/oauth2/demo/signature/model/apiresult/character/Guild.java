package com.blizzard.documentation.oauth2.demo.signature.model.apiresult.character;

import com.blizzard.documentation.oauth2.demo.signature.model.apiresult.character.guild.GuildEmblem;
import lombok.Data;

/**
 * An optional piece of data that can be retrieved when fetching character data.
 *
 * Contains information about a character's guild.
 */
@Data
public class Guild {
    private String name;
    private String realm;
    private String battlegroup;
    private Integer members;
    private Integer achievementPoints;
    private GuildEmblem emblem;
}
