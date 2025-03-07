package org.example.ProjectTraninng.Common.DTOs;

import jakarta.persistence.Convert;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.ProjectTraninng.Common.Converters.MapToJsonConverter;
import org.example.ProjectTraninng.Common.Entities.User;
import org.example.ProjectTraninng.Common.Enums.Specialization;

import java.time.LocalTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DoctorDTO {
    //@Convert(converter = MapToJsonConverter.class)
    private Long id;
    private Specialization specialization;
    private LocalTime beginTime;
    private LocalTime endTime;
    private User user;

}
