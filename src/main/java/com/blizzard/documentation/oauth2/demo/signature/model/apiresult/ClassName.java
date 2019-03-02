package com.blizzard.documentation.oauth2.demo.signature.model.apiresult;

import lombok.Data;

import java.util.Collection;

/**
 * Used for determining the name of a given class, when specified by class id.
 */
@Data
public class ClassName {
    private Object _links;
    private Integer id;
    private String name;
    private Object genderName;
    private Object powerType;
    private Collection<Object> specializations;
    private Object media;
    private Object pvpTalentSlots;
}
