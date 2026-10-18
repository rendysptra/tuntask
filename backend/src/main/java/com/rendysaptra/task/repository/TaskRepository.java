package com.rendysaptra.task.repository;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.rendysaptra.task.domain.entitty.Task;

@Repository
public interface TaskRepository extends JpaRepository<Task, UUID> {

}
