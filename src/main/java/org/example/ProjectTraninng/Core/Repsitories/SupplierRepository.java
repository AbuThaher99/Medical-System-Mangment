package org.example.ProjectTraninng.Core.Repsitories;

import org.example.ProjectTraninng.Common.Entities.Supplier;
import org.example.ProjectTraninng.Common.Enums.CompanyNames;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SupplierRepository  extends JpaRepository<Supplier, Long> {

    @Query("SELECT s FROM Supplier s WHERE s.id = :id AND s.isDeleted = false")
    Optional<Supplier> findById(Long id);

    @Query("SELECT s FROM Supplier s WHERE s.isDeleted = false AND " +
            "(:search IS NULL OR :search = '' OR " +
            "s.name LIKE %:search% OR " +
            "s.phone LIKE %:search% OR " +
            "s.address LIKE %:search%) AND " +
            "(:companyName IS NULL OR s.companyName LIKE %:companyName%)")
    Page<Supplier> findAll(Pageable pageable, @Param("search") String search, @Param("companyName") String companyName);

    @Query("SELECT s FROM Supplier s WHERE s.isDeleted = true AND " +
            "(:search IS NULL OR :search = '' OR " +
            "s.name LIKE %:search% OR " +
            "s.phone LIKE %:search% OR " +
            "s.address LIKE %:search%) AND " +
            "(:companyName IS NULL OR s.companyName LIKE %:companyName%)")
    Page<Supplier> findDeletedAll(Pageable pageable, @Param("search") String search, @Param("companyName") String companyName);
    @Query("SELECT s FROM Supplier s WHERE s.id = :id AND s.isDeleted = true")
    Optional<Supplier> findByIdAndDeleted(Long id);
}
