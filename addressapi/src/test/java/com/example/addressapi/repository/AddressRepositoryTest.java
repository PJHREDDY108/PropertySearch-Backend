package com.example.addressapi.repository;

import com.example.addressapi.AddressapiApplication;
import com.example.addressapi.entity.Address;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = AddressapiApplication.class)
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
@Transactional
class AddressRepositoryTest {

    @Autowired
    private AddressRepository addressRepository;

    @BeforeEach
    void setUp() {
        addressRepository.deleteAll();
        addressRepository.save(new Address(null, "123 MG Road", "Chennai", "TN", "600001",
                "Apartment", 5000000.0, 3, 2, 1200));
        addressRepository.save(new Address(null, "45 Anna Salai", "Chennai", "TN", "600002",
                "Villa", 9000000.0, 4, 3, 2500));
        addressRepository.save(new Address(null, "9 Park Street", "Kolkata", "WB", "700016",
                "House", 4000000.0, 2, 1, 900));
    }

    @Test
    void findAll_returnsAllSavedAddresses() {
        List<Address> all = addressRepository.findAll();
        assertThat(all).hasSize(3);
    }

    @Test
    void search_byCity_returnsMatchingAddressesOnly() {
        List<Address> result = addressRepository.search("", "chennai", "");
        assertThat(result).hasSize(2);
        assertThat(result).allMatch(a -> a.getCity().equalsIgnoreCase("Chennai"));
    }

    @Test
    void search_byStreetPartialMatch_isCaseInsensitive() {
        List<Address> result = addressRepository.search("mg road", "", "");
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStreet()).isEqualTo("123 MG Road");
    }

    @Test
    void search_byZip_returnsPartialMatches() {
        List<Address> result = addressRepository.search("", "", "600");
        assertThat(result).hasSize(2);
    }

    @Test
    void search_withAllEmptyFilters_returnsEverything() {
        List<Address> result = addressRepository.search("", "", "");
        assertThat(result).hasSize(3);
    }

    @Test
    void search_withNoMatches_returnsEmptyList() {
        List<Address> result = addressRepository.search("", "mumbai", "");
        assertThat(result).isEmpty();
    }
}