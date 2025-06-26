package com.plant_management.repository;

import com.plant_management.entity.OrdersProducts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrdersProductsRepository extends JpaRepository<OrdersProducts, Integer> {
}