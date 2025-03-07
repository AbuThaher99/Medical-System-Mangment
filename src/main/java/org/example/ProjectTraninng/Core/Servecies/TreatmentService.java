package org.example.ProjectTraninng.Core.Servecies;

import lombok.RequiredArgsConstructor;
import org.example.ProjectTraninng.Common.Converters.DepartmentMapper;
import org.example.ProjectTraninng.Common.Converters.DoctorMapper;
import org.example.ProjectTraninng.Common.Converters.PatientMapper;
import org.example.ProjectTraninng.Common.Converters.TreatmentMapper;
import org.example.ProjectTraninng.Common.DTOs.*;
import org.example.ProjectTraninng.Common.Entities.*;
import org.example.ProjectTraninng.Common.Enums.Role;
import org.example.ProjectTraninng.Common.Responses.GeneralResponse;
import org.example.ProjectTraninng.Core.Repsitories.*;
import org.example.ProjectTraninng.WebApi.Exceptions.UserNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class TreatmentService {
    private final TreatmentRepository treatmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final TreatmentDeletedRepository treatmentDeletedRepository;
    private final DeletedPatientMedicineRepository deletedPatientMedicineRepository;

    @Transactional
    public GeneralResponse createTreatment(Treatment request) throws UserNotFoundException {

        Patients patients = patientRepository.findById(request.getPatient().getId()).orElseThrow(
                () -> new UserNotFoundException("Patient not found"));

        Doctor doctor = doctorRepository.findById(request.getDoctor().getId()).orElseThrow(
                () -> new UserNotFoundException("Doctor not found"));

        Treatment treatment = Treatment.builder()
                .patient(patients)
                .doctor(doctor)
                .diseaseDescription(request.getDiseaseDescription())
                .note(request.getNote())
                .treatmentDate(request.getTreatmentDate())
                .price(request.getPrice())
                .build();
//        for (PatientMedicine patientMedicine : request.getPatientMedicines()) {
//            patientMedicine.setTreatment(treatment);
//        }
//        treatment.setPatientMedicines(request.getPatientMedicines());
        treatmentRepository.save(treatment);
        return  GeneralResponse.builder().message("Treatment created successfully").build();
    }
    @Transactional
    public GeneralResponse updateTreatment(Treatment request, Long treatmentId) throws UserNotFoundException {
        var treatmentOptional = treatmentRepository.findById(treatmentId).orElseThrow(
                () -> new UserNotFoundException("Treatment not found"));

        doctorRepository.findById(request.getDoctor().getId()).orElseThrow(
                () -> new UserNotFoundException("Doctor not found"));

        patientRepository.findById(request.getPatient().getId()).orElseThrow(
                () -> new UserNotFoundException("Patient not found"));


        Treatment treatment = treatmentOptional;
        treatment.setPatient(request.getPatient());
        treatment.setDoctor(request.getDoctor());
        treatment.setDiseaseDescription(request.getDiseaseDescription());
        treatment.setNote(request.getNote());
//        for (PatientMedicine patientMedicine : request.getPatientMedicines()) {
//            patientMedicine.setTreatment(treatment);
//        }
//        treatment.setPatientMedicines(request.getPatientMedicines());

        treatmentRepository.save(treatment);
        return GeneralResponse.builder().message("Treatment updated successfully").build();
    }

    public GeneralResponse deleteTreatment(Long treatmentId) throws UserNotFoundException {
        var treatmentOptional = treatmentRepository.findById(treatmentId).orElseThrow(
                () -> new UserNotFoundException("Treatment not found"));

        Treatment treatment = treatmentOptional;

        TreatmentDeleted treatmentDeleted = TreatmentDeleted.builder()
                .doctor(treatment.getDoctor())
                .diseaseDescription(treatment.getDiseaseDescription())
                .note(treatment.getNote())
                .treatmentDate(treatment.getTreatmentDate())
                .treatmentDeletedId(treatment.getId())
                .build();

        treatmentDeletedRepository.save(treatmentDeleted);

        List<DeletedPatientMedicine> deletedPatientMedicines = new ArrayList<>();
        for (PatientMedicine patientMedicine : treatment.getPatientMedicines()) {
            DeletedPatientMedicine deletedPatientMedicine = DeletedPatientMedicine.builder()
                    .quantity(patientMedicine.getQuantity())
                    .price(patientMedicine.getPrice())
                    .treatmentDeleted(treatmentDeleted)
                    .medicine(patientMedicine.getMedicine())
                    .build();
            deletedPatientMedicines.add(deletedPatientMedicine);
        }
        deletedPatientMedicineRepository.saveAll(deletedPatientMedicines);

        treatmentRepository.delete(treatmentOptional);

        return GeneralResponse.builder().message("Treatment and associated patient medicines deleted successfully").build();
    }

    @Transactional
    public Treatment getTreatment(Long treatmentId) throws UserNotFoundException {
        var treatmentOptional = treatmentRepository.findById(treatmentId).orElseThrow(
                () -> new UserNotFoundException("Treatment not found"));
        Treatment treatment = treatmentOptional;
        return Treatment.builder()
                .patient(treatment.getPatient())
                .doctor(treatment.getDoctor())
                .diseaseDescription(treatment.getDiseaseDescription())
                .note(treatment.getNote())
                .build();
    }
    @Transactional
    public PaginationDTO<TreatmentDTO> getAllTreatments(int page, int size, List<Long> patientIds, Long patientId, String search, User user) {
        if (page < 1) {
            page = 1;
        }
        if (patientIds != null && patientIds.isEmpty()) {
            patientIds = null;
        }
        if (search != null && search.isEmpty()) {
            search = null;
        }
        if (patientId != null && patientId == 0) {
            patientId = null;
        }

        Long doctorId = null;
        if (user.getRole() == Role.DOCTOR) {  // Ensure this matches your actual role enum
            doctorId = user.getDoctor().getId(); // Retrieve doctor's ID
        }

        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Treatment> treatments = treatmentRepository.findAll(pageable, patientIds, patientId, search, doctorId);
        List<TreatmentDTO> treatmentDTOs = treatments.getContent().stream()
                .map(TreatmentMapper::toDTO)
                .collect(Collectors.toList());

        PaginationDTO<TreatmentDTO> paginationDTO = new PaginationDTO<>();
        paginationDTO.setTotalElements(treatments.getTotalElements());
        paginationDTO.setTotalPages(treatments.getTotalPages());
        paginationDTO.setSize(treatments.getSize());
        paginationDTO.setNumber(treatments.getNumber() + 1);
        paginationDTO.setNumberOfElements(treatments.getNumberOfElements());
        paginationDTO.setContent(treatmentDTOs);

        return paginationDTO;
    }


    @Transactional
    public PaginationDTO<PatientDTO> getAllPatient(int page, int size) {
        if (page < 1) {
            page = 1;
        }

        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Patients> treatments = treatmentRepository.findAllPatient(pageable);
        List<PatientDTO> departmentDTOs = treatments.getContent().stream()
                .map(PatientMapper::toDTO)
                .collect(Collectors.toList());
        PaginationDTO<PatientDTO> paginationDTO = new PaginationDTO<>();
        paginationDTO.setTotalElements(treatments.getTotalElements());
        paginationDTO.setTotalPages(treatments.getTotalPages());
        paginationDTO.setSize(treatments.getSize());
        paginationDTO.setNumber(treatments.getNumber() + 1);
        paginationDTO.setNumberOfElements(treatments.getNumberOfElements());
        paginationDTO.setContent(departmentDTOs);

        return paginationDTO;
    }

    @Transactional
    public PaginationDTO<DoctorDTO> getAllDoctor(int page, int size) {
        if (page < 1) {
            page = 1;
        }

        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Doctor> treatments = treatmentRepository.findAllDoctor(pageable);
        List<DoctorDTO> departmentDTOs = treatments.getContent().stream()
                .map(DoctorMapper::toDTO)
                .collect(Collectors.toList());
        PaginationDTO<DoctorDTO> paginationDTO = new PaginationDTO<>();
        paginationDTO.setTotalElements(treatments.getTotalElements());
        paginationDTO.setTotalPages(treatments.getTotalPages());
        paginationDTO.setSize(treatments.getSize());
        paginationDTO.setNumber(treatments.getNumber() + 1);
        paginationDTO.setNumberOfElements(treatments.getNumberOfElements());
        paginationDTO.setContent(departmentDTOs);

        return paginationDTO;
    }

    @Transactional
    public PaginationDTO<TreatmentDTO> getAllTreatmentsForPatient(Long patientId,int page ,int size) throws UserNotFoundException {
        patientRepository.findById(patientId).orElseThrow(
                () -> new UserNotFoundException("Patient not found"));
        if (page < 1) {
            page = 1;
        }
        Pageable pageable = PageRequest.of(page - 1, size);


        Page<Treatment> treatments  = treatmentRepository.findAllByPatientId(patientId, pageable);

        List<TreatmentDTO> treatmentDTOs = treatments.getContent().stream()
                .map(TreatmentMapper::toDTO)
                .collect(Collectors.toList());

        PaginationDTO<TreatmentDTO> paginationDTO = new PaginationDTO<>();
        paginationDTO.setTotalElements(treatments.getTotalElements());
        paginationDTO.setTotalPages(treatments.getTotalPages());
        paginationDTO.setSize(treatments.getSize());
        paginationDTO.setNumber(treatments.getNumber() + 1);
        paginationDTO.setNumberOfElements(treatments.getNumberOfElements());
        paginationDTO.setContent(treatmentDTOs);

        return paginationDTO;
    }
}
