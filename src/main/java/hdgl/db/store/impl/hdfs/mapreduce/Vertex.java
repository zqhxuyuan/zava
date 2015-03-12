package hdgl.db.store.impl.hdfs.mapreduce;

import hdgl.db.conf.GraphConf;

import java.util.ArrayList;

import org.apache.hadoop.conf.Configuration;

public class Vertex extends GraphWritable{
	private ArrayList<EdgePart> outEdges;
	private ArrayList<EdgePart> inEdges;
	
	private static class EdgePart
	{
		public int edgeId;
		public int vertexId;
		
		public EdgePart(int edge, int vertex)
		{
			edgeId = edge;
			vertexId = vertex;
		}
		
		public String getString()
		{
			String str = "(" + edgeId + "," + vertexId + ")";
			return str;
		}
	}
	
	public Vertex(int id, Configuration conf)
	{
		super(id, GraphConf.getVertexTrunkSize(conf));
		outEdges = new ArrayList<EdgePart>();
		inEdges = new ArrayList<EdgePart>();
	}
	
	public void addEdges(int direction, int edgeId, int vertexId)
	{
		EdgePart edgePart = new EdgePart(edgeId, vertexId);
		if (direction == 1)
		{
			inEdges.add(edgePart);
		}
		else
		{
			outEdges.add(edgePart);
		}
	}
	
	public void addEdgesAggregate(int direction, String value, String split)
	{
		String[] strs = value.split(split);
		EdgePart edgePart = new EdgePart(Integer.parseInt(strs[0]), Integer.parseInt(strs[1]));
		
		if (direction == 1)
		{
			inEdges.add(edgePart);
		}
		else
		{
			outEdges.add(edgePart);
		}
	}
	
	public void addLabel(String attr, String val)
	{
		Label label = new Label(attr, val);
		labels.add(label);
	}
	
	public void addLabelAggregate(String str, String split)
	{
		String[] strs = str.split(split);
		Label label = new Label(strs[0], strs[1]);
		labels.add(label);
	}
	
	public String getString()
	{
		StringBuffer str = new StringBuffer();
		str.append("[v");
		str.append(id);
		str.append(" out-edges:[");
		if (outEdges.size() != 0){
			str.append(outEdges.get(0).getString());
			for (int i = 1; i < outEdges.size(); i++){
				str.append(", " + outEdges.get(i).getString());
			}
		}
		str.append("] in-edges:[");
		if (inEdges.size() != 0){
			str.append(inEdges.get(0).getString());
			for (int i = 1; i < inEdges.size(); i++){
				str.append(", " + inEdges.get(i).getString());
			}
		}
		str.append("] label:[");
		if (labels.size() != 0){
			str.append(labels.get(0).getString());
			for (int i = 1; i < labels.size(); i++){
				str.append(", " + labels.get(i).getString());
			}
		}
		str.append("]]");
		return str.toString();
	}
	
	public long prepareData(long offset)
	{
		bb.putInt(id);
		bb.putInt(outEdges.size());
		bb.putInt(inEdges.size());
		count = count + 12;
		for (int i = 0; i < outEdges.size(); i++)
		{
			bb.putInt(outEdges.get(i).edgeId);
			bb.putInt(outEdges.get(i).vertexId);
			count = count + 8;
		}
		for (int i = 0; i < inEdges.size(); i++)
		{
			bb.putInt(inEdges.get(i).edgeId);
			bb.putInt(inEdges.get(i).vertexId);
			count = count + 8;
		}
		bb.putInt(labels.size());
		count = count + 4;
		for (int i = 0; i < labels.size(); i++)
		{
			count = count + labels.get(i).getBytes(bb);
		}
		return super.prepareData(offset);
	}

}
