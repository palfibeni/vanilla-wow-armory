package com.palfib.vanilla.wow.armory.service.common.validator;

import com.palfib.vanilla.wow.armory.data.enums.CharacterClass;
import com.palfib.vanilla.wow.armory.data.enums.Race;
import lombok.val;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class CharacterValidatorService {

    public String validateSimpleName(final String name) {
        if (StringUtils.isEmpty(name)) {
            return "Name cannot be empty!";
        }
        if (StringUtils.containsWhitespace(name)) {
            return "Name cannot contain whitespace";
        }
        if (name.length() < 2) {
            return "Name cannot have less then 2 character.";
        }
        if (name.length() > 15) {
            return "Name cannot have more then 15 character.";
        }
        return null;
    }

    public String validateRace(final String race) {
        if (Race.parseAsEnum(race) == null) {
            return "Given race is not valid.";
        }
        return null;
    }

    public String validateCharacterClass(final String characterClass) {
        if (CharacterClass.parseAsEnum(characterClass) == null) {
            return "Given class is not valid.";
        }
        return null;
    }

    public String validateLevel(final String level) {
        try {
            val longValue = Long.parseLong(level);
            if (longValue < 1) {
                return "Level cannot be less then 1.";
            }
            if (longValue > 60) {
                return "Level cannot be more then 60.";
            }
        } catch (NumberFormatException ex) {
            return "Not a valid number!";
        }
        return null;
    }
}
