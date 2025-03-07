package org.example.ProjectTraninng.Common.DTOs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.ProjectTraninng.Common.Entities.Medicine;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WarehosueDTO {
    private Long id;
    private int quantity;
    private Medicine medicine;
}
