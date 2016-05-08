package com.airjay.redis.benchmark.jedis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CyclicBarrier;

public class ReadThread extends Thread {

    protected JedisPool pool;
    protected JedisCluster jedisCluster;
    protected CyclicBarrier barrier;
    private Map<String, Long> costMapPerThread;

    public ReadThread(JedisPool pool, CyclicBarrier barrier) {
        this.pool = pool;
        this.barrier = barrier;
    }

    public ReadThread(JedisCluster jedisCluster, CyclicBarrier barrier) {
        this.jedisCluster = jedisCluster;
        this.barrier = barrier;
    }

    public void run() {

        try {
            barrier.await();

            if (Cli.enableCluster) {
                this.setCostMapPerThread(getKey(jedisCluster, Cli.repeatCount));
            } else {
                this.setCostMapPerThread(getKey(pool, Cli.repeatCount));
            }

            barrier.await();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Map<String, Long> getKey(JedisPool pool,
                                     int repeats) {

        Jedis jedis = pool.getResource();

        long avgGetCostPerThread = 0;
        long maxGetCostPerThread = Long.MIN_VALUE;
        long minGetCostPerThread = Long.MAX_VALUE;
        long sumGetCostPerThread = 0;
        Map<String, Long> costMapPerThread = new HashMap<>();

        for (int i = 1; i <= repeats; i++) {
            String key = "redis-check-noc-" + i;
            long startTime = System.nanoTime();
            jedis.get(key);
            long estimatedTime = System.nanoTime() - startTime;

            sumGetCostPerThread = sumGetCostPerThread + estimatedTime;
            avgGetCostPerThread = sumGetCostPerThread / i;
            maxGetCostPerThread = maxGetCostPerThread > estimatedTime ? maxGetCostPerThread
                    : estimatedTime;
            minGetCostPerThread = minGetCostPerThread < estimatedTime ? minGetCostPerThread
                    : estimatedTime;

        }

        if (jedis != null) {
            pool.returnResource(jedis);
        }

        costMapPerThread.put("avgGetCostPerThread", avgGetCostPerThread);
        costMapPerThread.put("maxGetCostPerThread", maxGetCostPerThread);
        costMapPerThread.put("minGetCostPerThread", minGetCostPerThread);

        return costMapPerThread;
    }

    private Map<String, Long> getKey(JedisCluster jedisCluster,
                                     int repeats) {

        long avgGetCostPerThread = 0;
        long maxGetCostPerThread = Long.MIN_VALUE;
        long minGetCostPerThread = Long.MAX_VALUE;
        long sumGetCostPerThread = 0;
        Map<String, Long> costMapPerThread = new HashMap<>();

        for (int i = 1; i <= repeats; i++) {
            String key = "redis-check-noc-" + i;
            long startTime = System.nanoTime();
            jedisCluster.get(key);
            long estimatedTime = System.nanoTime() - startTime;

            sumGetCostPerThread = sumGetCostPerThread + estimatedTime;
            avgGetCostPerThread = sumGetCostPerThread / i;
            maxGetCostPerThread = maxGetCostPerThread > estimatedTime ? maxGetCostPerThread
                    : estimatedTime;
            minGetCostPerThread = minGetCostPerThread < estimatedTime ? minGetCostPerThread
                    : estimatedTime;

        }

        costMapPerThread.put("avgGetCostPerThread", avgGetCostPerThread);
        costMapPerThread.put("maxGetCostPerThread", maxGetCostPerThread);
        costMapPerThread.put("minGetCostPerThread", minGetCostPerThread);

        return costMapPerThread;
    }

    public Map<String, Long> getCostMapPerThread() {
        return costMapPerThread;
    }

    public void setCostMapPerThread(Map<String, Long> costMapPerThread) {
        this.costMapPerThread = costMapPerThread;
    }

}
