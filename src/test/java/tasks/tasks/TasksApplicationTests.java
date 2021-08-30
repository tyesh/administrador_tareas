package tasks.tasks;

import static org.mockito.Mockito.verify;

import com.tasks.controller.TasksController;
import com.tasks.repositories.TasksRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TasksApplicationTests {

	@Mock
	private TasksRepository tasksRepository;
	private TasksController underTest;

	@BeforeEach
	void setup(){
		underTest = new TasksController();
	}

	@Test
	void canGetAllTasks(){
		underTest.getTasks();
		verify(tasksRepository.findAll());
	}

}