@echo off

rd /s/q .\out
md .\out

rem # Java
protoc --java_out=.\out .\commProtocol.proto
protoc --java_out=.\out .\passportServerProtocol.proto
protoc --java_out=.\out .\hallServerProtocol.proto
protoc --java_out=.\out .\MJ_weihai_Protocol.proto
protoc --java_out=.\out .\clubServerProtocol.proto
protoc --java_out=.\out .\chatServerProtocol.proto
protoc --java_out=.\out .\recordServerProtocol.proto

rem # JS
rem # 第二条命令可能不被执行,
rem # 直接复制粘贴到 DOS 命令行手动执行解决上面这个问题
set "_pbjs=node %NODE_HOME%\node_modules\protobufjs\bin\pbjs"
set "_pbts=node %NODE_HOME%\node_modules\protobufjs\bin\pbts"

%_pbjs% -t static-module -w commonjs --es6 --keep-case --root comm -o .\out\mod_commProtocol.js .\commProtocol.proto
%_pbts% -o .\out\mod_commProtocol.d.ts .\out\mod_commProtocol.js

%_pbjs% -t static-module -w commonjs --es6 --keep-case --root passportServer -o .\out\mod_passportServerProtocol.js .\passportServerProtocol.proto
%_pbts% -o .\out\mod_passportServerProtocol.d.ts .\out\mod_passportServerProtocol.js

%_pbjs% -t static-module -w commonjs --es6 --keep-case --root hallServer -o .\out\mod_hallServerProtocol.js .\hallServerProtocol.proto
%_pbts% -o .\out\mod_hallServerProtocol.d.ts .\out\mod_hallServerProtocol.js

%_pbjs% -t static-module -w commonjs --es6 --keep-case --root MJ_weihai_ -o .\out\mod_MJ_weihai_Protocol.js .\MJ_weihai_Protocol.proto
%_pbts% -o .\out\mod_MJ_weihai_Protocol.d.ts .\out\mod_MJ_weihai_Protocol.js

%_pbjs% -t static-module -w commonjs --es6 --keep-case --root clubServer -o .\out\mod_clubServerProtocol.js .\clubServerProtocol.proto
%_pbts% -o .\out\mod_clubServerProtocol.d.ts .\out\mod_clubServerProtocol.js

%_pbjs% -t static-module -w commonjs --es6 --keep-case --root chatServer -o .\out\mod_chatServerProtocol.js .\chatServerProtocol.proto
%_pbts% -o .\out\mod_chatServerProtocol.d.ts .\out\mod_chatServerProtocol.js

%_pbjs% -t static-module -w commonjs --es6 --keep-case --root recordServer -o .\out\mod_recordServerProtocol.js .\recordServerProtocol.proto
%_pbts% -o .\out\mod_recordServerProtocol.d.ts .\out\mod_recordServerProtocol.js
