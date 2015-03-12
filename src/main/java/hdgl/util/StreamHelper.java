package hdgl.util;

        import java.io.BufferedInputStream;
        import java.io.BufferedOutputStream;
        import java.io.ByteArrayOutputStream;
        import java.io.IOException;
        import java.io.InputStream;
        import java.io.OutputStream;

public class StreamHelper {

    /**
     * 不能实例化辅助类
     */
    private StreamHelper(){

    }

    public static int BUFFER_SIZE=256*256;




    /**
     * 从InputStream中读取全部数据，并输出到OutputStream中
     */
    public static void transfer(InputStream in, OutputStream out, boolean closeStreams) throws IOException{
        BufferedInputStream bin = new BufferedInputStream(in);
        BufferedOutputStream bout = new BufferedOutputStream(out);
        if(!closeStreams){
            byte[] buf=new byte[BUFFER_SIZE];
            int len = bin.read(buf);
            while(len>0){
                bout.write(buf, 0, len);
                len = bin.read(buf);
            }
            bout.flush();
        }else{
            try{
                byte[] buf = new byte[BUFFER_SIZE];
                int len = bin.read(buf);
                while(len>0){
                    bout.write(buf, 0, len);
                    len = bin.read(buf);
                }
                bout.flush();
            }finally{
                try{bin.close();}catch(IOException ex){}
                try{bout.close();}catch(IOException ex){}
            }
        }
    }

    public static void transfer(InputStream in, OutputStream out) throws IOException{
        transfer(in, out, false);
    }

    public static byte[] toBytes(InputStream in)throws IOException{
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        transfer(in, buf, true);
        return buf.toByteArray();
    }
}
