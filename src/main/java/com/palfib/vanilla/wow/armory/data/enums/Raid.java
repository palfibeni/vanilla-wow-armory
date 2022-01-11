package com.palfib.vanilla.wow.armory.data.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Raid {
    MOLTEN_CORE("Molten Core", "molten-core"),
    BLACKWING_LAIR("Blackwing Lair", "blackwing-lair"),
    ZUL_GURUB("Zul'Gurub", "zulgurub"),
    AQ20("Ruins of Ahn'Qiraj", "ruins-of-ahnqiraj"),
    AQ40("Ahn'Qiraj", "ahnqiraj"),
    NAXXRAMAS("Naxxramas", "naxxramas");

    private final String name;
    private final String uri;

    Raid(final String name, final String uri) {
        this.name = name;
        this.uri = uri;
    }

    public String getName() {
        return name;
    }

    public String getUri() {
        return uri;
    }

    @Override
    public String toString() {
        return name;
    }

    @JsonCreator
    public static Raid getByName(final String name){
        for(Raid item : values()){
            if(item.getName().equals(name)){
                return item;
            }
        }
        return null;
    }
}
