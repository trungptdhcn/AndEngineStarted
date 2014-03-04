LOCAL_PATH := $(call my-dir)
# SVG library
include $(CLEAR_VARS)
LOCAL_MODULE := svg-prebuilt
LOCAL_SRC_FILES := $(TARGET_ARCH_ABI)/libsvgandroid.so
include $(PREBUILT_SHARED_LIBRARY)

# Chess engine
include $(CLEAR_VARS)
# add `-DANDROID_DEBUG` to `LOCAL_CFLAGS` to enable debug
LOCAL_CFLAGS := -Wall -g 
LOCAL_LDFLAGS := -Wl
LOCAL_LDLIBS := -llog
LOCAL_C_INCLUDES += C:/android-ndk-r8b/platforms/android-8/arch-arm/usr/include
LOCAL_MODULE    := engine
LOCAL_SRC_FILES := engine_jni.cpp
LOCAL_SRC_FILES += engine.cpp bkdata.cpp
include $(BUILD_SHARED_LIBRARY)
