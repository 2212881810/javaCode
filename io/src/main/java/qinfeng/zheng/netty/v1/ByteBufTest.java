package qinfeng.zheng.netty.v1;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.UnpooledByteBufAllocator;
import org.junit.Test;

/**
 * @Author ZhengQinfeng
 * @Date 2021/1/17 20:18
 * @dec
 */
public class ByteBufTest {
    @Test
    public void test2() throws Exception {
        ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer(20);
        buffer.writeBytes("123456789".getBytes());

        System.out.println(buffer.readableBytes());
        byte[] buf = new byte[5];
        buffer.getBytes(0, buf);

        System.out.println(buffer.readableBytes());

        buffer.readBytes(buf);

        System.out.println(buffer.readableBytes());

    }


    @Test
    public void testByteBuf() throws Exception {
        // 创建buf的几种方式
        ByteBuf buffer = null;
        buffer = ByteBufAllocator.DEFAULT.buffer(8, 16);
//        buffer = UnpooledByteBufAllocator.DEFAULT.buffer(8, 20);
//        buffer = UnpooledByteBufAllocator.DEFAULT.heapBuffer(8, 20); // 非池化,堆内buf
//        buffer = PooledByteBufAllocator.DEFAULT.heapBuffer(8, 20); // 池化


        print(buffer);

        buffer.writeBytes(new byte[]{1, 2, 3, 4});
        print(buffer);


    }


    public static void print(ByteBuf buf) {
        System.out.println("=======read==============");
        System.out.println("是否可读：" + buf.isReadable());
        System.out.println("读指针：" + buf.readerIndex());
        System.out.println("可读字节数量："+buf.readableBytes());


        System.out.println("是否可写：" + buf.isWritable());
        System.out.println(buf.writerIndex());
        System.out.println(buf.writableBytes());

        System.out.println("capacity :" + buf.capacity());
        System.out.println("max capacity :" + buf.maxCapacity());
        System.out.println("isDirect :" + buf.isDirect());


    }
}
