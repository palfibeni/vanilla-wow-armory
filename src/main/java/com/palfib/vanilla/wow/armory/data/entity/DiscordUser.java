package com.palfib.vanilla.wow.armory.data.entity;

import com.palfib.vanilla.wow.armory.data.wrapper.DiscordUserWrapper;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "discord_user")
@Data
@EqualsAndHashCode(exclude = {"created"})
@NoArgsConstructor
public class DiscordUser {

    @Id
    private Long id;

    @Column(name = "username")
    private String username;

    @Column(name = "server_id")
    private Long serverId;

    @Column(name = "created")
    private Date created;

    @Builder
    public DiscordUser(final DiscordUserWrapper userWrapper) {
        this.id = userWrapper.getId();
        this.username = userWrapper.getUsername();
        this.serverId = userWrapper.getServerId();
        this.created = new Date();
    }
}
