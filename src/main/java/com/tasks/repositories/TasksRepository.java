package com.tasks.repositories;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import com.tasks.entitys.Tasks;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TasksRepository extends JpaRepository<Tasks, Long>{
    public abstract List<Tasks> findByStatus(Long status);
    @Transactional
    public abstract Long deleteByStatus(Long status);
    @Query(value = "SELECT * FROM tasks ORDER BY (date_end-date_start) DESC LIMIT 1", nativeQuery = true)
    public abstract Optional<Tasks> findBiggestTasks();
    @Query(value = "SELECT * FROM tasks ORDER BY (date_end-date_start) Asc LIMIT 1", nativeQuery = true)
    public abstract Optional<Tasks> findSmallestTasks();
    @Query(value = "SELECT * FROM tasks WHERE status = 1 AND date_start BETWEEN ?1 AND ?2", nativeQuery = true)
    public abstract List<Tasks> findActiveTodayTasks(Long startDate, Long endDate);
    @Query(value = "SELECT * FROM tasks WHERE status = 0 OR Status = 1 OR status = 2 AND date_end BETWEEN ?1 AND ?2", nativeQuery = true)
    public abstract List<Tasks> findCompleteTodayTasks(Long startDate, Long endDate);
    @Query(value = "SELECT * FROM tasks WHERE status = 3", nativeQuery = true)
    public abstract List<Tasks> findCompleteTasks();
}