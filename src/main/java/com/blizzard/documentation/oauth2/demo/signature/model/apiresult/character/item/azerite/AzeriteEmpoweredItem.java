package com.blizzard.documentation.oauth2.demo.signature.model.apiresult.character.item.azerite;

import lombok.Data;

import java.util.Collection;

@Data
public class AzeriteEmpoweredItem {
    private Collection<AzeritePower> azeritePowers;
}
