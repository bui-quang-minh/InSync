@echo off
"D:\\Users\\PhanQuangHuy59\\AppData\\Local\\Android\\Sdk\\cmake\\3.22.1\\bin\\cmake.exe" ^
  "-HD:\\ProjectThesis\\ProjectInSync\\InSync\\opencv\\libcxx_helper" ^
  "-DCMAKE_SYSTEM_NAME=Android" ^
  "-DCMAKE_EXPORT_COMPILE_COMMANDS=ON" ^
  "-DCMAKE_SYSTEM_VERSION=21" ^
  "-DANDROID_PLATFORM=android-21" ^
  "-DANDROID_ABI=x86_64" ^
  "-DCMAKE_ANDROID_ARCH_ABI=x86_64" ^
  "-DANDROID_NDK=D:\\Users\\PhanQuangHuy59\\AppData\\Local\\Android\\Sdk\\ndk\\25.1.8937393" ^
  "-DCMAKE_ANDROID_NDK=D:\\Users\\PhanQuangHuy59\\AppData\\Local\\Android\\Sdk\\ndk\\25.1.8937393" ^
  "-DCMAKE_TOOLCHAIN_FILE=D:\\Users\\PhanQuangHuy59\\AppData\\Local\\Android\\Sdk\\ndk\\25.1.8937393\\build\\cmake\\android.toolchain.cmake" ^
  "-DCMAKE_MAKE_PROGRAM=D:\\Users\\PhanQuangHuy59\\AppData\\Local\\Android\\Sdk\\cmake\\3.22.1\\bin\\ninja.exe" ^
  "-DCMAKE_LIBRARY_OUTPUT_DIRECTORY=D:\\ProjectThesis\\ProjectInSync\\InSync\\opencv\\build\\intermediates\\cxx\\Debug\\2f6t2w56\\obj\\x86_64" ^
  "-DCMAKE_RUNTIME_OUTPUT_DIRECTORY=D:\\ProjectThesis\\ProjectInSync\\InSync\\opencv\\build\\intermediates\\cxx\\Debug\\2f6t2w56\\obj\\x86_64" ^
  "-DCMAKE_BUILD_TYPE=Debug" ^
  "-BD:\\ProjectThesis\\ProjectInSync\\InSync\\opencv\\.cxx\\Debug\\2f6t2w56\\x86_64" ^
  -GNinja ^
  "-DANDROID_STL=c++_shared"
