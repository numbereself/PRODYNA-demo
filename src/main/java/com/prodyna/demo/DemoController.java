package com.prodyna.demo;

import java.util.*;

import org.springframework.beans.factory.annotation.*;
import org.springframework.http.*;

import org.springframework.web.bind.annotation.*;

import org.springframework.beans.factory.annotation.Autowired;

@RestController
public class DemoController {

	@org.springframework.beans.factory.annotation.Autowired
	private PersonRepository repo;

	@GetMapping("/")
	public String index() {
		return "Greetings from Spring Boot!";
	}

	@GetMapping("/persons")
	public List<Person> list() {
		return repo.findAll();
	}

	@GetMapping("/persons/{id}")
public ResponseEntity<Person> get(@PathVariable Integer id) {
	try {
		Person person = repo.findById(id).get();
		return new ResponseEntity<Person>(person, HttpStatus.OK);
	} catch (NoSuchElementException e) {
		return new ResponseEntity<Person>(HttpStatus.NOT_FOUND);
	}		
}

@PostMapping("/persons")
public ResponseEntity<Person> add(@RequestBody Person person) {
	repo.save(person);
	return new ResponseEntity<Person>(person, HttpStatus.OK);
}

@PutMapping("/persons/{id}")
public ResponseEntity<?> update(@RequestBody Person person, @PathVariable Integer id) {
	try {
		Person existPerson = repo.findById(id).get();
		person.setId(existPerson.getId());
		repo.save(person);
		return new ResponseEntity<>(HttpStatus.OK);
	} catch (NoSuchElementException e) {
		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}		
}

@DeleteMapping("/persons/{id}")
public void delete(@PathVariable Integer id) {
	repo.deleteById(id);
}

}