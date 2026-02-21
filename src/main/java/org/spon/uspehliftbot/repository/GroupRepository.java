package org.spon.uspehliftbot.repository;

import org.spon.uspehliftbot.entity.UserAction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupRepository extends JpaRepository<UserAction, Long> {

}

