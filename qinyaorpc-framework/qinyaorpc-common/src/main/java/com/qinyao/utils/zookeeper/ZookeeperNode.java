package com.qinyao.utils.zookeeper;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author LinQi
 * @createTime 2023-08-29
 */
// lombok
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ZookeeperNode {
    private String nodePath;
    private byte[] data;
}
