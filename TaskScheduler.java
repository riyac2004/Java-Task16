import java.util.concurrent.*;

public class TaskScheduler {

    static class Task implements Callable<String> {

        private final int taskId;

        public Task(int taskId) {
            this.taskId = taskId;
        }

        @Override
        public String call() throws Exception {

            System.out.println("Task " + taskId + " started by "
                    + Thread.currentThread().getName());

            // Simulate work
            Thread.sleep(2000);

            // Simulate failure for one task
            if (taskId == 5) {
                throw new RuntimeException("Error in Task " + taskId);
            }

            return "Task " + taskId + " completed successfully";
        }
    }

    public static void main(String[] args) {

        long startTime = System.currentTimeMillis();

        ExecutorService executor = Executors.newFixedThreadPool(3);

        Future<String>[] futures = new Future[7];

        System.out.println("Submitting tasks...\n");

        for (int i = 0; i < 7; i++) {
            futures[i] = executor.submit(new Task(i));
        }

        // Monitor task execution
        for (int i = 0; i < futures.length; i++) {
            try {
                System.out.println(futures[i].get());
            } catch (Exception e) {
                System.out.println("Task " + i + " failed: " + e.getMessage());
            }
        }

        // Graceful shutdown
        executor.shutdown();

        try {
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
        }

        long endTime = System.currentTimeMillis();

        System.out.println("\nAll tasks finished.");
        System.out.println("Total Execution Time: "
                + (endTime - startTime) + " ms");
    }
}
