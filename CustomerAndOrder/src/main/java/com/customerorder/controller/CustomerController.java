package com.customerorder.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.customerorder.entity.Customer;
import com.customerorder.exception.ResourceNotFoundException;
import com.customerorder.repository.CustomerRepository;
import com.customerorder.service.CustomerService;

import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

	@Autowired
	private CustomerService customerService;

	@Autowired
	private CustomerRepository customerRepo;

	@PostMapping
	public Customer createCustomer(@RequestBody Customer customer) {
		return customerService.createCustomer(customer);
	}

	@GetMapping("/{id}")
	public ResponseEntity<Customer> getCustomerById(@PathVariable Long id) {
		Customer customer = customerService.getCustomerById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
		return ResponseEntity.ok(customer);
	}

	@PutMapping("/{id}")
	public ResponseEntity<Customer> updateCustomer(@PathVariable Long id, @RequestBody Customer customerDetails) {
		Customer updatedCustomer = customerService.updateCustomer(id, customerDetails);
		return ResponseEntity.ok(updatedCustomer);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
		customerService.deleteCustomer(id);
		return ResponseEntity.noContent().build();
	}

	@GetMapping
	public Page<Customer> getCustomers(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "id") String sortField,
			@RequestParam(defaultValue = "ASC") String sortDirection, @RequestParam(required = false) String search,
			@RequestParam(required = false) String name, @RequestParam(required = false) String email) {

		return customerService.getCustomers(page, size, sortField, sortDirection, search, name, email);
	}

	@GetMapping("/export")

	public void exportCustomers(HttpServletResponse response) throws IOException {

		response.setContentType("application/octet-stream");

		response.setHeader("Content-Disposition", "attachment; filename=projects1.xlsx");

		List<Customer> customerDtos = customerService.getAllCustomers();

		try (Workbook workbook = new XSSFWorkbook(); OutputStream os = response.getOutputStream()) {

			Sheet sheet = workbook.createSheet("Customers");

			// Create header row

			Row headerRow = sheet.createRow(0);

			headerRow.createCell(0).setCellValue("Name");

			headerRow.createCell(1).setCellValue("Email");

			headerRow.createCell(2).setCellValue("Password");

			// Create other headers as needed

			// Fill data

			int rowNum = 1;

			for (Customer customer : customerDtos) {

				Row row = sheet.createRow(rowNum++);

				row.createCell(0).setCellValue(customer.getName());

				row.createCell(1).setCellValue(customer.getEmail());

				row.createCell(2).setCellValue(customer.getPassword());

			}

			workbook.write(os);
			workbook.close();
		}

	}

	@PostMapping("/upload")

	public ResponseEntity<String> importCustomers(@RequestParam("file") MultipartFile file) {

		try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {

			Sheet sheet = workbook.getSheetAt(0);

			for (int i = 1; i <= sheet.getLastRowNum(); i++) {

				Row row = sheet.getRow(i);

				Customer customer = new Customer();

				if (row.getCell(0).getCellType() == CellType.STRING) {
					customer.setName(row.getCell(0).getStringCellValue());
				} else if (row.getCell(0).getCellType() == CellType.NUMERIC) {
					customer.setName(String.valueOf((int) row.getCell(0).getNumericCellValue()));
				}

				// Set Email (String)
				if (row.getCell(1).getCellType() == CellType.STRING) {
					customer.setEmail(row.getCell(1).getStringCellValue());
				} else if (row.getCell(1).getCellType() == CellType.NUMERIC) {
					customer.setEmail(String.valueOf((int) row.getCell(1).getNumericCellValue()));
				}

				// Set Password (String)
				if (row.getCell(2).getCellType() == CellType.STRING) {
					customer.setPassword(row.getCell(2).getStringCellValue());
				} else if (row.getCell(2).getCellType() == CellType.NUMERIC) {
					customer.setPassword(String.valueOf((int) row.getCell(2).getNumericCellValue()));
				}

				customerRepo.save(customer);

			}

		} catch (IOException e) {

			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error importing customers: " + e.getMessage());

		}

		return ResponseEntity.ok("Customers imported successfully!");

	}
}
