package org.example.ProjectTraninng.WebApi.Controllers.AdminMedicine;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.ProjectTraninng.Common.DTOs.PaginationDTO;
import org.example.ProjectTraninng.Common.Entities.User;
import org.example.ProjectTraninng.Common.Entities.WarehouseStore;
import org.example.ProjectTraninng.Common.Responses.GeneralResponse;
import org.example.ProjectTraninng.Core.Servecies.AuthenticationService;
import org.example.ProjectTraninng.Core.Servecies.WarehouseStoreService;
import org.example.ProjectTraninng.SessionManagement;
import org.example.ProjectTraninng.WebApi.Exceptions.UserNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/warehouse-store")
public class WarehouseStoreController extends SessionManagement {
    private final WarehouseStoreService warehouseStoreService;
    private final AuthenticationService service;
    @PostMapping("/")
    public GeneralResponse addToWarehouse(@RequestBody @Valid WarehouseStore warehouseStoreRequest, HttpServletRequest httpServletRequest) throws UserNotFoundException {
        String token = service.extractToken(httpServletRequest);
        User user = service.extractUserFromToken(token);
        validateLoggedInWarehouseEmployee(user);
        return warehouseStoreService.addMedicineToWarehouse(warehouseStoreRequest);
    }
    @PutMapping("/{medicineId}")
    public GeneralResponse updateWarehouseQuantity(@RequestBody @Valid WarehouseStore quantity , @PathVariable Long medicineId, HttpServletRequest httpServletRequest) throws UserNotFoundException {
        String token = service.extractToken(httpServletRequest);
        User user = service.extractUserFromToken(token);
        validateLoggedInWarehouseEmployee(user);
        return warehouseStoreService.updateMedicineQuantity(quantity, medicineId);
    }

    @GetMapping("")
    public PaginationDTO<WarehouseStore> getWarehouseStore(@RequestParam(defaultValue = "1",required = false) int page,
                                                           @RequestParam(defaultValue = "10",required = false) int size,
                                                           HttpServletRequest httpServletRequest) throws UserNotFoundException {
        String token = service.extractToken(httpServletRequest);
        User user = service.extractUserFromToken(token);
        validateLoggedInWarehouseEmployee(user);

        return warehouseStoreService.getWarehouseStore(page, size);
    }

}
