package hdgl.db.store;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.Writable;

public class Log implements Writable {
	
	static final byte ADD_VERTEX = 0;
	static final byte ADD_EDGE = 1;
	static final byte SET_LABEL = 2;
	//static final byte DELETE_ENTITY = 3;
	//static final byte DELETE_LABEL = 4;
	
	public static Log addVertex(long tempId, String oftype){
		return new Log(ADD_VERTEX, tempId, 0, 0, oftype, null);
	}
	
	public static Log addEdge(long tempId, String oftype, long v1, long v2){
		return new Log(ADD_EDGE, tempId, v1, v2, oftype, null);
	}
	
	public static Log setLabel(long entity, String name, byte[] value){
		return new Log(SET_LABEL, entity, 0, 0, name, value);
	}
	
	byte type;
	long id1,id2,id3;
	String name;
	byte[] data;
	
	public byte getType() {
		return type;
	}

	public void setType(byte type) {
		this.type = type;
	}

	public long getId1() {
		return id1;
	}

	public void setId1(long id1) {
		this.id1 = id1;
	}

	public long getId2() {
		return id2;
	}

	public void setId2(long id2) {
		this.id2 = id2;
	}

	public long getId3() {
		return id3;
	}

	public void setId3(long id3) {
		this.id3 = id3;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public Log(){
		
	}
	
	public Log(byte type, long id1, long id2,long id3, String name, byte[] data) {
		super();
		this.type = type;
		this.id1 = id1;
		this.id2 = id2;
		this.id3 = id3;
		this.name = name;
		this.data = data;
	}
	
	@Override
	public void readFields(DataInput in) throws IOException {
		type = in.readByte();
		switch (type) {
		case ADD_VERTEX:
			id1 = in.readLong();
			name = in.readUTF();
			break;
		case ADD_EDGE:
			id1 = in.readLong();
			id2 = in.readLong();
			id3 = in.readLong();
			name = in.readUTF();
			break;
		case SET_LABEL:
			id1 = in.readLong();
			name = in.readUTF();
			int len=in.readInt();
			if(len>0){
				data = new byte[len];
				in.readFully(data);
			}else{
				data=null;
			}
			break;
		default:
			throw new IllegalArgumentException("Illegal log type: "+ type);
		}
	}
	@Override
	public void write(DataOutput out) throws IOException {
		out.writeByte(type);
		switch (type) {
		case ADD_VERTEX:
			out.writeLong(id1);
			out.writeUTF(name);
			break;
		case ADD_EDGE:
			out.writeLong(id1);
			out.writeLong(id2);
			out.writeLong(id3);
			out.writeUTF(name);	
			break;
		case SET_LABEL:
			out.writeLong(id1);
			out.writeUTF(name);
			if(data!=null){
				out.writeInt(data.length);
				out.write(data);
			}else{
				out.writeInt(0);
			}
			break;
		default:
			throw new IllegalArgumentException("Illegal log type: "+ type);
		}
		
	}
	
	
}
