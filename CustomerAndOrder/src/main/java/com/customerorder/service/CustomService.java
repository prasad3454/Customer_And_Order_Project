package com.customerorder.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.customerorder.entity.Customer;
import com.customerorder.repository.CustomerRepository;

@Service
public class CustomService  implements UserDetailsService {
	
	@Autowired
	private CustomerRepository userRepo;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		Customer customer = userRepo.findByEmail(username);
		if(customer==null) {
			throw new UsernameNotFoundException("User not found with the email"+username);
		}
		
		List<GrantedAuthority> authorities =new ArrayList<>();
		
		return new org.springframework.security.core.userdetails.User(customer.getEmail(),customer.getPassword(),authorities);
	}

	
	
}