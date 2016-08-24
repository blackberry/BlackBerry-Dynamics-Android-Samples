# Some modifications Copyright (C) 2016 BlackBerry Limited
#
# Copyright (C) 2011 The Android Open Source Project
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
LOCAL_PATH := $(call my-dir)

# Blackberry modifications - Define the GD prebuilt library which GD SDK ships as a prebuilt
# Add the path to the exported include files (which contain the GD C Posix APIs) so app code can build against them
include $(CLEAR_VARS)
LOCAL_MODULE := gd-prebuilt
LOCAL_SRC_FILES := ../GD_SDK/libs/handheld/gd/libs/armeabi-v7a/libgdndk.so
LOCAL_EXPORT_C_INCLUDES := $(LOCAL_PATH)/../GD_SDK/libs/handheld/gd/inc
include $(PREBUILT_SHARED_LIBRARY)

# Blackberry modifications - end 

include $(CLEAR_VARS)

LOCAL_MODULE    := native-media-jni
LOCAL_SRC_FILES := native-media-jni.c
# for native multimedia
LOCAL_LDLIBS    += -lOpenMAXAL
# for logging
LOCAL_LDLIBS    += -llog
# for native windows
LOCAL_LDLIBS    += -landroid
LOCAL_CFLAGS    += -UNDEBUG

# Blackberry modifications - We mark the prebuilt library so is used to link against
LOCAL_SHARED_LIBRARIES := gd-prebuilt
include $(BUILD_SHARED_LIBRARY)

# Blackberry modifications - end 