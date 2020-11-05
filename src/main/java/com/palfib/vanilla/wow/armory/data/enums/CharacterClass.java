package com.palfib.vanilla.wow.armory.data.enums;

import lombok.Getter;

@Getter
public enum CharacterClass {
    WARRIOR("Warrior"),
    ROGUE("Rogue"),
    HUNTER("Hunter"),
    DRUID("Druid"),
    SHAMAN("Shaman"),
    PALADIN("Paladin"),
    PRIEST("Priest"),
    MAGE("Mage"),
    WARLOCK("Warlock");

    private final String name;

    CharacterClass(final String name) {
        this.name = name;
    }

    public static CharacterClass parseAsEnum(final String str) {
        for (CharacterClass characterClass : CharacterClass.values()) {
            if (characterClass.name().equalsIgnoreCase(str))
                return characterClass;
        }
        return null;
    }
}
