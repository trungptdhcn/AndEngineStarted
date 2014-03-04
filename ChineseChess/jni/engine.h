/*
 * engine.h
 *
 *  Created on: 2012-10-27
 *      Author: thor
 */

#ifndef ENGINE_H_
#define ENGINE_H_

#if defined(ANDROID_DEBUG)
#	include <android/log.h>
#	define DEBUG_LOG
#	define DEBUG_TRACE
#	define DEBUG_STATE
#endif

#include <string.h>
#include <vector>
#include <stdlib.h>

#include "zobrist.h"

#ifdef DEBUG_LOG
#	define LOG(x) __android_log_write( \
		ANDROID_LOG_DEBUG, "ChineseChess", x);
#	define LOG1(f,x) __android_log_print( \
		ANDROID_LOG_DEBUG, "ChineseChess", f, x);
#else
#	define LOG(x)
#	define LOG1(f,x)
#endif

#ifdef DEBUG_TRACE
#	define ENTER(x) LOG(x);
#else
#	define ENTER(x)
#endif

namespace chess {

static const int LIMIT_DEPTH = 64;    	// 最大的搜索深度
static const int HASH_SIZE = 1 << 20; 	// 置换表大小
static const int BOOK_SIZE = 16384;   	// 开局库大小
static const int MAX_MOVES = 256;     	// 最大的历史走法数
static const int MAX_GEN_MOVES = 128; 	// 最大的生成走法数
static const int MATE_VALUE = 10000;  	// 最高分值，即将死的分值
// 长将判负的分值，低于该值将不写入置换表
static const int BAN_VALUE = MATE_VALUE - 100;
// 搜索出胜负的分值界限，超出此值就说明已经搜索出杀棋了
static const int WIN_VALUE = MATE_VALUE - 200;
static const int DRAW_VALUE = 20;     	// 和棋时返回的分数(取负值)
static const int ADVANCED_VALUE = 3;  	// 先行权分值
static const int RANDOM_MASK = 7;     	// 随机性分值
static const int NULL_MARGIN = 400;   	// 空步裁剪的子力边界
static const int NULL_DEPTH = 2;      	// 空步裁剪的裁剪深度
static const int HASH_ALPHA = 1;      	// ALPHA节点的置换表项
static const int HASH_BETA = 2;       	// BETA节点的置换表项
static const int HASH_PV = 3;         	// PV节点的置换表项

typedef unsigned char byte;
typedef unsigned short uint16;
typedef unsigned int uint32;
typedef short int16;
typedef int int32;

template <class T>
inline T* begin_ptr(std::vector<T>& v)
{return  v.empty() ? NULL : &v[0];}

template <class T>
inline const T* begin_ptr(const std::vector<T>& v)
{return  v.empty() ? NULL : &v[0];}

template <class T>
inline T* end_ptr(std::vector<T>& v)
{return v.empty() ? NULL : (begin_ptr(v) + v.size());}

template <class T>
inline const T* end_ptr(const std::vector<T>& v)
{return v.empty() ? NULL : (begin_ptr(v) + v.size());}


// 历史走法信息(占4字节)
typedef struct MoveStruct {
	uint16 wmv;
	byte ucpcCaptured;
	byte ucbCheck;
	uint32 dwKey;
	inline void set(int mv, int pcCaptured, bool bCheck, uint32 dwKey) {
		wmv = mv;
		ucpcCaptured = pcCaptured;
		ucbCheck = bCheck;
		this->dwKey = dwKey;
	}
} MoveStruct;

// 置换表项结构
typedef struct HashItem {
	byte ucDepth, ucFlag;
	short svl;
	uint16 wmv, wReserved;
	uint32 dwLock0, dwLock1;
} HashItem;

// 开局库项结构
typedef struct BookItem {
	uint32 dwLock;
	uint16 wmv, wvl;
} BookItem;

// 与搜索有关的全局变量
typedef struct Search {
	int mvResult;                  		// 电脑走的棋
	int nHistoryTable[65536];      		// 历史表
	int mvKillers[LIMIT_DEPTH][2]; 		// 杀手走法表
	HashItem hashTable[HASH_SIZE]; 		// 置换表
	inline Search();
	inline void init();
} Search;

inline Search::Search() {
	init();
}
inline void Search::init() {
	mvResult = 0;
	memset(nHistoryTable, 0, 65536 * sizeof(int));       // 清空历史表
	memset(mvKillers, 0, LIMIT_DEPTH * 2 * sizeof(int)); // 清空杀手走法表
	memset(hashTable, 0, HASH_SIZE * sizeof(HashItem));  // 清空置换表
	LOG1("HashItem size: %d", sizeof(HashItem));
}

class Engine {
private:
	// 棋盘范围
	static const int RANK_TOP = 3;
	static const int RANK_BOTTOM = 12;
	static const int FILE_LEFT = 3;
	static const int FILE_RIGHT = 11;

private:
	// 静态辅助查找表
	///////////////////////////////////////////////////////////////////
	// 判断棋子是否在棋盘中的数组
	static const char ccInBoard[256];
	// 判断棋子是否在九宫的数组
	static const char ccInFort[256];
	// 判断步长是否符合特定走法的数组，1=帅(将)，2=仕(士)，3=相(象)
	static const char ccLegalSpan[512];
	// 根据步长判断马是否蹩腿的数组
	static const signed char ccKnightPin[512];
	// 棋盘初始设置
	static const byte cucpcStartup[256];
	// 子力位置价值表
	static const byte cucvlPiecePos[7][256];
	// 帅(将)的步长
	static const signed char ccKingDelta[4];
	// 仕(士)的步长
	static const signed char ccAdvisorDelta[4];
	// 马的步长，以帅(将)的步长作为马腿
	static const signed char ccKnightDelta[4][2];
	// 马被将军的步长，以仕(士)的步长作为马腿
	static const signed char ccKnightCheckDelta[4][2];
private:
	// 静态工具方法
	///////////////////////////////////////////////////////////////////
	// 判断棋子是否在棋盘中
	static inline bool IN_BOARD(int sq);
	// 判断棋子是否在九宫中
	static inline bool IN_FORT(int sq);
	// 获得格子的横坐标
	static inline int RANK_Y(int sq);
	// 获得格子的纵坐标
	static inline int FILE_X(int sq);
	// 根据纵坐标和横坐标获得格子
	static inline int COORD_XY(int x, int y);
	// 翻转格子
	static inline int SQUARE_FLIP(int sq);
	// 纵坐标水平镜像
	static inline int FILE_FLIP(int x);
	// 横坐标垂直镜像
	static inline int RANK_FLIP(int y);
	// 格子水平镜像
	static inline int MIRROR_SQUARE(int sq);
	// 格子水平镜像
	static inline int SQUARE_FORWARD(int sq, int sd);
	// 走法是否符合帅(将)的步长
	static inline bool KING_SPAN(int sqSrc, int sqDst);
	// 走法是否符合仕(士)的步长
	static inline bool ADVISOR_SPAN(int sqSrc, int sqDst);
	// 走法是否符合相(象)的步长
	static inline bool BISHOP_SPAN(int sqSrc, int sqDst);
	// 相(象)眼的位置
	static inline int BISHOP_PIN(int sqSrc, int sqDst);
	// 马腿的位置
	static inline int KNIGHT_PIN(int sqSrc, int sqDst);
	// 是否未过河
	static inline bool HOME_HALF(int sq, int sd);
	// 是否已过河
	static inline bool AWAY_HALF(int sq, int sd);
	// 是否在河的同一边
	static inline bool SAME_HALF(int sqSrc, int sqDst);
	// 是否在同一行
	static inline bool SAME_RANK(int sqSrc, int sqDst);
	// 是否在同一列
	static inline bool SAME_FILE(int sqSrc, int sqDst);
	// 获得红黑标记(红子是8，黑子是16)
	static inline int SIDE_TAG(int sd);
	// 获得对方红黑标记
	static inline int OPP_SIDE_TAG(int sd);
	// 获得走法的起点
	static inline int SRC(int mv);
	// 获得走法的终点
	static inline int DST(int mv);
	// 根据起点和终点获得走法
	static inline int MOVE(int sqSrc, int sqDst);
	// 走法水平镜像
	static inline int MIRROR_MOVE(int mv);

private:
	friend struct SortStruct;
	// 轮到谁走，0=红方，1=黑方
	int player;
	byte board[256];
	int vlWhite, vlBlack;           	// 红、黑双方的子力价值
	int nDistance, nMoveNum;        	// 距离根节点的步数，历史走法数
	MoveStruct mvsList[MAX_MOVES];  	// 历史走法信息列表
	ZobristStruct zobr;             	// Zobrist
	static Zobrist zobrist;
	std::vector<MoveStruct> history;	// 这个保留所有历史，而mvsList在吃子后会
										// 被清空。
	int direction;
	Search search;

private:
	// 棋盘方法
	///////////////////////////////////////////////////////////////////
	// 清空棋盘
	inline void clear(void);
	// 清空(初始化)历史走法信息
	inline void clearHistory(void);
	// 交换走子方
	inline void changeSide(void);
	// 在棋盘上放一枚棋子
	void addPiece(int sq, int pc);
	// 从棋盘上拿走一枚棋子
	void delPiece(int sq, int pc);
	// 局面评价函数
	int evaluate(void) const;
	// 是否被将军
	bool inCheck(void) const;
	// 上一步是否吃子
	bool captured(void) const;
	// 搬一步棋的棋子
	int putPiece(int mv);
	// 撤消搬一步棋的棋子
	void undoPut(int mv, int pcCaptured);
	// 走一步棋
	bool move(int mv);
	// 撤消走一步棋
	void undoMove(void);
	// 走一步空步
	void nullMove(void);
	// 撤消走一步空步
	void undoNullMove(void);
	// 判断走法是否合理
	bool testMove(int mv) const;
	// 生成所有走法，如果"bCapture"为"true"则只生成吃子走法
	int generateMoves(int *mvs, bool bCapture = false) const;
	// 判断是否被将军
	bool checked() const;
	// 判断是否被杀
	bool isMate(void);
	// 得到和棋的分值
	inline int getDrawValue(void) const;
	// 检测重复局面，得到重复局面所在的步骤偏移位置
	int getRepeatPos(int nRecur = 1) const;
	// 得到重复局面分值
	inline int getRepeatValue(int nRepStatus) const;
	// 对局面镜像
	void mirror(Engine &posMirror) const;
	// 判断是否允许空步裁剪
	inline bool allowNullMove(void) const;

private:
	// 搜索方法
	///////////////////////////////////////////////////////////////////
	// 搜索开局库
	int searchBook(void);
	// 提取置换表项
	int probeHash(int vlAlpha, int vlBeta, int nDepth, int &mv);
	// 保存置换表项
	void recordHash(int nFlag, int vl, int nDepth, int mv);
	// 静态(Quiescence)搜索过程
	int searchQuiescence(int vlAlpha, int vlBeta);
	// 对最佳走法的处理
	void setBestMove(int mv, int nDepth);
	// 超出边界(Fail-Soft)的Alpha-Beta搜索过程
	int searchFull(int vlAlpha, int vlBeta, int nDepth,
			bool bNoNull = false);
	// 根节点的Alpha-Beta搜索过程
	int searchRoot(int nDepth);
	// 迭代加深搜索过程
	int searchMain(float seconds);

public:
	// 棋子编号
	static const int PIECE_KING = 0;
	static const int PIECE_ADVISOR = 1;
	static const int PIECE_BISHOP = 2;
	static const int PIECE_KNIGHT = 3;
	static const int PIECE_ROOK = 4;
	static const int PIECE_CANNON = 5;
	static const int PIECE_PAWN = 6;

	// 局面状态
	static const int NORMAL = 0;
	static const int NORMAL_CAPTURED = 1;
	static const int NORMAL_CHECKED = 2;
	static const int RED_WIN = 3;
	static const int BLACK_WIN = 4;
	static const int DRAW = 5;
	static const int REPEATED = 0x100;
	static const int EXCEEDED_100 = 0x200;

public:
	// 初始化棋盘
	void startup(int direction);
	// 走棋, direction: 面向棋盘红方在下侧是 0，否则是1
	bool move(int fromX, int fromY, int toX, int toY);
	// 悔棋
	void undo();
	// 得到走了多少步棋
	inline int getMoveCount() const;
	// 当前该谁走
	inline int getPlayer() const;
	// 得到游戏状态, 如果board指针不为NULL
	// 则还会复制当前棋盘局面信息到board指向的内存中, board 是简化的9x10棋盘
	// 可以是 byte a[10][9] 也可以是 byte a[90]
	// direction: 面向棋盘红方在下侧是 0，否则是1
	int getState(byte* board);
	// 盘面求解, 返回结果在 fromX fromY toX toY 中
	// direction: 面向棋盘红方在下侧是 0，否则是1
	// searchSeconds: 搜索多少秒
	bool findSolution(float searchSeconds, int& fromX, int& fromY,
			int& toX, int& toY);

	inline bool getLastMove(int& fromX, int& fromY, int& toX, int& toY);
};


///////////////////////////////////////////////////////////////////////////

// 判断棋子是否在棋盘中
inline bool Engine::IN_BOARD(int sq) {
#if defined(ANDROID_DEBUG)
	if (sq > 255) {
		exit(-1);
	}
#endif
	return ccInBoard[sq] != 0;
}

// 判断棋子是否在九宫中
inline bool Engine::IN_FORT(int sq) {
	return ccInFort[sq] != 0;
}

// 获得格子的横坐标
inline int Engine::RANK_Y(int sq) {
	return sq >> 4;
}

// 获得格子的纵坐标
inline int Engine::FILE_X(int sq) {
	return sq & 15;
}

// 根据纵坐标和横坐标获得格子
inline int Engine::COORD_XY(int x, int y) {
	return x + (y << 4);
}

// 翻转格子
inline int Engine::SQUARE_FLIP(int sq) {
	return 254 - sq;
}

// 纵坐标水平镜像
inline int Engine::FILE_FLIP(int x) {
	return 14 - x;
}

// 横坐标垂直镜像
inline int Engine::RANK_FLIP(int y) {
	return 15 - y;
}

// 格子水平镜像
inline int Engine::MIRROR_SQUARE(int sq) {
	return COORD_XY(FILE_FLIP(FILE_X(sq)), RANK_Y(sq));
}

// 格子水平镜像
inline int Engine::SQUARE_FORWARD(int sq, int sd) {
	return sq - 16 + (sd << 5);
}

// 走法是否符合帅(将)的步长
inline bool Engine::KING_SPAN(int sqSrc, int sqDst) {
	return ccLegalSpan[sqDst - sqSrc + 256] == 1;
}

// 走法是否符合仕(士)的步长
inline bool Engine::ADVISOR_SPAN(int sqSrc, int sqDst) {
	return ccLegalSpan[sqDst - sqSrc + 256] == 2;
}

// 走法是否符合相(象)的步长
inline bool Engine::BISHOP_SPAN(int sqSrc, int sqDst) {
	return ccLegalSpan[sqDst - sqSrc + 256] == 3;
}

// 相(象)眼的位置
inline int Engine::BISHOP_PIN(int sqSrc, int sqDst) {
	return (sqSrc + sqDst) >> 1;
}

// 马腿的位置
inline int Engine::KNIGHT_PIN(int sqSrc, int sqDst) {
	return sqSrc + ccKnightPin[sqDst - sqSrc + 256];
}

// 是否未过河
inline bool Engine::HOME_HALF(int sq, int sd) {
	return (sq & 0x80) != (sd << 7);
}

// 是否已过河
inline bool Engine::AWAY_HALF(int sq, int sd) {
	return (sq & 0x80) == (sd << 7);
}

// 是否在河的同一边
inline bool Engine::SAME_HALF(int sqSrc, int sqDst) {
	return ((sqSrc ^ sqDst) & 0x80) == 0;
}

// 是否在同一行
inline bool Engine::SAME_RANK(int sqSrc, int sqDst) {
	return ((sqSrc ^ sqDst) & 0xf0) == 0;
}

// 是否在同一列
inline bool Engine::SAME_FILE(int sqSrc, int sqDst) {
	return ((sqSrc ^ sqDst) & 0x0f) == 0;
}

// 获得红黑标记(红子是8，黑子是16)
inline int Engine::SIDE_TAG(int sd) {
	return 8 + (sd << 3);
}

// 获得对方红黑标记
inline int Engine::OPP_SIDE_TAG(int sd) {
	return 16 - (sd << 3);
}

// 获得走法的起点
inline int Engine::SRC(int mv) {
	return mv & 255;
}

// 获得走法的终点
inline int Engine::DST(int mv) {
	return mv >> 8;
}

// 根据起点和终点获得走法
inline int Engine::MOVE(int sqSrc, int sqDst) {
	return sqSrc + sqDst * 256;
}

// 走法水平镜像
inline int Engine::MIRROR_MOVE(int mv) {
	return MOVE(MIRROR_SQUARE(SRC(mv)), MIRROR_SQUARE(DST(mv)));
}

//////////////////////////////////////////////////////////////////////

// 清空棋盘
inline void Engine::clear(void) {
	player = vlWhite = vlBlack = nDistance = 0;
	memset(board, 0, 256);
	zobr.InitZero();
}

// 清空(初始化)历史走法信息
inline void Engine::clearHistory(void) {
	mvsList[0].set(0, 0, checked(), zobr.dwKey);
	nMoveNum = 1;
}

inline void Engine::changeSide(void) {
	player = 1 - player;
	zobr.Xor(zobrist.Player);
}

// 在棋盘上放一枚棋子
inline void Engine::addPiece(int sq, int pc) {
	board[sq] = pc;
	// 红方加分，黑方(注意"cucvlPiecePos"取值要颠倒)减分
	if (pc < 16) {
		vlWhite += cucvlPiecePos[pc - 8][sq];
		zobr.Xor(zobrist.Table[pc - 8][sq]);
	} else {
		vlBlack += cucvlPiecePos[pc - 16][SQUARE_FLIP(sq)];
		zobr.Xor(zobrist.Table[pc - 9][sq]);
	}
}

// 从棋盘上拿走一枚棋子
inline void Engine::delPiece(int sq, int pc) {
	board[sq] = 0;
	// 红方减分，黑方(注意"cucvlPiecePos"取值要颠倒)加分
	if (pc < 16) {
		vlWhite -= cucvlPiecePos[pc - 8][sq];
		zobr.Xor(zobrist.Table[pc - 8][sq]);
	} else {
		vlBlack -= cucvlPiecePos[pc - 16][SQUARE_FLIP(sq)];
		zobr.Xor(zobrist.Table[pc - 9][sq]);
	}
}

// 局面评价函数
inline int Engine::evaluate(void) const {
	return (player == 0 ? vlWhite - vlBlack : vlBlack - vlWhite)
			+ ADVANCED_VALUE;
}

// 是否被将军
inline bool Engine::inCheck(void) const {
	return mvsList[nMoveNum - 1].ucbCheck != 0;
}

// 上一步是否吃子
inline bool Engine::captured(void) const {
	if (history.empty())
		return false;
	else
		return history.back().ucpcCaptured != 0;
//	return mvsList[nMoveNum - 1].ucpcCaptured != 0;
}

// 摆放一个棋子
inline int Engine::putPiece(int mv) {
	int sqSrc, sqDst, pc, pcCaptured;
	sqSrc = SRC(mv);
	sqDst = DST(mv);
	pcCaptured = board[sqDst];
	if (pcCaptured != 0) {
		delPiece(sqDst, pcCaptured);
	}
	pc = board[sqSrc];
	delPiece(sqSrc, pc);
	addPiece(sqDst, pc);
	return pcCaptured;
}

// 撤消摆放一个棋子
inline void Engine::undoPut(int mv, int pcCaptured) {
	int sqSrc, sqDst, pc;
	sqSrc = SRC(mv);
	sqDst = DST(mv);
	pc = board[sqDst];
	delPiece(sqDst, pc);
	addPiece(sqSrc, pc);
	if (pcCaptured != 0) {
		addPiece(sqDst, pcCaptured);
	}
}

// 走一步棋
inline bool Engine::move(int mv) {
	int pcCaptured;
	uint32 dwKey;

	dwKey = zobr.dwKey;
	pcCaptured = putPiece(mv);
	if (checked()) {
		undoPut(mv, pcCaptured);
		return false;
	}
	changeSide();
	mvsList[nMoveNum].set(mv, pcCaptured, checked(), dwKey);
	nMoveNum++;
#if defined(ANDROID_DEBUG)
	if (nMoveNum >= MAX_MOVES) {
		LOG("nMoveNum out of range.");
		exit(nMoveNum);
	}
#endif
	nDistance++;
	return true;
}

// 撤消走一步棋
inline void Engine::undoMove(void) {
	nDistance--;
	nMoveNum--;
	changeSide();
	undoPut(mvsList[nMoveNum].wmv, mvsList[nMoveNum].ucpcCaptured);
}

// 走一步空步
inline void Engine::nullMove(void) {
	uint32 dwKey;
	dwKey = zobr.dwKey;
	changeSide();
	mvsList[nMoveNum].set(0, 0, false, dwKey);
	nMoveNum++;
#if defined(ANDROID_DEBUG)
	if (nMoveNum >= MAX_MOVES) {
		LOG("nMoveNum out of range.");
		exit(nMoveNum);
	}
#endif
	nDistance++;
}

// 撤消走一步空步
inline void Engine::undoNullMove(void) {
	nDistance--;
	nMoveNum--;
	changeSide();
}

// 判断是否允许空步裁剪
inline bool Engine::allowNullMove(void) const {
	return (player == 0 ? vlWhite : vlBlack) > NULL_MARGIN;
}

// 得到重复局面分值
inline int Engine::getRepeatValue(int nRepStatus) const {
	int vlReturn;
	vlReturn = ((nRepStatus & 2) == 0 ? 0 : nDistance - BAN_VALUE)
			+ ((nRepStatus & 4) == 0 ? 0 : BAN_VALUE - nDistance);
	return vlReturn == 0 ? getDrawValue() : vlReturn;
}

// 得到和棋的分值
inline int Engine::getDrawValue(void) const {
	return (nDistance & 1) == 0 ? -DRAW_VALUE : DRAW_VALUE;
}

// 得到走了多少步棋
inline int Engine::getMoveCount() const {
	return (int)history.size();
}

inline bool Engine::getLastMove(int& fromX, int& fromY, int& toX, int& toY) {
	if (history.size() <= 0)
		return false;
	uint32 mv = history.back().wmv;
	int from = SRC(mv);
	int to = DST(mv);
	fromX = FILE_X(from) - 3;
	if (direction == 0)
		fromY = RANK_Y(from) - 3;
	else
		fromY = RANK_FLIP(RANK_Y(from)) - 3;
	toX = FILE_X(to) - 3;
	if (direction == 0)
		toY = RANK_Y(to) - 3;
	else
		toY = RANK_FLIP(RANK_Y(to)) - 3;
	return true;
}

// 当前该谁走
inline int Engine::getPlayer() const {
	return player;
}



} // End namespace.


#endif /* ENGINE_H_ */
