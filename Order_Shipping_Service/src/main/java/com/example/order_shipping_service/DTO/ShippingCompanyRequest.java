package com.example.order_shipping_service.DTO;

import java.util.Set;

public class ShippingCompanyRequest {
    private String name;
    private Set<String> coverageRegions;

    public ShippingCompanyRequest() {}

    public ShippingCompanyRequest(String name, Set<String> coverageRegions) {
        this.name = name;
        this.coverageRegions = coverageRegions;
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
