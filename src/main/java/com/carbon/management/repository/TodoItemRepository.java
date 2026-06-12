package com.carbon.management.repository;

import com.carbon.management.common.enums.TodoType;
import com.carbon.management.entity.TodoItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TodoItemRepository extends JpaRepository<TodoItem, Long>, JpaSpecificationExecutor<TodoItem> {

    Optional<TodoItem> findByTodoCode(String todoCode);

    List<TodoItem> findByAssigneeOrderByCreatedTimeDesc(String assignee);

    List<TodoItem> findByAssigneeAndIsRead(String assignee, Boolean isRead);

    List<TodoItem> findByAssigneeAndIsCompleted(String assignee, Boolean isCompleted);

    List<TodoItem> findByAssigneeAndTodoType(String assignee, TodoType todoType);

    List<TodoItem> findByRelatedTypeAndRelatedId(String relatedType, Long relatedId);
}
