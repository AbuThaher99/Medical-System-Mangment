package org.example.ProjectTraninng.Common.DTOs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TreatmentDTO {
    private Long id;
    private String diseaseDescription;
    private String note;
    private Date treatmentDate;
    private double price;
    private PatientDTO patient;
    private DoctorDTO doctor;
    private boolean isRated;

}
