package com.palfib.vanilla.wow.armory.repository;

import com.palfib.vanilla.wow.armory.data.entity.CharacterTalent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface CharacterTalentRepository extends JpaRepository<CharacterTalent, Long>, QueryByExampleExecutor<CharacterTalent> {
}
