package com.zqh.base;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

public class TestIO {

    private static void testSimplePrint() throws Exception{
        System.out.println(System.getProperty("user.dir"));

        System.out.println(1 << 16); //65536=2^16=2^10*2^6=1024*64.在1后面补16个0

        System.out.println(0xF); //15=0000 1111=1+2+4+8
        System.out.println(0x7F); //0111 1111 = 127  +1= 128=1000 0000=2^7
        //0x7FFFFFFF=0111 1111 | 1111 1111 | 1111 1111 | 1111 1111
        System.out.println(0x7FFFFFFF); //就是MAX_VALUE

        System.out.println(Integer.MAX_VALUE);
        System.out.println(Integer.MIN_VALUE);

        File nonExist = new File("/home/hadoop/test/nonexist/again/test.txt");
        File parent = nonExist.getParentFile();
        if(!parent.exists()){
            parent.mkdirs();
        }
        if(!nonExist.exists()){
            nonExist.createNewFile();
        }
    }

    public static void main(String[] args)throws Exception{
        //testSimplePrint();
        //testMappedByteBuffer();

        //testVarInt(36);
        //testVarInt(127);

        //testCopyWriteFile();

        testByteSlice5();
    }

    public static void testCopyWriteFile()throws Exception{
        String folder = "/home/hadoop/github.com/sparrow/src/main/java/com/shansun/sparrow";
        String srcFolder = "/home/hadoop/github.com/sparrow";
        String targetFolder = "/home/hadoop/IdeaProjects/go-bigdata/zava";
        //testReadAndWrite(folder, srcFolder, targetFolder);

        folder = "/home/hadoop/Downloads/jvm-study-cases-master/src";
        srcFolder = "/home/hadoop/Downloads/jvm-study-cases-master/src";
        targetFolder = "/home/hadoop/IdeaProjects/go-bigdata/zava/src/main/java";
        //testReadAndWrite(folder, srcFolder, targetFolder);

        srcFolder = "/home/hadoop/github.com/sparrow-actor/sparrow-actor-core/src/main/java/com/shansun/sparrow/actor";
        targetFolder = "/home/hadoop/IdeaProjects/go-bigdata/zava/src/main/java/com/shansun/sparrow/actor";
        testReadAndWrite(srcFolder, targetFolder);
    }

    //gbk格式的文件转成utf-8格式
    public static void testReadAndWrite(String srcFolder, String targetFolder)throws Exception{
        //test one file
        /*
        String fileName = "/home/hadoop/github.com/sparrow/src/main/java/com/shansun/sparrow/actor/api/Actor.java";
        String fileOut = "/home/hadoop/IdeaProjects/go-bigdata/zava/src/main/java/com/shansun/sparrow/actor/api/Actor.java";
        String fileConent = FileIOEncode.readInput(fileName, "gbk");
        FileIOEncode.writeOutput(fileOut, fileConent, "UTF-8");
        */

        List<String> sb = new ArrayList<>();
        File folderFile = new File(srcFolder);
        recurFiles(folderFile, sb);

        for(String filename : sb){
            String fileConent = FileIOEncode.readInput(filename, "gbk");
            String fileout = filename.replace(srcFolder,targetFolder);

            File targetFile = new File(fileout);
            File parent = targetFile.getParentFile();
            if(!parent.exists()){
                parent.mkdirs();
            }
            if(!targetFile.exists()){
                targetFile.createNewFile();
            }

            FileIOEncode.writeOutput(fileout, fileConent, "UTF-8");
        }
    }
    private static void recurFiles(File folder, List<String> sb){
        File[] files = folder.listFiles();
        for(File f : files){
            if(f.isDirectory()){
                recurFiles(f,sb);
            }else{
                sb.add(f.getAbsolutePath());
            }
        }
    }

    private static void testVarInt(int value){
        ByteBuffer buf = ByteBuffer.allocate(10);
        writeVarInt(value, buf);

        buf.position(0);
        buf.limit(buf.capacity());
        while (buf.hasRemaining()) {
            System.out.print(" "+buf.get());
        }
        System.out.println();

        //value占用了几个字节? 1个字节!
        //0--127都是占用1个字节! 因为byte的范围是0-127. 总共128个数
    }

    //varInt的范围从1到7.
    public static void writeVarInt(int value, ByteBuffer buf) {
        while ((value & 0xFFFFFF80) != 0L) {
            buf.put((byte) ((value & 0x7F) | 0x80));
            value >>>= 7;
        }
        buf.put((byte) (value & 0x7F));
    }

    private static void testByteSlice5() {
        ByteBuffer buf = ByteBuffer.allocate(10);
        ByteBuffer newBuf = buf.slice();

        //修改原始buf
        buf.put((byte)1);
        buf.put((byte)2);
        buf.put((byte)3);
        buf.put((byte)4);
        buf.put((byte)5);

        System.out.println("buf remain:" + buf.remaining());
        System.out.println("slice remain:" + newBuf.remaining());

        System.out.println("buf pos:" + buf.position());
        System.out.println("slice pos:" + newBuf.position());
    }

    private static void testByteSlice4() {
        ByteBuffer buf = ByteBuffer.allocate(10);
        ByteBuffer newBuf = buf.slice();

        //修改slice副本
        newBuf.put((byte)1);
        newBuf.put((byte)2);
        newBuf.put((byte)3);
        newBuf.put((byte)4);
        newBuf.put((byte)5);

        //容量都一样大
        System.out.println("buf capacity:" + buf.capacity());
        System.out.println("slice capacity:" + newBuf.capacity());

        System.out.println("buf remain:" + buf.remaining());
        //slice的remain减少, 而原始buf的remain不变
        System.out.println("slice remain:" + newBuf.remaining());

        //原始buf的位置还是在0, 而slice的position向右移动了写入的字节数量
        System.out.println("buf pos:" + buf.position());
        System.out.println("slice pos:" + newBuf.position());
    }

    //修改slice,原始buf也被修改
    private static void testByteSlice3() {
        ByteBuffer buf = ByteBuffer.allocate(10);
        ByteBuffer newBuf = buf.slice();

        //修改slice
        newBuf.put((byte)1);
        newBuf.put((byte)2);
        newBuf.put((byte)3);
        newBuf.put((byte)4);
        newBuf.put((byte)5);

        //slice被修改
        newBuf.position(0);
        newBuf.limit(buf.capacity());
        while (newBuf.hasRemaining()) {
            System.out.print(newBuf.get());
        }
        System.out.println();

        //buf也被修改
        buf.position(0);
        buf.limit(buf.capacity());
        while (buf.hasRemaining()) {
            System.out.print(buf.get());
        }
    }

    //修改原始buf,slice也被修改
    private static void testByteSlice2() {
        ByteBuffer buf = ByteBuffer.allocate(10);
        ByteBuffer newBuf = buf.slice();
        //修改buf
        buf.position(1);
        buf.put((byte)1);

        //buf被改了
        buf.position(0);
        buf.limit(buf.capacity());
        while (buf.hasRemaining()) {
            System.out.print(buf.get());
        }
        System.out.println();

        //slice也被修改了
        newBuf.position(0);
        newBuf.limit(newBuf.capacity());
        while (newBuf.hasRemaining()) {
            System.out.print(newBuf.get());
        }
    }

    private static void testByteSlice(){
        byte array[] = new byte[10];
        ByteBuffer buffer = ByteBuffer.wrap(array);
        for (int i = 0; i < buffer.capacity(); i++) {
            buffer.put((byte)i);
        }

        buffer.position(3);
        buffer.limit(7);
        ByteBuffer slice = buffer.slice();

        for (int i = 0; i < slice.capacity(); i++) {
            byte b = slice.get(i);
            b *= 11;
            slice.put(i, b);
        }

        buffer.position(0);
        buffer.limit(buffer.capacity());

        while (buffer.hasRemaining()) {
            System.out.print(buffer.get());
        }
    }

    private static void testByteMove(){
        //[0000 0000] | 0000 0000 | 0000 0100 | 0000 0000 >>> 24 = 0000 0000 右移24位,只剩最左边8位=1个byte=0
        //0000 0000 | [0000 0000] | 0000 0100 | 0000 0000 >>> 16 = 0000 0000 右移16位,剩余左边16位,由于byte只有8位,取最右边的8位=0
        //0000 0000 | 0000 0000 | [0000 0100] | 0000 0000 >>> 8 = 0000 0000 右移16位,剩余左边32位,由于byte只有8位,取最右边的8位=0
        //0000 0000 | 0000 0000 | 0000 0100 | [0000 0000] >>> 0 = 0000 0000 没有移位,由于byte只有8位,取最右边的8位=0
        byte[] buf = new byte[4];
        int i=0;
        int offset = 1024;
        buf[i++] = (byte) (offset >>> 24);//0
        buf[i++] = (byte) (offset >>> 16);//0
        buf[i++] = (byte) (offset >>> 8); //4
        buf[i++] = (byte) offset; //0
        for (byte b:buf){
            System.out.println(b);
        }
    }

    private static void testFixedKey(){
        // 12-->12|  
        // 123-->123| 
        // 1234-->1234|
        // 12345-->ERROR
        String key = "12";
        byte[] fixed = toFixedKey(key);
        System.out.println(new String(fixed));
        System.out.println(fixed.length);
        System.out.println(key.length());
        System.out.println(key.getBytes().length);
        byte zero = (byte)'\u0000'; // 
        System.out.println(zero);

        byte[] init = new byte[keyLength];
        for (byte b:init){
            System.out.println(b);
        }
    }

    private static int keyLength = 5;

    private static byte[] toFixedKey(String key) {
        byte[] keyBytes = key.getBytes();
        if (key.length() >= keyLength) {
            throw new IllegalArgumentException("key length overflow" + key);
        }

        //固定的字节数组,假设keyLength=5
        byte[] fixed = new byte[keyLength];
        //keyBytes的长度最大=keyLength-1,比如keyLength=5,keyBytes最多4个字节
        int len = keyBytes.length;
        //拷贝keyBytes的全部内容到fixed中.因为keyBytes的长度小于fixed的.
        //所以最多只会拷贝到fixed的最后一个字节就停止了
        System.arraycopy(keyBytes, 0, fixed, 0, len);
        //在拷贝的内容之后的一个字节填充一个分隔符.
        //假设len=4,keyLength=5.要拷贝keyBytes的全部len=4个字节到fixed中
        //因为数组下标从0开始,所以填充到fixed中的是[0-3]
        //现在在接下来的一个字节处即fixed[4]填充分隔符.
        //假如len=1,则fixed[0]是keyBytes的1个字节的拷贝. fixed[1]是分隔符
        //假如len=2,则fixed[0-1]是keyBytes的2个字节的拷贝. fixed[2]是分隔符
        fixed[len] = (byte) '|';
        return fixed;
    }

    private static void testMappedByteBuffer()throws Exception{
        RandomAccessFile f = new RandomAccessFile("/home/hadoop/test.txt", "rw");
        byte[] bytes = "hello world. I am a new guy...".getBytes();

        if(f.length()>=0){
            //如果文件为空,则mbb的大小为0,无法将数据放入缓冲区为0的内存中,否则会报错:java.nio.BufferOverflowException
            MappedByteBuffer mbb = f.getChannel().map(FileChannel.MapMode.READ_WRITE,0,f.length());
            //如果文件不为空,但是mbb的大小为文件的大小,要写入的数据比声明的大小,还要大,也会报错.
            if(f.length() > bytes.length){
                mbb.put(bytes);
            }
        }

        //所以可以事先声明一个大的长度,就可以写入数据了.当然也要注意不要溢出!
        MappedByteBuffer mbb = f.getChannel().map(FileChannel.MapMode.READ_WRITE,0,100);
        mbb.put(bytes);

        f = new RandomAccessFile("/home/hadoop/test.txt", "r");
        System.out.println(f.length());
    }
}
