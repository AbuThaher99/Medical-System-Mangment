package org.example.ProjectTraninng.Core.Repsitories;

import org.example.ProjectTraninng.Common.Entities.Doctor;
import org.example.ProjectTraninng.Common.Entities.Patients;
import org.example.ProjectTraninng.Common.Entities.Treatment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface TreatmentRepository extends JpaRepository<Treatment, Long> {
    @Override
    Optional<Treatment> findById(Long aLong);

    @Query("SELECT t FROM Treatment t WHERE t.patient.id = :patientId")
    Page<Treatment> findAllByPatientId(@Param("patientId") Long patientId, Pageable pageable);

    @Query("SELECT t FROM Treatment t WHERE t.patient.id = :patientId")
    List<Treatment> findAllByPatientIdD(@Param("patientId") Long patientId);


    @Query("SELECT t FROM Treatment t " +
            "WHERE (:search IS NULL OR :search = '' OR t.diseaseDescription LIKE %:search% OR t.note LIKE %:search% OR cast(t.price as string) like %:search%) " +
            "AND (:patientIds IS NULL OR t.patient.id IN :patientIds) " +
            "AND (:patientId IS NULL OR t.patient.id = :patientId) " +
            "AND (:doctorId IS NULL OR t.doctor.id = :doctorId)")
    Page<Treatment> findAll(Pageable pageable,
                            @Param("patientIds") List<Long> patientIds,
                            @Param("patientId") Long patientId,
                            @Param("search") String search,
                            @Param("doctorId") Long doctorId);



    @Query("SELECT t FROM Treatment t")
    List<Treatment> getAllTreatments();



    @Query("SELECT t FROM Treatment t WHERE DATE(t.treatmentDate) = CURRENT_DATE and t.notificationSent = false")
    List<Treatment> findTodayTreatments();

    // query to get a all patient user
    @Query("SELECT p from Patients p where p.user.isDeleted = false")
    Page<Patients> findAllPatient(Pageable pageable);

    @Query("SELECT d from Doctor d where d.user.isDeleted = false")
    Page<Doctor> findAllDoctor(Pageable pageable);

}
