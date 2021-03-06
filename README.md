# 使用百度开放平台的接口实现本地文件上传

### 百度网盘API地址: https://pan.baidu.com/union/doc/nksg0sbfs

---

### 使用方法

1. 创建上传任务
```
BaiduUploadTaskBuilder builder = new BaiduUploadTaskBuilder();
builder.setAccessToken("百度网盘的授权码")
        .setFile(new File("上传的文件"))
        .setAppPath("需要保存在网盘的哪个位置");
BaiduUploadTask task = builder.build();
```

2. 设置上传的参数, 也可以使用默认值
```
task.setIsDir(0) // 是否时文件夹: 0 文件 1: 文件夹(没做, 麻烦)
    .setRType(0) // 上传时遇到同名文件如何处理: 0 表示不进行重命名，若云端存在同名文件返回错误
                                           1 表示当path冲突时，进行重命名
                                           2 表示当path冲突且block_list不同时，进行重命名
                                           3 当云端存在同名文件时，对该文件进行覆盖
    .setDeleteAll(false); // 上传后是否删除已经上传的文件
```

3. 开始上传, 上传后会返回一个字符串列表, 包括上传后得到的秒传链接以及文件在云端的唯一标识ID
```
List<String> result = task.startUpload();
String secondLink = result.get(0); // 秒传链接
String fsId = result.get(1); // 文件在云端的唯一标识ID(无用?)
```

### 未解决
1. 文件夹上传功能
2. 优化上传进度条
3. 忘了