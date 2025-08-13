package com.repository;

import com.model.PatientProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PatientProfileRepository extends JpaRepository<PatientProfile, Long> {
    @Query("SELECT u FROM PatientProfile u WHERE u.statusId = ?1")
    List<PatientProfile> findByStatusId(Integer statusId_);

    @Query("SELECT d.oldClientGuid FROM PatientProfile u INNER JOIN OldClientGuid d ON d.patientProfileId.id = u.id WHERE d.patientProfileId.id = ?1")
    List<String> findAllGuid(Long id_);
}