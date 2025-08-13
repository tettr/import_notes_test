package com.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@Table(name = "patient_note")
public class PatientNote {
    @jakarta.persistence.Id
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime createdDateTime;

    private LocalDateTime lastModifiedDateTime;

    @JsonIgnore
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="created_by_user_id")
    private CompanyUser createdByUserId;

    @JsonIgnore
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="last_modified_by_user_id")
    private CompanyUser lastModifiedByUserId;

    @Column(length=4000)
    private String note;

    private String oldNotesGuid;

    @JsonIgnore
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="patient_profile_id")
    private PatientProfile patientProfileId;

}