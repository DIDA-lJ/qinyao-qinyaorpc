package com.qinyao;


import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * @author LinQi
 * @createTime 2023-07-20
 */
public class ZookeeperTest {

    ZooKeeper zooKeeper;
    CountDownLatch countDownLatch = new CountDownLatch(1);

    @Before
    public void createZk(){

        // 定义连接参数
        String connectString = "127.0.0.1:2181";
//        String connectString = "192.168.126.129:2181,192.168.126.132:2181,192.168.126.133:2181";
        // 定义超时时间
        int timeout = 10000;

        try {
            // new MyWatcher() 默认的watcher
            // 创建一个zookeeper实例，是否需要时间，是否需要等待
            // 构建zookeeper是否需要等待连接
//            zooKeeper = new ZooKeeper(connectString,timeout,new MyWatcher());
            zooKeeper = new ZooKeeper(connectString, timeout, event -> {
                // 只有连接成功才放行
                if(event.getState() == Watcher.Event.KeeperState.SyncConnected){
                    System.out.println("客户端已经连接成功。");
                    countDownLatch.countDown();
                }
            });

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testCreatePNode(){
        try {
            // 会等待连接成功
            countDownLatch.await();
            zooKeeper.setData("/ydlclass","hi".getBytes(),-1);
            String result = zooKeeper.create("/ydlclass", "hello".getBytes(),
                    ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            System.out.println("result = " + result);
        } catch (KeeperException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            try {
                if(zooKeeper != null){
                    zooKeeper.close();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void testDeletePNode(){
        try {
            // version: cas  mysql  乐观锁，  也可以无视版本号  -1
            zooKeeper.delete("/ydlclass",-1);
        } catch (KeeperException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            try {
                if(zooKeeper != null){
                    zooKeeper.close();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void testExistsPNode(){
        try {
            // version: cas  mysql  乐观锁，  也可以无视版本号  -1
            Stat stat = zooKeeper.exists("/ydlclass", null);

            zooKeeper.setData("/ydlclass","hi".getBytes(),-1);

            // 当前节点的数据版本
            int version = stat.getVersion();
            System.out.println("version = " + version);
            // 当前节点的acl数据版本
            int aversion = stat.getAversion();
            System.out.println("aversion = " + aversion);
            // 当前子节点数据的版本
            int cversion = stat.getCversion();
            System.out.println("cversion = " + cversion);


        } catch (KeeperException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            try {
                if(zooKeeper != null){
                    zooKeeper.close();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    @Test
    public void testWatcher(){
        try {
            // 以下三个方法可以注册watcher，可以直接new一个新的watcher，
            // 也可以使用true来选定默认的watcher
            zooKeeper.exists("/ydlclass", true);
//            zooKeeper.getChildren();
//            zooKeeper.getData();

            while(true){
                Thread.sleep(1000);
            }

        } catch (KeeperException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            try {
                if(zooKeeper != null){
                    zooKeeper.close();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }



}
