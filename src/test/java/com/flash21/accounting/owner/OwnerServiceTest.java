package com.flash21.accounting.owner;

import com.flash21.accounting.common.exception.AccountingException;
import com.flash21.accounting.common.exception.errorcode.OwnerErrorCode;
import com.flash21.accounting.common.exception.errorcode.UserErrorCode;
import com.flash21.accounting.owner.domain.Owner;
import com.flash21.accounting.owner.dto.request.OwnerRequest;
import com.flash21.accounting.owner.dto.response.OwnerResponse;
import com.flash21.accounting.owner.repository.OwnerRepository;
import com.flash21.accounting.owner.service.OwnerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.flash21.accounting.common.ErrorCodeAssertions.assertErrorCode;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class OwnerServiceTest {

    @Mock
    private OwnerRepository ownerRepository;

    @InjectMocks
    private OwnerService ownerService;

    private OwnerRequest ownerRequest;
    private Owner owner;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        ownerRequest = new OwnerRequest(
                "John Doe",
                "010-1234-5678",
                "john@example.com",
                "02-987-6543"
        );

        owner = Owner.builder()
                .ownerId(1L)
                .name("John Doe")
                .phoneNumber("010-1234-5678")
                .email("john@example.com")
                .faxNumber("02-987-6543")
                .build();
    }

    @Test
    @DisplayName("Create Owner - Success")
    void createOwner_Success() {
        when(ownerRepository.save(any(Owner.class))).thenReturn(owner);

        OwnerResponse response = ownerService.create(ownerRequest);

        assertNotNull(response);
        assertEquals(owner.getOwnerId(), response.ownerId());
        assertEquals(owner.getName(), response.name());
        verify(ownerRepository, times(1)).save(any(Owner.class));
    }

    @Test
    @DisplayName("Read All Owners - Success")
    void readAllOwners_Success() {
        when(ownerRepository.findAll()).thenReturn(Arrays.asList(owner));

        List<OwnerResponse> responses = ownerService.readAll();

        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals(owner.getName(), responses.get(0).name());
        verify(ownerRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Read Owner by ID - Success")
    void readOwnerById_Success() {
        when(ownerRepository.findById(1L)).thenReturn(Optional.of(owner));

        OwnerResponse response = ownerService.readById(1L);

        assertNotNull(response);
        assertEquals(owner.getOwnerId(), response.ownerId());
        verify(ownerRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Read Owner by ID - Not Found")
    void readOwnerById_NotFound() {
        when(ownerRepository.findById(1L)).thenReturn(Optional.empty());

        assertErrorCode(OwnerErrorCode.OWNER_NOT_FOUND,
                () -> ownerService.readById(1L));

        verify(ownerRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Update Owner - Success")
    void updateOwner_Success() {
        when(ownerRepository.findById(1L)).thenReturn(Optional.of(owner));

        OwnerResponse response = ownerService.update(1L, ownerRequest);

        assertNotNull(response);
        assertEquals(owner.getOwnerId(), response.ownerId());
        assertEquals(ownerRequest.name(), response.name());
        verify(ownerRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Update Owner - Not Found")
    void updateOwner_NotFound() {
        when(ownerRepository.findById(1L)).thenReturn(Optional.empty());

        assertErrorCode(OwnerErrorCode.OWNER_NOT_FOUND,
                () -> ownerService.update(1L, ownerRequest));
        verify(ownerRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Delete Owner - Success")
    void deleteOwner_Success() {
        when(ownerRepository.findById(1L)).thenReturn(Optional.of(owner));

        Long deletedId = ownerService.delete(1L);

        assertEquals(1L, deletedId);
        verify(ownerRepository, times(1)).findById(1L);
        verify(ownerRepository, times(1)).delete(owner);
    }

    @Test
    @DisplayName("Delete Owner - Not Found")
    void deleteOwner_NotFound() {
        when(ownerRepository.findById(1L)).thenReturn(Optional.empty());

        assertErrorCode(OwnerErrorCode.OWNER_NOT_FOUND,
                () -> ownerService.delete(1L));

        verify(ownerRepository, times(1)).findById(1L);
        verify(ownerRepository, never()).delete(any(Owner.class));
    }
}
