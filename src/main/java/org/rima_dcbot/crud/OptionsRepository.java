package org.rima_dcbot.crud;

import org.rima_dcbot.model.Options;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OptionsRepository extends JpaRepository<Options, String> {
}
