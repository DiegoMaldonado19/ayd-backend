package com.ayd.sie.business.infrastructure.web;

import com.ayd.sie.business.application.dto.*;
import com.ayd.sie.business.application.usecases.*;
import com.ayd.sie.shared.infrastructure.security.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/business")
@RequiredArgsConstructor
@Tag(name = "Business", description = "Business operations for tracking guides, cancellations and loyalty benefits")
public class BusinessController {

        private final CreateTrackingGuideUseCase createTrackingGuideUseCase;
        private final UpdateTrackingGuideUseCase updateTrackingGuideUseCase;
        private final GetGuideStatusUseCase getGuideStatusUseCase;
        private final CancelDeliveryUseCase cancelDeliveryUseCase;
        private final GetLoyaltyBenefitsUseCase getLoyaltyBenefitsUseCase;
        private final GetBusinessGuidesUseCase getBusinessGuidesUseCase;

        @PostMapping("/guides")
        @Operation(summary = "Create a new tracking guide", description = "Creates a new tracking guide for delivery. Applies loyalty discounts automatically.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "Guide created successfully"),
                        @ApiResponse(responseCode = "400", description = "Invalid input data"),
                        @ApiResponse(responseCode = "401", description = "Unauthorized"),
                        @ApiResponse(responseCode = "403", description = "Forbidden - User is not a business"),
                        @ApiResponse(responseCode = "404", description = "Business or branch not found")
        })
        public ResponseEntity<GuideResponseDto> createGuide(
                        @Valid @RequestBody CreateGuideDto createGuideDto) {

                Integer userId = getCurrentUserId();
                GuideResponseDto response = createTrackingGuideUseCase.execute(createGuideDto, userId);
                return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }

        @PutMapping("/guides/{guideId}")
        @Operation(summary = "Update a tracking guide", description = "Updates a tracking guide before pickup. Cannot be modified after pickup.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Guide updated successfully"),
                        @ApiResponse(responseCode = "400", description = "Invalid update data or guide cannot be updated"),
                        @ApiResponse(responseCode = "401", description = "Unauthorized"),
                        @ApiResponse(responseCode = "403", description = "Forbidden - Guide does not belong to business"),
                        @ApiResponse(responseCode = "404", description = "Guide not found")
        })
        public ResponseEntity<GuideResponseDto> updateGuide(
                        @Parameter(description = "Guide ID") @PathVariable Integer guideId,
                        @Valid @RequestBody UpdateGuideDto updateGuideDto) {

                Integer userId = getCurrentUserId();
                GuideResponseDto response = updateTrackingGuideUseCase.execute(guideId, updateGuideDto, userId);
                return ResponseEntity.ok(response);
        }

        @GetMapping("/guides")
        @Operation(summary = "Get business guides", description = "Retrieves paginated list of tracking guides for the business.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Guides retrieved successfully"),
                        @ApiResponse(responseCode = "401", description = "Unauthorized"),
                        @ApiResponse(responseCode = "404", description = "Business not found")
        })
        public ResponseEntity<GuideListResponseDto> getGuides(
                        @Parameter(description = "Filter only active guides") @RequestParam(required = false, defaultValue = "false") Boolean activeOnly,
                        @PageableDefault(size = 20, sort = "createdAt,desc") Pageable pageable) {

                Integer userId = getCurrentUserId();
                GuideListResponseDto response = activeOnly
                                ? getBusinessGuidesUseCase.executeActiveOnly(userId, pageable)
                                : getBusinessGuidesUseCase.execute(userId, pageable);
                return ResponseEntity.ok(response);
        }

        @GetMapping("/guides/{guideId}")
        @Operation(summary = "Get guide status by ID", description = "Retrieves the current status of a specific tracking guide.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Guide status retrieved successfully"),
                        @ApiResponse(responseCode = "401", description = "Unauthorized"),
                        @ApiResponse(responseCode = "403", description = "Forbidden - Guide does not belong to business"),
                        @ApiResponse(responseCode = "404", description = "Guide not found")
        })
        public ResponseEntity<GuideStatusDto> getGuideStatus(
                        @Parameter(description = "Guide ID") @PathVariable Integer guideId) {

                Integer userId = getCurrentUserId();
                GuideStatusDto response = getGuideStatusUseCase.execute(guideId, userId);
                return ResponseEntity.ok(response);
        }

        @GetMapping("/guides/by-number/{guideNumber}")
        @Operation(summary = "Get guide status by guide number", description = "Retrieves the current status of a tracking guide by its guide number.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Guide status retrieved successfully"),
                        @ApiResponse(responseCode = "401", description = "Unauthorized"),
                        @ApiResponse(responseCode = "403", description = "Forbidden - Guide does not belong to business"),
                        @ApiResponse(responseCode = "404", description = "Guide not found")
        })
        public ResponseEntity<GuideStatusDto> getGuideStatusByNumber(
                        @Parameter(description = "Guide number") @PathVariable String guideNumber) {

                Integer userId = getCurrentUserId();
                GuideStatusDto response = getGuideStatusUseCase.executeByGuideNumber(guideNumber, userId);
                return ResponseEntity.ok(response);
        }

        @PostMapping("/guides/{guideId}/cancel")
        @Operation(summary = "Cancel a tracking guide", description = "Cancels a tracking guide before pickup. Applies penalties based on loyalty level.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Guide cancelled successfully"),
                        @ApiResponse(responseCode = "400", description = "Invalid cancellation data or guide cannot be cancelled"),
                        @ApiResponse(responseCode = "401", description = "Unauthorized"),
                        @ApiResponse(responseCode = "403", description = "Forbidden - Guide does not belong to business"),
                        @ApiResponse(responseCode = "404", description = "Guide not found")
        })
        public ResponseEntity<CancellationResponseDto> cancelGuide(
                        @Parameter(description = "Guide ID") @PathVariable Integer guideId,
                        @Valid @RequestBody CancelGuideDto cancelGuideDto) {

                Integer userId = getCurrentUserId();
                CancellationResponseDto response = cancelDeliveryUseCase.execute(guideId, cancelGuideDto, userId);
                return ResponseEntity.ok(response);
        }

        @GetMapping("/loyalty/benefits")
        @Operation(summary = "Get loyalty benefits", description = "Retrieves current loyalty level benefits and status for the business.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Loyalty benefits retrieved successfully"),
                        @ApiResponse(responseCode = "401", description = "Unauthorized"),
                        @ApiResponse(responseCode = "404", description = "Business or loyalty level not found")
        })
        public ResponseEntity<LoyaltyBenefitsDto> getLoyaltyBenefits() {
                Integer userId = getCurrentUserId();
                LoyaltyBenefitsDto response = getLoyaltyBenefitsUseCase.execute(userId);
                return ResponseEntity.ok(response);
        }

        private Integer getCurrentUserId() {
                return SecurityUtils.getCurrentUserId();
        }
}