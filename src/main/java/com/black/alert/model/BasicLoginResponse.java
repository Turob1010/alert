package com.black.alert.model;

import java.util.UUID;

public record BasicLoginResponse(UUID userId, String phoneNumber, String token) {}
