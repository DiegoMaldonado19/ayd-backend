package com.ayd.sie.admin.infrastructure.web;

import com.ayd.sie.admin.application.dto.*;
import com.ayd.sie.admin.application.services.AdminApplicationService;
import com.ayd.sie.shared.domain.entities.ContractType;
import com.ayd.sie.shared.domain.entities.Role;
import com.ayd.sie.shared.infrastructure.security.CustomUserDetails;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Admin Management", description = "Administrator operations")
public class AdminController {

    private final AdminApplicationService adminApplicationService;

    @GetMapping("/access")
    @Operation(summary = "Verify admin access", description = "Verify administrator access permissions")
    public ResponseEntity<Map<String, Object>> verifyAdminAccess(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Admin access confirmed");
        response.put("user", userDetails.getFullName());
        response.put("role", userDetails.getRole());
        response.put("timestamp", System.currentTimeMillis());

        return ResponseEntity.ok(response);
    }

    // ==============================================
    // BRANCH MANAGEMENT
    // ==============================================

    @PostMapping("/branches")
    @Operation(summary = "Create branch", description = "Create a new branch")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Branch created successfully", content = @Content(schema = @Schema(implementation = BranchDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "409", description = "Branch code already exists")
    })
    public ResponseEntity<BranchDto> createBranch(@Valid @RequestBody CreateBranchRequestDto request) {
        BranchDto branch = adminApplicationService.createBranch(request);
        return ResponseEntity.ok(branch);
    }

    @GetMapping("/branches")
    @Operation(summary = "Get branches", description = "Retrieve paginated list of branches")
    public ResponseEntity<Page<BranchDto>> getBranches(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "branchName") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<BranchDto> branches = adminApplicationService.getBranches(search, pageable);
        return ResponseEntity.ok(branches);
    }

    @PutMapping("/branches/{branchId}")
    @Operation(summary = "Update branch", description = "Update branch information")
    public ResponseEntity<BranchDto> updateBranch(
            @PathVariable Integer branchId,
            @Valid @RequestBody UpdateBranchRequestDto request) {
        BranchDto branch = adminApplicationService.updateBranch(branchId, request);
        return ResponseEntity.ok(branch);
    }

    @PatchMapping("/branches/{branchId}/status")
    @Operation(summary = "Change branch status", description = "Activate or deactivate a branch")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Branch status changed successfully"),
            @ApiResponse(responseCode = "404", description = "Branch not found")
    })
    public ResponseEntity<Map<String, String>> changeBranchStatus(
            @PathVariable Integer branchId,
            @RequestParam boolean active) {
        adminApplicationService.activateBranch(branchId, active);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Branch status changed successfully");
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/branches/{branchId}")
    @Operation(summary = "Delete branch", description = "Permanently delete a branch from the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Branch deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Branch not found"),
            @ApiResponse(responseCode = "400", description = "Cannot delete branch with active tracking guides")
    })
    public ResponseEntity<Map<String, Object>> deleteBranch(@PathVariable Integer branchId) {
        adminApplicationService.deleteBranch(branchId);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Branch deleted successfully");
        response.put("timestamp", System.currentTimeMillis());

        return ResponseEntity.ok(response);
    }

    // ==============================================
    // BUSINESS MANAGEMENT
    // ==============================================

    @PostMapping("/businesses")
    @Operation(summary = "Register business", description = "Register a new business in the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Business registered successfully", content = @Content(schema = @Schema(implementation = BusinessDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "409", description = "Email or Tax ID already exists")
    })
    public ResponseEntity<BusinessDto> registerBusiness(@Valid @RequestBody BusinessRegistrationRequestDto request) {
        BusinessDto business = adminApplicationService.registerBusiness(request);
        return ResponseEntity.ok(business);
    }

    @GetMapping("/businesses")
    @Operation(summary = "Get businesses", description = "Retrieve paginated list of businesses")
    public ResponseEntity<Page<BusinessDto>> getBusinesses(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "businessName") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<BusinessDto> businesses = adminApplicationService.getBusinesses(search, pageable);
        return ResponseEntity.ok(businesses);
    }

    @PutMapping("/businesses/{businessId}")
    @Operation(summary = "Update business", description = "Update business information")
    public ResponseEntity<BusinessDto> updateBusiness(
            @PathVariable Integer businessId,
            @Valid @RequestBody UpdateBusinessRequestDto request) {
        BusinessDto business = adminApplicationService.updateBusiness(businessId, request);
        return ResponseEntity.ok(business);
    }

    @PatchMapping("/businesses/{businessId}/status")
    @Operation(summary = "Change business status", description = "Activate or deactivate a business")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Business status changed successfully"),
            @ApiResponse(responseCode = "404", description = "Business not found")
    })
    public ResponseEntity<Map<String, String>> changeBusinessStatus(
            @PathVariable Integer businessId,
            @RequestParam boolean active) {
        adminApplicationService.activateBusiness(businessId, active);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Business status changed successfully");
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/businesses/{businessId}")
    @Operation(summary = "Delete business", description = "Permanently delete a business from the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Business deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Business not found"),
            @ApiResponse(responseCode = "400", description = "Cannot delete business with active tracking guides")
    })
    public ResponseEntity<Map<String, Object>> deleteBusiness(@PathVariable Integer businessId) {
        adminApplicationService.deleteBusiness(businessId);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Business deleted successfully");
        response.put("timestamp", System.currentTimeMillis());

        return ResponseEntity.ok(response);
    }

    // ==============================================
    // EMPLOYEE MANAGEMENT
    // ==============================================

    @PostMapping("/employees")
    @Operation(summary = "Register employee", description = "Register a new employee in the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Employee registered successfully", content = @Content(schema = @Schema(implementation = EmployeeDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "409", description = "Email or National ID already exists")
    })
    public ResponseEntity<EmployeeDto> registerEmployee(
            @Valid @RequestBody EmployeeRegistrationRequestDto request) {
        EmployeeDto employee = adminApplicationService.registerEmployee(request);
        return ResponseEntity.ok(employee);
    }

    @GetMapping("/employees")
    @Operation(summary = "Get employees", description = "Retrieve paginated list of employees")
    public ResponseEntity<Page<EmployeeDto>> getEmployees(
            @RequestParam(required = false) Integer roleId,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "firstName") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<EmployeeDto> employees = adminApplicationService.getEmployees(roleId, search, pageable);
        return ResponseEntity.ok(employees);
    }

    @DeleteMapping("/employees/{userId}")
    @Operation(summary = "Delete employee", description = "Permanently delete an employee from the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Employee deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Employee not found"),
            @ApiResponse(responseCode = "400", description = "Cannot delete employee with active contracts")
    })
    public ResponseEntity<Map<String, Object>> deleteEmployee(@PathVariable Integer userId) {
        adminApplicationService.deleteEmployee(userId);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Employee deleted successfully");
        response.put("timestamp", System.currentTimeMillis());

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/employees/{userId}/status")
    @Operation(summary = "Change employee status", description = "Activate or deactivate an employee")
    public ResponseEntity<Map<String, String>> changeEmployeeStatus(
            @PathVariable Integer userId,
            @RequestParam boolean active) {
        adminApplicationService.activateEmployee(userId, active);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Employee status changed successfully");
        return ResponseEntity.ok(response);
    }

    // ==============================================
    // CONTRACT MANAGEMENT
    // ==============================================

    @PostMapping("/contracts")
    @Operation(summary = "Create contract", description = "Create a new contract for an employee")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Contract created successfully", content = @Content(schema = @Schema(implementation = ContractDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "404", description = "Employee or contract type not found")
    })
    public ResponseEntity<ContractDto> createContract(
            @Valid @RequestBody CreateContractRequestDto request,
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails) {
        ContractDto contract = adminApplicationService.createContract(request, userDetails.getUserId());
        return ResponseEntity.ok(contract);
    }

    @GetMapping("/contracts")
    @Operation(summary = "Get contracts", description = "Retrieve paginated list of contracts")
    public ResponseEntity<Page<ContractDto>> getContracts(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<ContractDto> contracts = adminApplicationService.getContracts(search, pageable);
        return ResponseEntity.ok(contracts);
    }

    @PatchMapping("/contracts/{contractId}/terminate")
    @Operation(summary = "Terminate contract", description = "Terminate an active contract")
    public ResponseEntity<Map<String, Object>> terminateContract(@PathVariable Integer contractId) {
        adminApplicationService.terminateContract(contractId);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Contract terminated successfully");
        response.put("timestamp", System.currentTimeMillis());

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/contracts/{contractId}")
    @Operation(summary = "Delete contract", description = "Permanently delete a contract from the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Contract deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Contract not found")
    })
    public ResponseEntity<Map<String, Object>> deleteContract(@PathVariable Integer contractId) {
        adminApplicationService.deleteContract(contractId);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Contract deleted successfully");
        response.put("timestamp", System.currentTimeMillis());

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/contracts/{contractId}/status")
    @Operation(summary = "Change contract status", description = "Activate or deactivate a contract")
    public ResponseEntity<Map<String, String>> changeContractStatus(
            @PathVariable Integer contractId,
            @RequestParam boolean active) {
        adminApplicationService.activateContract(contractId, active);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Contract status changed successfully");
        return ResponseEntity.ok(response);
    }

    // ==============================================
    // ROLE MANAGEMENT
    // ==============================================

    @GetMapping("/roles")
    @Operation(summary = "Get roles", description = "Retrieve all available roles")
    public ResponseEntity<List<Role>> getRoles() {
        List<Role> roles = adminApplicationService.getRoles();
        return ResponseEntity.ok(roles);
    }

    @PostMapping("/roles")
    @Operation(summary = "Create new role", description = "Create a new role in the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Role created successfully", content = @Content(schema = @Schema(implementation = RoleDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "409", description = "Role name already exists")
    })
    public ResponseEntity<RoleDto> createRole(@Valid @RequestBody CreateRoleRequestDto request) {
        RoleDto role = adminApplicationService.createRole(request);
        return ResponseEntity.ok(role);
    }

    @PutMapping("/roles/{roleId}")
    @Operation(summary = "Update role", description = "Update role information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Role updated successfully", content = @Content(schema = @Schema(implementation = RoleDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "404", description = "Role not found"),
            @ApiResponse(responseCode = "409", description = "Role name already exists")
    })
    public ResponseEntity<RoleDto> updateRole(
            @PathVariable Integer roleId,
            @Valid @RequestBody UpdateRoleRequestDto request) {
        RoleDto role = adminApplicationService.updateRole(roleId, request);
        return ResponseEntity.ok(role);
    }

    @PatchMapping("/roles/{roleId}/status")
    @Operation(summary = "Change role status", description = "Activate or deactivate a role")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Role status changed successfully"),
            @ApiResponse(responseCode = "404", description = "Role not found")
    })
    public ResponseEntity<Map<String, String>> changeRoleStatus(
            @PathVariable Integer roleId,
            @RequestParam boolean active) {
        adminApplicationService.activateRole(roleId, active);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Role status changed successfully");
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/roles/{roleId}")
    @Operation(summary = "Delete role", description = "Permanently delete a role from the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Role deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Role not found"),
            @ApiResponse(responseCode = "400", description = "Cannot delete role with active users")
    })
    public ResponseEntity<Map<String, Object>> deleteRole(@PathVariable Integer roleId) {
        adminApplicationService.deleteRole(roleId);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Role deleted successfully");
        response.put("timestamp", System.currentTimeMillis());

        return ResponseEntity.ok(response);
    }

    // ==============================================
    // CONTRACT TYPE MANAGEMENT
    // ==============================================

    @GetMapping("/contract-types")
    @Operation(summary = "Get contract types", description = "Retrieve all available contract types")
    public ResponseEntity<List<ContractType>> getContractTypes() {
        List<ContractType> contractTypes = adminApplicationService.getContractTypes();
        return ResponseEntity.ok(contractTypes);
    }

    @PostMapping("/contract-types")
    @Operation(summary = "Create new contract type", description = "Create a new contract type in the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Contract type created successfully", content = @Content(schema = @Schema(implementation = ContractTypeDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "409", description = "Contract type name already exists")
    })
    public ResponseEntity<ContractTypeDto> createContractType(
            @Valid @RequestBody CreateContractTypeRequestDto request) {
        ContractTypeDto contractType = adminApplicationService.createContractType(request);
        return ResponseEntity.ok(contractType);
    }

    @PutMapping("/contract-types/{contractTypeId}")
    @Operation(summary = "Update contract type", description = "Update contract type information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Contract type updated successfully", content = @Content(schema = @Schema(implementation = ContractTypeDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "404", description = "Contract type not found"),
            @ApiResponse(responseCode = "409", description = "Contract type name already exists")
    })
    public ResponseEntity<ContractTypeDto> updateContractType(
            @PathVariable Integer contractTypeId,
            @Valid @RequestBody UpdateContractTypeRequestDto request) {
        ContractTypeDto contractType = adminApplicationService.updateContractType(contractTypeId, request);
        return ResponseEntity.ok(contractType);
    }

    @PatchMapping("/contract-types/{contractTypeId}/status")
    @Operation(summary = "Change contract type status", description = "Activate or deactivate a contract type")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Contract type status changed successfully"),
            @ApiResponse(responseCode = "404", description = "Contract type not found")
    })
    public ResponseEntity<Map<String, String>> changeContractTypeStatus(
            @PathVariable Integer contractTypeId,
            @RequestParam boolean active) {
        adminApplicationService.activateContractType(contractTypeId, active);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Contract type status changed successfully");
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/contract-types/{contractTypeId}")
    @Operation(summary = "Delete contract type", description = "Permanently delete a contract type from the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Contract type deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Contract type not found"),
            @ApiResponse(responseCode = "400", description = "Cannot delete contract type with active contracts")
    })
    public ResponseEntity<Map<String, Object>> deleteContractType(@PathVariable Integer contractTypeId) {
        adminApplicationService.deleteContractType(contractTypeId);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Contract type deleted successfully");
        response.put("timestamp", System.currentTimeMillis());

        return ResponseEntity.ok(response);
    }

    // ==============================================
    // LOYALTY LEVEL MANAGEMENT
    // ==============================================

    @PostMapping("/loyalty-levels")
    @Operation(summary = "Create loyalty level", description = "Create a new loyalty level")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Loyalty level created successfully", content = @Content(schema = @Schema(implementation = LoyaltyLevelDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "409", description = "Loyalty level name already exists")
    })
    public ResponseEntity<LoyaltyLevelDto> createLoyaltyLevel(
            @Valid @RequestBody CreateLoyaltyLevelRequestDto request) {
        LoyaltyLevelDto loyaltyLevel = adminApplicationService.createLoyaltyLevel(request);
        return ResponseEntity.ok(loyaltyLevel);
    }

    @GetMapping("/loyalty-levels")
    @Operation(summary = "Get loyalty levels", description = "Retrieve all available loyalty levels")
    public ResponseEntity<List<LoyaltyLevelDto>> getLoyaltyLevels() {
        List<LoyaltyLevelDto> loyaltyLevels = adminApplicationService.getLoyaltyLevels();
        return ResponseEntity.ok(loyaltyLevels);
    }

    @PatchMapping("/loyalty-levels/{levelId}/status")
    @Operation(summary = "Change loyalty level status", description = "Activate or deactivate a loyalty level")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Loyalty level status changed successfully"),
            @ApiResponse(responseCode = "404", description = "Loyalty level not found")
    })
    public ResponseEntity<Map<String, String>> changeLoyaltyLevelStatus(
            @PathVariable Integer levelId,
            @RequestParam boolean active) {
        adminApplicationService.activateLoyaltyLevel(levelId, active);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Loyalty level status changed successfully");
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/loyalty-levels/{levelId}")
    @Operation(summary = "Delete loyalty level", description = "Permanently delete a loyalty level from the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Loyalty level deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Loyalty level not found"),
            @ApiResponse(responseCode = "400", description = "Cannot delete loyalty level with active businesses")
    })
    public ResponseEntity<Map<String, Object>> deleteLoyaltyLevel(@PathVariable Integer levelId) {
        adminApplicationService.deleteLoyaltyLevel(levelId);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Loyalty level deleted successfully");
        response.put("timestamp", System.currentTimeMillis());

        return ResponseEntity.ok(response);
    }

    // ==============================================
    // SYSTEM CONFIGURATION
    // ==============================================

    @GetMapping("/system-config")
    @Operation(summary = "Get system configuration", description = "Retrieve all system configuration parameters")
    public ResponseEntity<List<SystemConfigDto>> getSystemConfig() {
        List<SystemConfigDto> config = adminApplicationService.getSystemConfig();
        return ResponseEntity.ok(config);
    }

    @PutMapping("/system-config/{configKey}")
    @Operation(summary = "Update system configuration", description = "Update a specific configuration parameter")
    public ResponseEntity<SystemConfigDto> updateSystemConfig(
            @PathVariable String configKey,
            @Valid @RequestBody UpdateSystemConfigRequestDto request) {
        SystemConfigDto config = adminApplicationService.updateSystemConfig(configKey, request);
        return ResponseEntity.ok(config);
    }

    // ==============================================
    // AUDIT LOG
    // ==============================================

    @GetMapping("/audit-log")
    @Operation(summary = "Get audit log", description = "Retrieve system audit log with optional filters")
    public ResponseEntity<Page<AuditLogDto>> getAuditLog(
            @RequestParam(required = false) String tableName,
            @RequestParam(required = false) Integer userId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size);

        LocalDateTime startDateTime = startDate != null ? LocalDateTime.parse(startDate) : null;
        LocalDateTime endDateTime = endDate != null ? LocalDateTime.parse(endDate) : null;

        Page<AuditLogDto> auditLog = adminApplicationService.getAuditLog(
                tableName, userId, startDateTime, endDateTime, pageable);
        return ResponseEntity.ok(auditLog);
    }
}