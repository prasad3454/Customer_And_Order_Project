package com.customerorder.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.customerorder.entity.Order;

public interface OrderRepository extends JpaRepository<Order, Long>{

	List<Order> findByCustomerId(Long customerId);

}
