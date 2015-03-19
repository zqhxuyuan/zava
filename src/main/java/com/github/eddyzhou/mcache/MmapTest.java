package com.github.eddyzhou.mcache;


import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Random;

public class MmapTest {
    static void test(Mmap mmap) {
        int testNum = 1000000;
        Random random = new Random();

        // 校验用数据
        long[] keySeeds = { 7868797499859060846L, 4262993262341174994L,
                6518201275381165207L, 7884500662164179315L,
                4671385403810139220L, 4932093476209010285L,
                6898017371440917510L, 5808027261012785374L,
                3853596143247611704L, 4329336606641544466L,
                3078879081909255277L, 1416608275843121435L,
                7042562888130868337L, 3403511101821674952L,
                5986385789424861416L, 2323373653542128089L,
                6658400276959819296L, 7490590946357947745L,
                112406948046948454L, 6396704972401176672L,
                3466502632114237950L, 1840897760703935020L,
                7889758053945887701L, 9218968122261087379L,
                2490287921733379541L, 6335440545004090290L,
                1218349416020864590L, 2518258005892113104L,
                2717225042777350474L, 1058843668856080862L,
                3844064534656725052L, 9129526911788897871L,
                1254538217568632602L, 475010888540299593L,
                1598177308347095347L, 2936882228107291615L, 51939574420523964L,
                7665378345376805808L, 4651581635382826184L,
                4329142596519445900L, 6441364554699876496L,
                6872847251173641955L, 8921954930909392542L,
                174923561686491640L, 8660300019210361076L,
                4234729737381036699L, 6195753429963818684L,
                2779818747784720146L, 7185734689627702158L,
                8980726305096850857L, 5762562833774660711L,
                7516486953962632122L, 2349762093732898048L,
                9021689016620422616L, 68355226267828155L, 1833216245572595043L,
                3426443314539405791L, 3210619448027679191L,
                2192680177019405041L, 1471467842535756469L,
                4778093366281028285L, 4246170960047728476L,
                3731365287812817104L, 7797777684578153782L,
                8007698950436451136L, 5686194486701952664L,
                1614123447548157080L, 1426484754446233071L, 29030913269747168L,
                8552059078811124188L, 53637264635364085L, 1498925719980731250L,
                5898877109252352339L, 4141054170303074384L,
                2074217234997222591L, 7429748054152800417L,
                5739100035065179718L, 131664620650706061L,
                7092643277925674721L, 3659562726820418591L,
                2208079408584643741L, 1207586363735017197L,
                1611519616161493921L, 3155560673710029105L,
                6628992879003680140L, 7063809315639459220L,
                2999400000354458239L, 2478487879834213136L,
                5391171500123070431L, 8065244664294631799L,
                2134768841383563302L, 3615997608520965710L,
                666936225751218062L, 2210450599077269930L,
                1072115668648899926L, 9164286598413751880L,
                4146826908099837345L, 2106414536505334400L,
                1092432731702811017L, 6515017418556629604L };

        long startTime = System.currentTimeMillis();
        int fullCount = 0;
        int c1 = 0;
        int c2 = 0;
        int c3 = 0;
        System.out.println("start at:" + startTime);
        for (int i = 0; i < testNum; i++) {
            int randRate = random.nextInt(100);
            try {
                if (randRate < 80) {
                    c1++;
                    mmap.get(genRandKeyWithSeed(random, keySeeds));
                } else if (randRate < 85) {
                    c2++;
                    mmap.free(genRandKeyWithSeed(random, keySeeds));
                } else {
                    c3++;
                    long key = genRandKeyWithSeed(random, keySeeds);
                    mmap.put(key, enbyteLong(key * 2));
                }
                // System.out.println(i + ":" + mmap);
            } catch (MmapException e) {
                fullCount++;
            }
        }
        long endTime = System.currentTimeMillis();
        System.out.println("stop at:" + endTime);
        System.out.println("test " + testNum + ", use " + (endTime - startTime) + "ms, " + (testNum * 1000 / (endTime - startTime) + "/s"));
        System.out.println("full count:" + fullCount + ",t1:" + c1 + ",t2:" + c2 + ",t3:" + c3);
        System.out.println(mmap);
    }

    private static long genRandKeyWithSeed(Random random, long[] keys) {
        int idx = Math.abs((int) (random.nextLong() % keys.length));
        return keys[idx];
    }

    private static byte[] enbyteLong(long l) {
        byte[] data = new byte[8];
        for (int i = 0; i < 8; i++) {
            data[7 - i] = (byte) (l >> 8 * i & 0xFF);
        }
        return data;
    }

    static void test2(Mmap mmap) {
        int testNum = 10000000;
        Random random = new Random();

        // 校验用数据
        long[] keySeeds = { 7868797499859060846L, 4262993262341174994L,
                6518201275381165207L, 7884500662164179315L,
                4671385403810139220L, 4932093476209010285L,
                6898017371440917510L, 5808027261012785374L,
                3853596143247611704L, 4329336606641544466L,
                3078879081909255277L, 1416608275843121435L,
                7042562888130868337L, 3403511101821674952L,
                5986385789424861416L, 2323373653542128089L,
                6658400276959819296L, 7490590946357947745L,
                112406948046948454L, 6396704972401176672L };

        long startTime = System.currentTimeMillis();
        int fullCount = 0;
        int c1 = 0;
        int c2 = 0;
        System.out.println("start at:" + startTime);
        for (int i = 0; i < testNum; i++) {
            int randRate = random.nextInt(100);
            try {
                if (randRate < 70) {
                    mmap.get(genRandKeyWithoutSeed(random, keySeeds));
                } else if (randRate < 72) {
                    mmap.free(genRandKeyWithoutSeed(random, keySeeds));
                } else if (randRate < 85) {
                    long key = genRandKeyWithoutSeed(random, keySeeds);
                    mmap.put(key, enbyteLong(key * 2));
                } else if (randRate < 90) {
                    long key = genRandKeyWithSeed(random, keySeeds);
                    ByteBuffer bb = mmap.getByteBuffer(key);
                    c1++;
                    if (bb != null) {
                        c2++;
                        assert (bb.asLongBuffer().get(0) == (key * 2));
                    }
                } else if (randRate < 95) {
                    mmap.free(genRandKeyWithSeed(random, keySeeds));
                } else {
                    long key = genRandKeyWithSeed(random, keySeeds);
                    mmap.put(key, enbyteLong(key * 2));
                }
                // System.out.println(i + ":" + mmap);
            } catch (MmapException e) {
                fullCount++;
            }
        }
        long endTime = System.currentTimeMillis();
        System.out.println("stop at:" + endTime);
        System.out.println("test " + testNum + ",use " + (endTime - startTime) + "ms, " + (testNum / ((endTime - startTime) / 1000)) + "/s");
        System.out.println("full count:" + fullCount + ",t1:" + c1 + ",t2:" + c2);
        System.out.println(mmap);
    }

    private static long genRandKeyWithoutSeed(Random random, long[] keys) {
        for (;;) {
            long l = Math.abs(random.nextLong());
            boolean eq = false;
            for (int i = 0; i < keys.length; i++) {
                if (l == keys[i]) {
                    eq = true;
                    break;
                }
            }
            if (!eq)
                return l;
        }
    }

    public static void main(String[] args) throws MmapException, IOException {
        Mmap mmap = new Mmap("/tmp/mcache", 4, 31);
        // Mmap mmap = new Mmap("E:/test2",5199732,40);

        System.out.println("mmap succ");
        System.out.println(mmap);
        // test测试方式: datanum从1-101逐步测试
        test(mmap);
        // test2(mmap);
        try {
            Thread.sleep(1000 * 60);
        } catch (InterruptedException e) {
        }
        test(mmap);
        // pc测试结果： 120万/s
    }
}