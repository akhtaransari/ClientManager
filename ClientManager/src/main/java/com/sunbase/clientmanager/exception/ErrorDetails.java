package com.sunbase.clientmanager.exception;

import java.time.LocalDateTime;

public record ErrorDetails(
        String message,
        String description,
        LocalDateTime timeStamp) {
}
