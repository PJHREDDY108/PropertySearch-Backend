package com.example.addressapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressDTO {
    private Long id;
    private String street;
    private String city;
    private String state;
    private String zip;
    private String propertyType;
    private Double price;
    private Integer bedrooms;
    private Integer bathrooms;
    private Integer squareFeet;
}