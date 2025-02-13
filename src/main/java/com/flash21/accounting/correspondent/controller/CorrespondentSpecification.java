package com.flash21.accounting.correspondent.controller;

import com.flash21.accounting.correspondent.dto.request.CorrespondentRequest;
import com.flash21.accounting.correspondent.dto.response.CorrespondentResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

public interface CorrespondentSpecification {

    @Operation(summary = "거래처 등록", description = "거래처를 등록합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "거래처 등록 성공")
    })
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<CorrespondentResponse> createCorrespondent(
        @Parameter(
            description = "거래처 정보",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(type = "object", implementation = CorrespondentRequest.class)
            )
        )
        @RequestPart("json") @Valid CorrespondentRequest correspondentRequest,
        @Parameter(
            description = "사업자등록증",
            content = @Content(
                mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                schema = @Schema(type = "string", format = "binary")
            )
        )
        @RequestPart(name = "businessRegNumberImage", required = false) MultipartFile businessRegNumberImage,
        @Parameter(
            description = "통장사본",
            content = @Content(
                mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                schema = @Schema(type = "string", format = "binary")
            )
        )
        @RequestPart(name = "bankBookImage", required = false) MultipartFile bankBookImage);

    @Operation(summary = "거래처 조회", description = "거래처를 검색합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "거래처 조회 성공")
    })
    @GetMapping
    ResponseEntity<List<CorrespondentResponse>> getCorrespondents(
        @Parameter(description = "조회 카테고리")
        @RequestParam(required = false, name = "condition") String condition,
        @Parameter(description = "조회할 거래처명")
        @RequestParam(required = false, name = "value") String value);

    @Operation(summary = "거래처 수정", description = "거래처를 수정합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "거래처 수정 성공")
    })
    @PutMapping(path = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<Void> updateCorrespondent(
        @Parameter(description = "거래처 id") @PathVariable(name = "id") Long correspondentId,
        @RequestPart("json") @Valid CorrespondentRequest correspondentRequest,
        @Parameter(
            description = "사업자등록증",
            content = @Content(
                mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                schema = @Schema(type = "string", format = "binary")
            )
        )
        @RequestPart(name = "businessRegNumberImage", required = false) MultipartFile businessRegNumberImage,
        @Parameter(
            description = "통장사본",
            content = @Content(
                mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                schema = @Schema(type = "string", format = "binary")
            )
        )
        @RequestPart(name = "bankBookImage", required = false) MultipartFile bankBookImage);

    @Operation(summary = "거래처 삭제", description = "거래처를 삭제합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "거래처 삭제 성공")
    })
    @DeleteMapping("/{id}")
    ResponseEntity<Void> deleteCorrespondent(
        @Parameter(description = "거래처 id") @PathVariable(name = "id") Long correspondentId);
}
