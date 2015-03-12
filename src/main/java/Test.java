import java.io.File;
import java.io.RandomAccessFile;

/**
 * Created by zqhxuyuan on 15-3-11.
 */
public class Test {
    public static void main(String[] args)throws Exception{
        System.out.println("abcdefgsf".getBytes().length);

        RandomAccessFile f = new RandomAccessFile("/home/hadoop/test.txt", "rw");
    }
}
