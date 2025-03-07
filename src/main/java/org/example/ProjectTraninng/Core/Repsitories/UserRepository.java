package org.example.ProjectTraninng.Core.Repsitories;

import org.example.ProjectTraninng.Common.Entities.User;
import org.example.ProjectTraninng.Common.Enums.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Query("SELECT u FROM User u WHERE u.email = :email AND u.isDeleted = false")
    Optional<User> findByEmail(String email);
    // make a query to get the user when the user is deleted equal to false
    @Query("SELECT u FROM User u WHERE u.isDeleted = false AND u.id = :id")
    @EntityGraph(attributePaths = {"doctor", "patient"})
    Optional<User> findById(@Param("id") Long id);
    @Query("SELECT u FROM User u WHERE u.role = :role AND u.isDeleted = false")
    Page<User> findAllByRole(@Param("role") Role role, Pageable pageable);

    @Query("SELECT u FROM User u WHERE u.isDeleted = false ")
    Page<User> findAll(Pageable pageable );
    @Query("SELECT u FROM User u WHERE u.isDeleted = false AND" +
            " (:search IS NULL OR :search = '' OR u.firstName LIKE %:search% or u.lastName LIKE %:search% or u.email LIKE %:search% or u.phone LIKE %:search% or u.address LIKE %:search% ) and " +
            "(:role IS NULL OR u.role = :role)")
    Page<User> findAll(Pageable pageable , @Param("search") String search , @Param("role") Role role);
    @Query("SELECT u FROM User u WHERE u.isDeleted = false AND " +
            "(:roles IS NULL OR u.role IN :roles)  " )
    List<User> findByRoleIn(@Param("roles") List<Role> roles);
    @Query("SELECT u FROM User u WHERE u.role=:role AND u.isDeleted = false  " )
    List<User> findAllByRole(@Param("role")Role role);

    @Query("SELECT u FROM User u WHERE u.isDeleted = true AND" +
            " (:search IS NULL OR :search = '' OR u.firstName LIKE %:search% or u.lastName LIKE %:search% or u.email LIKE %:search% or u.phone LIKE %:search% or u.address LIKE %:search% ) and " +
            "(:role IS NULL OR u.role = :role)")
    Page<User> findAllDeleted(Pageable pageable , @Param("search") String search , @Param("role") Role role);

    // make a query to restore the user when the user is deleted equal to true
    @Query("SELECT u FROM User u WHERE u.isDeleted = true AND u.id = :id")
    Optional<User> findDeletedById(@Param("id") Long id);

    // get all the users that have role doctor
    @Query("SELECT u FROM User u " +
            "LEFT JOIN u.headId d " +
            "WHERE u.role = 'DOCTOR' AND u.isDeleted = false AND d IS NULL")
    Page<User> findAllDoctors(Pageable pageable );

    // get all the users that have role secretary
    @Query("SELECT u FROM User u " +
            "LEFT JOIN u.secretaryId d " +
            "WHERE u.role = 'SECRETARY' AND u.isDeleted = false AND d IS NULL")
    Page<User> findAllSecretaries(Pageable pageable );

    @Query("SELECT u FROM User u " +
            "LEFT JOIN u.headId d " +
            "WHERE u.role = 'DOCTOR' AND u.isDeleted = false  ")
    Page<User> findAllDoctorsUpdated(Pageable pageable );

    // get all the users that have role secretary
    @Query("SELECT u FROM User u " +
            "LEFT JOIN u.secretaryId d " +
            "WHERE u.role = 'SECRETARY' AND u.isDeleted = false")
    Page<User> findAllSecretariesUpdated(Pageable pageable );

}
