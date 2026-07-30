package com.example.addressapi.service;

import com.example.addressapi.dto.AddressDTO;
import com.example.addressapi.entity.Address;
import com.example.addressapi.repository.AddressRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddressServiceTest {

    @Mock
    private AddressRepository addressRepository;

    @InjectMocks
    private AddressService addressService;

    private Address sampleAddress;

    @BeforeEach
    void setUp() {
        sampleAddress = new Address(1L, "123 Main St", "Springfield", "IL", "62704",
                "House", 250000.0, 3, 2, 1500);
    }

    @Test
    void findAll_returnsListOfAddressDTOs() {
        when(addressRepository.findAll()).thenReturn(List.of(sampleAddress));

        List<AddressDTO> result = addressService.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCity()).isEqualTo("Springfield");
        verify(addressRepository, times(1)).findAll();
    }

    @Test
    void search_trimsInputsAndDelegatesToRepository() {
        when(addressRepository.search("main", "spring", "62704"))
                .thenReturn(List.of(sampleAddress));

        List<AddressDTO> result = addressService.search("  main  ", "  spring  ", "  62704  ");

        assertThat(result).hasSize(1);
        verify(addressRepository).search("main", "spring", "62704");
    }

    @Test
    void search_convertsNullsToEmptyStrings() {
        when(addressRepository.search("", "", "")).thenReturn(List.of());

        List<AddressDTO> result = addressService.search(null, null, null);

        assertThat(result).isEmpty();
        verify(addressRepository).search("", "", "");
    }

    @Test
    void create_savesAndReturnsNewAddress_withIdReset() {
        AddressDTO incoming = new AddressDTO(999L, "456 Oak Ave", "Chicago", "IL",
                "60601", "Condo", 300000.0, 2, 1, 900);

        when(addressRepository.save(any(Address.class))).thenAnswer(invocation -> {
            Address original = invocation.getArgument(0);
            // Return a NEW object instead of mutating the one we were passed,
            // so the original argument (used for verification) stays untouched.
            return new Address(1L, original.getStreet(), original.getCity(), original.getState(),
                    original.getZip(), original.getPropertyType(), original.getPrice(),
                    original.getBedrooms(), original.getBathrooms(), original.getSquareFeet());
        });

        AddressDTO result = addressService.create(incoming);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getCity()).isEqualTo("Chicago");
        verify(addressRepository).save(argThat(a -> a.getId() == null));
    }

    @Test
    void update_whenAddressExists_updatesAndReturnsIt() {
        AddressDTO updateDto = new AddressDTO(null, "New St", "New City", "TX",
                "77000", "House", 400000.0, 4, 3, 2000);

        when(addressRepository.findById(1L)).thenReturn(Optional.of(sampleAddress));
        when(addressRepository.save(any(Address.class))).thenReturn(sampleAddress);

        AddressDTO result = addressService.update(1L, updateDto);

        assertThat(result.getStreet()).isEqualTo("New St");
        verify(addressRepository).save(sampleAddress);
    }

    @Test
    void update_whenAddressDoesNotExist_throwsEntityNotFoundException() {
        when(addressRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> addressService.update(99L, new AddressDTO()))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("99");

        verify(addressRepository, never()).save(any());
    }

    @Test
    void delete_whenAddressExists_callsRepositoryDelete() {
        when(addressRepository.findById(1L)).thenReturn(Optional.of(sampleAddress));

        addressService.delete(1L);

        verify(addressRepository).delete(sampleAddress);
    }

    @Test
    void delete_whenAddressDoesNotExist_throwsEntityNotFoundException() {
        when(addressRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> addressService.delete(99L))
                .isInstanceOf(EntityNotFoundException.class);

        verify(addressRepository, never()).delete(any());
    }
}