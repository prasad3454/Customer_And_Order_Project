package com.customerorder.specifications;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.customerorder.entity.Customer;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public class CustomerSpecification {
    public static Specification<Customer> filterCustomers(String search, String name, String email) {
        return (Root<Customer> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (search != null && !search.isEmpty()) {
                Predicate searchPredicate = criteriaBuilder.or(
                        criteriaBuilder.like(root.get("name"), "%" + search + "%"),
                        criteriaBuilder.like(root.get("email"), "%" + search + "%")
                );
                predicates.add(searchPredicate);
            }

            if (name != null && !name.isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("name"), name));
            }

            if (email != null && !email.isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("email"), email));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
