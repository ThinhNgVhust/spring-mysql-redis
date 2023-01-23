package com.irsyad.springmysqlredis.api;

import com.irsyad.springmysqlredis.model.Dog;
import com.irsyad.springmysqlredis.service.DogService;
import com.irsyad.springmysqlredis.util.ResponseUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@Slf4j
public class DogREST {

    @Autowired
    DogService dogService;
    @PostMapping("/dogs/random")
    public ResponseEntity<Dog>random(){
    	try {
    		for (int i = 0; i < 1000000; i++) {
				Dog dog = new Dog();
				dog.setName(String.valueOf(i));
				dogService.saveDog(dog);
			}
    		 return new ResponseEntity<>(HttpStatus.NO_CONTENT);

        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/dogs/cache")
    public ResponseEntity<Void>cache(){
    	System.out.println("/dogs/cache");
    	try {
    		for (int i = 0; i < 1000000; i++) {
    			dogService.findDogById((long)i+1);
    			System.out.println("find");
			}
    		 return new ResponseEntity<>(HttpStatus.NO_CONTENT);

        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/dogs")
    public ResponseEntity<Page<Dog>> getAllDogs(
            @RequestParam(value = "page", defaultValue = ResponseUtils.DEFAULT_PAGE_NUM) Integer page,
            @RequestParam(value = "size", defaultValue = ResponseUtils.DEFAULT_PAGE_SIZE) Integer size) {
        try {

            Page<Dog> dogs = dogService.getAllDogs(page, size);
            if (dogs.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(dogs, HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/dogs/{id}")
    public ResponseEntity<Dog> getDogById(@PathVariable("id") long id) {
        Dog dog = dogService.findDogById(id);
        log.info("Dog DogREST {}", dog);
        if (dog != null){
            return new ResponseEntity<>(dog, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping("/dogs")
    public ResponseEntity<Dog> createDog(@RequestBody Dog dog) {
        try {
            Dog _dog = dogService.saveDog(dog);
            URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                    .buildAndExpand(_dog.getId()).toUri();

            return ResponseEntity.created(location).build();
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/dogs/{id}")
    public ResponseEntity<Dog> updateTutorial(@PathVariable("id") long id, @RequestBody Dog dog) {
        Optional<Dog> dogData = Optional.ofNullable(dogService.findDogById(id));

        if (dogData.isPresent()) {
            Dog _dog = dogData.get();
            dog.setName(dog.getName());
            return new ResponseEntity<>(dogService.saveDog(_dog), HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/dogs/{id}")
    public ResponseEntity<HttpStatus> deleteDogs(@PathVariable("id") long id) {
        try {
            dogService.deleteDog(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
