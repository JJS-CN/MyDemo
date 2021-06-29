#!/bin/bash

BASE=../jiagu/jiagu.jar
NAME=994462623@qq.com #360登录账号
PASSWORD=abc123 #登录密码
KEY_PATH=../jiagu/keystore.jks #密钥路径
KEY_PASSWORD=123456 #密钥密码
ALIAS=key0 #别名
ALIAS_PASSWORD=123456 #别名密码
WALLE_CHANNELS_CONFIG=../jiagu/channels

APK=$1   #需要加固的apk路径
DEST=$2  #输出加固包的路径 

echo "------ running! ------"

java -jar ${BASE} -version
java -jar ${BASE} -login ${NAME} ${PASSWORD}
java -jar ${BASE} -importsign ${KEY_PATH} ${KEY_PASSWORD} ${ALIAS} ${ALIAS_PASSWORD}
java -jar ${BASE} -showsign
java -jar ${BASE} -importmulpkg ${WALLE_CHANNELS_CONFIG}/all.txt #根据自身情况使用
java -jar ${BASE} -showmulpkg
java -jar ${BASE} -showconfig
java -jar ${BASE} -jiagu ${APK} ${DEST} -autosign

echo "------ finished! ------"