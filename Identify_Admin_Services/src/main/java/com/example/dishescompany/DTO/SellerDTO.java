package com.example.dishescompany.DTO;

public class SellerDTO {
    private Long id;
    private String companyName;
    private String username;

    public SellerDTO() {
    }

    public SellerDTO(Long id, String companyName, String username) {
        this.id = id;
        this.companyName = companyName;
        this.username = username;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }


}
