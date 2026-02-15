package com.example.hibernateonetoonedemo.bootstrap;

import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.HikariPoolMXBean;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@Order(2)
public class ConnectionPoolProofRunner implements CommandLineRunner {

    private final ConnectionWorkService connectionWorkService;
    private final HikariDataSource hikariDataSource;

    public ConnectionPoolProofRunner(ConnectionWorkService connectionWorkService, HikariDataSource hikariDataSource) {
        this.connectionWorkService = connectionWorkService;
        this.hikariDataSource = hikariDataSource;
    }

    @Override
    public void run(String... args) throws Exception {
        int workers = 8;
        int holdSeconds = 6;

        HikariPoolMXBean pool = hikariDataSource.getHikariPoolMXBean();
        System.out.println("---- Connection Pool Proof Started ----");
        System.out.printf("Pool config: maxPoolSize=%d, minIdle=%d%n",
                hikariDataSource.getMaximumPoolSize(), hikariDataSource.getMinimumIdle());

        ExecutorService executor = Executors.newFixedThreadPool(workers);
        AtomicBoolean monitoring = new AtomicBoolean(true);
        AtomicInteger peakActive = new AtomicInteger(0);
        AtomicInteger peakTotal = new AtomicInteger(0);

        Thread monitorThread = new Thread(() -> {
            while (monitoring.get()) {
                int active = pool.getActiveConnections();
                int total = pool.getTotalConnections();
                int idle = pool.getIdleConnections();
                int waiting = pool.getThreadsAwaitingConnection();
                peakActive.accumulateAndGet(active, Math::max);
                peakTotal.accumulateAndGet(total, Math::max);
                System.out.printf("POOL STATS -> active=%d, idle=%d, total=%d, waiting=%d%n",
                        active, idle, total, waiting);
                try {
                    Thread.sleep(300);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }, "pool-monitor");
        monitorThread.start();

        List<CompletableFuture<Void>> tasks = new ArrayList<>();
        for (int i = 1; i <= workers; i++) {
            tasks.add(CompletableFuture.runAsync(() -> connectionWorkService.holdConnectionForSeconds(holdSeconds), executor));
        }

        CompletableFuture.allOf(tasks.toArray(new CompletableFuture[0])).join();
        monitoring.set(false);
        monitorThread.join();

        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);

        System.out.printf("POOL PEAK -> active=%d, total=%d%n", peakActive.get(), peakTotal.get());
        System.out.println("---- Connection Pool Proof Finished ----");
    }
}
