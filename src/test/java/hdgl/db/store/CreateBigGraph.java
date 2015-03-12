package hdgl.db.store;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import hdgl.db.conf.GraphConf;
import hdgl.db.store.impl.hdfs.mapreduce.MutableGraph;

import org.apache.hadoop.conf.Configuration;
import org.junit.Test;

public class CreateBigGraph {

	@Test
	public void test() throws Exception {
		Configuration conf = GraphConf.getDefault();
		MutableGraph m = new MutableGraph(conf, 1);
		int vertex=100000;
		int vp=vertex/100;
		int edge=10;
		Map<Integer, Long> vids = new HashMap<Integer, Long>();
		for(int i=1;i<=vertex;i++){
			vids.put(i, m.createVertex("v"));
			if(i%vp==0){
				System.out.print(".");
			}
		}
		System.out.println("OK");
		Random r=new Random();
		int ecount = 0;
		float p=edge/(float)vertex;
		for(int i=1;i<=vertex;i++){
			for(int j=1;j<=vertex;j++){
				if(r.nextFloat()<p){
					ecount++;
					m.createEdge("e", vids.get(i), vids.get(j));
				}
			}
			if(i%vp==0){
				System.out.print(".");
			}
		}
		System.out.println("OK");
		System.out.println("create "+vertex+" vertives and "+ecount+" edges.");
		assertTrue(m.commit().get());

	}

}
