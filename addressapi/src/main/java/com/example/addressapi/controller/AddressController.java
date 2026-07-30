package com.example.addressapi.controller;

import com.example.addressapi.dto.AddressDTO;
import com.example.addressapi.service.AddressService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/addresses")
public class AddressController {

    private final AddressService addressService;

    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    @GetMapping("/search")
    public List<AddressDTO> search(@RequestParam(defaultValue = "") String street,
                                   @RequestParam(defaultValue = "") String city,
                                   @RequestParam(defaultValue = "") String zip) {
        return addressService.search(street, city, zip);
    }

    @GetMapping
    public List<AddressDTO> findAll() {
        return addressService.findAll();
    }

    @PostMapping
    public ResponseEntity<AddressDTO> create(@RequestBody AddressDTO dto) {
        return ResponseEntity.ok(addressService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody AddressDTO dto) {
        try {
            return ResponseEntity.ok(addressService.update(id, dto));
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            addressService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.notFound().build();
        }
    }
}