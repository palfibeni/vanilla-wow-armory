package com.palfib.vanilla.wow.armory.data.enums;

import lombok.Getter;

@Getter
public enum Fraction {
    HORDE("Horde"),
    ALLIANCE("Alliance");
    private final String name;

    Fraction(final String name) {
        this.name = name;
    }
}
