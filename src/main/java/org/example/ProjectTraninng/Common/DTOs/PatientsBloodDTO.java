package org.example.ProjectTraninng.Common.DTOs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.ProjectTraninng.Common.Enums.BloodTypes;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PatientsBloodDTO {
    private Long id;
    private int quantity;
    private PatientDTO patient;
    private BloodTypes bloodType;
}
