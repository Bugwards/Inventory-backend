package com.example.inventoryAuth.Service;

import com.example.inventoryAuth.DTO.AuditLogDto.AuditLogResponse;
import com.example.inventoryAuth.Entity.AuditLog;
import com.example.inventoryAuth.Repository.AuditLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AuditLogService {

    private static final String FALLBACK_USERNAME = "System";

    @Autowired
    private AuditLogRepository auditLogRepository;

    // Creates and saves an audit log for a system transaction  //

    public void log(String username, String transactionId, String description) {
        AuditLog auditLog = new AuditLog();
        auditLog.setUsername(username != null ? username : FALLBACK_USERNAME);
        auditLog.setTransactionId(transactionId);
        auditLog.setDescription(description);
        auditLog.setTimestamp(LocalDateTime.now());
        auditLogRepository.save(auditLog);
    }


    // Retrieves all audit logs in descending order of timestamp and maps them to response DTOs //

    public List<AuditLogResponse> getAllAuditLogs() {
        List<AuditLog> auditLogs = auditLogRepository.findAllByOrderByTimestampDesc();
        List<AuditLogResponse> response = new java.util.ArrayList<>();

        for (AuditLog auditLog : auditLogs) {
            response.add(new AuditLogResponse(
                    auditLog.getUsername(),
                    auditLog.getTransactionId(),
                    auditLog.getDescription(),
                    auditLog.getTimestamp()
            ));
        }

        return response;
    }




}
