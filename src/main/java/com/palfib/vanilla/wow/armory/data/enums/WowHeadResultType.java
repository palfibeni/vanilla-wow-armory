package com.palfib.vanilla.wow.armory.data.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum WowHeadResultType {
    NPC("NPC", "npc"),
    CLASS("Class", "class"),
    RESOURCE("Resource", "resource"),
    ZONE("Zone", ""),
    QUEST("Quest", "quest"),
    ITEM_SET("Item Set", "item-set"),
    ITEM("Item", "item"),
    SPELL("Spell", "spell"),
    ITEM_APPEARANCE_SET("Item Appearance Set", "transmog-set"),
    OBJECT("Object", "object");

    private final String name;
    private final String uri;

    WowHeadResultType(final String name, final String uri) {
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
    public static WowHeadResultType getByName(final String name){
        for(WowHeadResultType item : values()){
            if(item.getName().equals(name)){
                return item;
            }
        }
        return null;
    }
}
