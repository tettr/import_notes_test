package com.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

@Entity
@Setter
@Getter
@Table(name = "old_client_guid")
public class OldClientGuid {
    @Id
    @jakarta.persistence.Id
    @Column(insertable=false, updatable=false)
    private String oldClientGuid;

    @JsonIgnore
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="patient_profile_id")
    private PatientProfile patientProfileId;
}