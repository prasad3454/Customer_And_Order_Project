package com.customerorder.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.customerorder.entity.Customer;
import com.customerorder.jwtConfig.JwtProvider;
import com.customerorder.repository.CustomerRepository;
import com.customerorder.request.AuthRequest;
import com.customerorder.response.AuthResponse;
import com.customerorder.service.CustomService;

@RestController
@RequestMapping("/auth")
public class AuthRestController {
	
	@Autowired
	private CustomerRepository customerRepo;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private CustomService serviceImpl;
	
	@PostMapping("/signup")
	public ResponseEntity<AuthResponse> createUserHandler(@RequestBody Customer entity) throws Exception{
		
		Customer isEmailExist = customerRepo.findByEmail(entity.getEmail());
		
		if (isEmailExist != null) {
			throw new Exception("Email is Already Used in Another Account");
		}
		
		//Create new User
		
		Customer createdUser= new Customer();
		createdUser.setEmail(entity.getEmail());
		createdUser.setName(entity.getName());
		createdUser.setPassword(passwordEncoder.encode(entity.getPassword()));
		
		Customer savedUser = customerRepo.save(createdUser);
		
		Authentication authentication = new  UsernamePasswordAuthenticationToken(savedUser.getEmail(), savedUser.getPassword());
		SecurityContextHolder.getContext().setAuthentication(authentication);
		
		String token =JwtProvider.generateToken(authentication);
		
		
		AuthResponse response =new AuthResponse();
		response.setJwt(token);
		response.setMessage("Registration Success");
		
		return new ResponseEntity<>(response,HttpStatus.OK);
	}
	
	@PostMapping("/signin")
	public ResponseEntity<AuthResponse> signIn(@RequestBody AuthRequest  request){
		
		String username = request.getEmail();
		String password=request.getPassword();
		
		Authentication authentication= authenticate(username,password);
		
		
		SecurityContextHolder.getContext().setAuthentication(authentication);
		
		String token = JwtProvider.generateToken(authentication);
		
		AuthResponse response =new AuthResponse();
		
		response.setJwt(token);
		response.setMessage("Login Success");
		
		return new ResponseEntity<>(response,HttpStatus.OK);
	}
	
	
	private Authentication authenticate(String username, String password) {
		
		UserDetails userDetails= serviceImpl.loadUserByUsername(username);
		
		if (userDetails==null) {
			throw new BadCredentialsException("Invalid Username ....");
		}
		if (!passwordEncoder.matches(password,userDetails.getPassword())) {
			throw new BadCredentialsException("Inavlid  Password....");
		}
		return new UsernamePasswordAuthenticationToken(userDetails, null,userDetails.getAuthorities());
	}
	
	

}