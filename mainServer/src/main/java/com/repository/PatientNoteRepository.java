package com.repository;

import com.model.PatientNote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface PatientNoteRepository extends JpaRepository<PatientNote, Long> {
    @Query("SELECT n FROM PatientNote n WHERE n.oldNotesGuid = ?1")
    PatientNote findByGuid(String guid);
}