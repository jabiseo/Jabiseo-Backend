package com.jabiseo.api.plan.controller;

import com.jabiseo.api.plan.application.usecase.*;
import com.jabiseo.api.config.auth.AuthMember;
import com.jabiseo.api.config.auth.AuthenticatedMember;
import com.jabiseo.api.plan.dto.response.ActivePlanResponse;
import com.jabiseo.api.plan.dto.request.CreatePlanRequest;
import com.jabiseo.api.plan.dto.request.ModifyPlanRequest;
import com.jabiseo.api.plan.dto.response.calender.PlanCalenderSearchResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/plans")
public class PlanController {

    private final CreatePlanUseCase createPlanUseCase;
    private final FindActivePlanUseCase findActivePlanUseCase;
    private final SearchPlanCalenderUseCase searchPlanCalenderUseCase;
    private final ModifyPlanUseCase modifyPlanUseCase;
    private final DeletePlanUseCase deletePlanUseCase;

    @PostMapping
    public ResponseEntity<Void> create(@Valid @RequestBody CreatePlanRequest request, @AuthenticatedMember AuthMember member) {
        Long planId = createPlanUseCase.execute(member.getMemberId(), request);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(planId)
                .toUri();

        return ResponseEntity.created(location).build();
    }

    @GetMapping("/active")
    public ResponseEntity<ActivePlanResponse> getActive(@AuthenticatedMember AuthMember member) {
        ActivePlanResponse result = findActivePlanUseCase.execute(member.getMemberId());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}/calender")
    public ResponseEntity<PlanCalenderSearchResponse> getPlanCalender(@AuthenticatedMember AuthMember member,
                                                                      @PathVariable("id") Long planId,
                                                                      @RequestParam(name = "year") int year,
                                                                      @RequestParam(name = "month") int month) {
        PlanCalenderSearchResponse result = searchPlanCalenderUseCase.execute(member.getMemberId(), planId, year, month);
        return ResponseEntity.ok(result);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@AuthenticatedMember AuthMember member,
                                       @PathVariable("id") Long planId,
                                       @RequestBody ModifyPlanRequest request) {
        modifyPlanUseCase.execute(planId, member.getMemberId(), request);

        return ResponseEntity.noContent().build();
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@AuthenticatedMember AuthMember member,
                                       @PathVariable("id") Long planId) {
        deletePlanUseCase.execute(planId, member.getMemberId());
        return ResponseEntity.noContent().build();
    }
}
