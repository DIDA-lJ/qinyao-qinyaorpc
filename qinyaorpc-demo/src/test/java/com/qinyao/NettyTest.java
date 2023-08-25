package com.qinyao;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.Unpooled;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * @author LinQi
 * @createTime 2023-08-10
 */
public class NettyTest {

    @Test
    public void testCompositeByteBuf (){
        ByteBuf header = Unpooled.buffer();
        ByteBuf body = Unpooled.buffer();

        // 通过逻辑组装而不是物理拷贝，实现在jvm中的零拷贝
        CompositeByteBuf byteBuf = Unpooled.compositeBuffer();
        byteBuf.addComponents(header,body);
    }

    @Test
    public void testWrapper (){
        byte[] buf = new byte[1024];
        byte[] buf2 = new byte[1024];
        // 共享byte数组的内容而不是拷贝，这也算零拷贝
        ByteBuf byteBuf = Unpooled.wrappedBuffer(buf, buf2);
    }

    @Test
    public void testSlice (){
        byte[] buf = new byte[1024];
        byte[] buf2 = new byte[1024];
        // 共享byte数组的内容而不是拷贝，这也算零拷贝
        ByteBuf byteBuf = Unpooled.wrappedBuffer(buf, buf2);

        // 同样可以将一个byteBuf，分割成多个，使用共享地址，而非拷贝
        ByteBuf buf1 = byteBuf.slice(1, 5);
        ByteBuf buf3 = byteBuf.slice(6, 15);
    }

    @Test
    public void testMessage() throws IOException {
        ByteBuf message = Unpooled.buffer();
        message.writeBytes("ydl".getBytes(StandardCharsets.UTF_8));
        message.writeByte(1);
        message.writeShort(125);
        message.writeInt(256);
        message.writeByte(1);
        message.writeByte(0);
        message.writeByte(2);
        message.writeLong(251455L);
        // 用对象流转化为字节数据
        AppClient appClient = new AppClient();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(outputStream);
        oos.writeObject(appClient);
        byte[] bytes = outputStream.toByteArray();
        message.writeBytes(bytes);

        printAsBinary(message);

    }

    public static void printAsBinary(ByteBuf byteBuf) {
        byte[] bytes = new byte[byteBuf.readableBytes()];
        byteBuf.getBytes(byteBuf.readerIndex(), bytes);

        String binaryString = ByteBufUtil.hexDump(bytes);
        StringBuilder formattedBinary = new StringBuilder();

        for (int i = 0; i < binaryString.length(); i += 2) {
            formattedBinary.append(binaryString.substring(i, i + 2)).append(" ");
        }

        System.out.println("Binary representation: " + formattedBinary.toString());
    }

    @Test
    public void testCompress() throws IOException {
        byte[] buf = new byte[]{12,12,12,12,12,25,34,23,25,14,12,12,12,12,25,34,23,25,14,12,12,12,12,25,34,23,25,14,12,12,12,12,25,34,23,25,14,12,12,12,12,25,34,23,25,14,12,12,12,12,25,34,23,25,14,12,12,12,12,25,34,23,25,14,12,12,12,12,25,34,23,25,14,12,12,12,12,25,34,23,25,14,12,12,12,12,25,34,23,25,14,12,12,12,12,25,34,23,25,14,12,12,12,12,25,34,23,25,14,12,12,12,12,25,34,23,25,14,12,12,12,12,25,34,23,25,14,12,12,12,12,25,34,23,25,14,12,12,12,12,25,34,23,25,14};

        // 本质就是，将buf作为输入，将结果输出到另一个字节数组当中
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        GZIPOutputStream gzipOutputStream = new GZIPOutputStream(baos);

        gzipOutputStream.write(buf);
        gzipOutputStream.finish();

        byte[] bytes = baos.toByteArray();
        System.out.println(buf.length + "--> " + bytes.length);
        System.out.println(Arrays.toString(bytes));
    }


    @Test
    public void testDeCompress() throws IOException {
        byte[] buf = new byte[]{31, -117, 8, 0, 0, 0, 0, 0, 0, -1, -29, -31, 1, 2, 73, 37, 113, 73, -66, 65, -62, 0, 0, 25, -102, -59, -115, -111, 0, 0, 0};

        // 本质就是，将buf作为输入，将结果输出到另一个字节数组当中
        ByteArrayInputStream bais = new ByteArrayInputStream(buf);
        GZIPInputStream gzipInputStream = new GZIPInputStream(bais);

        byte[] bytes = gzipInputStream.readAllBytes();
        System.out.println(buf.length + "--> " + bytes.length);
        System.out.println(Arrays.toString(bytes));
    }
}
