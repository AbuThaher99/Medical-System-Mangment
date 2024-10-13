package org.example.ProjectTraninng.Core.Servecies;

import lombok.RequiredArgsConstructor;
import org.example.ProjectTraninng.Common.DTOs.PaginationDTO;
import org.example.ProjectTraninng.Common.Entities.*;
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
        for (PatientMedicine patientMedicine : request.getPatientMedicines()) {
            patientMedicine.setTreatment(treatment);
        }
        treatment.setPatientMedicines(request.getPatientMedicines());

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
    public PaginationDTO<Treatment> getAllTreatments(int page, int size,List<Long> patientIds ,Long patientId,String search) {
        if (page < 1) {
            page = 1;
        }
        if (patientIds != null && patientIds.isEmpty()) {
            patientIds = null;
        }
        if (search != null && search.isEmpty()) {
            search = null;
        }
        if(patientId != null && patientId == 0){
            patientId = null;
        }
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Treatment> treatments = treatmentRepository.findAll(pageable,patientIds,patientId,search);
        PaginationDTO<Treatment> paginationDTO = new PaginationDTO<>();
        paginationDTO.setTotalElements(treatments.getTotalElements());
        paginationDTO.setTotalPages(treatments.getTotalPages());
        paginationDTO.setSize(treatments.getSize());
        paginationDTO.setNumber(treatments.getNumber() + 1);
        paginationDTO.setNumberOfElements(treatments.getNumberOfElements());
        paginationDTO.setContent(treatments.getContent());

        return paginationDTO;
    }

    @Transactional
    public Page<Treatment> getAllTreatmentsForPatient(Long patientId,int page ,int size) throws UserNotFoundException {
        patientRepository.findById(patientId).orElseThrow(
                () -> new UserNotFoundException("Patient not found"));
        if (page < 1) {
            page = 1;
        }
        Pageable pageable = PageRequest.of(page - 1, size);

        return treatmentRepository.findAllByPatientId(patientId, pageable);
    }
}
