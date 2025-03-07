package org.example.ProjectTraninng.Core.Servecies;

import lombok.RequiredArgsConstructor;
import org.example.ProjectTraninng.Common.Converters.DepartmentMapper;
import org.example.ProjectTraninng.Common.DTOs.DepartmentDTO;
import org.example.ProjectTraninng.Common.DTOs.PaginationDTO;
import org.example.ProjectTraninng.Common.Entities.Department;
import org.example.ProjectTraninng.Common.Responses.GeneralResponse;
import org.example.ProjectTraninng.Core.Repsitories.DepartmentRepsitory;
import org.example.ProjectTraninng.WebApi.Exceptions.UserNotFoundException;
import org.example.ProjectTraninng.Common.Entities.User;
import org.example.ProjectTraninng.Core.Repsitories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DepartmentService {

    @Autowired
    private DepartmentRepsitory departmentRepository;
    @Autowired
    private  UserRepository userRepository;

    @Transactional
    public GeneralResponse addDepartment(Department request) throws UserNotFoundException {
        Optional<User> headSecretary = userRepository.findById(request.getHeadId().getId());
        if (headSecretary.isEmpty()) {
            throw new UserNotFoundException("User not found");
        }

        Optional<User> secretary = userRepository.findById(request.getSecretaryId().getId());
        if (secretary.isEmpty()) {
            throw new UserNotFoundException("User not found");
        }

        Department department = Department.builder()
                .name(request.getName())
                .headId(headSecretary.get()).
                secretaryId(secretary.get())
                .build();
        departmentRepository.save(department);
      return   GeneralResponse.builder().message("Department added successfully").build();
    }

    @Transactional
    public void updateDepartment(Department request, Long departmentId) throws UserNotFoundException {

        var department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new UserNotFoundException("Department not found"));
        department.setName(request.getName());

        User headSecretary = userRepository.findById(request.getHeadId().getId())
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        department.setHeadId(headSecretary);

        User secretary = userRepository.findById(request.getSecretaryId().getId())
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        department.setSecretaryId(secretary);
        departmentRepository.save(department);
    }

    @Transactional
    public void deleteDepartment(Long departmentId) throws UserNotFoundException {
        var department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new UserNotFoundException("Department not found"));
        department.setDeleted(true);
        departmentRepository.save(department);
    }
    @Transactional
    public Department findDepartmentById(Long departmentId) throws UserNotFoundException {
  var department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new UserNotFoundException("Department not found"));
        return department;

    }

    @Transactional
    public PaginationDTO<DepartmentDTO> getAllDepartment(int page, int size, String search) {
        if (page < 1) {
            page = 1;
        }
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Department> departments = departmentRepository.findAll(pageable, search);

        // Map Department entities to DepartmentDTOs
        List<DepartmentDTO> departmentDTOs = departments.getContent().stream()
                .map(DepartmentMapper::toDTO)
                .collect(Collectors.toList());

        // Create PaginationDTO
        PaginationDTO<DepartmentDTO> paginationDTO = new PaginationDTO<>();
        paginationDTO.setTotalElements(departments.getTotalElements());
        paginationDTO.setTotalPages(departments.getTotalPages());
        paginationDTO.setSize(departments.getSize());
        paginationDTO.setNumber(departments.getNumber() + 1);
        paginationDTO.setNumberOfElements(departments.getNumberOfElements());
        paginationDTO.setContent(departmentDTOs);

        return paginationDTO;
    }

    @Transactional
    public PaginationDTO<DepartmentDTO> getAllDeletedDepartment(int page, int size, String search) {
        if (page < 1) {
            page = 1;
        }
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Department> departments = departmentRepository.findDeletedAll(pageable, search);

        // Map Department entities to DepartmentDTOs
        List<DepartmentDTO> departmentDTOs = departments.getContent().stream()
                .map(DepartmentMapper::toDTO)
                .collect(Collectors.toList());

        // Create PaginationDTO
        PaginationDTO<DepartmentDTO> paginationDTO = new PaginationDTO<>();
        paginationDTO.setTotalElements(departments.getTotalElements());
        paginationDTO.setTotalPages(departments.getTotalPages());
        paginationDTO.setSize(departments.getSize());
        paginationDTO.setNumber(departments.getNumber() + 1);
        paginationDTO.setNumberOfElements(departments.getNumberOfElements());
        paginationDTO.setContent(departmentDTOs);

        return paginationDTO;
    }

    public void restoreDepartment(Long departmentId) throws UserNotFoundException {
        var department = departmentRepository.findDeletedById(departmentId)
                .orElseThrow(() -> new UserNotFoundException("Department not found"));
        department.setDeleted(false);
        departmentRepository.save(department);
    }
}
