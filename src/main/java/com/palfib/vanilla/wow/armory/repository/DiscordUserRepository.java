package com.palfib.vanilla.wow.armory.repository;

import com.palfib.vanilla.wow.armory.data.entity.DiscordUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface DiscordUserRepository extends JpaRepository<DiscordUser, Long>, QueryByExampleExecutor<DiscordUser> {
}
