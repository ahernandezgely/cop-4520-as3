import java.util.*;
import java.util.concurrent.*;

public class MarsRover
{
    private static final int NUM_SENSORS = 8;
    private static final int NUM_READINGS_PER_HOUR = 60; // 1 hour of readings, taken every minute
    private static final Random RAND = new Random();

    public static void main(String[] args) throws InterruptedException
    {
        ConcurrentLinkedQueue<Integer> temperatureReadings = new ConcurrentLinkedQueue<>();
        ExecutorService executor = Executors.newFixedThreadPool(NUM_SENSORS);

        // Generate hourly reports
        for (int i = 0; i < 24; i++)
        {
            CountDownLatch latch = new CountDownLatch(NUM_SENSORS);

            // Create and start sensor tasks for the current hour
            for (int j = 0; j < NUM_SENSORS; j++)
            {
                executor.execute(() -> {
                    for (int k = 0; k < NUM_READINGS_PER_HOUR; k++)
                    {
                        temperatureReadings.add(RAND.nextInt(171) - 100); // Random temperature from -100F to 70F
                    }
                    latch.countDown();
                });
            }

            // Wait for all tasks for the current hour to finish
            latch.await();

            // Generate report for the current hour
            generateReport(temperatureReadings, i);
        }

        executor.shutdown();
    }

    private static void generateReport(Queue<Integer> temperatureReadings, int hour)
    {
        List<Integer> pastHourReadings = new ArrayList<>();
        for (int i = 0; i < NUM_READINGS_PER_HOUR * NUM_SENSORS; i++) // 1 hour of readings, taken every minute
        {
            pastHourReadings.add(temperatureReadings.poll());
        }

        Collections.sort(pastHourReadings);
        System.out.println("Hour " + hour + " Report:");
        System.out.println("Top 5 highest temperatures: " + pastHourReadings.subList(pastHourReadings.size() - 5, pastHourReadings.size()));
        System.out.println("Top 5 lowest temperatures: " + pastHourReadings.subList(0, 5));

        int maxDiff = 0;
        int maxDiffStart = 0;
        for (int i = 0; i < pastHourReadings.size() - 10; i++)
        {
            int diff = pastHourReadings.get(i + 10) - pastHourReadings.get(i);
            if (diff > maxDiff)
            {
                maxDiff = diff;
                maxDiffStart = i;
            }
        }
        System.out.println("10-minute interval with largest temperature difference: " + maxDiffStart + " to " + (maxDiffStart + 10));
    }
}