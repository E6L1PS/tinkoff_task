package task2;

import java.util.List;

/**
 * Создан: 15.02.2024.
 *
 * @author Pesternikov Danil
 */
public record Event(List<Address> recipients, Payload payload) {}
