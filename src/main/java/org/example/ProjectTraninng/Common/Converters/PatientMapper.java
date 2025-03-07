package org.example.ProjectTraninng.Common.Converters;

import org.example.ProjectTraninng.Common.DTOs.PatientDTO;
import org.example.ProjectTraninng.Common.Entities.Patients;

public class PatientMapper {
    public static PatientDTO toDTO(Patients doctor) {
        return PatientDTO.builder()
                .id(doctor.getId())
                .age(doctor.getAge())
                .bloodType(doctor.getBloodType().getType())
                .user(doctor.getUser())
                .build();
    }
}
