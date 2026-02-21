package org.spon.uspehliftbot.repository;

import org.jspecify.annotations.NonNull;
import org.spon.uspehliftbot.entity.Settings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SettingsRepository extends JpaRepository<Settings, Integer> {

    @NonNull Optional<Settings> findById(Integer id);

}
