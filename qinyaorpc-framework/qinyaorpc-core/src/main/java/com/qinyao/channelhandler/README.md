<h3> 1、服务调用方 </h3>
<hr>
发送报文 writeAndFlush(object) <br/>
请求此object应该是什么？ 应该包含一些什么样的信息？
QinYaorpcRequest
<ol>
    <li> 请求id  （long） </li>
    <li> 压缩类型 （1byte）  </li>
    <li> 序列化的方式 （1byte）  </li>
    <li> 消息类型（普通请求，心跳检测请求）（1byte）   </li>
    <li> 负载 payload（接口的名字，方法的名字，参数列表，返回值类型） </li>
</ol>
<b>pipeline就生效了，报文开始出站</b> <br>
<b>----> </b> 第一个处理器 in/out log <br>
<b>----> </b> 第二个处理器 编码器（out）(转化 qinyaorpcRequest -> msg(请求报文)，序列化，压缩）

<h3> 2、服务提供方 </h3>
<hr>
通过netty接受报文 <br/>
<b>pipeline就生效了，报文开始出站</b>
<b>----></b> 第一个处理器 in/out log   <br/>
<b>----></b> 第二个处理器 解码器（in）（解压缩，反序列化，msg-> qinyaorpcrequest）<br/>
<b>----></b> 第三个处理器 想办法处理 （in） qinyaorpcrequest 执行方法调用，得到结果 <br/>

<h3> 3、 执行方法调用，得到结果 </h3>
<br>
<h3> 4、服务提供方 </h3>
<hr>
<b> 发送报文 writeAndFlush(object) 响应 </b> <br/>
<b> pipeline就生效了，报文开始出站 </b><br/>
<b>----></b> 第一个处理器（out）（转化 object -> msg(响应报文)）  <br/>
<b>----></b> 第二个处理器（out）（序列化）<br/>
<b>----></b> 第三个处理器（out）（压缩）<br/>

<h3> 5、服务调用方 </h3>
<hr>
通过netty接受响应报文 <br/>
pipeline就生效了，报文开始出站 <br/>
<b>----></b> 第一个处理器（in）（解压缩）  <br/>
<b>----></b> 第二个处理器（in）（反序列化） <br/>
<b>----></b> 第三个处理器（in）（解析报文） <br/>

<h3> 6、得到结果返回 </h3>

