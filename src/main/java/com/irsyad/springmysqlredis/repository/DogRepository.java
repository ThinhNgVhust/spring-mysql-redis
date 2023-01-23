package com.irsyad.springmysqlredis.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.irsyad.springmysqlredis.model.Dog;

public interface DogRepository extends JpaRepository<Dog, Long>{
	 Page<Dog> findAll(Pageable pageable);
}
