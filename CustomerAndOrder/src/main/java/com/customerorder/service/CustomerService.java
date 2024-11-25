package com.customerorder.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.customerorder.entity.Customer;
import com.customerorder.exception.ResourceNotFoundException;
import com.customerorder.repository.CustomerRepository;
import com.customerorder.specifications.CustomerSpecification;
//import com.customerorder.utils.ExcelHelper;

@Service
public class CustomerService {

	@Autowired
	private CustomerRepository customerRepository;

	public Customer createCustomer(Customer customer) {
		return customerRepository.save(customer);
	}

	public List<Customer> getAllCustomers() {
		return customerRepository.findAll();
	}

	public Optional<Customer> getCustomerById(Long id) {
		return customerRepository.findById(id);
	}

	public Customer updateCustomer(Long id, Customer customerDetails) {
		Customer customer = customerRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

		customer.setName(customerDetails.getName());
		customer.setEmail(customerDetails.getEmail());

		return customerRepository.save(customer);
	}

	public void deleteCustomer(Long id) {
		Customer customer = customerRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
		customerRepository.delete(customer);
	}

	public Page<Customer> getCustomers(int page, int size, String sortField, String sortDirection, String search,
			String name, String email) {
		Sort sort = Sort.by(
				sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.Direction.ASC : Sort.Direction.DESC,
				sortField);
		PageRequest pageable = PageRequest.of(page, size, sort);

		Specification<Customer> spec = CustomerSpecification.filterCustomers(search, name, email);
		return customerRepository.findAll(spec, pageable);
	}

//	public Slice<Customer> getAllCustomers1() {
//		// TODO Auto-generated method stub
//		return customerRepository.findAll();
//	}

//	public void saveCustomersFromExcel(MultipartFile file) throws IOException {
//		List<Customer> customers = ExcelHelper.excelToCustomers(file.getInputStream());
//		customerRepository.saveAll(customers);
//	}
//
//	public ByteArrayInputStream exportCustomersToExcel() throws IOException {
//		List<Customer> customers = customerRepository.findAll();
//		return ExcelHelper.customersToExcel(customers);
//	}
}
