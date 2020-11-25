package com.palfib.vanilla.wow.armory.data.wrapper;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class BossDetailWrapper {
    private final String id;
    private final String name;


    public String toString(){
        return String.format("- [%s](https://classic.wowhead.com/npc=%s/%s)", name, id, name.replaceAll("['_.]", "").replaceAll(" ", "-").toLowerCase());
    }
}
