package task2;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Создан: 15.02.2024.
 *
 * @author Pesternikov Danil
 */
public class HandlerImpl implements Handler {

    private final Client client;
    private final ExecutorService executor;

    public HandlerImpl(Client client) {
        this.client = client;
        this.executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    }

    @Override
    public Duration timeout() {
        return Duration.ofMillis(100);
    }

    @Override
    public void performOperation() {
        while (!Thread.interrupted()) {
            Event event = client.readData();

            List<CompletableFuture<Void>> futures = event.recipients()
                    .stream()
                    .map(address -> sendAsync(address, event.payload()))
                    .toList();

            CompletableFuture<Void> allOf = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));

            try {
                allOf.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        executor.shutdown();
    }

    private CompletableFuture<Void> sendAsync(Address address, Payload payload) {
        return CompletableFuture.runAsync(() -> {
            Result result;
            do {
                result = client.sendData(address, payload);
                if (result == Result.REJECTED) {
                    try {
                        Thread.sleep(timeout().toMillis());
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            } while (result == Result.REJECTED);
        }, executor);
    }
}
