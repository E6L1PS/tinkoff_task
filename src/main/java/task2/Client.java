package task2;

/**
 * Создан: 15.02.2024.
 *
 * @author Pesternikov Danil
 */
public interface Client {
    //блокирующий метод для чтения данных
    Event readData();

    //блокирующий метод отправки данных
    Result sendData(Address dest, Payload payload);
}
