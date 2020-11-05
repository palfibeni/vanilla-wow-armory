package com.palfib.vanilla.wow.armory.data.entity;

import com.palfib.vanilla.wow.armory.data.wrapper.DiscordUserWrapper;
import lombok.*;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "armory_user")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"created"})
public class ArmoryUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username")
    private String username;

    @Column(name = "discord_user_id")
    private String discordUserId;

    @Column(name = "discord_server_id")
    private String discordServerId;

    @Column(name = "created")
    private Date created;

    public ArmoryUser(final DiscordUserWrapper userWrapper) {
        this.username = userWrapper.getUsername();
        this.discordUserId = userWrapper.getDiscordUserId();
        this.discordServerId = userWrapper.getDiscordServerId();
        this.created = new Date();
    }
}
