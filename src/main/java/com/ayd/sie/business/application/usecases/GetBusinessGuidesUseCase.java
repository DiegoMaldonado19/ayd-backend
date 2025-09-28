package com.ayd.sie.business.application.usecases;

import com.ayd.sie.business.application.dto.GuideListResponseDto;
import com.ayd.sie.business.application.dto.GuideResponseDto;
import com.ayd.sie.shared.domain.entities.Business;
import com.ayd.sie.shared.domain.entities.TrackingGuide;
import com.ayd.sie.shared.domain.exceptions.ResourceNotFoundException;
import com.ayd.sie.shared.infrastructure.persistence.BusinessJpaRepository;
import com.ayd.sie.shared.infrastructure.persistence.TrackingGuideJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetBusinessGuidesUseCase {

        private final TrackingGuideJpaRepository trackingGuideRepository;
        private final BusinessJpaRepository businessRepository;

        public GuideListResponseDto execute(Integer businessId, Pageable pageable) {
                // Validate business exists and is active
                Business business = businessRepository.findByUserUserIdAndActiveTrue(businessId)
                                .orElseThrow(() -> new ResourceNotFoundException("Business not found"));

                // Get guides for this business using JOIN FETCH to avoid
                // LazyInitializationException
                List<TrackingGuide> allGuides = trackingGuideRepository
                                .findByBusinessBusinessIdWithFetch(business.getBusinessId());

                // Convert to pageable result
                int start = (int) pageable.getOffset();
                int end = Math.min((start + pageable.getPageSize()), allGuides.size());

                List<TrackingGuide> pageContent = allGuides.subList(start, end);

                List<GuideResponseDto> guideResponses = pageContent.stream()
                                .map(this::mapToResponseDto)
                                .collect(Collectors.toList());

                return GuideListResponseDto.builder()
                                .guides(guideResponses)
                                .total_count(allGuides.size())
                                .page(pageable.getPageNumber())
                                .size(pageable.getPageSize())
                                .has_next(end < allGuides.size())
                                .has_previous(start > 0)
                                .build();
        }

        public GuideListResponseDto executeActiveOnly(Integer businessId, Pageable pageable) {
                // Validate business exists and is active
                Business business = businessRepository.findByUserUserIdAndActiveTrue(businessId)
                                .orElseThrow(() -> new ResourceNotFoundException("Business not found"));

                // Get active guides for this business using JOIN FETCH to avoid
                // LazyInitializationException
                List<TrackingGuide> allActiveGuides = trackingGuideRepository
                                .findActiveByBusinessIdWithFetch(business.getBusinessId());

                // Convert to pageable result
                int start = (int) pageable.getOffset();
                int end = Math.min((start + pageable.getPageSize()), allActiveGuides.size());

                List<TrackingGuide> pageContent = allActiveGuides.subList(start, end);

                List<GuideResponseDto> guideResponses = pageContent.stream()
                                .map(this::mapToResponseDto)
                                .collect(Collectors.toList());

                return GuideListResponseDto.builder()
                                .guides(guideResponses)
                                .total_count(allActiveGuides.size())
                                .page(pageable.getPageNumber())
                                .size(pageable.getPageSize())
                                .has_next(end < allActiveGuides.size())
                                .has_previous(start > 0)
                                .build();
        }

        private GuideResponseDto mapToResponseDto(TrackingGuide guide) {
                return GuideResponseDto.builder()
                                .guide_id(guide.getGuideId())
                                .guide_number(guide.getGuideNumber())
                                .business_id(guide.getBusiness().getBusinessId())
                                .business_name(guide.getBusiness().getBusinessName())
                                .origin_branch_id(guide.getOriginBranch().getBranchId())
                                .origin_branch_name(guide.getOriginBranch().getBranchName())
                                .courier_id(guide.getCourier() != null ? guide.getCourier().getUserId() : null)
                                .courier_name(guide.getCourier() != null ? guide.getCourier().getFullName() : null)
                                .coordinator_id(guide.getCoordinator() != null ? guide.getCoordinator().getUserId()
                                                : null)
                                .coordinator_name(guide.getCoordinator() != null ? guide.getCoordinator().getFullName()
                                                : null)
                                .current_state_id(guide.getCurrentState().getStateId())
                                .current_state_name(guide.getCurrentState().getStateName())
                                .current_state_description(guide.getCurrentState().getDescription())
                                .state_is_final(guide.getCurrentState().getIsFinal())
                                .base_price(guide.getBasePrice())
                                .courier_commission(guide.getCourierCommission())
                                .recipient_name(guide.getRecipientName())
                                .recipient_phone(guide.getRecipientPhone())
                                .recipient_address(guide.getRecipientAddress())
                                .recipient_city(guide.getRecipientCity())
                                .recipient_state(guide.getRecipientState())
                                .observations(guide.getObservations())
                                .assignment_accepted(guide.getAssignmentAccepted())
                                .assignment_accepted_at(guide.getAssignmentAcceptedAt())
                                .created_at(guide.getCreatedAt())
                                .updated_at(guide.getUpdatedAt())
                                .assignment_date(guide.getAssignmentDate())
                                .pickup_date(guide.getPickupDate())
                                .delivery_date(guide.getDeliveryDate())
                                .cancellation_date(guide.getCancellationDate())
                                .build();
        }
}