package com.irsyad.springmysqlredis.service.impl;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import com.irsyad.springmysqlredis.exception.ResourceNotFoundException;
import com.irsyad.springmysqlredis.model.Dog;
import com.irsyad.springmysqlredis.repository.DogRepository;
import com.irsyad.springmysqlredis.service.DogService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class DogServiceImpl implements DogService {

	private final DogRepository dogRepository;
	private final RedisTemplate<String, Dog> redisTemplate;

	 @Override
	    public Dog findDogById(Long id) {
	        var key = "dog_" + id;
	        final ValueOperations<String, Dog> operations = redisTemplate.opsForValue();
	        final boolean hasKey = redisTemplate.hasKey(key);
	        if (hasKey) {
	            final Dog post = operations.get(key);
	            log.info("DogServiceImpl.findDogById() : cache post >> " + post.toString());
	            return post;
	        }
	        final Optional<Dog> dog = dogRepository.findById(id);
	        if(dog.isPresent()) {
	            operations.set(key, dog.get());
	            log.info("DogServiceImpl.findDogById() : cache insert >> " + dog.get().toString());
	            return dog.get();
	        } else {
	            throw new ResourceNotFoundException();
	        }
	    }

	    @Override
	    public Page<Dog> getAllDogs(Integer page, Integer size) {
	        Pageable pageable = PageRequest.of(page, size, Sort.by(
	                Sort.Order.desc("id")));
	        return dogRepository.findAll(pageable);
	    }

	    @Override
	    public Dog saveDog(Dog dog) {
	        return dogRepository.save(dog);
	    }

	    @Override
	    public Dog updateDog(Dog dog) {
	        final String key = "emp_" + dog.getId();
	        final boolean hasKey = redisTemplate.hasKey(key);
	        if (hasKey) {
	            redisTemplate.delete(key);
	            log.info("DogServiceImpl.updateDog() : cache delete >> " + dog.toString());
	        }
	        return dogRepository.save(dog);
	    }

	    @Override
	    public void deleteDog(Long id) {
	        final String key = "emp_" + id;
	        final boolean hasKey = redisTemplate.hasKey(key);
	        if (hasKey) {
	            redisTemplate.delete(key);
	            log.info("DogServiceImpl.deletePost() : cache delete ID >> " + id);
	        }
	        final Optional<Dog> dog = dogRepository.findById(id);
	        if(dog.isPresent()) {
	            dogRepository.delete(dog.get());
	        } else {
	            throw new ResourceNotFoundException();
	        }
	    }
}
