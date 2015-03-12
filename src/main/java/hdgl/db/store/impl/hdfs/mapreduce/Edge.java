package hdgl.db.store.impl.hdfs.mapreduce;

import org.apache.hadoop.conf.Configuration;

import hdgl.db.conf.GraphConf;


public class Edge extends GraphWritable{
	private int vertex1;
	private int vertex2;
	
	public Edge(int id, Configuration conf)
	{
		super(id, GraphConf.getEdgeTrunkSize(conf));
		vertex1 = 0;
		vertex2 = 0;
	}

	public void setVertex1(int v_id)
	{
		vertex1 = v_id;
	}
	
	public void setVertex2(int v_id)
	{
		vertex2 = v_id;
	}
	
	public void setTwoVertex(String str, String split)
	{
		String[] vertexs = str.split(split);
		vertex1 = Integer.parseInt(vertexs[0]);
		vertex2 = Integer.parseInt(vertexs[1]);
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
		str.append("[e");
		str.append(id);
		str.append(" [" + vertex1 + "," + vertex2 + "] label:[");
		if (labels.size() != 0)
		{
			str.append(labels.get(0).getString());
			for (int i = 1; i < labels.size(); i++){
				str.append("," + labels.get(i).getString());
			}
		}
		str.append("]]");
		return str.toString();
	}
	
	public long prepareData(long offset)
	{
		bb.putInt(id);
		bb.putInt(vertex1);
		bb.putInt(vertex2);
		bb.putInt(labels.size());
		count = count + 16;
		for (int i = 0; i < labels.size(); i++)
		{
			count = count + labels.get(i).getBytes(bb);
		}
		return super.prepareData(offset);
	}

}
