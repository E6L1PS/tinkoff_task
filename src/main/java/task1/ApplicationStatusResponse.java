package task1;

import org.jetbrains.annotations.Nullable;

import java.time.Duration;

/**
 * Создан: 14.02.2024.
 *
 * @author Pesternikov Danil
 */
public sealed interface ApplicationStatusResponse {
    record Failure(@Nullable Duration lastRequestTime, int retriesCount) implements ApplicationStatusResponse {}
    record Success(String id, String status) implements ApplicationStatusResponse {}
}
