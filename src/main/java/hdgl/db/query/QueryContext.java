package hdgl.db.query;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.hadoop.io.Writable;

import hdgl.db.query.stm.StateMachine;

public class QueryContext implements Writable {

	StateMachine stm;
	SortedMap<Long, Integer> idMap = new TreeMap<Long, Integer>();
	String zkRoot;
	
	
	public String getZkRoot() {
		return zkRoot;
	}

	public void setZkRoot(String zkRoot) {
		this.zkRoot = zkRoot;
	}

	public void put(Long blockId, Integer regionId){
		idMap.put(blockId, regionId);
	}

	public StateMachine getStateMachine() {
		return stm;
	}

	public void setStateMachine(StateMachine stm) {
		this.stm = stm;
	}

	public SortedMap<Long, Integer> getIdMap() {
		return idMap;
	}

	@Override
	public void readFields(DataInput in) throws IOException {
		stm = new StateMachine();
		stm.readFields(in);
		zkRoot = in.readUTF();
		int len = in.readInt();
		idMap.clear();
		for(int i = 0;i<len;i++){
			long id = in.readLong();
			int regionId = in.readInt();
			idMap.put(id, regionId);
		}
	}

	@Override
	public void write(DataOutput out) throws IOException {
		stm.write(out);
		out.writeUTF(zkRoot);
		Set<Map.Entry<Long, Integer>> idset = idMap.entrySet();
		out.writeInt(idset.size());
		for(Map.Entry<Long, Integer> pair:idset){
			out.writeLong(pair.getKey());
			out.writeInt(pair.getValue());
		}
	}
	
	
	
}
