package ru.yandex.practicum.filmorate.exception;

public class UploadException extends RuntimeException {
    public UploadException(final String message) {
        super(message);
    }
}
