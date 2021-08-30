package com.tasks.repositories;

import java.util.List;

import com.tasks.entitys.UserTasks;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserTasksRepository extends JpaRepository<UserTasks, Long>{
    public abstract List<UserTasks> findByUserId(Long userId);
}