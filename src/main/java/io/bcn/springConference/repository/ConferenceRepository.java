package io.bcn.springConference.repository;

import io.bcn.springConference.model.Conference;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface ConferenceRepository extends JpaRepository<Conference, UUID> {
    List<Conference> findByDate(LocalDate date);
}