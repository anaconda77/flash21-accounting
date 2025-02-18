package com.flash21.accounting.owner.controller;

import com.flash21.accounting.correspondent.dto.request.CorrespondentRequest;
import com.flash21.accounting.correspondent.dto.response.CorrespondentResponse;
import com.flash21.accounting.owner.domain.Owner;
import com.flash21.accounting.owner.dto.request.OwnerRequest;
import com.flash21.accounting.owner.dto.response.OwnerResponse;
import com.flash21.accounting.owner.service.OwnerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/owner")
@RequiredArgsConstructor
public class OwnerController {
    private final OwnerService ownerService;

    @PostMapping
    public ResponseEntity<OwnerResponse> createOwner(
            @RequestBody OwnerRequest ownerRequest
            ){
        return new ResponseEntity<>(ownerService.create(ownerRequest),HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<OwnerResponse>> getAllOwner(){
        return new ResponseEntity<>(ownerService.readAll(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OwnerResponse> getOwnerById(@PathVariable("id") Long id){
        return new ResponseEntity<>(ownerService.readById(id), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OwnerResponse> updateOwner(@PathVariable("id") Long id, @RequestBody OwnerRequest ownerRequest){
        return new ResponseEntity<>(ownerService.update(id, ownerRequest), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Long> deleteOwner(@PathVariable("id") Long id){
        return new ResponseEntity<>(ownerService.delete(id), HttpStatus.OK);
    }
}
