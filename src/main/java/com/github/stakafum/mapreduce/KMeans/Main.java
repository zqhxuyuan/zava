package com.github.stakafum.mapreduce.KMeans;

import com.github.stakafum.mapreduce.MapReduce;

import java.util.Date;
import java.util.List;
import java.util.Random;


/*
 * Kmeansを実行するクラス
 *　二次元の座標上に点在するプロットを複数のグループでまとめる
 */
public class Main {

    /**
     * KMeansを実行する。
     * @param args 第一引数にすべての座標の数、第二引数にグループの数、第三引数に並列数を格納する
     */
    public static void main(String[] args) {
        int vectornum = 100;
        int clusternum = 3;
        int parallelnum = 1;

        VectorKM[] vectors;
        VectorKM[] clusters;
        VectorKM[] tmpclusters;

        if(args.length > 0){
            vectornum = new Integer(args[0]).intValue();
        }
        if(args.length > 1){
            clusternum = new Integer(args[1]).intValue();
        }
        if(args.length > 2){
            parallelnum = new Integer(args[2]).intValue();
        }

        vectors= new VectorKM[vectornum];
        clusters= new VectorKM[clusternum];
        tmpclusters= new VectorKM[clusternum];


        initializeVectorKM(vectors, 0, 0, 1, 1);
        initializeVectorKM(clusters, 0.4f, 0.4f, 0.6f, 0.6f);

		showVectorKM(vectors);
		showVectorKM(clusters);

        MapReduce<VectorKM, VectorKM[], VectorKM, VectorKM, VectorKM, VectorKM> mmKM = new MapReduce<VectorKM, VectorKM[], VectorKM, VectorKM, VectorKM, VectorKM>(MapKM.class, ReduceKM.class, "MAP_REDUCE");
        mmKM.setResultOutput(true);
        mmKM.setParallelThreadNum(parallelnum);

        kernelKMeans(mmKM, vectors, clusters, tmpclusters);

        System.out.println("");

        while(!compareClusters(clusters, tmpclusters)){
            for(int i = 0; i < tmpclusters.length; i++)
                clusters[i] = tmpclusters[i];
            mmKM = new MapReduce<VectorKM, VectorKM[], VectorKM, VectorKM, VectorKM, VectorKM>(MapKM.class, ReduceKM.class, "MAP_REDUCE");
            mmKM.setResultOutput(true);
            mmKM.setParallelThreadNum(parallelnum);
            kernelKMeans(mmKM, vectors, clusters, tmpclusters);

            System.out.println("");
        }

        for(int i = 0; i < tmpclusters.length; i++)
            clusters[i] = tmpclusters[i];

        mmKM = new MapReduce<VectorKM, VectorKM[], VectorKM, VectorKM, VectorKM, VectorKM>(MapKM.class, ReduceKM.class, "MAP_SHUFFLE");

        for(VectorKM vkm : vectors){
            mmKM.addKeyValue(vkm, clusters);
        }

        mmKM.run();


    }

    /*
     * Kmeansの計算を行うメソッド
     * 1.MapReduceクラスのmmKMのキーにプロット、バリューに全クラスタの重点の座標が格納されている配列をそれぞれ入れる
     * 2.mmKMにMapReduce処理を行わせる
     * 3.makeNewClusterで新しいクラスタを獲得する
     * @param mmKM MapReduceの処理を行うクラス
     * @param vectors プロットの座標を入れた配列
     * @param clusters　クラスタの重点の座標が入った配列
     * @param tmpclusters
     * @param parallelnum 並列数
     */
    public static void kernelKMeans(MapReduce<VectorKM, VectorKM[], VectorKM, VectorKM, VectorKM, VectorKM> mmKM,
                                    VectorKM[] vectors,
                                    VectorKM[] clusters,
                                    VectorKM[] tmpclusters){
        for(VectorKM vkm : vectors){
            mmKM.addKeyValue(vkm, clusters);
        }
        mmKM.run();
        makeNewCluster(clusters, tmpclusters, mmKM.getKeys(), mmKM.getValues());
    }

    /*
     * 二次元座標のプロットをランダムに生成するためのメソッド
     * @param vectors 二次元座標の配列。この中にプロットが生成される
     * @param xmin 二次元座標におけるx軸の最小値
     * @param ymin 二次元座標におけるy軸の最小値
     * @param xmax 二次元座標におけるx軸の最大値
     * @param ymin 二次元座標におけるy軸の最大値
     */
    public static void initializeVectorKM(
            VectorKM[] vectors,
            float xmin,
            float ymin,
            float xmax,
            float ymax){
        Date d = new Date();
        Random rdm = new Random(d.getTime());

        for(int i = 0; i < vectors.length; i++){
            vectors[i] = new VectorKM(xmin + rdm.nextFloat()*(xmax - xmin), ymin + rdm.nextFloat()*(ymax - ymin));
        }
    }

    /*
     * プロットの座標をConsoleに表示するためのメソッド
     * @param vectors プロットが格納されている配列
     */
    public static void showVectorKM(VectorKM[] vectors){
        for(VectorKM vkm : vectors){
            System.out.println("(x, y) = (" + vkm.getX() + ", " + vkm.getY() + ")");
        }
    }

    /*
     * 計算後のクラスタの重点を計算するためのメソッド
     *
     * @param cluster 計算に用いたクラスタの重点が格納されている配列
     * @param cluster 計算後のクラスタの重点が格納される配列
     * @param keylist 計算後のキーが格納されているリスト
     * @param valuelist 計算後のバリューが格納されているリスト
     * keylistとvaluelistはインデックスで相互に対応している
     */
    public static void makeNewCluster(
            VectorKM[] clusters,
            VectorKM[] tmpclusters,
            List<VectorKM> keylist,
            List<VectorKM> valuelist){
        int clusterindex;

        for(int i = 0; i < clusters.length; i++){
            if((clusterindex = keylist.indexOf(clusters[i])) != -1)
                tmpclusters[i] = valuelist.get(clusterindex);
            else
                tmpclusters[i] = clusters[i];
        }
    }

    /*
     * クラスタの座標を比較し中身が同じならば真を返すメソッド
     * @param tmpcluster 計算後のクラスタの重点が格納されている配列
     * @param cluster 計算後のクラスタの重点が格納されている配列
     */
    public static boolean compareClusters(
            VectorKM[] clusters,
            VectorKM[] tmpclusters){
        int fixclusternum = 0;
        for(int i = 0; i < tmpclusters.length; i++){
            for(int j = i; j < clusters.length; j++){
                if(tmpclusters[i].equals(clusters[j])){
                    fixclusternum++;
                }
            }
        }
        return (fixclusternum == clusters.length) ? true : false;
    }

}