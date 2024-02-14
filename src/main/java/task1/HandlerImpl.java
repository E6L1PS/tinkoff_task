package task1;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * Создан: 14.02.2024.
 *
 * @author Pesternikov Danil
 */
public class HandlerImpl implements Handler {

    private final Client client;

    public HandlerImpl(Client client) {
        this.client = client;
    }

    @Override
    public ApplicationStatusResponse performOperation(String id) {
        long startTime = System.currentTimeMillis();
        int retriesCount = 0;

        while (System.currentTimeMillis() - startTime < 15000) {
            CompletableFuture<Response> future1 = CompletableFuture.supplyAsync(() -> client.getApplicationStatus1(id));
            CompletableFuture<Response> future2 = CompletableFuture.supplyAsync(() -> client.getApplicationStatus2(id));

            CompletableFuture<Object> anyOf = CompletableFuture.anyOf(future1, future2);

            try {
                Response response = (Response) anyOf.get();
                return handleResponse(response, startTime, retriesCount);
            } catch (InterruptedException | ExecutionException e) {
                retriesCount++;
            }
        }

        return new ApplicationStatusResponse.Failure(Duration.ofMillis(System.currentTimeMillis() - startTime), retriesCount);
    }

    private ApplicationStatusResponse handleResponse(Response response, long startTime, int retriesCount) {
        if (response instanceof Response.Success success) {
            return new ApplicationStatusResponse.Success(success.applicationId(), success.applicationStatus());
        }

        Duration delay = getRetryDelay(response);
        if (delay != null) {
            sleep(delay);
            return new ApplicationStatusResponse.Failure(null, retriesCount + 1);
        }

        return new ApplicationStatusResponse.Failure(Duration.ofMillis(System.currentTimeMillis() - startTime), retriesCount);
    }

    private void sleep(Duration duration) {
        try {
            Thread.sleep(duration.toMillis());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private Duration getRetryDelay(Response response) {
        if (response instanceof Response.RetryAfter retryAfter) {
            return retryAfter.delay();
        }
        return null;
    }
}