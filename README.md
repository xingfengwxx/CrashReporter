# Crash监控

Crash（应用崩溃）是由于代码异常而导致 App 非正常退出，导致应用程序无法继续使用，所有工作都停止的现象。发生 Crash 后需要重新启动应用（有些情况会自动重启），而且不管应用在开发阶段做得 多么优秀，也无法避免 Crash 发生，特别是在 Android 系统中，系统碎片化严重、各 ROM 之间的差异，甚至系统Bug，都可能会导致Crash的发生。

在 Android 应用中发生的 Crash 有两种类型，Java 层的 Crash 和 Native 层 Crash。这两种Crash 的监控和获取堆栈信息有所不同。

## Java Crash

Java的Crash监控非常简单，Java中的Thread定义了一个接口： UncaughtExceptionHandler ；用于处理未捕获的异常导致线程的终止（注意：catch了的是捕获不到的），当我们的应用crash的时候，就 会走UncaughtExceptionHandler.uncaughtException ，在该方法中可以获取到异常的信息，我们通过Thread.setDefaultUncaughtExceptionHandler 该方法来设置线程的默认异常处理器，我们可以将异常信息保存到本地或者是上传到服务器，方便我们快速的定位问题。

## NDK Crash

相对于Java的Crash，NDK的错误无疑更加让人头疼，特别是对初学NDK的同学，不说监控，就算是错误堆栈都不知道怎么看。

### Linux信号机制

信号机制是Linux进程间通信的一种重要方式，Linux信号一方面用于正常的进程间通信和同步，另一方面它还负责监控系统异常及中断。当应用程序运行异常时，Linux内核将产生错误信号并通知当前进程。当前进程在接收到该错误信号后，可以有三种不同的处理方式。

- 忽略该信号
- 捕捉该信号并执行对应的信号处理函数（信号处理程序）
- 执行该信号的缺省操作（如终止进程）

当Linux应用程序在执行时发生严重错误，一般会导致程序崩溃。其中，Linux专门提供了一类crash信号，在程序接收到此类信号时，缺省操作是将崩溃的现场信息记录到核心文件，然后终止进程。

常见崩溃信号列表：

![](https://gitee.com/xingfengwxx/blogImage/raw/master/img/20211119180930.png)

一般的出现崩溃信号，Android系统默认缺省操作是直接退出我们的程序。但是系统允许我们给某一个进程的某一个特定信号注册一个相应的处理函数（signal），即对该信号的默认处理动作进行修改。因此NDK Crash的监控可以采用这种信号机制，捕获崩溃信号执行我们自己的信号处理函数从而捕获NDK Crash。

### 墓碑

Android本机程序本质上就是一个Linux程序，当它在执行时发生严重错误，也会导致程序崩溃，然后产生一个记录崩溃的现场信息的文件，而这个文件在Android系统中就是 tombstones 墓碑文件。

### BreakPad

Google breakpad是一个跨平台的崩溃转储和分析框架和工具集合，其开源地址是：

https://github.com/google/breakpad

breakpad在Linux中的实现就是借助了Linux信号捕获机制实现的。因为其实现为C++，因此在Android中使用，必须借助NDK工具。

如果出现NDK Crash，会在我们指定的目录： /data/data/[packageName]/files/native_crash 下生成NDKCrash信息文件。

### Crash解析

采集到的Crash信息记录在minidump文件中。minidump是由微软开发的用于崩溃上传的文件格式。我们可以将此文件上传到服务器完成上报，但是此文件没有可读性可言，要将文件解析为可读的崩溃堆栈 需要按照breakpad文档编译 minidump_stackwalk 工具，而Windows系统编译个人不会。不过好在，无论你是Mac、windows还是ubuntu在 Android Studio 的安装目录下的 bin\lldb\bin 里面就存在一 个对应平台的minidump_stackwalk 。

```shell
minidump_stackwalk xxxx.dump > crash.txt
```

接下来使用 Android NDK 里面提供的 addr2line 工具将寄存器地址转换为对应符号。addr2line 要用和自己so 的 ABI 匹配的目录，同时需要使用有符号信息的so(一般debug的就有)。

因为我使用的是模拟器x86架构，因此addr2line位于：

Android\Sdk\ndk\21.3.6528147\toolchains\x86-4.9\prebuilt\windows-x86_64\bin\i686-linux

```shell
i686-linux-android-addr2line.exe -f -C -e libcrashreporter.so 0x1feab
```

libcrashreporter.so 目录：

PerformanceOptimizing\code\CrashReporter\app\build\intermediates\cxx\Debug\701p5a2z\obj\x86