package com.blizzard.documentation.oauth2.demo.signature.model.apiresult.character.item.azerite;

import lombok.Data;

@Data
public class AzeritePower {
    private Integer id, tier;
    private Long spellId;
    private Long bonusListId;
}
