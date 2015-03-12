package hdgl.db.store.impl.hdfs;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import hdgl.db.conf.GraphConf;
import hdgl.db.store.HConf;
import hdgl.db.store.Log;
import hdgl.db.store.LogStore;

public class HdfsLogStore implements LogStore {

	Configuration configuration;
	int sessionId;
	FileSystem fs;
	FileStatus logfile;
	FSDataOutputStream outputStream;
	boolean closed = false;
	
	public HdfsLogStore(Configuration configuration, int sessionId) throws IOException {
		super();
		this.configuration = configuration;
		this.sessionId = sessionId;
		this.fs = HConf.getFileSystem(configuration);
		Path sessionRoot = new Path(GraphConf.getGraphRoot(configuration),"s"+sessionId);
		Path logPath = new Path(sessionRoot, "log");
		if(fs.exists(logPath)){
			fs.delete(logPath, false);
		}
		outputStream = fs.create(logPath);
	}

	@Override
	public void writeLog(Log log) throws IOException {
		log.write(outputStream);
	}

	@Override
	public synchronized FileStatus complete() throws IOException {
		if(closed){
			throw new IOException("Illegal file state");
		}
		closed = true;
		try{
			outputStream.close();			
		}finally{
			fs.close();
		}
		return logfile;
	}

	@Override
	public synchronized void abort() throws IOException {
		if(closed){
			throw new IOException("Illegal file state");
		}
		closed = true;
		try{
			outputStream.close();
			fs.delete(logfile.getPath(), true);
		}finally{
			fs.close();
		}
	}

	@Override
	public synchronized void close() throws IOException {
		if(closed){
			return;
		}
		closed = true;
		try{
			outputStream.close();			
		}finally{
			fs.close();
		}		
	}

}
