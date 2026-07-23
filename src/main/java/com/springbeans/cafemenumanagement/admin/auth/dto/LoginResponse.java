package com.springbeans.cafemenumanagement.admin.auth.dto;

public record LoginResponse(
        Long adminId,
        String username
) {
}