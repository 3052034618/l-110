package com.carbon.management.service;

import com.carbon.management.common.PageResult;
import com.carbon.management.common.enums.TodoType;
import com.carbon.management.dto.TodoQueryDTO;
import com.carbon.management.vo.TodoVO;

import java.time.LocalDateTime;

public interface TodoService {

    Long createTodo(String assignee, String todoTypeCode, String title, String content,
                    String relatedType, Long relatedId, LocalDateTime deadline);

    PageResult<TodoVO> queryTodos(TodoQueryDTO dto);

    void markAsRead(Long id);

    void markAsCompleted(Long id);

    void markAllAsRead(String assignee);

    long getUnreadCount(String assignee);

    long getUncompletedCount(String assignee);
}
