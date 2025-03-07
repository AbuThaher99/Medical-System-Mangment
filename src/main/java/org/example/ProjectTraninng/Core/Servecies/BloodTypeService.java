package org.example.ProjectTraninng.Core.Servecies;


import org.example.ProjectTraninng.Common.Converters.DepartmentMapper;
import org.example.ProjectTraninng.Common.Converters.DonationMapper;
import org.example.ProjectTraninng.Common.Converters.PatientsBloodMapper;
import org.example.ProjectTraninng.Common.DTOs.DepartmentDTO;
import org.example.ProjectTraninng.Common.DTOs.DonationDTO;
import org.example.ProjectTraninng.Common.DTOs.PaginationDTO;
import org.example.ProjectTraninng.Common.DTOs.PatientsBloodDTO;
import org.example.ProjectTraninng.Common.Entities.*;
import org.example.ProjectTraninng.Common.Enums.BloodTypes;

import org.example.ProjectTraninng.Common.Enums.Gender;
import org.example.ProjectTraninng.Common.Responses.GeneralResponse;
import org.example.ProjectTraninng.Core.Repsitories.*;
import org.example.ProjectTraninng.WebApi.Exceptions.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BloodTypeService {

    @Autowired
    private BloodTypeRepository bloodTypeRepository;

    @Autowired
    private DonationRepository donationRepository;

    @Autowired
    private DonorRepository donorRepository;

    @Autowired
    private PatientRepository patientsRepository;

    @Autowired
    private PatientsBloodRepository patientsBloodRepository;

    public void initializeBloodTypes() {
        if (bloodTypeRepository.count() == 0) {
            Arrays.stream(BloodTypes.values()).forEach(bloodTypeEnum -> {
                BloodType bloodType = BloodType.builder()
                        .type(bloodTypeEnum)
                        .quantity(0)
                        .build();
                bloodTypeRepository.save(bloodType);
            });
        }
    }

    public GeneralResponse addDonor(Donor donor) {
        donorRepository.save(donor);
        return GeneralResponse.builder().message("Donor added successfully").build();
    }

    public GeneralResponse deleteDonor(Long donorId) throws UserNotFoundException {
        Donor donor = donorRepository.findById(donorId).orElseThrow(
                () -> new UserNotFoundException("Donor not found"));
        donor.setDeleted(true);
        donorRepository.save(donor);
        return GeneralResponse.builder().message("Donor deleted successfully").build();
    }

    public GeneralResponse updateDonor(Donor donor, Long donorId) throws UserNotFoundException {
        Donor donorOptional = donorRepository.findById(donorId).orElseThrow(
                () -> new UserNotFoundException("Donor not found"));
        donorOptional.setName(donor.getName());
        donorOptional.setPhone(donor.getPhone());
        donorOptional.setBloodType(donor.getBloodType());
        donorOptional.setGender(donor.getGender());
        donorRepository.save(donorOptional);
        return GeneralResponse.builder().message("Donor updated successfully").build();
    }

    public GeneralResponse addDonation(Long donorId, Donation donation) throws UserNotFoundException {
        Donor donor = donorRepository.findById(donorId).orElseThrow(
                () -> new UserNotFoundException("Donor not found"));

        BloodType bloodType = bloodTypeRepository.findByType(donor.getBloodType());
        donation.setDonor(donor);
        donation.setBloodType(bloodType);
        bloodType.setQuantity(bloodType.getQuantity() + donation.getQuantity());
        donation.setDonationDate(donation.getDonationDate());
        donationRepository.save(donation);
        bloodTypeRepository.save(bloodType);
        return GeneralResponse.builder().message("Donation added successfully").build();

    }
    public Donor getDonor(Long donorId) throws UserNotFoundException {
        return donorRepository.findById(donorId).orElseThrow(
                () -> new UserNotFoundException("Donor not found"));
    }
    public PaginationDTO<Donor> getDonors(int page, int size , String search, BloodTypes bloodType, List<Long> donorIds, Gender gender) {
        if(search!=null && search.isEmpty()){
            search = null;
        }

        if(bloodType!=null && !EnumSet.allOf(BloodTypes.class).contains(bloodType)){
            bloodType = null;
        }

        if(donorIds!=null && donorIds.isEmpty()){
            donorIds = null;
        }

        if(gender!=null && !EnumSet.allOf(Gender.class).contains(gender)){
            gender = null;
        }

        if (page < 1) {
            page = 1;
        }
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Donor> donors = donorRepository.findAll(pageable, search, bloodType, donorIds ,gender);
        PaginationDTO<Donor> paginationDTO = new PaginationDTO<>();
        paginationDTO.setTotalElements(donors.getTotalElements());
        paginationDTO.setTotalPages(donors.getTotalPages());
        paginationDTO.setSize(donors.getSize());
        paginationDTO.setNumber(donors.getNumber() + 1);
        paginationDTO.setNumberOfElements(donors.getNumberOfElements());
        paginationDTO.setContent(donors.getContent());
        return paginationDTO;
    }


    public Donation getDonation(Long donationId) throws UserNotFoundException {
        return donationRepository.findById(donationId).orElseThrow(
                () -> new UserNotFoundException("Donation not found"));

    }

//    public GeneralResponse deleteDonation(Long donationId) throws UserNotFoundException {
//        Donation donation = donationRepository.findById(donationId).orElseThrow(
//                () -> new UserNotFoundException("Donation not found"));
//        BloodType bloodType = donation.getBloodType();
//        bloodType.setQuantity(bloodType.getQuantity() - donation.getQuantity());
//        donation.setDeleted(true);
//        donationRepository.save(donation);
//        bloodTypeRepository.save(bloodType);
//        return GeneralResponse.builder().message("Donation deleted successfully").build();
//    }


    public PaginationDTO<DonationDTO> getDonations(int page, int size , String search, BloodTypes bloodType, List<Long> donorIds, Integer quantity) {
        if(search!=null && search.isEmpty()){
            search = null;
        }

        if(bloodType!=null && !EnumSet.allOf(BloodTypes.class).contains(bloodType)){
            bloodType = null;
        }

        if(donorIds!=null && donorIds.isEmpty()){
            donorIds = null;
        }

        if(quantity!=null && quantity==0){
            quantity = null;
        }
        if (page < 1) {
            page = 1;
        }
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Donation> donations = donationRepository.findAll(pageable, search, bloodType, donorIds, quantity);
        List<DonationDTO> donationsDTOs = donations.getContent().stream()
                .map(DonationMapper::toDTO)
                .collect(Collectors.toList());


        PaginationDTO<DonationDTO> paginationBloodDTO = new PaginationDTO<>();
        paginationBloodDTO.setTotalElements(donations.getTotalElements());
        paginationBloodDTO.setTotalPages(donations.getTotalPages());
        paginationBloodDTO.setSize(donations.getSize());
        paginationBloodDTO.setNumber(donations.getNumber() + 1);
        paginationBloodDTO.setNumberOfElements(donations.getNumberOfElements());
        paginationBloodDTO.setContent(donationsDTOs);
        return paginationBloodDTO;
    }


    public GeneralResponse takeBlood(PatientsBlood patientsBlood) throws UserNotFoundException {
        System.out.println("Patients ID: " + patientsBlood.getPatients().getId());
        Patients patients1 = patientsRepository.findById(patientsBlood.getPatients().getId()).orElseThrow(
                () -> new UserNotFoundException("Patient not found"));

        BloodType bloodTypeEntity = patients1.getBloodType();
        System.out.println("Patients Blood ID: " + bloodTypeEntity.getQuantity());

        if (bloodTypeEntity.getQuantity() == 0) {
//            return GeneralResponse.builder().message("Blood not available").build();
            throw new UserNotFoundException("Blood not available");
        }

        if(patientsBlood.getQuantity() > bloodTypeEntity.getQuantity()){
//            return GeneralResponse.builder().message("Blood not available").build();
            throw new UserNotFoundException("Blood not available");
        }
        System.out.println("Patients Blood Type: " + patients1.getBloodType().getType());
        System.out.println("Blood Type: " + bloodTypeEntity.getType());
        if(bloodTypeEntity.getType() != patients1.getBloodType().getType()){
//            return GeneralResponse.builder().message("Blood not available").build();
            throw new UserNotFoundException("Blood not available");
        }

        bloodTypeEntity.setQuantity(bloodTypeEntity.getQuantity() - patientsBlood.getQuantity());
        bloodTypeRepository.save(bloodTypeEntity);

        PatientsBlood patientsBlood1 = PatientsBlood.builder()
                .patients(patients1)
                .patientsBlood(bloodTypeEntity)
                .quantity(patientsBlood.getQuantity())
                .build();
        patientsBloodRepository.save(patientsBlood1);

        return GeneralResponse.builder().message("Blood taken successfully").build();
    }

    public PaginationDTO<PatientsBloodDTO> getPatientsBlood(int page, int size , String search, List<Long> patientIds, BloodTypes bloodType, Integer quantity) {
        if(search!=null && search.isEmpty()){
            search = null;
        }

        if(bloodType!=null && !EnumSet.allOf(BloodTypes.class).contains(bloodType)){
            bloodType = null;
        }

        if(patientIds!=null && patientIds.isEmpty()){
            patientIds = null;
        }

        if(quantity!=null && quantity==0){
            quantity = null;
        }
        if (page < 1) {
            page = 1;
        }
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<PatientsBlood> patientsBlood = patientsBloodRepository.findAll(pageable, search, patientIds, bloodType, quantity);
        List<PatientsBloodDTO> PatientsBloodDTOs = patientsBlood.getContent().stream()
                .map(PatientsBloodMapper::toDTO)
                .collect(Collectors.toList());
        PaginationDTO<PatientsBloodDTO> paginationDTO = new PaginationDTO<>();
        paginationDTO.setTotalElements(patientsBlood.getTotalElements());
        paginationDTO.setTotalPages(patientsBlood.getTotalPages());
        paginationDTO.setSize(patientsBlood.getSize());
        paginationDTO.setNumber(patientsBlood.getNumber() + 1);
        paginationDTO.setNumberOfElements(patientsBlood.getNumberOfElements());
        paginationDTO.setContent(PatientsBloodDTOs);

        return paginationDTO;
    }
    public PaginationDTO<Donor> getDeletedDonors(int page, int size , String search, BloodTypes bloodType, List<Long> donorIds, Gender gender) {
        if(search!=null && search.isEmpty()){
            search = null;
        }

        if(bloodType!=null && !EnumSet.allOf(BloodTypes.class).contains(bloodType)){
            bloodType = null;
        }

        if(donorIds!=null && donorIds.isEmpty()){
            donorIds = null;
        }

        if(gender!=null && !EnumSet.allOf(Gender.class).contains(gender)){
            gender = null;
        }

        if (page < 1) {
            page = 1;
        }
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Donor> donors = donorRepository.findAllDeleted(pageable, search, bloodType, donorIds ,gender);
        PaginationDTO<Donor> paginationDTO = new PaginationDTO<>();
        paginationDTO.setTotalElements(donors.getTotalElements());
        paginationDTO.setTotalPages(donors.getTotalPages());
        paginationDTO.setSize(donors.getSize());
        paginationDTO.setNumber(donors.getNumber() + 1);
        paginationDTO.setNumberOfElements(donors.getNumberOfElements());
        paginationDTO.setContent(donors.getContent());
        return paginationDTO;
    }

    public GeneralResponse restoreDonor(Long donorId) throws UserNotFoundException {
        Donor donor = donorRepository.findDeletedById(donorId);
        donor.setDeleted(false);
        donorRepository.save(donor);
        return GeneralResponse.builder().message("Donor restored successfully").build();
    }

    public GeneralResponse deletePatientBlood(Long patientBloodId) throws UserNotFoundException {
        PatientsBlood patientsBlood = patientsBloodRepository.findById(patientBloodId).orElseThrow(
                () -> new UserNotFoundException("Patient Blood not found"));
        BloodType bloodType = patientsBlood.getPatientsBlood();
        bloodType.setQuantity(bloodType.getQuantity() + patientsBlood.getQuantity());
        patientsBlood.setDeleted(true);
        patientsBloodRepository.save(patientsBlood);
        bloodTypeRepository.save(bloodType);
        return GeneralResponse.builder().message("Patient Blood deleted successfully").build();
    }

    public GeneralResponse restorePatientBlood(Long patientBloodId) throws UserNotFoundException {
        PatientsBlood patientsBlood = patientsBloodRepository.findDeletedById(patientBloodId);
        BloodType bloodType = patientsBlood.getPatientsBlood();
        bloodType.setQuantity(bloodType.getQuantity() - patientsBlood.getQuantity());
        patientsBlood.setDeleted(false);
        patientsBloodRepository.save(patientsBlood);
        bloodTypeRepository.save(bloodType);
        return GeneralResponse.builder().message("Patient Blood restored successfully").build();
    }


    public PaginationDTO<PatientsBloodDTO> getDeletedPatientsBlood(int page, int size , String search, List<Long> patientIds, BloodTypes bloodType, Integer quantity) {
        if(search!=null && search.isEmpty()){
            search = null;
        }

        if(bloodType!=null && !EnumSet.allOf(BloodTypes.class).contains(bloodType)){
            bloodType = null;
        }

        if(patientIds!=null && patientIds.isEmpty()){
            patientIds = null;
        }

        if(quantity!=null && quantity==0){
            quantity = null;
        }
        if (page < 1) {
            page = 1;
        }
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<PatientsBlood> patientsBlood = patientsBloodRepository.findDeletedAll(pageable, search, patientIds, bloodType, quantity);
        List<PatientsBloodDTO> PatientsBloodDTOs = patientsBlood.getContent().stream()
                .map(PatientsBloodMapper::toDTO)
                .collect(Collectors.toList());
        PaginationDTO<PatientsBloodDTO> paginationDTO = new PaginationDTO<>();
        paginationDTO.setTotalElements(patientsBlood.getTotalElements());
        paginationDTO.setTotalPages(patientsBlood.getTotalPages());
        paginationDTO.setSize(patientsBlood.getSize());
        paginationDTO.setNumber(patientsBlood.getNumber() + 1);
        paginationDTO.setNumberOfElements(patientsBlood.getNumberOfElements());
        paginationDTO.setContent(PatientsBloodDTOs);

        return paginationDTO;
    }
}
