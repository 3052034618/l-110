package com.carbon.management.service.impl;

import com.carbon.management.common.PageResult;
import com.carbon.management.common.enums.TodoType;
import com.carbon.management.common.exception.BusinessException;
import com.carbon.management.dto.TodoQueryDTO;
import com.carbon.management.entity.TodoItem;
import com.carbon.management.repository.TodoItemRepository;
import com.carbon.management.service.TodoService;
import com.carbon.management.vo.TodoVO;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TodoServiceImpl implements TodoService {

    private final TodoItemRepository todoItemRepository;

    @Override
    @Transactional
    public Long createTodo(String assignee, String todoTypeCode, String title, String content,
                           String relatedType, Long relatedId, LocalDateTime deadline) {
        TodoItem todo = new TodoItem();
        todo.setTodoCode(generateTodoCode());
        todo.setTodoType(TodoType.PROGRESS_REMIND);
        for (TodoType type : TodoType.values()) {
            if (type.getCode().equals(todoTypeCode) || type.name().equals(todoTypeCode)) {
                todo.setTodoType(type);
                break;
            }
        }
        todo.setTitle(title);
        todo.setContent(content);
        todo.setRelatedType(relatedType);
        todo.setRelatedId(relatedId);
        todo.setAssignee(assignee);
        todo.setIsRead(false);
        todo.setIsCompleted(false);
        todo.setDeadline(deadline);
        TodoItem saved = todoItemRepository.save(todo);
        return saved.getId();
    }

    @Override
    public PageResult<TodoVO> queryTodos(TodoQueryDTO dto) {
        Specification<TodoItem> spec = buildTodoSpecification(dto);
        PageRequest pageRequest = PageRequest.of(
                dto.getPageNum() - 1,
                dto.getPageSize(),
                Sort.by(Sort.Direction.DESC, "createdTime")
        );
        Page<TodoItem> page = todoItemRepository.findAll(spec, pageRequest);
        List<TodoVO> records = page.getContent().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        return PageResult.of(records, page.getTotalElements(), dto.getPageNum(), dto.getPageSize());
    }

    @Override
    @Transactional
    public void markAsRead(Long id) {
        TodoItem todo = todoItemRepository.findById(id)
                .orElseThrow(() -> new BusinessException("待办事项不存在"));
        todo.setIsRead(true);
        todo.setReadTime(LocalDateTime.now());
        todoItemRepository.save(todo);
    }

    @Override
    @Transactional
    public void markAsCompleted(Long id) {
        TodoItem todo = todoItemRepository.findById(id)
                .orElseThrow(() -> new BusinessException("待办事项不存在"));
        todo.setIsCompleted(true);
        todo.setCompleteTime(LocalDateTime.now());
        todo.setIsRead(true);
        if (todo.getReadTime() == null) {
            todo.setReadTime(LocalDateTime.now());
        }
        todoItemRepository.save(todo);
    }

    @Override
    @Transactional
    public void markAllAsRead(String assignee) {
        List<TodoItem> unread = todoItemRepository.findByAssigneeAndIsRead(assignee, false);
        unread.forEach(todo -> {
            todo.setIsRead(true);
            todo.setReadTime(LocalDateTime.now());
        });
        todoItemRepository.saveAll(unread);
    }

    @Override
    public long getUnreadCount(String assignee) {
        return todoItemRepository.findByAssigneeAndIsRead(assignee, false).size();
    }

    @Override
    public long getUncompletedCount(String assignee) {
        return todoItemRepository.findByAssigneeAndIsCompleted(assignee, false).size();
    }

    private Specification<TodoItem> buildTodoSpecification(TodoQueryDTO dto) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (dto.getAssignee() != null && !dto.getAssignee().isEmpty()) {
                predicates.add(cb.equal(root.get("assignee"), dto.getAssignee()));
            }
            if (dto.getTodoType() != null) {
                predicates.add(cb.equal(root.get("todoType"), dto.getTodoType()));
            }
            if (dto.getIsRead() != null) {
                predicates.add(cb.equal(root.get("isRead"), dto.getIsRead()));
            }
            if (dto.getIsCompleted() != null) {
                predicates.add(cb.equal(root.get("isCompleted"), dto.getIsCompleted()));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private TodoVO convertToVO(TodoItem todo) {
        TodoVO vo = new TodoVO();
        BeanUtils.copyProperties(todo, vo);
        vo.setTodoTypeDesc(todo.getTodoType().getDesc());
        return vo;
    }

    private String generateTodoCode() {
        return "TODO-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                + "-" + UUID.randomUUID().toString().replace("-", "").substring(0, 4).toUpperCase();
    }
}
