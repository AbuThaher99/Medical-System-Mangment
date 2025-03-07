package org.example.ProjectTraninng.Common.DTOs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PatientMedicineDTO {
    private Long id;
    private int quantity;
    private double price;
    private TreatmentDTO treatment;
    private MedicineDTO medicine;
}
