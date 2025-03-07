package org.example.ProjectTraninng.Common.Converters;

import org.example.ProjectTraninng.Common.DTOs.TreatmentDTO;
import org.example.ProjectTraninng.Common.Entities.Treatment;

public class TreatmentMapper {
    public static TreatmentDTO toDTO(Treatment treatment) {
        return TreatmentDTO.builder()
                .id(treatment.getId())
                .diseaseDescription(treatment.getDiseaseDescription())
                .note(treatment.getNote())
                .treatmentDate(treatment.getTreatmentDate())
                .price(treatment.getPrice())
                .patient(PatientMapper.toDTO(treatment.getPatient()))
                .doctor(DoctorMapper.toDTO(treatment.getDoctor()))
                .isRated(treatment.isRated())
                .build();
    }
}