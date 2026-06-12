package com.carbon.management.service;

import com.carbon.management.common.PageResult;
import com.carbon.management.dto.AttachmentDTO;
import com.carbon.management.dto.ProgressReportDTO;
import com.carbon.management.dto.TaskCreateDTO;
import com.carbon.management.dto.TaskQueryDTO;
import com.carbon.management.dto.TaskUpdateDTO;
import com.carbon.management.vo.AttachmentVO;
import com.carbon.management.vo.TaskProgressVO;
import com.carbon.management.vo.TaskVO;

import java.util.List;

public interface TaskService {

    Long createTask(TaskCreateDTO dto);

    void updateTask(Long id, TaskUpdateDTO dto);

    void deleteTask(Long id);

    TaskVO getTaskById(Long id);

    TaskVO getTaskByCode(String taskCode);

    PageResult<TaskVO> queryTasks(TaskQueryDTO dto);

    List<TaskVO> getTasksByBudgetId(Long budgetId);

    List<TaskVO> getTasksByDeptId(String deptId);

    List<TaskVO> getTasksByResponsibilityPerson(String person);

    Long reportProgress(ProgressReportDTO dto);

    List<TaskProgressVO> getProgressByTaskId(Long taskId);

    Long addAttachment(AttachmentDTO dto);

    void removeAttachment(Long attachmentId);

    List<AttachmentVO> getAttachmentsByTaskId(Long taskId);

    List<AttachmentVO> getAttachmentsByProgressId(Long progressId);
}
