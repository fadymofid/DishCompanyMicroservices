package com.example.seller.DAO;
import com.example.seller.models.Seller;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.sql.Connection;

@Stateless
public class SellerDAO {

    private Connection conn;

    public SellerDAO(Connection conn){
        this.conn =conn;
    }

    private EntityManager em;

    public Seller find(Long id)             { return em.find(Seller.class, id); }
    public Seller findByUsername(String u) {
        return em.createQuery("SELECT s FROM Seller s WHERE s.username=:u", Seller.class)
                .setParameter("u", u)
                .getSingleResult();
    }
    public void save(Seller seller) {
        em.persist(seller);
    }
}
