ictanalyzer-lucene-4.3.0
========================

ICTCLAS实现的Lucene Analyzer,用Lucene-4.3.0版本实现。
不支持记录词在原始文本中的位置偏移信息

关于ICTCLAS的Data和lib目录，可用相对路径，也可用绝对路径。

相对路径即把Data和lib目录置于项目根目录即可。

绝对路径需要设置JVM的启动参数：ict-path
如果使用绝对路径，则Data目录和lib目录要放在一起。
例如，如果目录结构如下：
D:\
 |-ict-
 |	  └--Data
 |    |--lib
那么JVM的启动参数为： -Dict-path="D:\ict"

里面还包含一个web项目，演示如何在web中使用该分词


