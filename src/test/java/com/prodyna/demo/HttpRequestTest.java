package com.prodyna.demo;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class HttpRequestTest {

	@LocalServerPort
	private int port;

	@Autowired
	private TestRestTemplate restTemplate;

	@Test
	public void greetingShouldReturnDefaultMessage() throws Exception {
		com.fasterxml.jackson.databind.ObjectMapper objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();
		Person requestPerson;
		ResponseEntity<String> response;

		//test that too short names are rejected
		requestPerson = new Person(null, "Li");
		response = this.restTemplate.postForEntity("http://localhost:" + port + "/persons", requestPerson, String.class);
		assertThat(response.getStatusCode()).isNotEqualTo(org.springframework.http.HttpStatus.OK);

		//test that too long names are rejected
		requestPerson = new Person(null, "Willibrordus Martinus Pancratius \"Wil\" van der Aalst");
		response = this.restTemplate.postForEntity("http://localhost:" + port + "/persons", requestPerson, String.class);
		assertThat(response.getStatusCode()).isNotEqualTo(org.springframework.http.HttpStatus.OK);

		//post a guy and check that the response has same name
		requestPerson = new Person(null, "Testman");
		response = this.restTemplate.postForEntity("http://localhost:" + port + "/persons", requestPerson, String.class);
		Person postPerson = objectMapper.readValue(response.getBody(), Person.class);
		assertThat(postPerson.getName()).isEqualTo(requestPerson.getName());

		//check that querying our guy by id has equal name and id to the prev. response
		response = this.restTemplate.getForEntity("http://localhost:" + port + "/persons/" + postPerson.getId(), String.class);
		Person getPerson = objectMapper.readValue(response.getBody(), Person.class);
		assertThat(postPerson.getName()).isEqualTo(getPerson.getName());
		assertThat(postPerson.getId()).isEqualTo(getPerson.getId());

		//change the name (and gender) of our guy and check if she exists in the list of persons
		final String changedName = "Testwoman";
		requestPerson = new Person(null, changedName);
		this.restTemplate.put("http://localhost:" + port + "/persons/" + postPerson.getId(), requestPerson);
		response = this.restTemplate.getForEntity("http://localhost:" + port + "/persons", String.class);
		Person[] allPersons = objectMapper.readValue(response.getBody(), Person[].class);
		boolean hasWhatWeWant = java.util.Arrays.stream(allPersons).anyMatch(x -> x.getName().equals(changedName) && x.getId().equals(postPerson.getId()));
		assertThat(hasWhatWeWant).isEqualTo(true);

		//delete her and check that no person with her id exists anymore
		this.restTemplate.delete("http://localhost:" + port + "/persons/" + postPerson.getId());
		response = this.restTemplate.getForEntity("http://localhost:" + port + "/persons", String.class);
		allPersons = objectMapper.readValue(response.getBody(), Person[].class);
		hasWhatWeWant = java.util.Arrays.stream(allPersons).anyMatch(x -> x.getId().equals(postPerson.getId()));
		assertThat(hasWhatWeWant).isEqualTo(false);
	}
}