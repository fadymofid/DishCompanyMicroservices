package com.example.order_shipping.DTO;



import java.util.Set;

public class ShippingCompanyResponse {
    private Long id;
    private String name;
    private Set<String> coverageRegions;

    public ShippingCompanyResponse() {}

    public ShippingCompanyResponse(Long id, String name, Set<String> coverageRegions) {
        this.id              = id;
        this.name            = name;
        this.coverageRegions = coverageRegions;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<String> getCoverageRegions() {
        return coverageRegions;
    }

    public void setCoverageRegions(Set<String> coverageRegions) {
        this.coverageRegions = coverageRegions;
    }
}
