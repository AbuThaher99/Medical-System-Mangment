package org.example.ProjectTraninng.Common.Converters;

import org.example.ProjectTraninng.Common.DTOs.*;
import org.example.ProjectTraninng.Common.Entities.*;

public class DonationMapper {
    public static DonationDTO toDTO(Donation donation) {
        if (donation == null) {
            return null;
        }
        return DonationDTO.builder()
                .id(donation.getId())
                .quantity(donation.getQuantity())
                .donationDate(donation.getDonationDate())
                .bloodType(donation.getBloodType().getType())
                .donor(donation.getDonor())
                .build();
    }
}
