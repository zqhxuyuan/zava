package hdgl.db.store.impl.cache;

import static org.junit.Assert.*;
import hdgl.util.IterableHelper;
import hdgl.util.WritableHelper;

import org.junit.Test;

public class MemoryGraphStoreTest {
	
	public byte[] data(int i){
		return WritableHelper.toBytes(i);
	}
	
	public byte[] data(String i){
		return WritableHelper.toBytes(i);
	}
	
	@Test
	public MemoryGraphStore test() throws Exception{
		MemoryGraphStore g = new MemoryGraphStore();
		MemoryVertexImpl v1 = new MemoryVertexImpl(1l, "t1", 
				IterableHelper.<String, byte[]>makeMap("name", data("one"), "price", data(100)), 
				IterableHelper.<Long>makeSet(-5l), 
				IterableHelper.<Long>makeSet(-1l,-6l,-7l), g);
		MemoryVertexImpl v2 = new MemoryVertexImpl(2l, "t1", 
				IterableHelper.<String, byte[]>makeMap("name", data("two"), "price", data(50)), 
				IterableHelper.<Long>makeSet(-1l, -9l), 
				IterableHelper.<Long>makeSet(-2l, -8l), g);
		MemoryVertexImpl v3 = new MemoryVertexImpl(3l, "t1", 
				IterableHelper.<String, byte[]>makeMap("name", data("three"), "price", data(500)), 
				IterableHelper.<Long>makeSet(-2l, -6l), 
				IterableHelper.<Long>makeSet(-3l, -9l), g);
		MemoryVertexImpl v4 = new MemoryVertexImpl(4l, "t2", 
				IterableHelper.<String, byte[]>makeMap("name", data("four"), "price", data(10)), 
				IterableHelper.<Long>makeSet(-3l, -7l), 
				IterableHelper.<Long>makeSet(-4l), g);
		MemoryVertexImpl v5 = new MemoryVertexImpl(5l, "t2", 
				IterableHelper.<String, byte[]>makeMap("name", data("five"), "price", data(30)), 
				IterableHelper.<Long>makeSet(-4l, -8l), 
				IterableHelper.<Long>makeSet(-5l), g);
		MemoryEdgeImpl e1=new MemoryEdgeImpl(-1l, "forward", 1l, 2l, 
				IterableHelper.<String, byte[]>makeMap("len", data(1)), g);
		MemoryEdgeImpl e2=new MemoryEdgeImpl(-2l, "forward", 2l, 3l, 
				IterableHelper.<String, byte[]>makeMap("len", data(1)), g);
		MemoryEdgeImpl e3=new MemoryEdgeImpl(-3l, "forward", 3l, 4l, 
				IterableHelper.<String, byte[]>makeMap("len", data(1)), g);
		MemoryEdgeImpl e4=new MemoryEdgeImpl(-4l, "forward", 4l, 5l, 
				IterableHelper.<String, byte[]>makeMap("len", data(1)), g);
		MemoryEdgeImpl e5=new MemoryEdgeImpl(-5l, "back", 5l, 1l, 
				IterableHelper.<String, byte[]>makeMap("len", data(-4)), g);
		MemoryEdgeImpl e6=new MemoryEdgeImpl(-6l, "jump", 1l, 3l, 
				IterableHelper.<String, byte[]>makeMap("len", data(2)), g);
		MemoryEdgeImpl e7=new MemoryEdgeImpl(-7l, "jump", 1l, 4l, 
				IterableHelper.<String, byte[]>makeMap("len", data(3)), g);
		MemoryEdgeImpl e8=new MemoryEdgeImpl(-8l, "jump", 2l, 5l, 
				IterableHelper.<String, byte[]>makeMap("len", data(3)), g);
		MemoryEdgeImpl e9=new MemoryEdgeImpl(-9l, "back", 3l, 2l, 
				IterableHelper.<String, byte[]>makeMap("len", data(-1)), g);
		g.addVertex(v1);
		g.addVertex(v2);
		g.addVertex(v3);
		g.addVertex(v4);
		g.addVertex(v5);
		g.addEdge(e1);
		g.addEdge(e2);
		g.addEdge(e3);
		g.addEdge(e4);
		g.addEdge(e5);
		g.addEdge(e6);
		g.addEdge(e7);
		g.addEdge(e8);
		g.addEdge(e9);
		
		assertEquals(5, g.getVertexCount());
		assertEquals(9, g.getEdgeCount());
		assertEquals("back", g.parseEdge(-5).getType());
		assertEquals("forward", g.parseEdge(-1).getType());
		assertEquals("jump", g.parseEdge(-8).getType());
		return g;
	}

}
