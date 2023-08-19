package com.qinyao.compress.impl;

import com.qinyao.compress.Compressor;
import com.qinyao.exceptions.CompressException;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * 使用gzip算法进行压缩的具体实现
 * @author LinQi
 * @createTime 2023-07-05
 */
@Slf4j
public class GzipCompressor implements Compressor {
    @Override
    public byte[] compress(byte[] bytes) {
    
        try (
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            GZIPOutputStream gzipOutputStream = new GZIPOutputStream(baos);
        ) {
            gzipOutputStream.write(bytes);
            gzipOutputStream.finish();
            byte[] result = baos.toByteArray();
            if(log.isDebugEnabled()){
                log.debug("对字节数组进行了压缩长度由【{}】压缩至【{}】.",bytes.length,result.length);
            }
            return result;
        } catch (IOException e){
            log.error("对字节数组进行压缩时发生异常",e);
            throw new CompressException(e);
        }

    }
    
    @Override
    public byte[] decompress(byte[] bytes) {
        try (
            ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
            GZIPInputStream gzipInputStream = new GZIPInputStream(bais);
        ) {
            byte[] result = gzipInputStream.readAllBytes();
            if(log.isDebugEnabled()){
                log.debug("对字节数组进行了解压缩长度由【{}】变为【{}】.",bytes.length,result.length);
            }
            return result;
        } catch (IOException e){
            log.error("对字节数组进行压缩时发生异常",e);
            throw new CompressException(e);
        }
    }
}
