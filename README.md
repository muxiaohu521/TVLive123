# TVBox - Android 电视直播盒子

基于 Android 平台的开源电视直播应用，支持 IPTV 直播源播放、EPG 电子节目单、多源订阅管理等功能。

## 许可证

本项目采用 **GNU Affero General Public License v3.0 (AGPL-3.0)** 开源许可协议。

> 注意：如果你修改了本项目代码并通过网络提供服务，你必须向远程用户公开修改后的源代码。详见 [LICENSE](LICENSE) 文件。

## 功能特性

- **直播电视播放**：支持 M3U8、FLV 等多种流媒体格式的电视直播播放
- **多播放器引擎**：
  - IJKPlayer（基于 FFmpeg 的软解播放器）
  - ExoPlayer（Google 官方播放器）
  - 支持第三方播放器调用（MXPlayer、VLC、Kodi、ReexPlayer）
- **EPG 电子节目单**：支持 EPG 数据集成与模糊匹配，提供节目预告信息
- **多源订阅**：
  - Jar 格式爬虫加载器
  - JavaScript（QuickJS 引擎）爬虫支持
  - Python（Chaquopy 引擎）爬虫支持
  - M3U 直播源订阅
  - TXT 格式订阅源
- **远程控制**：内置 HTTP 服务器（NanoHTTPD），支持局域网内远程操控
- **广告屏蔽**：内置广告过滤功能
- **多编码支持**：支持 GBK、Unicode 等多种字符编码
- **DNS over HTTPS**：支持安全 DNS 解析
- **IPv6 支持**：优先使用 IPv6 地址解析
- **自定义设置**：
  - 频道分组管理
  - 直播源编辑与管理
  - 播放器解码模式切换
  - 代理规则配置

## 技术架构

### 模块结构

| 模块 | 说明 |
|------|------|
| `app` | 主应用模块，包含 UI 界面、业务逻辑、数据模型 |
| `player` | 视频播放器模块，封装 IJKPlayer 与 ExoPlayer |
| `quickjs` | QuickJS JavaScript 引擎的 Android 集成封装 |
| `pyramid` | Python 运行时集成（基于 Chaquopy），支持 Python 爬虫 |

### 技术栈

| 类别 | 技术 |
|------|------|
| **开发语言** | Java 8 |
| **构建工具** | Gradle 7.5, Android Gradle Plugin 7.2.2 |
| **最低 SDK** | API 16 (Android 4.1) |
| **目标 SDK** | API 28 (Android 9.0) |
| **编译 SDK** | API 33 (Android 13.0) |
| **视频播放** | IJKPlayer (FFmpeg), ExoPlayer |
| **网络请求** | OkHttp 3.12, OkGo |
| **JSON 解析** | Gson 2.10 |
| **图片加载** | Glide 4.16, Picasso 2.71828 |
| **HTML 解析** | Jsoup 1.14 |
| **数据存储** | Room 2.3, Hawk 2.0 |
| **事件总线** | EventBus 3.2 |
| **Web 服务器** | NanoHTTPD 2.3 |
| **JS 引擎** | QuickJS |
| **Python 引擎** | Chaquopy 12.0 (Python 3.8) |
| **UI 适配** | AutoSize 1.2 |

### Python 依赖（Pyramid 模块）

- `lxml` - XML/HTML 解析
- `ujson` - 高性能 JSON 处理
- `pyquery` - jQuery 风格的 HTML 解析
- `requests` - HTTP 请求
- `jsonpath` - JSON 路径查询
- `cachetools` - 缓存工具
- `pycryptodome` - 加密解密
- `beautifulsoup4` - HTML/XML 解析

## 项目结构

```
TVLive/
├── app/                          # 主应用模块
│   └── src/
│       ├── main/
│       │   ├── java/com/github/
│       │   │   ├── tvbox/osc/    # 主业务代码
│       │   │   │   ├── api/      # API 配置与数据加载
│       │   │   │   ├── base/     # Application 基类
│       │   │   │   ├── bean/     # 数据模型
│       │   │   │   ├── event/    # 事件定义
│       │   │   │   ├── player/   # 播放器封装
│       │   │   │   ├── receiver/ # 广播接收器
│       │   │   │   ├── server/   # HTTP 服务器（远程控制）
│       │   │   │   ├── ui/       # 界面层
│       │   │   │   │   ├── activity/  # Activity
│       │   │   │   │   ├── adapter/   # 适配器
│       │   │   │   │   ├── dialog/    # 对话框
│       │   │   │   │   └── fragment/  # Fragment
│       │   │   │   ├── util/     # 工具类
│       │   │   │   └── viewmodel/# ViewModel
│       │   │   └── catvod/       # 爬虫框架
│       │   │       ├── crawler/  # 爬虫加载器(Jar/JS/Python)
│       │   │       └── net/      # 网络层
│       │   ├── assets/           # 静态资源(JS脚本等)
│       │   └── res/              # Android 资源文件
│       └── python/               # Python 爬虫相关
│       └── normal/               # 标准版源码
├── player/                       # 播放器模块(IJKPlayer + ExoPlayer)
├── quickjs/                      # QuickJS JS引擎模块
├── pyramid/                      # Python运行时模块(Chaquopy)
├── build.gradle                  # 根构建配置
├── settings.gradle               # 项目设置
└── LICENSE                       # AGPL-3.0 许可证
```

## 构建说明

### 环境要求

- Android Studio 2021+ 
- JDK 8
- Android SDK API 33
- NDK（armeabi-v7a / arm64-v8a）
- Python 3.8（用于 Pyramid 模块构建）

### 构建步骤

1. 克隆项目
```bash
git clone https://github.com/muxiaohu521/TVLive123.git
cd TVLive
```

2. 使用 Android Studio 打开项目

3. 配置 Python 3.8 环境（设置环境变量 `PYTHON38_HOME` 指向 Python 3.8 安装路径，或者确保系统 PATH 中有 `python` 命令）

4. 同步 Gradle 依赖

5. 构建 APK
```bash
./gradlew assembleJava32Release
```

6. 构建产物位于 `output/apk/` 目录

## 主要界面

- **LivePlayActivity**：直播播放主界面，支持频道列表、EPG 节目单、信号源切换
- **SettingActivity**：设置界面，支持直播源管理、播放器配置等

## 直播源格式支持

- M3U/M3U8 格式直播源
- TXT 文本格式订阅
- Jar 加载的自定义数据源
- JavaScript 爬虫数据源（QuickJS）
- Python 爬虫数据源（Chaquopy）

## 致谢

本项目基于开源社区众多优秀项目构建，包括但不限于：

- [TVBoxOS](https://github.com/q215613905/TVBoxOS) - 原始项目
- [CatVod](https://github.com/catvod/CatVod) - 爬虫框架
- [IJKPlayer](https://github.com/bilibili/ijkplayer) - 视频播放器
- [ExoPlayer](https://github.com/google/ExoPlayer) - Google 播放器
- [Chaquopy](https://chaquo.com/chaquopy/) - Android Python 运行时
- [QuickJS](https://bellard.org/quickjs/) - JavaScript 引擎
- [NanoHTTPD](https://github.com/NanoHttpd/nanohttpd) - 轻量级 HTTP 服务器

---

**免责声明**：本项目仅供学习交流使用，请勿用于商业用途。使用本项目产生的任何法律责任由使用者自行承担。