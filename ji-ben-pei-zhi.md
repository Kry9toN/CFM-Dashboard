# 基本配置

### 原版 <a href="#undefined" id="undefined"></a>

使用支持Root的文件管理器打开**模块默认配置目录**`/data/clash`, 把**符合clash配置规范**的配置文件重命名为`config.yaml`放到该目录, 然后下载[GeoIP.dat](https://github.com/Loyalsoldier/v2ray-rules-dat/releases/latest)、[GeoSite.dat](https://github.com/Loyalsoldier/geoip/releases/latest)、[Country.mmdb](https://github.com/Loyalsoldier/geoip/releases/latest)文件到该目录。

### 魔改版 <a href="#undefined" id="undefined"></a>

**魔改版1、魔改版2**自带上述所需文件，只需配置订阅链接即可。[详细配置方法](broken-reference)

配置完成后你可以尝试重启手机, 你就可以享受模块带来的快乐啦>\_<。

### 注意 <a href="#undefined" id="undefined"></a>

模块工作时, 会在Clash配置目录(默认为模块默认配置目录)的`run`文件夹下生成适用于Clash For Magisk模块的配置文件且从第一行到`proxies:`的前一行使用的是`template`文件的内容。

即如需更改实际运行时所使用的Clash配置文件的proxies:之前的内容(_**Clash本身的特性大多在此处定义**_)可通过更改`template`文件实现。

## 更新Clash内核 <a href="#update_kernel" id="update_kernel"></a>

* 别问CFM的最新版是啥，**科技以换核为本**，自己换内核

**原版、魔改2** 内核位置：**/data/adb/modules/Clash\_For\_Magisk/system/bin/clash**

**魔改1** 内核位置：**/data/clash/kernel/clash**

自行下载内核文件替换后重启手机即可（魔改版免重启）

主流内核下载地址：[原版Clash](https://github.com/Dreamacro/clash)、[Clash.Meta](https://github.com/MetaCubeX/Clash.Meta)、[Clash Premium](https://github.com/Dreamacro/clash/releases/tag/premium)（闭源）、[experimental-clash](https://github.com/ClashDotNetFramework/experimental-clash)
