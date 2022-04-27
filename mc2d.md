# 魔二特别说明

三种代理方式可选：Tproxy、Tun-Iptable、Tun-Routing

## Tproxy

安卓推荐配置，也是默认配置

### 启用方式

```yaml
tproxy-port: 7893  # 推荐值
dns:
  enable: true
  listen: '0.0.0.0:1053'
```

### 优势

* 省电
* 网速上限高
* 完全体IPV6支持

### 劣势

* 不能解析IPV6 DNS，能连接，所以V6域名分流规则失效
* 内核异常退出后断网，包括国内
* UDP性能较差

## Tun-Iptables

### 启用方式

```yaml
#tproxy-port: 7893  # 建议注释掉，省电
dns:
  enable: true
  auto-route: false
  #listen: '0.0.0.0:1053'  # 建议注释掉，省电
```

### 优势

* UDP性能更高



### 劣势

* 不支持 `stack: system`&#x20;
* 内核异常退出后断网，包括国内
* IPV6不进内核，仅提供域名时能使用IPV6

## Tun-Routing

### 启用方式

```yaml
#tproxy-port: 7893  # 建议注释掉，省电
dns:
  enable: true
  auto-route: true
  #listen: '0.0.0.0:1053'  # 建议注释掉，省电
```

### 优势

* UDP性能更高
* 支持 `stack: system`&#x20;
* 内核异常退出后不影响国内网络
* 完全体IPV6支持

### 劣势

* IPV6不进内核，仅提供域名时能使用IPV6
* 开热点后，连接热点的设备不能与热点机直接通信（能上网，连接得走公网）
* 192.168.0.1/16网段的通信可能有问题（路由器管理页面）
