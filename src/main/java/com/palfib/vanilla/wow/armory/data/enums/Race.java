package com.palfib.vanilla.wow.armory.data.enums;

import lombok.Getter;

@Getter
public enum Race {
    HUMAN("Human", Fraction.ALLIANCE),
    DWARF("Dwarf", Fraction.ALLIANCE),
    GNOME("Gnome", Fraction.ALLIANCE),
    NIGHT_ELF("Night Elf", Fraction.ALLIANCE),
    ORC("Orc", Fraction.HORDE),
    TROLL("Troll", Fraction.HORDE),
    TAUREN("Tauren", Fraction.HORDE),
    UNDEAD("Undead", Fraction.HORDE);

    private final String name;
    private final Fraction fraction;

    Race(final String name, Fraction fraction) {
        this.name = name;
        this.fraction = fraction;
    }


    public static Race parseAsEnum(final String str) {
        for (Race race : Race.values()) {
            if (race.name().replaceAll("_", "")
                    .equalsIgnoreCase(str.replaceAll("[\\s_-]+", "")))
                return race;
        }
        return null;
    }
}
