package org.fleep;

import java.time.Duration;
import java.time.Instant;
import java.util.Random;

/**
 * Our main app will spawn 5 threads, each representing a horse.
 * Each horse will move at random speeds from 0-100% completion
 * of the track. The main thread will render the progress of each thread.
 * There will also be a limited amount of time for the horses to finish,
 * after which time the main thread will issue an interrupt to the child threads.
 */

public class Main {
    public static void main(String[] args) throws InterruptedException {
        // Create several horses
        Horse[] horses = new Horse[5];

        // Instatiate several threads
        Thread[] horseThreads = new Thread[horses.length];
        for (int i = 0; i < horses.length; i++) {
            horses[i] = new Horse(i);
            horseThreads[i] = new Thread(new HorseRunnable(horses[i]));
        }

        // Start all the threads
        for (Thread horseThread : horseThreads) {
            horseThread.start();
        }

        // Start a timer
        Instant startTime = Instant.now();
        int allowedTime = 10;

        boolean hasWinner = false;
        int winner = -1;
        long renderFrame = 0;
        while(!hasWinner) {
            // Clear the terminal
            System.out.println("\n\n\n");
            System.out.print("\033[H\033[2J");
            System.out.flush();

            // Calculate the elapsed time
            int remainingTime = allowedTime - (int)Duration.between(startTime, Instant.now()).getSeconds();
            System.out.println("Time Remaining: " + remainingTime + "\n\n");

            // Render the progress of each horse
            for (int i = 0; i < horses.length; i++) {
                Horse horse = horses[i];
                int progress = horse.getProgress() + 1;

                //System.out.printf("%3d || %"+progress+"c*%"+(100 - progress)+ "c#\n", i + 1, ' ', ' ');
                System.out.printf("%3d || %"+progress+"c*%"+(Math.max(1, 100 - progress))+"c##\n", i + 1, ' ', ' ');

                if (horse.getProgress() >= 100) {
                    hasWinner = true;
                    winner = i;
                }
            }

            if (remainingTime <= 0 && !hasWinner) {
                // End the race
                System.out.println("\n\nTime's up! No winner!");
                // Kill all child threads
                for (Thread horseThread : horseThreads) {
                    horseThread.interrupt();
                }
                // Wait a few seconds before terminating
                Thread.sleep(3000);
                System.exit(0);
            }

            // Draw 16 frames per second (if we can). 1000 milliseconds / 16 = 64
            Thread.sleep(64);
        }

        System.out.println("\n\n\n");
        System.out.println("Horse " + (winner + 1) + " won the race!");

        Thread.sleep(3000);

        // Kill all child threads
        for (Thread horseThread : horseThreads) {
            horseThread.interrupt();
        }
    }

    private static class Horse {
        private int id;
        private int progress = 0;

        public Horse(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }

        public int getProgress() {
            return progress;
        }

        public void setProgress(int progress) {
            this.progress = progress;
        }
    }

    private static class HorseRunnable implements Runnable {

        private static final long minWait = 50;
        private static final long maxWait = 150;

        private final Horse horse;

        public HorseRunnable(Horse horse) {
            this.horse = horse;
        }

        @Override
        public void run() {
            Random random = new Random();
            for (int progress = 0; progress <= 100; progress += 1) {
                horse.setProgress(progress);

                // Wait some time between minWait and maxWait
                try {
                    Long waitTime = random.nextLong(maxWait - minWait + 1) + minWait;
                    //System.out.println("Horse " + horse.getId() + " waiting " + waitTime + " progress" + horse.getProgress());
                    Thread.sleep(waitTime);
                } catch (InterruptedException e) {
                    return;
                }
            }

            // Sleep until killed off by the master thread
            while (!Thread.interrupted()) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    return;
                }
            }
        }
    }
}