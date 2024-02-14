package task1;

import java.time.Duration;

/**
 * Создан: 14.02.2024.
 *
 * @author Pesternikov Danil
 */
public sealed interface Response {
    record Success(String applicationStatus, String applicationId) implements Response {}
    record RetryAfter(Duration delay) implements Response {}
    record Failure(Throwable ex) implements Response {}
}

