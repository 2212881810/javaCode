package qinfeng.zheng;

import java.io.File;
import java.io.FileOutputStream;
import java.util.concurrent.TimeUnit;

/**
 * @Author ZhengQinfeng
 * @Date 2021/1/16 20:29
 * @dec 文件io操作
 */
public class OsFileIo {

    /**
     * 数据源
     */
    static byte[] data = "123456789\n".getBytes();

    static String path = "/usr/local/test/io/data.txt";

    public static void main(String[] args) throws Exception {
        OsFileIo obj = new OsFileIo();
        obj.fileIo();
    }

    private void fileIo() throws Exception {
        File file = new File(path);
        FileOutputStream outputStream = new FileOutputStream(file);
        while (true) {
            TimeUnit.SECONDS.sleep(10);
            outputStream.write(data);
//            outputStream.flush();
//            outputStream.close();
        }

    }
}
