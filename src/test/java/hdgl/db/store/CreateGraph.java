package hdgl.db.store;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;
import hdgl.db.conf.GraphConf;
import hdgl.db.store.impl.hdfs.mapreduce.MutableGraph;
import hdgl.util.WritableHelper;

import org.apache.hadoop.conf.Configuration;
import org.junit.Test;

public class CreateGraph {

	@Test
	public void test() throws Exception {
		Configuration conf = GraphConf.getDefault();
		MutableGraph m = new MutableGraph(conf, 1);
		Map<Integer, Long> vids = new HashMap<Integer, Long>();
		Map<Integer, Long> eids = new HashMap<Integer, Long>();
		vids.put(1, m.createVertex("person"));
		vids.put(2, m.createVertex("person"));
		vids.put(3, m.createVertex("person"));
		vids.put(4, m.createVertex("person"));
		vids.put(5, m.createVertex("person"));
		eids.put(1, m.createEdge("forward", vids.get(1), vids.get(2)));
		eids.put(2, m.createEdge("forward", vids.get(2), vids.get(3)));
		eids.put(3, m.createEdge("forward", vids.get(3), vids.get(4)));
		eids.put(4, m.createEdge("forward", vids.get(4), vids.get(5)));
		eids.put(5, m.createEdge("back", vids.get(5), vids.get(1)));
		eids.put(6, m.createEdge("jump", vids.get(1), vids.get(3)));
		eids.put(7, m.createEdge("jump", vids.get(1), vids.get(4)));
		eids.put(8, m.createEdge("jump", vids.get(2), vids.get(5)));
		eids.put(9, m.createEdge("back", vids.get(3), vids.get(2)));
		m.setLabel(eids.get(1), "len", WritableHelper.toBytes(1));
		m.setLabel(eids.get(2), "len", WritableHelper.toBytes(1));
		m.setLabel(eids.get(3), "len", WritableHelper.toBytes(1));
		m.setLabel(eids.get(4), "len", WritableHelper.toBytes(1));
		m.setLabel(eids.get(5), "len", WritableHelper.toBytes(-4));
		m.setLabel(eids.get(6), "len", WritableHelper.toBytes(2));
		m.setLabel(eids.get(7), "len", WritableHelper.toBytes(3));
		m.setLabel(eids.get(8), "len", WritableHelper.toBytes(3));
		m.setLabel(eids.get(9), "len", WritableHelper.toBytes(-1));
		assertTrue(m.commit().get());
		
		
		
	}

}
