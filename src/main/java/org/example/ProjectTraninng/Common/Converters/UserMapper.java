package org.example.ProjectTraninng.Common.Converters;

import org.example.ProjectTraninng.Common.DTOs.UserDTO;
import org.example.ProjectTraninng.Common.Entities.User;

public class UserMapper {
    public static UserDTO toDTO(User user) {
        if (user == null) {
            return null;
        }
        return UserDTO.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .address(user.getAddress())
                .phone(user.getPhone())
                .dateOfBirth(user.getDateOfBirth())
                .build();
    }
}