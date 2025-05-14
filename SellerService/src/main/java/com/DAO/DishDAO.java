package com.DAO;


import com.models.Dish;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;

@Stateless
public class DishDAO {
    @PersistenceContext(unitName="sellerPU")
    private EntityManager em;

    public List<Dish> findBySeller(Long sellerId) {
        return em.createQuery("SELECT d FROM Dish d WHERE d.seller.id = :id", Dish.class)
                .setParameter("id", sellerId)
                .getResultList();
    }

    public void save(Dish d) { em.persist(d); }
    public Dish update(Dish d) { return em.merge(d); }
    public Dish find(Long id) { return em.find(Dish.class, id); }
}