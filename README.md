# MyDemo
使用360加固在按理云EMAS上进行打包。通过修改EMAS构建步骤【执行构建命令】里的脚本内容为./gradlew clean assembleRelease360jiagu进行触发  
由于阿里云的环境是linux的，所以项目携带的360文件也是linux版本  
由于阿里云默认【上传构建产物】默认扫描路径为app/build/outputs，所以demo中也将360的多渠道包生成到build/outputs目录下  
