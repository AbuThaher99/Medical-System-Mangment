package org.example.ProjectTraninng.WebApi.Controllers.Patient.Treatment;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.example.ProjectTraninng.Common.DTOs.PaginationDTO;
import org.example.ProjectTraninng.Common.DTOs.TreatmentDTO;
import org.example.ProjectTraninng.Common.Entities.Notification;
import org.example.ProjectTraninng.Common.Entities.Treatment;
import org.example.ProjectTraninng.Common.Entities.User;
import org.example.ProjectTraninng.Core.Servecies.AuthenticationService;
import org.example.ProjectTraninng.Core.Servecies.NotificationService;
import org.example.ProjectTraninng.Core.Servecies.TreatmentService;
import org.example.ProjectTraninng.SessionManagement;
import org.example.ProjectTraninng.WebApi.Exceptions.UserNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/patients/treatment")
public class PatientTreatmentController extends SessionManagement {
    private final TreatmentService treatmentService;
    private final AuthenticationService service;
    private final NotificationService notificationService;

    @GetMapping("")
    public PaginationDTO<TreatmentDTO> getTreatment(@RequestParam(defaultValue = "1") int page,
                                                    @RequestParam(defaultValue = "10") int size,
                                                    HttpServletRequest httpServletRequest) throws UserNotFoundException {
        String token = service.extractToken(httpServletRequest);
        User user = service.extractUserFromToken(token);
        validateLoggedInAll(user);
      return   treatmentService.getAllTreatmentsForPatient( user.getPatient().getId(), page, size);
    }

    @GetMapping("/patientNotifications")
    public List<Notification> getNotificationsTreatment( HttpServletRequest httpServletRequest) throws UserNotFoundException {
        String token = service.extractToken(httpServletRequest);
        User user = service.extractUserFromToken(token);
        validateLoggedInAll(user);
        return notificationService.getUnreadNotifications(user.getPatient().getId());
    }

    @PostMapping("/read/{notificationId}")
    public void markAsRead(@PathVariable Long notificationId) {
        notificationService.markNotificationAsRead(notificationId);
    }

}
