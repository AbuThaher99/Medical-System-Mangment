package org.example.ProjectTraninng.Common.Converters;

import org.example.ProjectTraninng.Common.DTOs.*;
import org.example.ProjectTraninng.Common.Entities.*;

public class PatientsBloodMapper {
    public static PatientsBloodDTO toDTO(PatientsBlood donation) {
        if (donation == null) {
            return null;
        }
        return PatientsBloodDTO.builder()
                .id(donation.getId())
                .quantity(donation.getQuantity())
                .patient(PatientMapper.toDTO(donation.getPatients()))
                .bloodType(donation.getPatientsBlood().getType())
                .build();
    }
}
