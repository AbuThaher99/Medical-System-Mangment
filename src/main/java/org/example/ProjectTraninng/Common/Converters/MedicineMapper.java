package org.example.ProjectTraninng.Common.Converters;

import org.example.ProjectTraninng.Common.DTOs.MedicineDTO;
import org.example.ProjectTraninng.Common.DTOs.UserDTO;
import org.example.ProjectTraninng.Common.Entities.Medicine;
import org.example.ProjectTraninng.Common.Entities.User;

public class MedicineMapper {
    public static MedicineDTO toDTO(Medicine user) {
        if (user == null) {
            return null;
        }
        return MedicineDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .buyPrice(user.getBuyPrice())
                .purchasePrice(user.getPurchasePrice())
                .expirationDate(user.getExpirationDate())
                .supplier(user.getSupplier())
                .build();
    }
}
