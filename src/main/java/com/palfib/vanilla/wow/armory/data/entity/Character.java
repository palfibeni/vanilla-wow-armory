package com.palfib.vanilla.wow.armory.data.entity;

import com.palfib.vanilla.wow.armory.data.enums.CharacterClass;
import com.palfib.vanilla.wow.armory.data.enums.Race;
import com.palfib.vanilla.wow.armory.data.wrapper.CharacterWrapper;
import lombok.*;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "character")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(exclude = {"created"})
public class Character {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @ManyToOne()
    @JoinColumn(name = "armory_user_id", referencedColumnName="id")
    @ToString.Exclude
    private ArmoryUser armoryUser;

    @Column(name = "name")
    private String name;

    @Column(name = "level")
    private Long level;

    @Column(name = "race")
    @Enumerated(EnumType.STRING)
    private Race race;

    @Column(name = "character_class")
    @Enumerated(EnumType.STRING)
    private CharacterClass characterClass;

    @Column(name = "created")
    private Date created;

    public Character(final ArmoryUser armoryUser, final CharacterWrapper characterWrapper) {
        this.armoryUser = armoryUser;
        this.name = characterWrapper.getName();
        this.level = characterWrapper.getLevel();
        this.race = characterWrapper.getRace();
        this.characterClass = characterWrapper.getCharacterClass();
        this.created = new Date();
    }
}
