package io.bcn.springConference.repository;

import io.bcn.springConference.model.Speaker;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SpeakerRepository extends JpaRepository<Speaker, UUID> {
}