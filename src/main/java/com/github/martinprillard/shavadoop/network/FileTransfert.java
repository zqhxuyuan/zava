package com.github.martinprillard.shavadoop.network;

import com.github.martinprillard.shavadoop.util.Constant;

/**
 * 
 * @author martin prillard
 * 
 */
public class FileTransfert extends ShellThread {

    private String destFile;
    private boolean fromLocalToDistant;
    private boolean bulk;

    public FileTransfert(SSHManager _sm, String _hostOwner, String _fileToTreat, String _destFile, boolean _fromLocalToDistant, boolean _bulk) {
        super(_sm, _hostOwner, _fileToTreat);
        destFile = _destFile;
        fromLocalToDistant = _fromLocalToDistant;
        bulk = _bulk;
    }

    public void run() {
        transferFileScp();
    }

    /**
     * Transfer a file with scp
     */
    public void transferFileScp() {

        if (!sm.isLocal(distantHost)) {
            String cmdLine = null;
            if (fromLocalToDistant) {
                cmdLine = "scp " + fileToTreat + " " + username + "@" + distantHost + ":" + destFile;
            } else {
                String[] files = fileToTreat.split(Constant.SEP_SCP_FILES);
                if (bulk && files.length > 1) {
                    cmdLine = "scp " + username + "@" + distantHost + ":{" + fileToTreat + "} " + destFile;
                } else {
                    cmdLine = "scp " + username + "@" + distantHost + ":" + fileToTreat + " " + destFile;
                }
            }

            try {
                Process p = Runtime.getRuntime().exec(cmdLine);
                p.waitFor();
                p.destroy();
                if (Constant.MODE_DEBUG)
                    System.out.println("On local : " + cmdLine);
            } catch (Exception e) {
                System.out.println("Error on local : " + cmdLine);
                e.printStackTrace();
            }
        }
    }

}
