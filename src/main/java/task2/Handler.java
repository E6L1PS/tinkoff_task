package task2;

import java.time.Duration;

/**
 * Создан: 15.02.2024.
 *
 * @author Pesternikov Danil
 */
public interface Handler {
    Duration timeout();

    void performOperation();
}