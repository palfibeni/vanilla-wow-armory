package com.palfib.vanilla.wow.armory.repository;

import com.palfib.vanilla.wow.armory.data.entity.Character;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface CharacterRepository extends JpaRepository<Character, Long>, QueryByExampleExecutor<Character> {
}
