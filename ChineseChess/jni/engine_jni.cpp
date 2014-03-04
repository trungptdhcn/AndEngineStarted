
#include <jni.h>
#if defined(__GNUC__)
#include <pthread.h>
pthread_mutex_t mutex;

#define initLock(x) pthread_mutex_init(&x, 0)
#define mutex_lock(x) pthread_mutex_lock(&x)
#define mutex_unlock(x) pthread_mutex_unlock(&x)
#else
#include <Windows.h>

CRITICAL_SECTION mutex;
#define initLock(x) InitializeCriticalSection(&x)
#define mutex_lock(x) EnterCriticalSection(&x)
#define mutex_unlock(x) LeaveCriticalSection(&x)
#endif
#include <stdio.h>
#if !defined(GCC_DEBUG) && !defined(WIN32)
#include <android/log.h>
#endif
#include "engine.h"





JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *vm, void *reserved) {
	initLock(mutex);
	return JNI_VERSION_1_6;
}

static inline void setIntValue(JNIEnv* env, jobject obj, const char* name,
		int value) {
	jclass cls = env->GetObjectClass(obj);
	jfieldID fid = env->GetFieldID(cls, name, "I");
	env->SetIntField(obj, fid, value);
}


#ifdef __cplusplus
extern "C" {
#endif

/*
* Class:     com_thor_chess_Engine
* Method:    initialize
* Signature: ()J
*/
JNIEXPORT jlong JNICALL Java_com_thor_chess_Engine_jniInitialize(JNIEnv* env,
		jclass) {
	mutex_lock(mutex);
	chess::Engine* engine = new chess::Engine();
	mutex_unlock(mutex);
	return (jlong)engine;
}

/*
 * Class:     com_thor_chess_Engine
 * Method:    startup
 * Signature: (JI)V
 */
JNIEXPORT void JNICALL Java_com_thor_chess_Engine_jniStartup(JNIEnv* env,
		jclass, jlong enginePtr, jint direction) {
	mutex_lock(mutex);
	chess::Engine* engine = (chess::Engine*)enginePtr;
	engine->startup(direction);
	mutex_unlock(mutex);
}

/*
 * Class:     com_thor_chess_Engine
 * Method:    move
 * Signature: (Lcom/thor/chess/MoveInfo;)Z
 */
JNIEXPORT jboolean JNICALL Java_com_thor_chess_Engine_jniMove(JNIEnv* env,
		jclass, jlong enginePtr, jint fromX, jint fromY, jint toX,
		jint toY) {
	mutex_lock(mutex);
	ENTER("*jniMove");
	chess::Engine* engine = (chess::Engine*) enginePtr;
	bool state = engine->move(fromX, fromY, toX, toY);
	mutex_unlock(mutex);
	return state;
}

/*
 * Class:     com_thor_chess_Engine
 * Method:    undo
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_com_thor_chess_Engine_jniUndo(JNIEnv* env,
		jclass, jlong enginePtr) {
	mutex_lock(mutex);
	chess::Engine* engine = (chess::Engine*) enginePtr;
	engine->undo();
	mutex_unlock(mutex);
}

/*
 * Class:     com_thor_chess_Engine
 * Method:    getMoveCount
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_com_thor_chess_Engine_jniGetMoveCount(JNIEnv* env,
		jclass, jlong enginePtr) {
	mutex_lock(mutex);
	chess::Engine* engine = (chess::Engine*) enginePtr;
	int count = engine->getMoveCount();
	mutex_unlock(mutex);
	return count;
}

/*
 * Class:     com_thor_chess_Engine
 * Method:    getPlayer
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_com_thor_chess_Engine_jniGetPlayer(JNIEnv* env,
		jclass, jlong enginePtr) {
	mutex_lock(mutex);
	chess::Engine* engine = (chess::Engine*) enginePtr;
	int player = engine->getPlayer();
	mutex_unlock(mutex);
	return player;
}

#ifdef DEBUG_STATE
void showBoard(unsigned char* board) {
	static const char* chessName[24] = {
		0, 0, 0, 0, 0, 0, 0, 0,
		"帅","仕","相","马","车","炮","兵", 0,
		"将","士","象","马","车","炮","卒", 0
	};
	char buffer[512] = {0};
	strcat(buffer, "--------------------------\n");
	for (int y = 0; y < 10; y++) {
		for (int x = 0; x < 9; x++) {
			if (board[y * 9 + x] == 0) {
				strcat(buffer, "   ");
			} else {
				strcat(buffer, chessName[board[y * 9 + x]]);
				strcat(buffer, " ");
			}
		}
		strcat(buffer, "\n");
	}
	strcat(buffer, "\n--------------------------");
	LOG(buffer);
}
#endif

/*
 * Class:     com_thor_chess_Engine
 * Method:    getState
 * Signature: (JZ)Lcom/thor/chess/Engine$GameState
 */
JNIEXPORT jobject JNICALL Java_com_thor_chess_Engine_jniGetState(
		JNIEnv* env, jclass, jlong enginePtr, jboolean updateBoard) {
	mutex_lock(mutex);
	ENTER("*jniGetState");
	chess::Engine* engine = (chess::Engine*) enginePtr;
	jclass stateCls = env->FindClass("com/thor/chess/Engine$GameState");
	if (!stateCls) {
		mutex_unlock(mutex);
		return 0;
	}
	jmethodID construct = env->GetMethodID(stateCls, "<init>", "()V");
	if (!construct) {
		mutex_unlock(mutex);
		return 0;
	}
	jobject gameState = env->NewObject(stateCls, construct);
	if (updateBoard) {

		byte board[90];
		int state = engine->getState(board);
#ifdef DEBUG_STATE
		showBoard(board);
#endif
		//jclass byteArrayCls = env->FindClass("[B");
		jfieldID fid = env->GetFieldID(stateCls, "board", "[B");
		jbyteArray barr = env->NewByteArray(90);
		env->SetByteArrayRegion(barr, 0, 90, (const signed char*)board);
		env->SetObjectField(gameState, fid, barr);

		fid = env->GetFieldID(stateCls, "state", "I");
		env->SetIntField(gameState, fid, state);

		mutex_unlock(mutex);
		return gameState;
	} else {
		int state = engine->getState(0);
		jfieldID fid = env->GetFieldID(stateCls, "state", "I");
		env->SetIntField(gameState, fid, state);
		mutex_unlock(mutex);
		return gameState;
	}
}

/*
 * Class:     com_thor_chess_Engine
 * Method:    findSolution
 * Signature: (JI)Lcom/thor/chess/MoveInfo;
 */
JNIEXPORT jobject JNICALL Java_com_thor_chess_Engine_jniFindSolution(JNIEnv* env,
		jclass, jlong enginePtr, jfloat searchSeconds) {
	mutex_lock(mutex);
	ENTER("*jniFindSolution");
	chess::Engine* engine = (chess::Engine*) enginePtr;
	int fromX, fromY, toX, toY;
#ifdef DEBUG_STATE
	byte board[90];
	engine->getState(board);
	showBoard(board);
#endif
	bool state = engine->findSolution(searchSeconds, fromX, fromY, toX, toY);
	LOG1("------\ndebugState: %d\n------", engine->debugState);
#ifdef DEBUG_STATE
	engine->getState(board);
	showBoard(board);
#endif
	if (state) {
		jclass cls = env->FindClass("com/thor/chess/MoveInfo");
		jmethodID mid = env->GetMethodID(cls, "<init>", "()V");
		jobject mv = env->NewObject(cls, mid);
		setIntValue(env, mv, "fromX", fromX);
		setIntValue(env, mv, "fromY", fromY);
		setIntValue(env, mv, "toX", toX);
		setIntValue(env, mv, "toY", toY);
		mutex_unlock(mutex);
		return mv;
	} else {
		mutex_unlock(mutex);
		return 0;
	}
}

JNIEXPORT jobject JNICALL Java_com_thor_chess_Engine_jniGetLastMove(JNIEnv* env,
		jclass, jlong enginePtr) {
	mutex_lock(mutex);
	chess::Engine* engine = (chess::Engine*) enginePtr;
	int fromX, fromY, toX, toY;
	bool state = engine->getLastMove(fromX, fromY, toX, toY);
	if (state) {
		jclass cls = env->FindClass("com/thor/chess/MoveInfo");
		jmethodID mid = env->GetMethodID(cls, "<init>", "()V");
		jobject mv = env->NewObject(cls, mid);
		setIntValue(env, mv, "fromX", fromX);
		setIntValue(env, mv, "fromY", fromY);
		setIntValue(env, mv, "toX", toX);
		setIntValue(env, mv, "toY", toY);
		mutex_unlock(mutex);
		return mv;
	} else {
		mutex_unlock(mutex);
		return 0;
	}
}

/*
* Class:     com_thor_chess_Engine
* Method:    despose
* Signature: (J)V
*/
JNIEXPORT void JNICALL Java_com_thor_chess_Engine_jniDispose(JNIEnv* env,
		jclass, jlong enginePtr) {
	mutex_lock(mutex);
	chess::Engine* engine = (chess::Engine*) enginePtr;
	delete engine;
	mutex_unlock(mutex);
}

#ifdef __cplusplus
}
#endif
