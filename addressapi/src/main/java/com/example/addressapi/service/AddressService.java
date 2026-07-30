package com.example.addressapi.service;

import com.example.addressapi.dto.AddressDTO;
import com.example.addressapi.entity.Address;
import com.example.addressapi.repository.AddressRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressService {

    private final AddressRepository addressRepository;

    public AddressService(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    public List<AddressDTO> search(String street, String city, String zip) {
        return addressRepository.search(
                        street == null ? "" : street.trim(),
                        city == null ? "" : city.trim(),
                        zip == null ? "" : zip.trim())
                .stream().map(this::toDto).toList();
    }

    public List<AddressDTO> findAll() {
        return addressRepository.findAll().stream().map(this::toDto).toList();
    }

    public AddressDTO create(AddressDTO dto) {
        dto.setId(null);
        return toDto(addressRepository.save(toEntity(dto)));
    }

    public AddressDTO update(Long id, AddressDTO dto) {
        Address existing = getEntityOrThrow(id);
        existing.setStreet(dto.getStreet());
        existing.setCity(dto.getCity());
        existing.setState(dto.getState());
        existing.setZip(dto.getZip());
        existing.setPropertyType(dto.getPropertyType());
        existing.setPrice(dto.getPrice());
        existing.setBedrooms(dto.getBedrooms());
        existing.setBathrooms(dto.getBathrooms());
        existing.setSquareFeet(dto.getSquareFeet());
        return toDto(addressRepository.save(existing));
    }

    public void delete(Long id) {
        addressRepository.delete(getEntityOrThrow(id));
    }

    private Address getEntityOrThrow(Long id) {
        return addressRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Address not found with id " + id));
    }

    private AddressDTO toDto(Address a) {
        return new AddressDTO(a.getId(), a.getStreet(), a.getCity(), a.getState(), a.getZip(),
                a.getPropertyType(), a.getPrice(), a.getBedrooms(), a.getBathrooms(), a.getSquareFeet());
    }

    private Address toEntity(AddressDTO d) {
        return new Address(d.getId(), d.getStreet(), d.getCity(), d.getState(), d.getZip(),
                d.getPropertyType(), d.getPrice(), d.getBedrooms(), d.getBathrooms(), d.getSquareFeet());
    }
}