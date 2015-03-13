import java.io.File;
import java.io.RandomAccessFile;

/**
 * Created by zqhxuyuan on 15-3-11.
 */
public class Test {
    public static void main(String[] args)throws Exception{
        /**
        RandomAccessFile f = new RandomAccessFile("/home/hadoop/test.txt", "rw");

        System.out.println(System.getProperty("user.dir"));
        */

        // 12-->12|  
        // 123-->123| 
        // 1234-->1234|
        // 12345-->ERROR
        /**
        String key = "12";
        byte[] fixed = toFixedKey(key);
        System.out.println(new String(fixed));
        System.out.println(fixed.length);
        System.out.println(key.length());
        System.out.println(key.getBytes().length);
        byte zero = (byte)'\u0000'; // 
        System.out.println(zero);
        */
        byte[] fixed = new byte[keyLength];
        for (byte b:fixed){
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
}
