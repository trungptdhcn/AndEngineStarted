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

static const int LIMIT_DEPTH = 64;    	// �����������
static const int HASH_SIZE = 1 << 20; 	// �û����С
static const int BOOK_SIZE = 16384;   	// ���ֿ��С
static const int MAX_MOVES = 256;     	// ������ʷ�߷���
static const int MAX_GEN_MOVES = 128; 	// ���������߷���
static const int MATE_VALUE = 10000;  	// ��߷�ֵ���������ķ�ֵ
// �����и��ķ�ֵ�����ڸ�ֵ����д���û���
static const int BAN_VALUE = MATE_VALUE - 100;
// ������ʤ���ķ�ֵ���ޣ�������ֵ��˵���Ѿ�������ɱ����
static const int WIN_VALUE = MATE_VALUE - 200;
static const int DRAW_VALUE = 20;     	// ����ʱ���صķ���(ȡ��ֵ)
static const int ADVANCED_VALUE = 3;  	// ����Ȩ��ֵ
static const int RANDOM_MASK = 7;     	// ����Է�ֵ
static const int NULL_MARGIN = 400;   	// �ղ��ü��������߽�
static const int NULL_DEPTH = 2;      	// �ղ��ü��Ĳü����
static const int HASH_ALPHA = 1;      	// ALPHA�ڵ���û�����
static const int HASH_BETA = 2;       	// BETA�ڵ���û�����
static const int HASH_PV = 3;         	// PV�ڵ���û�����

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


// ��ʷ�߷���Ϣ(ռ4�ֽ�)
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

// �û�����ṹ
typedef struct HashItem {
	byte ucDepth, ucFlag;
	short svl;
	uint16 wmv, wReserved;
	uint32 dwLock0, dwLock1;
} HashItem;

// ���ֿ���ṹ
typedef struct BookItem {
	uint32 dwLock;
	uint16 wmv, wvl;
} BookItem;

// �������йص�ȫ�ֱ���
typedef struct Search {
	int mvResult;                  		// �����ߵ���
	int nHistoryTable[65536];      		// ��ʷ��
	int mvKillers[LIMIT_DEPTH][2]; 		// ɱ���߷���
	HashItem hashTable[HASH_SIZE]; 		// �û���
	inline Search();
	inline void init();
} Search;

inline Search::Search() {
	init();
}
inline void Search::init() {
	mvResult = 0;
	memset(nHistoryTable, 0, 65536 * sizeof(int));       // �����ʷ��
	memset(mvKillers, 0, LIMIT_DEPTH * 2 * sizeof(int)); // ���ɱ���߷���
	memset(hashTable, 0, HASH_SIZE * sizeof(HashItem));  // ����û���
	LOG1("HashItem size: %d", sizeof(HashItem));
}

class Engine {
private:
	// ���̷�Χ
	static const int RANK_TOP = 3;
	static const int RANK_BOTTOM = 12;
	static const int FILE_LEFT = 3;
	static const int FILE_RIGHT = 11;

private:
	// ��̬�������ұ�
	///////////////////////////////////////////////////////////////////
	// �ж������Ƿ��������е�����
	static const char ccInBoard[256];
	// �ж������Ƿ��ھŹ�������
	static const char ccInFort[256];
	// �жϲ����Ƿ�����ض��߷������飬1=˧(��)��2=��(ʿ)��3=��(��)
	static const char ccLegalSpan[512];
	// ���ݲ����ж����Ƿ����ȵ�����
	static const signed char ccKnightPin[512];
	// ���̳�ʼ����
	static const byte cucpcStartup[256];
	// ����λ�ü�ֵ��
	static const byte cucvlPiecePos[7][256];
	// ˧(��)�Ĳ���
	static const signed char ccKingDelta[4];
	// ��(ʿ)�Ĳ���
	static const signed char ccAdvisorDelta[4];
	// ��Ĳ�������˧(��)�Ĳ�����Ϊ����
	static const signed char ccKnightDelta[4][2];
	// �������Ĳ���������(ʿ)�Ĳ�����Ϊ����
	static const signed char ccKnightCheckDelta[4][2];
private:
	// ��̬���߷���
	///////////////////////////////////////////////////////////////////
	// �ж������Ƿ���������
	static inline bool IN_BOARD(int sq);
	// �ж������Ƿ��ھŹ���
	static inline bool IN_FORT(int sq);
	// ��ø��ӵĺ�����
	static inline int RANK_Y(int sq);
	// ��ø��ӵ�������
	static inline int FILE_X(int sq);
	// ����������ͺ������ø���
	static inline int COORD_XY(int x, int y);
	// ��ת����
	static inline int SQUARE_FLIP(int sq);
	// ������ˮƽ����
	static inline int FILE_FLIP(int x);
	// �����괹ֱ����
	static inline int RANK_FLIP(int y);
	// ����ˮƽ����
	static inline int MIRROR_SQUARE(int sq);
	// ����ˮƽ����
	static inline int SQUARE_FORWARD(int sq, int sd);
	// �߷��Ƿ����˧(��)�Ĳ���
	static inline bool KING_SPAN(int sqSrc, int sqDst);
	// �߷��Ƿ������(ʿ)�Ĳ���
	static inline bool ADVISOR_SPAN(int sqSrc, int sqDst);
	// �߷��Ƿ������(��)�Ĳ���
	static inline bool BISHOP_SPAN(int sqSrc, int sqDst);
	// ��(��)�۵�λ��
	static inline int BISHOP_PIN(int sqSrc, int sqDst);
	// ���ȵ�λ��
	static inline int KNIGHT_PIN(int sqSrc, int sqDst);
	// �Ƿ�δ����
	static inline bool HOME_HALF(int sq, int sd);
	// �Ƿ��ѹ���
	static inline bool AWAY_HALF(int sq, int sd);
	// �Ƿ��ںӵ�ͬһ��
	static inline bool SAME_HALF(int sqSrc, int sqDst);
	// �Ƿ���ͬһ��
	static inline bool SAME_RANK(int sqSrc, int sqDst);
	// �Ƿ���ͬһ��
	static inline bool SAME_FILE(int sqSrc, int sqDst);
	// ��ú�ڱ��(������8��������16)
	static inline int SIDE_TAG(int sd);
	// ��öԷ���ڱ��
	static inline int OPP_SIDE_TAG(int sd);
	// ����߷������
	static inline int SRC(int mv);
	// ����߷����յ�
	static inline int DST(int mv);
	// ���������յ����߷�
	static inline int MOVE(int sqSrc, int sqDst);
	// �߷�ˮƽ����
	static inline int MIRROR_MOVE(int mv);

private:
	friend struct SortStruct;
	// �ֵ�˭�ߣ�0=�췽��1=�ڷ�
	int player;
	byte board[256];
	int vlWhite, vlBlack;           	// �졢��˫����������ֵ
	int nDistance, nMoveNum;        	// ������ڵ�Ĳ�������ʷ�߷���
	MoveStruct mvsList[MAX_MOVES];  	// ��ʷ�߷���Ϣ�б�
	ZobristStruct zobr;             	// Zobrist
	static Zobrist zobrist;
	std::vector<MoveStruct> history;	// �������������ʷ����mvsList�ڳ��Ӻ��
										// ����ա�
	int direction;
	Search search;

private:
	// ���̷���
	///////////////////////////////////////////////////////////////////
	// �������
	inline void clear(void);
	// ���(��ʼ��)��ʷ�߷���Ϣ
	inline void clearHistory(void);
	// �������ӷ�
	inline void changeSide(void);
	// �������Ϸ�һö����
	void addPiece(int sq, int pc);
	// ������������һö����
	void delPiece(int sq, int pc);
	// �������ۺ���
	int evaluate(void) const;
	// �Ƿ񱻽���
	bool inCheck(void) const;
	// ��һ���Ƿ����
	bool captured(void) const;
	// ��һ���������
	int putPiece(int mv);
	// ������һ���������
	void undoPut(int mv, int pcCaptured);
	// ��һ����
	bool move(int mv);
	// ������һ����
	void undoMove(void);
	// ��һ���ղ�
	void nullMove(void);
	// ������һ���ղ�
	void undoNullMove(void);
	// �ж��߷��Ƿ����
	bool testMove(int mv) const;
	// ���������߷������"bCapture"Ϊ"true"��ֻ���ɳ����߷�
	int generateMoves(int *mvs, bool bCapture = false) const;
	// �ж��Ƿ񱻽���
	bool checked() const;
	// �ж��Ƿ�ɱ
	bool isMate(void);
	// �õ�����ķ�ֵ
	inline int getDrawValue(void) const;
	// ����ظ����棬�õ��ظ��������ڵĲ���ƫ��λ��
	int getRepeatPos(int nRecur = 1) const;
	// �õ��ظ������ֵ
	inline int getRepeatValue(int nRepStatus) const;
	// �Ծ��澵��
	void mirror(Engine &posMirror) const;
	// �ж��Ƿ�����ղ��ü�
	inline bool allowNullMove(void) const;

private:
	// ��������
	///////////////////////////////////////////////////////////////////
	// �������ֿ�
	int searchBook(void);
	// ��ȡ�û�����
	int probeHash(int vlAlpha, int vlBeta, int nDepth, int &mv);
	// �����û�����
	void recordHash(int nFlag, int vl, int nDepth, int mv);
	// ��̬(Quiescence)��������
	int searchQuiescence(int vlAlpha, int vlBeta);
	// ������߷��Ĵ���
	void setBestMove(int mv, int nDepth);
	// �����߽�(Fail-Soft)��Alpha-Beta��������
	int searchFull(int vlAlpha, int vlBeta, int nDepth,
			bool bNoNull = false);
	// ���ڵ��Alpha-Beta��������
	int searchRoot(int nDepth);
	// ����������������
	int searchMain(float seconds);

public:
	// ���ӱ��
	static const int PIECE_KING = 0;
	static const int PIECE_ADVISOR = 1;
	static const int PIECE_BISHOP = 2;
	static const int PIECE_KNIGHT = 3;
	static const int PIECE_ROOK = 4;
	static const int PIECE_CANNON = 5;
	static const int PIECE_PAWN = 6;

	// ����״̬
	static const int NORMAL = 0;
	static const int NORMAL_CAPTURED = 1;
	static const int NORMAL_CHECKED = 2;
	static const int RED_WIN = 3;
	static const int BLACK_WIN = 4;
	static const int DRAW = 5;
	static const int REPEATED = 0x100;
	static const int EXCEEDED_100 = 0x200;

public:
	// ��ʼ������
	void startup(int direction);
	// ����, direction: �������̺췽���²��� 0��������1
	bool move(int fromX, int fromY, int toX, int toY);
	// ����
	void undo();
	// �õ����˶��ٲ���
	inline int getMoveCount() const;
	// ��ǰ��˭��
	inline int getPlayer() const;
	// �õ���Ϸ״̬, ���boardָ�벻ΪNULL
	// �򻹻Ḵ�Ƶ�ǰ���̾�����Ϣ��boardָ����ڴ���, board �Ǽ򻯵�9x10����
	// ������ byte a[10][9] Ҳ������ byte a[90]
	// direction: �������̺췽���²��� 0��������1
	int getState(byte* board);
	// �������, ���ؽ���� fromX fromY toX toY ��
	// direction: �������̺췽���²��� 0��������1
	// searchSeconds: ����������
	bool findSolution(float searchSeconds, int& fromX, int& fromY,
			int& toX, int& toY);

	inline bool getLastMove(int& fromX, int& fromY, int& toX, int& toY);
};


///////////////////////////////////////////////////////////////////////////

// �ж������Ƿ���������
inline bool Engine::IN_BOARD(int sq) {
#if defined(ANDROID_DEBUG)
	if (sq > 255) {
		exit(-1);
	}
#endif
	return ccInBoard[sq] != 0;
}

// �ж������Ƿ��ھŹ���
inline bool Engine::IN_FORT(int sq) {
	return ccInFort[sq] != 0;
}

// ��ø��ӵĺ�����
inline int Engine::RANK_Y(int sq) {
	return sq >> 4;
}

// ��ø��ӵ�������
inline int Engine::FILE_X(int sq) {
	return sq & 15;
}

// ����������ͺ������ø���
inline int Engine::COORD_XY(int x, int y) {
	return x + (y << 4);
}

// ��ת����
inline int Engine::SQUARE_FLIP(int sq) {
	return 254 - sq;
}

// ������ˮƽ����
inline int Engine::FILE_FLIP(int x) {
	return 14 - x;
}

// �����괹ֱ����
inline int Engine::RANK_FLIP(int y) {
	return 15 - y;
}

// ����ˮƽ����
inline int Engine::MIRROR_SQUARE(int sq) {
	return COORD_XY(FILE_FLIP(FILE_X(sq)), RANK_Y(sq));
}

// ����ˮƽ����
inline int Engine::SQUARE_FORWARD(int sq, int sd) {
	return sq - 16 + (sd << 5);
}

// �߷��Ƿ����˧(��)�Ĳ���
inline bool Engine::KING_SPAN(int sqSrc, int sqDst) {
	return ccLegalSpan[sqDst - sqSrc + 256] == 1;
}

// �߷��Ƿ������(ʿ)�Ĳ���
inline bool Engine::ADVISOR_SPAN(int sqSrc, int sqDst) {
	return ccLegalSpan[sqDst - sqSrc + 256] == 2;
}

// �߷��Ƿ������(��)�Ĳ���
inline bool Engine::BISHOP_SPAN(int sqSrc, int sqDst) {
	return ccLegalSpan[sqDst - sqSrc + 256] == 3;
}

// ��(��)�۵�λ��
inline int Engine::BISHOP_PIN(int sqSrc, int sqDst) {
	return (sqSrc + sqDst) >> 1;
}

// ���ȵ�λ��
inline int Engine::KNIGHT_PIN(int sqSrc, int sqDst) {
	return sqSrc + ccKnightPin[sqDst - sqSrc + 256];
}

// �Ƿ�δ����
inline bool Engine::HOME_HALF(int sq, int sd) {
	return (sq & 0x80) != (sd << 7);
}

// �Ƿ��ѹ���
inline bool Engine::AWAY_HALF(int sq, int sd) {
	return (sq & 0x80) == (sd << 7);
}

// �Ƿ��ںӵ�ͬһ��
inline bool Engine::SAME_HALF(int sqSrc, int sqDst) {
	return ((sqSrc ^ sqDst) & 0x80) == 0;
}

// �Ƿ���ͬһ��
inline bool Engine::SAME_RANK(int sqSrc, int sqDst) {
	return ((sqSrc ^ sqDst) & 0xf0) == 0;
}

// �Ƿ���ͬһ��
inline bool Engine::SAME_FILE(int sqSrc, int sqDst) {
	return ((sqSrc ^ sqDst) & 0x0f) == 0;
}

// ��ú�ڱ��(������8��������16)
inline int Engine::SIDE_TAG(int sd) {
	return 8 + (sd << 3);
}

// ��öԷ���ڱ��
inline int Engine::OPP_SIDE_TAG(int sd) {
	return 16 - (sd << 3);
}

// ����߷������
inline int Engine::SRC(int mv) {
	return mv & 255;
}

// ����߷����յ�
inline int Engine::DST(int mv) {
	return mv >> 8;
}

// ���������յ����߷�
inline int Engine::MOVE(int sqSrc, int sqDst) {
	return sqSrc + sqDst * 256;
}

// �߷�ˮƽ����
inline int Engine::MIRROR_MOVE(int mv) {
	return MOVE(MIRROR_SQUARE(SRC(mv)), MIRROR_SQUARE(DST(mv)));
}

//////////////////////////////////////////////////////////////////////

// �������
inline void Engine::clear(void) {
	player = vlWhite = vlBlack = nDistance = 0;
	memset(board, 0, 256);
	zobr.InitZero();
}

// ���(��ʼ��)��ʷ�߷���Ϣ
inline void Engine::clearHistory(void) {
	mvsList[0].set(0, 0, checked(), zobr.dwKey);
	nMoveNum = 1;
}

inline void Engine::changeSide(void) {
	player = 1 - player;
	zobr.Xor(zobrist.Player);
}

// �������Ϸ�һö����
inline void Engine::addPiece(int sq, int pc) {
	board[sq] = pc;
	// �췽�ӷ֣��ڷ�(ע��"cucvlPiecePos"ȡֵҪ�ߵ�)����
	if (pc < 16) {
		vlWhite += cucvlPiecePos[pc - 8][sq];
		zobr.Xor(zobrist.Table[pc - 8][sq]);
	} else {
		vlBlack += cucvlPiecePos[pc - 16][SQUARE_FLIP(sq)];
		zobr.Xor(zobrist.Table[pc - 9][sq]);
	}
}

// ������������һö����
inline void Engine::delPiece(int sq, int pc) {
	board[sq] = 0;
	// �췽���֣��ڷ�(ע��"cucvlPiecePos"ȡֵҪ�ߵ�)�ӷ�
	if (pc < 16) {
		vlWhite -= cucvlPiecePos[pc - 8][sq];
		zobr.Xor(zobrist.Table[pc - 8][sq]);
	} else {
		vlBlack -= cucvlPiecePos[pc - 16][SQUARE_FLIP(sq)];
		zobr.Xor(zobrist.Table[pc - 9][sq]);
	}
}

// �������ۺ���
inline int Engine::evaluate(void) const {
	return (player == 0 ? vlWhite - vlBlack : vlBlack - vlWhite)
			+ ADVANCED_VALUE;
}

// �Ƿ񱻽���
inline bool Engine::inCheck(void) const {
	return mvsList[nMoveNum - 1].ucbCheck != 0;
}

// ��һ���Ƿ����
inline bool Engine::captured(void) const {
	if (history.empty())
		return false;
	else
		return history.back().ucpcCaptured != 0;
//	return mvsList[nMoveNum - 1].ucpcCaptured != 0;
}

// �ڷ�һ������
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

// �����ڷ�һ������
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

// ��һ����
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

// ������һ����
inline void Engine::undoMove(void) {
	nDistance--;
	nMoveNum--;
	changeSide();
	undoPut(mvsList[nMoveNum].wmv, mvsList[nMoveNum].ucpcCaptured);
}

// ��һ���ղ�
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

// ������һ���ղ�
inline void Engine::undoNullMove(void) {
	nDistance--;
	nMoveNum--;
	changeSide();
}

// �ж��Ƿ�����ղ��ü�
inline bool Engine::allowNullMove(void) const {
	return (player == 0 ? vlWhite : vlBlack) > NULL_MARGIN;
}

// �õ��ظ������ֵ
inline int Engine::getRepeatValue(int nRepStatus) const {
	int vlReturn;
	vlReturn = ((nRepStatus & 2) == 0 ? 0 : nDistance - BAN_VALUE)
			+ ((nRepStatus & 4) == 0 ? 0 : BAN_VALUE - nDistance);
	return vlReturn == 0 ? getDrawValue() : vlReturn;
}

// �õ�����ķ�ֵ
inline int Engine::getDrawValue(void) const {
	return (nDistance & 1) == 0 ? -DRAW_VALUE : DRAW_VALUE;
}

// �õ����˶��ٲ���
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

// ��ǰ��˭��
inline int Engine::getPlayer() const {
	return player;
}



} // End namespace.


#endif /* ENGINE_H_ */
