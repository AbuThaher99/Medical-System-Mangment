package org.example.ProjectTraninng.Common.Converters;

import org.example.ProjectTraninng.Common.DTOs.DoctorDTO;
import org.example.ProjectTraninng.Common.Entities.Doctor;

public class DoctorMapper {
    public static DoctorDTO toDTO(Doctor doctor) {
        return DoctorDTO.builder()
                .id(doctor.getId())
                .specialization(doctor.getSpecialization())
                .beginTime(doctor.getBeginTime())
                .endTime(doctor.getEndTime())
                .user(doctor.getUser())
                .build();
    }
}
