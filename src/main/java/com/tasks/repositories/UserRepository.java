package com.tasks.repositories;

import com.tasks.entitys.Users;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<Users, Long>{
}