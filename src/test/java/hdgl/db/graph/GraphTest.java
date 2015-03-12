package hdgl.db.graph;

import hdgl.db.conf.GraphConf;

import java.util.concurrent.ExecutionException;

import org.apache.hadoop.conf.Configuration;
import org.junit.Test;

public class GraphTest {
	
	@Test
	public void test() throws InterruptedException, ExecutionException {
		Configuration configuration = GraphConf.getDefault();
		Graph g = GraphFactory.connect(configuration);
		MutableGraph m = g.beginModify();
		long v1 = m.createVertex("v");
		long v2 = m.createVertex("v");
		long v3 = m.createVertex("v2");
		long e1 = m.createEdge("e", v1, v2);
		long e2 = m.createEdge("e", v2, v3);
		long e3 = m.createEdge("e2", v3, v1);
		m.setLabel(v1, "title", "v1".getBytes());
		m.setLabel(v2, "title", "v2".getBytes());
		m.setLabel(v3, "title", "v3".getBytes());
		m.setLabel(e1, "title", "e1".getBytes());
		m.setLabel(e2, "title", "e2".getBytes());
		m.setLabel(e3, "title", "e3".getBytes());
		System.out.println(m.commit().get());
	}

}
