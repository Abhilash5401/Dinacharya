package com.kanban.service;

import com.kanban.model.dto.request.TaskImportData;
import com.kanban.model.dto.response.TaskImportResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface FileImportService {
    TaskImportResponse importTasksFromExcel(MultipartFile file, Long teamId) throws IOException;
    TaskImportResponse importTasksFromWord(MultipartFile file, Long teamId) throws IOException;
    List<TaskImportData> parseExcelFile(MultipartFile file) throws IOException;
    List<TaskImportData> parseWordFile(MultipartFile file) throws IOException;
}
