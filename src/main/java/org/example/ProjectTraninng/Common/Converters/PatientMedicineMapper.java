package org.example.ProjectTraninng.Common.Converters;

import org.example.ProjectTraninng.Common.DTOs.PatientMedicineDTO;
import org.example.ProjectTraninng.Common.Entities.PatientMedicine;

public class PatientMedicineMapper {
    public static PatientMedicineDTO toDTO(PatientMedicine user) {
        if (user == null) {
            return null;
        }
        return PatientMedicineDTO.builder()
                .id(user.getId())
                .quantity(user.getQuantity())
                .price(user.getPrice())
                .medicine(MedicineMapper.toDTO(user.getMedicine()))
                .treatment(TreatmentMapper.toDTO(user.getTreatment()))
                .build();
    }
}
