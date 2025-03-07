package org.example.ProjectTraninng.Core.Servecies;

import lombok.RequiredArgsConstructor;
import org.example.ProjectTraninng.Common.Converters.MedicineMapper;
import org.example.ProjectTraninng.Common.Converters.WarehouseMapper;
import org.example.ProjectTraninng.Common.DTOs.MedicineDTO;
import org.example.ProjectTraninng.Common.DTOs.PaginationDTO;
import org.example.ProjectTraninng.Common.DTOs.WarehosueDTO;
import org.example.ProjectTraninng.Common.Entities.Medicine;
import org.example.ProjectTraninng.Common.Entities.WarehouseStore;
import org.example.ProjectTraninng.Common.Responses.GeneralResponse;
import org.example.ProjectTraninng.Core.Repsitories.MedicineRepository;
import org.example.ProjectTraninng.Core.Repsitories.WarehouseStoreRepository;
import org.example.ProjectTraninng.WebApi.Exceptions.UserNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WarehouseStoreService {
    private final WarehouseStoreRepository warehouseStoreRepository;
    private final MedicineRepository medicineRepository;
    @Transactional
    public GeneralResponse addMedicineToWarehouse(WarehouseStore warehouseStoreRequest) throws UserNotFoundException {
        medicineRepository.findById(warehouseStoreRequest.getMedicine().getId())
                .orElseThrow(() -> new UserNotFoundException("Medicine not found"));
        if (warehouseStoreRepository.existsByMedicineId(warehouseStoreRequest.getMedicine().getId())) {
           throw new UserNotFoundException("Medicine already exists in the warehouse store");
        }
        WarehouseStore warehouseStore = WarehouseStore.builder()
                .medicine(warehouseStoreRequest.getMedicine())
                .quantity(warehouseStoreRequest.getQuantity())
                .build();
        warehouseStoreRepository.save(warehouseStore);
        return GeneralResponse.builder()
                .message("Medicine added successfully")
                .build();
    }
    @Transactional
    public GeneralResponse updateMedicineQuantity(WarehouseStore quantityRequest , Long medicineId) {

        WarehouseStore warehouseStore = warehouseStoreRepository.findByMedicineId(medicineId);
        if (warehouseStore == null) {
            return GeneralResponse.builder()
                    .message("Medicine not found in the warehouse store")
                    .build();
        }
        System.out.println("warehouse Store quantity "+warehouseStore.getQuantity());
        System.out.println("quantity Request "+quantityRequest.getQuantity());
        warehouseStore.setQuantity(warehouseStore.getQuantity() + quantityRequest.getQuantity());
        warehouseStoreRepository.save(warehouseStore);
        return GeneralResponse.builder()
                .message("Medicine quantity updated successfully")
                .build();
    }

    @Transactional
    public GeneralResponse DecreaseMedicineQuantity(WarehouseStore quantityRequest , Long medicineId) {

        WarehouseStore warehouseStore = warehouseStoreRepository.findByMedicineId(medicineId);
        if (warehouseStore == null) {
            return GeneralResponse.builder()
                    .message("Medicine not found in the warehouse store")
                    .build();
        }
        System.out.println("warehouse Store quantity "+warehouseStore.getQuantity());
        System.out.println("quantity Request "+quantityRequest.getQuantity());
        int quantity = warehouseStore.getQuantity() - quantityRequest.getQuantity();
        warehouseStore.setQuantity(quantity);
        warehouseStoreRepository.save(warehouseStore);
        return GeneralResponse.builder()
                .message("Medicine quantity updated successfully")
                .build();
    }
    @Transactional
    public PaginationDTO<WarehosueDTO> getWarehouseStore(int page, int size) {
        if (page < 1) {
            page = 1;
        }
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<WarehouseStore> warehouseStores = warehouseStoreRepository.findAll(pageable);
        List<WarehosueDTO> medicineDTOs = warehouseStores.getContent().stream()
                .map(WarehouseMapper::toDTO)
                .collect(Collectors.toList());
        PaginationDTO<WarehosueDTO> paginationDTO = new PaginationDTO<>();
        paginationDTO.setTotalElements(warehouseStores.getTotalElements());
        paginationDTO.setTotalPages(warehouseStores.getTotalPages());
        paginationDTO.setSize(warehouseStores.getSize());
        paginationDTO.setNumber(warehouseStores.getNumber() + 1);
        paginationDTO.setNumberOfElements(warehouseStores.getNumberOfElements());
        paginationDTO.setContent(medicineDTOs);
        return paginationDTO;

    }

    @Transactional
    public PaginationDTO<MedicineDTO> getMedicinesNotInWarehouse(int page, int size) {
        if (page < 1) {
            page = 1;
        }
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Medicine> warehouseStores = warehouseStoreRepository.findMedicinesNotInWarehouse(pageable);
        List<MedicineDTO> medicineDTOs = warehouseStores.getContent().stream()
                .map(MedicineMapper::toDTO)
                .collect(Collectors.toList());
        PaginationDTO<MedicineDTO> paginationDTO = new PaginationDTO<>();
        paginationDTO.setTotalElements(warehouseStores.getTotalElements());
        paginationDTO.setTotalPages(warehouseStores.getTotalPages());
        paginationDTO.setSize(warehouseStores.getSize());
        paginationDTO.setNumber(warehouseStores.getNumber() + 1);
        paginationDTO.setNumberOfElements(warehouseStores.getNumberOfElements());
        paginationDTO.setContent(medicineDTOs);
        return paginationDTO;

    }
    public GeneralResponse deleteWarehouseStore(Long id) {
        if (!warehouseStoreRepository.existsById(id)) {
            return GeneralResponse.builder()
                    .message("Medicine not found in the warehouse store")
                    .build();
        }
        WarehouseStore warehouseStore = warehouseStoreRepository.findById(id).get();
        warehouseStore.setDeleted(true);
        warehouseStoreRepository.save(warehouseStore);
        return GeneralResponse.builder()
                .message("Medicine deleted successfully")
                .build();
    }

    @Transactional
    public PaginationDTO<WarehosueDTO> getDeletedMedicinesInWarehouse(int page, int size) {
        if (page < 1) {
            page = 1;
        }
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<WarehouseStore> warehouseStores = warehouseStoreRepository.findDeletedAll(pageable);
        List<WarehosueDTO> medicineDTOs = warehouseStores.getContent().stream()
                .map(WarehouseMapper::toDTO)
                .collect(Collectors.toList());
        PaginationDTO<WarehosueDTO> paginationDTO = new PaginationDTO<>();
        paginationDTO.setTotalElements(warehouseStores.getTotalElements());
        paginationDTO.setTotalPages(warehouseStores.getTotalPages());
        paginationDTO.setSize(warehouseStores.getSize());
        paginationDTO.setNumber(warehouseStores.getNumber() + 1);
        paginationDTO.setNumberOfElements(warehouseStores.getNumberOfElements());
        paginationDTO.setContent(medicineDTOs);
        return paginationDTO;

    }
    public GeneralResponse restoreWarehouseStore(Long id) {

        WarehouseStore warehouseStore = warehouseStoreRepository.findByMedicineDeletedId(id);
        if (warehouseStore == null) {
            return GeneralResponse.builder()
                    .message("Medicine not found in the warehouse store")
                    .build();
        }
        warehouseStore.setDeleted(false);
        warehouseStoreRepository.save(warehouseStore);
        return GeneralResponse.builder()
                .message("Medicine restored successfully")
                .build();
    }

}
