package org.example.ProjectTraninng.Common.DTOs;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    @NotNull(message = "Id cannot be blank")
    private Long id;
    @NotNull(message = "First name cannot be blank")
    private String firstName;
    @NotNull(message = "Last name cannot be blank")
    private String lastName;
    @NotNull(message = "Address cannot be blank")
    private String address;
    @NotNull(message = "Phone cannot be blank")
    private String phone;
    @NotNull(message = "Date of birth cannot be blank")
    private Date dateOfBirth;
}
