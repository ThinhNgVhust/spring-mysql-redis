package com.irsyad.springmysqlredis.service;
import org.springframework.data.domain.Page;

import com.irsyad.springmysqlredis.model.Dog;

public interface DogService {

	Dog findDogById(Long id);

    Page<Dog> getAllDogs(Integer page, Integer size);

    Dog saveDog(Dog dog);

    Dog updateDog(Dog dog);

    void deleteDog(Long id);

}
