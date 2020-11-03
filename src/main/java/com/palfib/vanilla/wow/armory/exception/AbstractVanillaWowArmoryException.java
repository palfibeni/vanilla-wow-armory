package com.palfib.vanilla.wow.armory.exception;

import org.slf4j.Logger;

public class AbstractVanillaWowArmoryException extends Exception {

    public AbstractVanillaWowArmoryException(final Logger log, final String message) {
        super(message);
        log.error(message);
    }
}
