package com.palfib.vanilla.wow.armory.data.entity;

import com.palfib.vanilla.wow.armory.data.wrapper.CharacterTalentWrapper;
import lombok.*;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "character_talent")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(exclude = {"created"})
public class CharacterTalent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @OneToOne
    @JoinColumn(name = "character_id", referencedColumnName="id")
    @ToString.Exclude
    private Character character;

    @Column(name = "name")
    private String name;

    @Column(name = "talent")
    private String talent;

    @Column(name = "created")
    private Date created;

    public CharacterTalent(final Character character, final CharacterTalentWrapper characterTalentWrapper) {
        this.character = character;
        this.name = characterTalentWrapper.getCharacterName();
        this.talent = characterTalentWrapper.getTalent();
        this.created = new Date();
    }

    public String toString(){
        return String.format("[%s](%s)", name, talent);
    }
}
