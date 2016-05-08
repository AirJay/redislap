package com.airjay.redis.benchmark.jedis;

import com.airjay.redis.benchmark.common.Constants;
import com.airjay.redis.benchmark.common.StringGenerator;
import redis.clients.jedis.*;

import java.text.NumberFormat;
import java.util.*;
import java.util.concurrent.CyclicBarrier;

public class JedisMain {
    public static void main(String[] args) throws Exception {

        new Cli(args).parse();

        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(Cli.connCount);

        NumberFormat numberFormat = NumberFormat.getInstance();
        numberFormat.setMaximumFractionDigits(2);

        if (Cli.enableCluster) {
            Set<HostAndPort> jedisClusterNodes = new HashSet<>();
            jedisClusterNodes.add(new HostAndPort(Cli.host, Integer.valueOf(Cli.port)));
            JedisCluster jedisCluster = new JedisCluster(jedisClusterNodes, Cli.opTimeout, config);

            if (Cli.operation.equals("set")) {
                System.out.println("JedisMain setkey startup");

                long startTime = System.nanoTime();
                Map<String, Long> costMap = setKey(jedisCluster, Cli.bytes,
                        Cli.threadCount, Cli.repeatCount);
                long estimatedTime = System.nanoTime() - startTime;

                float repeat = Cli.repeatCount;

                System.out.println("JedisMain setkey finish, cost time = "
                        + estimatedTime + "ns, " + "set count = " + repeat
                        + ", ops = " + (repeat / estimatedTime) * Constants.seed);

                System.out.println("avg set cost time = "
                        + costMap.get("avgSetCost") + "ns");
                System.out.println("max set cost time = "
                        + costMap.get("maxSetCost") + "ns");
                System.out.println("min set cost time = "
                        + costMap.get("minSetCost") + "ns");

            } else if (Cli.operation.equals("get")) {
                System.out.println("JedisMain getkey startup");

                CyclicBarrier barrier = new CyclicBarrier(Cli.threadCount + 1);
                ArrayList<Thread> threadList = new ArrayList<>();

                for (int i = 0; i < Cli.threadCount; i++) {
                    threadList.add(new ReadThread(jedisCluster, barrier));
                }

                for (int j = 0; j < threadList.size(); j++) {
                    threadList.get(j).start();
                }

                barrier.await();
                long startTime = System.nanoTime();
                barrier.await();
                long estimatedTime = System.nanoTime() - startTime;

                float totalRepeat = Cli.repeatCount * Cli.threadCount;

                System.out.println("JedisMain getkey finish, cost time = "
                        + estimatedTime + "ns, " + "get count = " + totalRepeat
                        + ", ops = " + (totalRepeat / estimatedTime)
                        * Constants.seed);

                long avgGetCost = 0;
                long maxGetCost = Long.MIN_VALUE;
                long minGetCost = Long.MAX_VALUE;
                long sumGetCost = 0;
                Map<String, Long> costMap;

                for (int m = 0; m < threadList.size(); m++) {
                    costMap = ((ReadThread) threadList.get(m))
                            .getCostMapPerThread();
                    sumGetCost = sumGetCost + costMap.get("avgGetCostPerThread");
                    avgGetCost = sumGetCost / (m + 1);
                    maxGetCost = maxGetCost > costMap.get("maxGetCostPerThread") ? maxGetCost
                            : costMap.get("maxGetCostPerThread");
                    minGetCost = minGetCost < costMap.get("minGetCostPerThread") ? minGetCost
                            : costMap.get("minGetCostPerThread");
                }

                System.out.println("avg get cost time = " + avgGetCost + "ns");
                System.out.println("max get cost time = " + maxGetCost + "ns");
                System.out.println("min get cost time = " + minGetCost + "ns");

            }

            jedisCluster.close();

        } else {
            JedisPool pool = new JedisPool(config, Cli.host, Integer.valueOf(Cli.port), Cli.opTimeout);

            if (Cli.operation.equals("set")) {
                System.out.println("JedisMain setkey startup");

                long startTime = System.nanoTime();
                Map<String, Long> costMap = setKey(pool, Cli.bytes,
                        Cli.threadCount, Cli.repeatCount);
                long estimatedTime = System.nanoTime() - startTime;

                float repeat = Cli.repeatCount;

                System.out.println("JedisMain setkey finish, cost time = "
                        + estimatedTime + "ns, " + "set count = " + repeat
                        + ", ops = " + (repeat / estimatedTime) * Constants.seed);

                System.out.println("avg set cost time = "
                        + costMap.get("avgSetCost") + "ns");
                System.out.println("max set cost time = "
                        + costMap.get("maxSetCost") + "ns");
                System.out.println("min set cost time = "
                        + costMap.get("minSetCost") + "ns");

            } else if (Cli.operation.equals("get")) {
                System.out.println("JedisMain getkey startup");

                CyclicBarrier barrier = new CyclicBarrier(Cli.threadCount + 1);
                ArrayList<Thread> threadList = new ArrayList<>();

                for (int i = 0; i < Cli.threadCount; i++) {
                    threadList.add(new ReadThread(pool, barrier));
                }

                for (int j = 0; j < threadList.size(); j++) {
                    threadList.get(j).start();
                }

                barrier.await();
                long startTime = System.nanoTime();
                barrier.await();
                long estimatedTime = System.nanoTime() - startTime;

                float totalRepeat = Cli.repeatCount * Cli.threadCount;

                System.out.println("JedisMain getkey finish, cost time = "
                        + estimatedTime + "ns, " + "get count = " + totalRepeat
                        + ", ops = " + (totalRepeat / estimatedTime)
                        * Constants.seed);

                long avgGetCost = 0;
                long maxGetCost = Long.MIN_VALUE;
                long minGetCost = Long.MAX_VALUE;
                long sumGetCost = 0;
                Map<String, Long> costMap;

                for (int m = 0; m < threadList.size(); m++) {
                    costMap = ((ReadThread) threadList.get(m))
                            .getCostMapPerThread();
                    sumGetCost = sumGetCost + costMap.get("avgGetCostPerThread");
                    avgGetCost = sumGetCost / (m + 1);
                    maxGetCost = maxGetCost > costMap.get("maxGetCostPerThread") ? maxGetCost
                            : costMap.get("maxGetCostPerThread");
                    minGetCost = minGetCost < costMap.get("minGetCostPerThread") ? minGetCost
                            : costMap.get("minGetCostPerThread");
                }

                System.out.println("avg get cost time = " + avgGetCost + "ns");
                System.out.println("max get cost time = " + maxGetCost + "ns");
                System.out.println("min get cost time = " + minGetCost + "ns");

            }

            pool.close();

        }
    }


    public static Map<String, Long> setKey(JedisPool pool,
                                           int length, int threads, int repeats) {

        Jedis jedis = pool.getResource();
        long avgSetCost = 0;
        long maxSetCost = Long.MIN_VALUE;
        long minSetCost = Long.MAX_VALUE;
        long sumSetCost = 0;
        Map<String, Long> costMap = new HashMap<>();

        for (int i = 1; i <= repeats; i++) {
            String key = "redis-check-noc-" + i;
            String value = StringGenerator.generateValue(i, length);

            long startTime = System.nanoTime();
            jedis.set(key, value);
            long estimatedTime = System.nanoTime() - startTime;

            sumSetCost = sumSetCost + estimatedTime;
            avgSetCost = sumSetCost / i;
            maxSetCost = maxSetCost > estimatedTime ? maxSetCost
                    : estimatedTime;
            minSetCost = minSetCost < estimatedTime ? minSetCost
                    : estimatedTime;

        }

        if (jedis != null) {
            pool.returnResource(jedis);
        }

        costMap.put("avgSetCost", avgSetCost);
        costMap.put("maxSetCost", maxSetCost);
        costMap.put("minSetCost", minSetCost);

        return costMap;

    }

    public static Map<String, Long> setKey(JedisCluster jedisCluster,
                                           int length, int threads, int repeats) {

        long avgSetCost = 0;
        long maxSetCost = Long.MIN_VALUE;
        long minSetCost = Long.MAX_VALUE;
        long sumSetCost = 0;
        Map<String, Long> costMap = new HashMap<>();

        for (int i = 1; i <= repeats; i++) {
            String key = "redis-check-noc-" + i;
            String value = StringGenerator.generateValue(i, length);

            long startTime = System.nanoTime();
            jedisCluster.set(key, value);
            long estimatedTime = System.nanoTime() - startTime;

            sumSetCost = sumSetCost + estimatedTime;
            avgSetCost = sumSetCost / i;
            maxSetCost = maxSetCost > estimatedTime ? maxSetCost
                    : estimatedTime;
            minSetCost = minSetCost < estimatedTime ? minSetCost
                    : estimatedTime;

        }

        costMap.put("avgSetCost", avgSetCost);
        costMap.put("maxSetCost", maxSetCost);
        costMap.put("minSetCost", minSetCost);

        return costMap;

    }
}
