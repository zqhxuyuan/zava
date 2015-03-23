package com.zqh.twopc.server;

import java.net.Socket;
import java.util.Map;

import com.zqh.twopc.client.Main;
import com.zqh.twopc.shared.FakeResource;
import com.zqh.twopc.shared.Log;
import com.zqh.twopc.shared.Message;

public class Coordinator {
	private Log log;
	private FakeResource resource;
	private Map<Socket, Participant> connections;
	
	volatile int voteCounter;
	
	public Coordinator(Log log, FakeResource fr, Map<Socket, Participant> connections){
		this.log = log;
		this.resource = fr;
		this.connections = connections;
	}
	public void startCommit(){
		voteCounter = 0;
		log.write(Log.START_2PC);
		Message voteRequestMessage = new Message("VOTE_REQUEST");
		
		try {
			Thread.sleep(Main.SLEEP_TIME);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		for(Participant p: connections.values()){
			p.sendMessage(voteRequestMessage, new ResponseEvent() { 
				@Override
				public void notify(Message message) { //successfull
					if(message.type.equals("VOTE_COMMIT")){
						participantCommit();
					}else{
						participantAbort();
					}
				}
			}, new ResponseEvent() {
				
				@Override
				public void notify(Message message) { //timeout
					participantAbort();
				}
			});
		}
		
	}
	
	private synchronized void participantCommit(){
		voteCounter++;
		String latestLog = log.latest();
		if(voteCounter == connections.size() && !latestLog.equals(Log.GLOBAL_ABORT)){
			log.write(Log.GLOBAL_COMMIT);
			Message globalCommitMessage = new Message("GLOBAL_COMMIT"); 
			for(Participant pp: connections.values()){
				pp.sendMessage(globalCommitMessage);
			}
		}
		
	}
	private synchronized void participantAbort(){
		log.write(Log.GLOBAL_ABORT);
		Message globalAbortMessage = new Message("GLOBAL_ABORT"); 
		for(Participant pp: connections.values()){
			pp.sendMessage(globalAbortMessage);
		}
	}
	

}
