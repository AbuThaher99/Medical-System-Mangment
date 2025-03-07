package org.example.ProjectTraninng.Core.Repsitories;

import jakarta.transaction.Transactional;
import org.example.ProjectTraninng.Common.Entities.Medicine;
import org.example.ProjectTraninng.Common.Entities.WarehouseStore;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface WarehouseStoreRepository extends JpaRepository<WarehouseStore, Long> {
  @Query("select w from WarehouseStore w where w.medicine.id = :medicineId and w.isDeleted = false")
    Optional<WarehouseStore> findById(@Param("medicineId") Long aLong);
   // @Query("select w from WarehouseStore w where w.medicine.id = :medicineId")
   @Query("SELECT COUNT(w) > 0 FROM WarehouseStore w WHERE w.medicine.id = :medicineId AND w.isDeleted = false")
   boolean existsByMedicineId(@Param("medicineId") Long medicineId);
    @Query("select w from WarehouseStore w where w.medicine.id = :medicineId and w.isDeleted = false")
    WarehouseStore findByMedicineId(@Param("medicineId") Long medicineId);
    @Query("select w from WarehouseStore w where w.isDeleted = false")
    Page<WarehouseStore> findAll(Pageable pageable);

    @Query("select w from WarehouseStore w where w.isDeleted = true")
    Page<WarehouseStore> findDeletedAll(Pageable pageable);
    @Query("select w from WarehouseStore w where w.quantity between 0 and 10 and w.isDeleted = false")
    List<WarehouseStore> findLowStockMedicines();
  @Query("select m from Medicine m where m.id not in (select w.medicine.id from WarehouseStore w where w.isDeleted = false)")
  Page<Medicine> findMedicinesNotInWarehouse(Pageable pageable);
    @Query("select w from WarehouseStore w where w.medicine.id = :medicineId and w.isDeleted = true")
    WarehouseStore findByMedicineDeletedId(@Param("medicineId") Long medicineId);
}
