package org.example.ProjectTraninng.Common.DTOs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.ProjectTraninng.Common.Entities.Donor;
import org.example.ProjectTraninng.Common.Enums.BloodTypes;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DonationDTO {
    private Long id;
    private int quantity;
    private Date donationDate;
    private BloodTypes bloodType;
    private Donor donor;
}
