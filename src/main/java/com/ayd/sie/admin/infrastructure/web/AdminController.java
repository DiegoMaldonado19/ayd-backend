package com.ayd.sie.admin.infrastructure.web;

import com.ayd.sie.admin.application.dto.*;
import com.ayd.sie.admin.application.services.AdminApplicationService;
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
import org.springframework.format.annotation.DateTimeFormat;
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
@Tag(name = "Administration", description = "Administrative operations for managing the system")
public class AdminController {

    private final AdminApplicationService adminApplicationService;

    // Branch Management Endpoints

    @PostMapping("/branches")
    @Operation(summary = "Create new branch", description = "Register a new branch in the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Branch created successfully", content = @Content(schema = @Schema(implementation = BranchDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "409", description = "Branch code already exists")
    })
    public ResponseEntity<BranchDto> createBranch(
            @Valid @RequestBody CreateBranchRequestDto request) {
        BranchDto branch = adminApplicationService.createBranch(request);
        return ResponseEntity.ok(branch);
    }

    @PutMapping("/branches/{branchId}")
    @Operation(summary = "Update branch", description = "Update branch information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Branch updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "404", description = "Branch not found")
    })
    public ResponseEntity<BranchDto> updateBranch(
            @PathVariable Integer branchId,
            @Valid @RequestBody UpdateBranchRequestDto request) {
        BranchDto branch = adminApplicationService.updateBranch(branchId, request);
        return ResponseEntity.ok(branch);
    }

    @GetMapping("/branches")
    @Operation(summary = "Get branches", description = "Retrieve paginated list of branches")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Branches retrieved successfully")
    })
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

    @DeleteMapping("/branches/{branchId}")
    @Operation(summary = "Deactivate branch", description = "Deactivate a branch")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Branch deactivated successfully"),
            @ApiResponse(responseCode = "404", description = "Branch not found")
    })
    public ResponseEntity<Map<String, Object>> deactivateBranch(@PathVariable Integer branchId) {
        adminApplicationService.deactivateBranch(branchId);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Branch deactivated successfully");
        response.put("timestamp", System.currentTimeMillis());

        return ResponseEntity.ok(response);
    }

    // Business Management Endpoints

    @PostMapping("/businesses")
    @Operation(summary = "Register business", description = "Register a new business in the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Business registered successfully", content = @Content(schema = @Schema(implementation = BusinessDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "409", description = "Email or Tax ID already exists")
    })
    public ResponseEntity<BusinessDto> registerBusiness(
            @Valid @RequestBody BusinessRegistrationRequestDto request) {
        BusinessDto business = adminApplicationService.registerBusiness(request);
        return ResponseEntity.ok(business);
    }

    @PutMapping("/businesses/{businessId}")
    @Operation(summary = "Update business", description = "Update business information")
    public ResponseEntity<BusinessDto> updateBusiness(
            @PathVariable Integer businessId,
            @Valid @RequestBody UpdateBusinessRequestDto request) {
        BusinessDto business = adminApplicationService.updateBusiness(businessId, request);
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

    @DeleteMapping("/businesses/{businessId}")
    @Operation(summary = "Suspend business", description = "Suspend a business account")
    public ResponseEntity<Map<String, Object>> suspendBusiness(@PathVariable Integer businessId) {
        adminApplicationService.suspendBusiness(businessId);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Business suspended successfully");
        response.put("timestamp", System.currentTimeMillis());

        return ResponseEntity.ok(response);
    }

    // Employee Management Endpoints

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

    // Contract Management Endpoints

    @PostMapping("/contracts")
    @Operation(summary = "Create contract", description = "Create a new contract for an employee")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Contract created successfully", content = @Content(schema = @Schema(implementation = ContractDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "409", description = "Employee already has an active contract")
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

    @DeleteMapping("/contracts/{contractId}")
    @Operation(summary = "Terminate contract", description = "Terminate an active contract")
    public ResponseEntity<Map<String, Object>> terminateContract(@PathVariable Integer contractId) {
        adminApplicationService.terminateContract(contractId);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Contract terminated successfully");
        response.put("timestamp", System.currentTimeMillis());

        return ResponseEntity.ok(response);
    }

    // Loyalty Level Management Endpoints

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
    @Operation(summary = "Get loyalty levels", description = "Retrieve all loyalty levels")
    public ResponseEntity<List<LoyaltyLevelDto>> getLoyaltyLevels() {
        List<LoyaltyLevelDto> loyaltyLevels = adminApplicationService.getLoyaltyLevels();
        return ResponseEntity.ok(loyaltyLevels);
    }

    // System Configuration Endpoints

    @GetMapping("/system-config")
    @Operation(summary = "Get system configuration", description = "Retrieve all system configuration")
    public ResponseEntity<List<SystemConfigDto>> getSystemConfig() {
        List<SystemConfigDto> configs = adminApplicationService.getSystemConfig();
        return ResponseEntity.ok(configs);
    }

    @PutMapping("/system-config/{configKey}")
    @Operation(summary = "Update system configuration", description = "Update a system configuration value")
    public ResponseEntity<SystemConfigDto> updateSystemConfig(
            @PathVariable String configKey,
            @Valid @RequestBody UpdateSystemConfigRequestDto request) {
        SystemConfigDto config = adminApplicationService.updateSystemConfig(configKey, request);
        return ResponseEntity.ok(config);
    }

    // Audit Log Endpoints

    @GetMapping("/audit-log")
    @Operation(summary = "Get audit log", description = "Retrieve paginated audit log entries")
    public ResponseEntity<Page<AuditLogDto>> getAuditLog(
            @RequestParam(required = false) String tableName,
            @RequestParam(required = false) Integer userId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Page<AuditLogDto> auditLog = adminApplicationService.getAuditLog(
                tableName, userId, startDate, endDate, pageable);
        return ResponseEntity.ok(auditLog);
    }

    // Test endpoints for development
    @GetMapping("/test/ping")
    @Operation(summary = "Test admin access", description = "Test endpoint for admin role verification")
    public ResponseEntity<Map<String, Object>> testAdminAccess(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Admin access confirmed");
        response.put("user", userDetails.getFullName());
        response.put("role", userDetails.getRole());
        response.put("timestamp", System.currentTimeMillis());

        return ResponseEntity.ok(response);
    }
}