package org.example.courseplanner.concurrency;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AppExecutor {

    // a fixed thread pool with 4 threads, shared by the whole application
    private static final ExecutorService executorService = Executors.newFixedThreadPool(4);

    // give other classes access to the shared pool without creating a new one
    public static ExecutorService getExecutorService() {
        return executorService;
    }

    // call this once when the application is closing, to shut down the threads cleanly
    public static void shutdown() {
        executorService.shutdown();
    }
}