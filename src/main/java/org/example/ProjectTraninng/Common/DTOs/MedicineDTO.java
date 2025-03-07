package org.example.ProjectTraninng.Common.DTOs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.ProjectTraninng.Common.Entities.Supplier;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MedicineDTO {
    private Long id;
    private String name;
    private Double buyPrice;
    private Double purchasePrice;
    private Date expirationDate;
    private Supplier supplier;
}
