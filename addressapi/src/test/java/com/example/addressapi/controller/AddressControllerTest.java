package com.example.addressapi.controller;

import com.example.addressapi.dto.AddressDTO;
import com.example.addressapi.service.AddressService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddressControllerTest {

    @Mock
    private AddressService addressService;

    @InjectMocks
    private AddressController addressController;

    @Test
    void getAllAddresses_returnsListFromService() {
        AddressDTO dto = new AddressDTO(1L, "123 Main St", "Springfield", "IL",
                "62704", "House", 250000.0, 3, 2, 1500);
        when(addressService.findAll()).thenReturn(List.of(dto));

        List<AddressDTO> result = addressController.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCity()).isEqualTo("Springfield");
    }

    @Test
    void searchAddresses_passesParamsToService() {
        when(addressService.search("main", "spring", "")).thenReturn(List.of());

        List<AddressDTO> result = addressController.search("main", "spring", "");

        assertThat(result).isEmpty();
        verify(addressService).search("main", "spring", "");
    }

    @Test
    void createAddress_returnsOkWithCreatedAddress() {
        AddressDTO request = new AddressDTO(null, "456 Oak Ave", "Chicago", "IL",
                "60601", "Condo", 300000.0, 2, 1, 900);
        AddressDTO saved = new AddressDTO(1L, "456 Oak Ave", "Chicago", "IL",
                "60601", "Condo", 300000.0, 2, 1, 900);

        when(addressService.create(any(AddressDTO.class))).thenReturn(saved);

        ResponseEntity<AddressDTO> response = addressController.create(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getId()).isEqualTo(1L);
    }

    @Test
    void updateAddress_whenFound_returnsOk() {
        AddressDTO request = new AddressDTO(null, "New St", "New City", "TX",
                "77000", "House", 400000.0, 4, 3, 2000);
        AddressDTO updated = new AddressDTO(1L, "New St", "New City", "TX",
                "77000", "House", 400000.0, 4, 3, 2000);

        when(addressService.update(eq(1L), any(AddressDTO.class))).thenReturn(updated);

        ResponseEntity<?> response = addressController.update(1L, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void updateAddress_whenNotFound_returns404() {
        when(addressService.update(eq(99L), any(AddressDTO.class)))
                .thenThrow(new EntityNotFoundException("Address not found with id 99"));

        ResponseEntity<?> response = addressController.update(99L, new AddressDTO());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void deleteAddress_whenFound_returns204() {
        doNothing().when(addressService).delete(1L);

        ResponseEntity<?> response = addressController.delete(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    void deleteAddress_whenNotFound_returns404() {
        doThrow(new EntityNotFoundException("Address not found with id 99"))
                .when(addressService).delete(99L);

        ResponseEntity<?> response = addressController.delete(99L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}