package com.qinyao.compress;

/**
 * @author LinQi
 * @createTime 2023-07-05
 */
public interface Compressor {
    
    /**
     * 对字节数据进行压缩
     * @param bytes 带压缩的字节数组
     * @return 压缩后的字节数据
     */
    byte[] compress(byte[] bytes);
    
    /**
     * 对字节数据进行解压缩
     * @param bytes 待解压缩的字节数据
     * @return 解压缩后的字节数据
     */
    byte[] decompress(byte[] bytes);
}
