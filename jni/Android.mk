LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := Engine
### Add all source file names to be included in lib separated by a whitespace
LOCAL_SRC_FILES := \
	CandiGen.cpp                    \
	FugouContext.cpp                \
	FugouGame.cpp                   \
	FugouKernel.cpp                 \
	FugouRule.cpp                   \
	FugouThink.cpp                  \
	Engine.cpp

include $(BUILD_SHARED_LIBRARY)
