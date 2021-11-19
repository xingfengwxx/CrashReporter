cmake_minimum_required(VERSION 3.10.2)

project("crashreporter")

# 引入breakpad的头文件
include_directories(breakpad/src breakpad/src/common/android/include)

# 引入breakpad的cmakelist，执行并生成libbreakpad.a
add_subdirectory(breakpad)

add_library(
        crashreporter
        SHARED
        native-lib.cpp)

target_link_libraries(
        crashreporter
        breakpad
        log)