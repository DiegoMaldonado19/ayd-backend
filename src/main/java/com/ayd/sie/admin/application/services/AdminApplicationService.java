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

    // Business management
    private final RegisterBusinessUseCase registerBusinessUseCase;
    private final UpdateBusinessUseCase updateBusinessUseCase;
    private final GetBusinessesUseCase getBusinessesUseCase;

    // Employee management
    private final RegisterEmployeeUseCase registerEmployeeUseCase;
    private final GetEmployeesUseCase getEmployeesUseCase;
    private final DeleteEmployeeUseCase deleteEmployeeUseCase;
    private final ActivateEmployeeUseCase activateEmployeeUseCase;
    private final CheckUserReferencesUseCase checkUserReferencesUseCase;

    // Contract management
    private final CreateContractUseCase createContractUseCase;
    private final GetContractsUseCase getContractsUseCase;
    private final TerminateContractUseCase terminateContractUseCase;
    private final DeleteContractUseCase deleteContractUseCase;
    private final ActivateContractUseCase activateContractUseCase;

    // Loyalty level management
    private final CreateLoyaltyLevelUseCase createLoyaltyLevelUseCase;
    private final GetLoyaltyLevelsUseCase getLoyaltyLevelsUseCase;

    // System configuration
    private final GetSystemConfigUseCase getSystemConfigUseCase;
    private final UpdateSystemConfigUseCase updateSystemConfigUseCase;

    // Audit log
    private final GetAuditLogUseCase getAuditLogUseCase;

    // Role management
    private final CreateRoleUseCase createRoleUseCase;
    private final UpdateRoleUseCase updateRoleUseCase;
    private final GetRolesUseCase getRolesUseCase;

    // Contract type management
    private final CreateContractTypeUseCase createContractTypeUseCase;
    private final UpdateContractTypeUseCase updateContractTypeUseCase;
    private final GetContractTypesUseCase getContractTypesUseCase;

    private final DeleteRoleUseCase deleteRoleUseCase;
    private final ActivateRoleUseCase activateRoleUseCase;
    private final DeleteContractTypeUseCase deleteContractTypeUseCase;
    private final ActivateContractTypeUseCase activateContractTypeUseCase;
    private final DeleteLoyaltyLevelUseCase deleteLoyaltyLevelUseCase;
    private final ActivateLoyaltyLevelUseCase activateLoyaltyLevelUseCase;
    private final DeleteBranchUseCase deleteBranchUseCase;
    private final ActivateBranchUseCase activateBranchUseCase;
    private final DeleteBusinessUseCase deleteBusinessUseCase;
    private final ActivateBusinessUseCase activateBusinessUseCase;

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

    // Employee operations
    public EmployeeDto registerEmployee(EmployeeRegistrationRequestDto request) {
        return registerEmployeeUseCase.execute(request);
    }

    public Page<EmployeeDto> getEmployees(Integer roleId, String search, Pageable pageable) {
        return getEmployeesUseCase.execute(roleId, search, pageable);
    }

    public void activateEmployee(Integer userId, boolean active) {
        activateEmployeeUseCase.execute(userId, active);
    }

    public UserReferencesDto checkUserReferences(Integer userId) {
        return checkUserReferencesUseCase.execute(userId);
    }

    public void deleteEmployee(Integer userId) {
        deleteEmployeeUseCase.execute(userId);
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

    public void deleteContract(Integer contractId) {
        deleteContractUseCase.execute(contractId);
    }

    public void activateContract(Integer contractId, boolean active) {
        activateContractUseCase.execute(contractId, active);
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

    // Role operations
    public RoleDto createRole(CreateRoleRequestDto request) {
        return createRoleUseCase.execute(request);
    }

    public RoleDto updateRole(Integer roleId, UpdateRoleRequestDto request) {
        return updateRoleUseCase.execute(roleId, request);
    }

    public List<Role> getRoles() {
        return getRolesUseCase.execute();
    }

    // Contract type operations
    public ContractTypeDto createContractType(CreateContractTypeRequestDto request) {
        return createContractTypeUseCase.execute(request);
    }

    public ContractTypeDto updateContractType(Integer contractTypeId, UpdateContractTypeRequestDto request) {
        return updateContractTypeUseCase.execute(contractTypeId, request);
    }

    public List<ContractType> getContractTypes() {
        return getContractTypesUseCase.execute();
    }

    // Role operations
    public void deleteRole(Integer roleId) {
        deleteRoleUseCase.execute(roleId);
    }

    public void activateRole(Integer roleId, boolean active) {
        activateRoleUseCase.execute(roleId, active);
    }

    // Contract type operations
    public void deleteContractType(Integer contractTypeId) {
        deleteContractTypeUseCase.execute(contractTypeId);
    }

    public void activateContractType(Integer contractTypeId, boolean active) {
        activateContractTypeUseCase.execute(contractTypeId, active);
    }

    // Loyalty level operations
    public void deleteLoyaltyLevel(Integer levelId) {
        deleteLoyaltyLevelUseCase.execute(levelId);
    }

    public void activateLoyaltyLevel(Integer levelId, boolean active) {
        activateLoyaltyLevelUseCase.execute(levelId, active);
    }

    // Branch operations
    public void deleteBranch(Integer branchId) {
        deleteBranchUseCase.execute(branchId);
    }

    public void activateBranch(Integer branchId, boolean active) {
        activateBranchUseCase.execute(branchId, active);
    }

    // Business operations
    public void deleteBusiness(Integer businessId) {
        deleteBusinessUseCase.execute(businessId);
    }

    public void activateBusiness(Integer businessId, boolean active) {
        activateBusinessUseCase.execute(businessId, active);
    }
}