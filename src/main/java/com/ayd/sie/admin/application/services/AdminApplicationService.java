package com.ayd.sie.admin.application.services;

import com.ayd.sie.admin.application.dto.*;
import com.ayd.sie.admin.application.usecases.*;
import com.ayd.sie.shared.domain.entities.ContractType;
import com.ayd.sie.shared.domain.entities.Role;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminApplicationService {

    // Branch management
    private final CreateBranchUseCase createBranchUseCase;
    private final UpdateBranchUseCase updateBranchUseCase;
    private final GetBranchesUseCase getBranchesUseCase;
    private final DeactivateBranchUseCase deactivateBranchUseCase;

    // Business management
    private final RegisterBusinessUseCase registerBusinessUseCase;
    private final UpdateBusinessUseCase updateBusinessUseCase;
    private final GetBusinessesUseCase getBusinessesUseCase;
    private final SuspendBusinessUseCase suspendBusinessUseCase;

    // Employee management
    private final RegisterEmployeeUseCase registerEmployeeUseCase;
    private final GetEmployeesUseCase getEmployeesUseCase;

    // Contract management
    private final CreateContractUseCase createContractUseCase;
    private final GetContractsUseCase getContractsUseCase;
    private final TerminateContractUseCase terminateContractUseCase;

    // Loyalty level management
    private final CreateLoyaltyLevelUseCase createLoyaltyLevelUseCase;
    private final GetLoyaltyLevelsUseCase getLoyaltyLevelsUseCase;

    // System configuration
    private final GetSystemConfigUseCase getSystemConfigUseCase;
    private final UpdateSystemConfigUseCase updateSystemConfigUseCase;

    // Audit log
    private final GetAuditLogUseCase getAuditLogUseCase;

    // Catalog tables
    private final GetContractTypesUseCase getContractTypesUseCase;
    private final GetRolesUseCase getRolesUseCase;

    // Branch operations
    public BranchDto createBranch(CreateBranchRequestDto request) {
        return createBranchUseCase.execute(request);
    }

    public BranchDto updateBranch(Integer branchId, UpdateBranchRequestDto request) {
        return updateBranchUseCase.execute(branchId, request);
    }

    public Page<BranchDto> getBranches(String search, Pageable pageable) {
        return getBranchesUseCase.execute(search, pageable);
    }

    public void deactivateBranch(Integer branchId) {
        deactivateBranchUseCase.execute(branchId);
    }

    // Business operations
    public BusinessDto registerBusiness(BusinessRegistrationRequestDto request) {
        return registerBusinessUseCase.execute(request);
    }

    public BusinessDto updateBusiness(Integer businessId, UpdateBusinessRequestDto request) {
        return updateBusinessUseCase.execute(businessId, request);
    }

    public Page<BusinessDto> getBusinesses(String search, Pageable pageable) {
        return getBusinessesUseCase.execute(search, pageable);
    }

    public void suspendBusiness(Integer businessId) {
        suspendBusinessUseCase.execute(businessId);
    }

    // Employee operations
    public EmployeeDto registerEmployee(EmployeeRegistrationRequestDto request) {
        return registerEmployeeUseCase.execute(request);
    }

    public Page<EmployeeDto> getEmployees(Integer roleId, String search, Pageable pageable) {
        return getEmployeesUseCase.execute(roleId, search, pageable);
    }

    // Contract operations
    public ContractDto createContract(CreateContractRequestDto request, Integer adminId) {
        return createContractUseCase.execute(request, adminId);
    }

    public Page<ContractDto> getContracts(String search, Pageable pageable) {
        return getContractsUseCase.execute(search, pageable);
    }

    public void terminateContract(Integer contractId) {
        terminateContractUseCase.execute(contractId);
    }

    // Loyalty level operations
    public LoyaltyLevelDto createLoyaltyLevel(CreateLoyaltyLevelRequestDto request) {
        return createLoyaltyLevelUseCase.execute(request);
    }

    public List<LoyaltyLevelDto> getLoyaltyLevels() {
        return getLoyaltyLevelsUseCase.execute();
    }

    // System configuration operations
    public List<SystemConfigDto> getSystemConfig() {
        return getSystemConfigUseCase.execute();
    }

    public SystemConfigDto updateSystemConfig(String configKey, UpdateSystemConfigRequestDto request) {
        return updateSystemConfigUseCase.execute(configKey, request);
    }

    // Audit log operations
    public Page<AuditLogDto> getAuditLog(String tableName, Integer userId,
            LocalDateTime startDate, LocalDateTime endDate,
            Pageable pageable) {
        return getAuditLogUseCase.execute(tableName, userId, startDate, endDate, pageable);
    }

    // Catalog tables retrieval
    public List<ContractType> getContractTypes() {
        return getContractTypesUseCase.execute();
    }

    public List<Role> getRoles() {
        return getRolesUseCase.execute();
    }
}