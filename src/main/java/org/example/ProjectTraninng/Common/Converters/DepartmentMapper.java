package org.example.ProjectTraninng.Common.Converters;

import org.example.ProjectTraninng.Common.DTOs.DepartmentDTO;
import org.example.ProjectTraninng.Common.Entities.Department;

public class DepartmentMapper {
    public static DepartmentDTO toDTO(Department department) {
        return DepartmentDTO.builder()
                .id(department.getId())
                .name(department.getName())
                .headId(UserMapper.toDTO(department.getHeadId()))
                .secretaryId(UserMapper.toDTO(department.getSecretaryId()))
                .build();
    }
}