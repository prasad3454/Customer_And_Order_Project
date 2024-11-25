package com.customerorder.entity;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Customer {

	    @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;

	    @NotBlank(message = "Name is mandatory")
	    @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
	    private String name;

	    @NotBlank(message = "Email is mandatory")
	    @Email(message = "Email should be valid")
	    private String email;

	    @NotBlank(message = "Password is mandatory")
	    @Size(min = 6, message = "Password must be at least 6 characters long")
	    private String password;

	    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
	    @JsonIgnore
	    private List<Order> orders = new ArrayList<>();

	   
	}
