@echo off

rem
rem Modify the following two paths before you compile.
rem
set ASIO_SDK_PATH=D:\Tempp\ASIO\Steinberg\ASIOSDK2.3_dc
set MINGW_PATH=D:\MinGW\bin

if exist _build (
    rd /s /q _build
)

mkdir _build

%MINGW_PATH%\gcc -mno-cygwin -D__int64="long long" -o jasiohost.dll -shared -O3 -w -Wl,--add-stdcall-alias^
-I.^
-I"%JAVA_HOME%\include"^
-I"%JAVA_HOME%\include\win32"^
-I"%ASIO_SDK_PATH%\common"^
-I"%ASIO_SDK_PATH%\host"^
-I"%ASIO_SDK_PATH%\host\pc"^
.\*.cpp^
"%ASIO_SDK_PATH%\common\*.cpp"^
"%ASIO_SDK_PATH%\host\*.cpp"^
"%ASIO_SDK_PATH%\host\pc\*.cpp"^
-lstdc++ -lole32 -luuid

pause

