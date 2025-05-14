package com.DAO;
import com.models.Seller;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Stateless
public class SellerDAO {
    @PersistenceContext(unitName="sellerPU")
    private EntityManager em;

    public Seller find(Long id)             { return em.find(Seller.class, id); }
    public Seller findByUsername(String u) {
        return em.createQuery("SELECT s FROM Seller s WHERE s.username=:u", Seller.class)
                .setParameter("u", u)
                .getSingleResult();
    }
}
