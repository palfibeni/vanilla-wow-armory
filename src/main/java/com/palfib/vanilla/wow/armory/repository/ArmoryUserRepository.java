package com.palfib.vanilla.wow.armory.repository;

import com.palfib.vanilla.wow.armory.data.entity.ArmoryUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ArmoryUserRepository extends JpaRepository<ArmoryUser, Long>, QueryByExampleExecutor<ArmoryUser> {
}
