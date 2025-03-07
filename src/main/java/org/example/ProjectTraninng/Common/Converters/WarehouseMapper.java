package org.example.ProjectTraninng.Common.Converters;

import org.example.ProjectTraninng.Common.DTOs.WarehosueDTO;
import org.example.ProjectTraninng.Common.Entities.WarehouseStore;


public class WarehouseMapper {
    public static WarehosueDTO toDTO(WarehouseStore user) {
        if (user == null) {
            return null;
        }
        return WarehosueDTO.builder()
                .id(user.getId())
                .quantity(user.getQuantity())
                .medicine(user.getMedicine())
                .build();
    }
}
