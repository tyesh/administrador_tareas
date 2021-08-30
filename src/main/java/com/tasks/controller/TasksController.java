package com.tasks.controller;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import com.tasks.repositories.*;
import com.tasks.entitys.Tasks;
import com.tasks.entitys.UserTasks;
import com.tasks.entitys.Users;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("tasks")
public class TasksController {

    @Autowired
    private TasksRepository tasksDAO;
    @Autowired
    private UserRepository userDAO;
    @Autowired
    private UserTasksRepository userTasksDAO;

    @GetMapping
    public ResponseEntity<List<Tasks>> getTasks(){
        List<Tasks> tasks = tasksDAO.findAll();
        return ResponseEntity.ok(tasks);
    }

    @RequestMapping(value = "/tasks/{taskId}")
    public ResponseEntity<Tasks> getTask(@PathVariable("taskId") Long taskId){
        Optional<Tasks> task = tasksDAO.findById(taskId);
        if(task.isPresent()){
            return ResponseEntity.ok(task.get());
        }
        return ResponseEntity.notFound().build();
    }

    @RequestMapping(value = "/status/{statusId}")
    public ResponseEntity<List<Tasks>> getTaskByStatus(@PathVariable("statusId") Long statusId){
        List<Tasks> tasks = tasksDAO.findByStatus(statusId);
        return ResponseEntity.ok(tasks);
    }

    @PostMapping
    public ResponseEntity<Tasks> createTasks(@RequestBody  Tasks task){
        ZoneId zone = ZoneId.systemDefault();
        ZonedDateTime zdt = ZonedDateTime.now(zone);
        long startDate = zdt.toInstant().toEpochMilli()/1000;
        task.setStatus(Long.valueOf(0));
        task.setDate_start(startDate);
        Tasks newTask = tasksDAO.save(task);
        return ResponseEntity.ok(newTask);
    }

    @DeleteMapping(value = "{taskId}")
    public ResponseEntity<List<Tasks>> deleteTasks(@PathVariable("taskId") Long taskId){
        Optional<Tasks> task = tasksDAO.findById(taskId);
        if(task.isPresent()){
            tasksDAO.deleteById(taskId);
            List<Tasks> tasks = tasksDAO.findAll();
            return ResponseEntity.ok(tasks);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping(value = "/TasksByStatus/{statusId}")
    public ResponseEntity<List<Tasks>> deleteTasksByStatus(@PathVariable("statusId") Long statusId){
        List<Tasks> tasks = tasksDAO.findByStatus(statusId);
        if(!tasks.isEmpty()){
            tasksDAO.deleteByStatus(statusId);
        }
        tasks = tasksDAO.findAll();
        return ResponseEntity.ok(tasks);
    }

    @PutMapping
    public ResponseEntity<Tasks> updateTask(@RequestBody Tasks taskParam){
        Optional<Tasks> task = tasksDAO.findById(taskParam.getId());
        if(task.isPresent()){
            Tasks updateTask = task.get();
            if(taskParam.getName() != null){
                updateTask.setName(taskParam.getName());
            }
            if(taskParam.getDescription() != null){
                updateTask.setDescription(taskParam.getDescription());
            }
            if(taskParam.getDate_start() != null){
                updateTask.setDate_start(taskParam.getDate_start());
            }
            if(taskParam.getDate_end() != null){
                updateTask.setDate_end(taskParam.getDate_end());
            }
            if(taskParam.getStatus() != null){
                updateTask.setStatus(taskParam.getStatus());
                if(taskParam.getStatus() == 3){
                    ZoneId zone = ZoneId.systemDefault();
                    ZonedDateTime zdt = ZonedDateTime.now(zone);
                    long endDate = zdt.toInstant().toEpochMilli()/1000;
                    updateTask.setDate_end(endDate);
                }
            }
            tasksDAO.save(updateTask);
            return ResponseEntity.ok(updateTask);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping(value = "/addTask")
    public ResponseEntity<UserTasks> createUserTask(@RequestBody UserTasks userTask){
        Optional<Tasks> task = tasksDAO.findById(userTask.getTasksId());
        if(!task.isPresent()){
            return ResponseEntity.notFound().build();
        }
        Optional<Users> user = userDAO.findById(userTask.getUserId());
        if(!user.isPresent()){
            return ResponseEntity.notFound().build();
        }
        List<UserTasks> userTasks = userTasksDAO.findByUserId(userTask.getUserId());
        if(userTasks.size() > 5){
            return ResponseEntity.badRequest().build();
        }
        UserTasks newUserTask = userTasksDAO.save(userTask);
        return ResponseEntity.ok(newUserTask);
    }

    @RequestMapping(value = "/biggestTask")
    public ResponseEntity<Tasks> getBiggestTask(){
        Optional<Tasks> task = tasksDAO.findBiggestTasks();
        if(task.isPresent()){
            return ResponseEntity.ok(task.get());
        }
        return ResponseEntity.notFound().build();
    }

    @RequestMapping(value = "/smallestTask")
    public ResponseEntity<Tasks> getSmallestTask(){
        Optional<Tasks> task = tasksDAO.findSmallestTasks();
        if(task.isPresent()){
            return ResponseEntity.ok(task.get());
        }
        return ResponseEntity.notFound().build();
    }

    @RequestMapping(value = "/activeTodayTasks")
    public ResponseEntity<Integer> getActiveTOdayTask(){
        ZoneId zone = ZoneId.systemDefault();
        ZonedDateTime zdt = ZonedDateTime.now(zone);
        zdt = zdt.toLocalDate().atStartOfDay(zone);
        long startDate = zdt.toInstant().toEpochMilli()/1000;
        long endDate = startDate + 86400;
        List<Tasks> task = tasksDAO.findActiveTodayTasks(startDate, endDate);
        return ResponseEntity.ok(task.size());
    }

    @RequestMapping(value = "/completeTodayTasks")
    public ResponseEntity<Integer> getCompleteTOdayTask(){
        ZoneId zone = ZoneId.systemDefault();
        ZonedDateTime zdt = ZonedDateTime.now(zone);
        zdt = zdt.toLocalDate().atStartOfDay(zone);
        long startDate = zdt.toInstant().toEpochMilli()/1000;
        long endDate = startDate + 86400;
        List<Tasks> task = tasksDAO.findCompleteTodayTasks(startDate, endDate);
        return ResponseEntity.ok(task.size());
    }

    @RequestMapping(value = "/completePercentage")
    public ResponseEntity<Integer> getCompletePercentage(){
        List<Tasks> tasks = tasksDAO.findAll();
        List<Tasks> completeTasks = tasksDAO.findCompleteTasks();
        if(tasks.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        if(completeTasks.isEmpty()){
            return ResponseEntity.ok(0);
        }
        Double percentage = (double) completeTasks.size()/tasks.size() * 100;
        return ResponseEntity.ok(percentage.intValue());
    }
    
}