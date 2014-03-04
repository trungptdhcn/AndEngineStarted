/*
 * engine.cpp
 *
 *  Created on: 2012-10-27
 *      Author: thor
 */

#include "engine.h"
#include <time.h>
#include <stdlib.h>
#include <algorithm>

namespace chess {



extern const unsigned int book_length;
extern const unsigned char book[];


Zobrist Engine::zobrist;

// 判断棋子是否在棋盘中的数组
const char Engine::ccInBoard[256] = {
	0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
	0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
	0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
	0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0,
	0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0,
	0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0,
	0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0,
	0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0,
	0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0,
	0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0,
	0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0,
	0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0,
	0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0,
	0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
	0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
	0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
};

// 判断棋子是否在九宫的数组
const char Engine::ccInFort[256] = {
	0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
	0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
	0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
	0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0,
	0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0,
	0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0,
	0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
	0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
	0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
	0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
	0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0,
	0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0,
	0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0,
	0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
	0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
	0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
};

// 判断步长是否符合特定走法的数组，1=帅(将)，2=仕(士)，3=相(象)
const char Engine::ccLegalSpan[512] = {
						 0, 0, 0, 0, 0, 0, 0, 0, 0,
	0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
	0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
	0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
	0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
	0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
	0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
	0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
	0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
	0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
	0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
	0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
	0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
	0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
	0, 0, 0, 0, 0, 3, 0, 0, 0, 3, 0, 0, 0, 0, 0, 0,
	0, 0, 0, 0, 0, 0, 2, 1, 2, 0, 0, 0, 0, 0, 0, 0,
	0, 0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0,
	0, 0, 0, 0, 0, 0, 2, 1, 2, 0, 0, 0, 0, 0, 0, 0,
	0, 0, 0, 0, 0, 3, 0, 0, 0, 3, 0, 0, 0, 0, 0, 0,
	0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
	0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
	0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
	0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
	0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
	0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
	0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
	0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
	0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
	0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
	0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
	0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
	0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
	0, 0, 0, 0, 0, 0, 0
};
// 根据步长判断马是否蹩腿的数组
const signed char Engine::ccKnightPin[512] = {
							  0,  0,  0,  0,  0,  0,  0,  0,  0,
  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
  0,  0,  0,  0,  0,  0,-16,  0,-16,  0,  0,  0,  0,  0,  0,  0,
  0,  0,  0,  0,  0, -1,  0,  0,  0,  1,  0,  0,  0,  0,  0,  0,
  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
  0,  0,  0,  0,  0, -1,  0,  0,  0,  1,  0,  0,  0,  0,  0,  0,
  0,  0,  0,  0,  0,  0, 16,  0, 16,  0,  0,  0,  0,  0,  0,  0,
  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
  0,  0,  0,  0,  0,  0,  0
};

// 棋盘初始设置
const byte Engine::cucpcStartup[256] = {
  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
  0,  0,  0, 20, 19, 18, 17, 16, 17, 18, 19, 20,  0,  0,  0,  0,
  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
  0,  0,  0,  0, 21,  0,  0,  0,  0,  0, 21,  0,  0,  0,  0,  0,
  0,  0,  0, 22,  0, 22,  0, 22,  0, 22,  0, 22,  0,  0,  0,  0,
  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
  0,  0,  0, 14,  0, 14,  0, 14,  0, 14,  0, 14,  0,  0,  0,  0,
  0,  0,  0,  0, 13,  0,  0,  0,  0,  0, 13,  0,  0,  0,  0,  0,
  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
  0,  0,  0, 12, 11, 10,  9,  8,  9, 10, 11, 12,  0,  0,  0,  0,
  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0
};

// 子力位置价值表
const byte Engine::cucvlPiecePos[7][256] = {
	{ // 帅(将)
	0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
	0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
	0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
	0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
	0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
	0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
	0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
	0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
	0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
	0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
	0,  0,  0,  0,  0,  0,  1,  1,  1,  0,  0,  0,  0,  0,  0,  0,
	0,  0,  0,  0,  0,  0,  2,  2,  2,  0,  0,  0,  0,  0,  0,  0,
	0,  0,  0,  0,  0,  0, 11, 15, 11,  0,  0,  0,  0,  0,  0,  0,
	0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
	0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
	0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0
	}, { // 仕(士)
	0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
	0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
	0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
	0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
	0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
	0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
	0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
	0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
	0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
	0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
	0,  0,  0,  0,  0,  0, 20,  0, 20,  0,  0,  0,  0,  0,  0,  0,
	0,  0,  0,  0,  0,  0,  0, 23,  0,  0,  0,  0,  0,  0,  0,  0,
	0,  0,  0,  0,  0,  0, 20,  0, 20,  0,  0,  0,  0,  0,  0,  0,
	0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
	0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
	0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0
	}, { // 相(象)
	0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
	0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
	0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
	0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
	0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
	0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
	0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
	0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
	0,  0,  0,  0,  0, 20,  0,  0,  0, 20,  0,  0,  0,  0,  0,  0,
	0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
	0,  0,  0, 18,  0,  0,  0, 23,  0,  0,  0, 18,  0,  0,  0,  0,
	0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
	0,  0,  0,  0,  0, 20,  0,  0,  0, 20,  0,  0,  0,  0,  0,  0,
	0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
	0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
	0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0
	}, { // 马
	0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
	0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
	0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
	0,  0,  0, 90, 90, 90, 96, 90, 96, 90, 90, 90,  0,  0,  0,  0,
	0,  0,  0, 90, 96,103, 97, 94, 97,103, 96, 90,  0,  0,  0,  0,
	0,  0,  0, 92, 98, 99,103, 99,103, 99, 98, 92,  0,  0,  0,  0,
	0,  0,  0, 93,108,100,107,100,107,100,108, 93,  0,  0,  0,  0,
	0,  0,  0, 90,100, 99,103,104,103, 99,100, 90,  0,  0,  0,  0,
	0,  0,  0, 90, 98,101,102,103,102,101, 98, 90,  0,  0,  0,  0,
	0,  0,  0, 92, 94, 98, 95, 98, 95, 98, 94, 92,  0,  0,  0,  0,
	0,  0,  0, 93, 92, 94, 95, 92, 95, 94, 92, 93,  0,  0,  0,  0,
	0,  0,  0, 85, 90, 92, 93, 78, 93, 92, 90, 85,  0,  0,  0,  0,
	0,  0,  0, 88, 85, 90, 88, 90, 88, 90, 85, 88,  0,  0,  0,  0,
	0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
	0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
	0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0
	}, { // 车
	0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
	0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
	0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
	0,  0,  0,206,208,207,213,214,213,207,208,206,  0,  0,  0,  0,
	0,  0,  0,206,212,209,216,233,216,209,212,206,  0,  0,  0,  0,
	0,  0,  0,206,208,207,214,216,214,207,208,206,  0,  0,  0,  0,
	0,  0,  0,206,213,213,216,216,216,213,213,206,  0,  0,  0,  0,
	0,  0,  0,208,211,211,214,215,214,211,211,208,  0,  0,  0,  0,
	0,  0,  0,208,212,212,214,215,214,212,212,208,  0,  0,  0,  0,
	0,  0,  0,204,209,204,212,214,212,204,209,204,  0,  0,  0,  0,
	0,  0,  0,198,208,204,212,212,212,204,208,198,  0,  0,  0,  0,
	0,  0,  0,200,208,206,212,200,212,206,208,200,  0,  0,  0,  0,
	0,  0,  0,194,206,204,212,200,212,204,206,194,  0,  0,  0,  0,
	0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
	0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
	0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0
	}, { // 炮
	0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
	0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
	0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
	0,  0,  0,100,100, 96, 91, 90, 91, 96,100,100,  0,  0,  0,  0,
	0,  0,  0, 98, 98, 96, 92, 89, 92, 96, 98, 98,  0,  0,  0,  0,
	0,  0,  0, 97, 97, 96, 91, 92, 91, 96, 97, 97,  0,  0,  0,  0,
	0,  0,  0, 96, 99, 99, 98,100, 98, 99, 99, 96,  0,  0,  0,  0,
	0,  0,  0, 96, 96, 96, 96,100, 96, 96, 96, 96,  0,  0,  0,  0,
	0,  0,  0, 95, 96, 99, 96,100, 96, 99, 96, 95,  0,  0,  0,  0,
	0,  0,  0, 96, 96, 96, 96, 96, 96, 96, 96, 96,  0,  0,  0,  0,
	0,  0,  0, 97, 96,100, 99,101, 99,100, 96, 97,  0,  0,  0,  0,
	0,  0,  0, 96, 97, 98, 98, 98, 98, 98, 97, 96,  0,  0,  0,  0,
	0,  0,  0, 96, 96, 97, 99, 99, 99, 97, 96, 96,  0,  0,  0,  0,
	0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
	0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
	0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0
	}, { // 兵(卒)
	0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
	0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
	0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
	0,  0,  0,  9,  9,  9, 11, 13, 11,  9,  9,  9,  0,  0,  0,  0,
	0,  0,  0, 19, 24, 34, 42, 44, 42, 34, 24, 19,  0,  0,  0,  0,
	0,  0,  0, 19, 24, 32, 37, 37, 37, 32, 24, 19,  0,  0,  0,  0,
	0,  0,  0, 19, 23, 27, 29, 30, 29, 27, 23, 19,  0,  0,  0,  0,
	0,  0,  0, 14, 18, 20, 27, 29, 27, 20, 18, 14,  0,  0,  0,  0,
	0,  0,  0,  7,  0, 13,  0, 16,  0, 13,  0,  7,  0,  0,  0,  0,
	0,  0,  0,  7,  0,  7,  0, 15,  0,  7,  0,  7,  0,  0,  0,  0,
	0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
	0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
	0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
	0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
	0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
	0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0
	}
};

// 帅(将)的步长
const signed char Engine::ccKingDelta[4] = {-16, -1, 1, 16};
// 仕(士)的步长
const signed char Engine::ccAdvisorDelta[4] = {-17, -15, 15, 17};
// 马的步长，以帅(将)的步长作为马腿
const signed char Engine::ccKnightDelta[4][2] =
	{{-33, -31}, {-18, 14}, {-14, 18}, {31, 33}};
// 马被将军的步长，以仕(士)的步长作为马腿
const signed char Engine::ccKnightCheckDelta[4][2] =
	{{-33, -18}, {-31, -14}, {14, 31}, {18, 33}};

////////////////////////////////////////////////////////////////////////


// 生成所有走法，如果"bCapture"为"true"则只生成吃子走法
int Engine::generateMoves(int *mvs, bool bCapture) const {
	ENTER("generateMoves");
	int i, j, nGenMoves, nDelta, sqSrc, sqDst;
	int pcSelfSide, pcOppSide, pcSrc, pcDst;
	// 生成所有走法，需要经过以下几个步骤：
	nGenMoves = 0;
	pcSelfSide = SIDE_TAG(player);
	pcOppSide = OPP_SIDE_TAG(player);
	for (sqSrc = 0; sqSrc < 256; sqSrc++) {

		// 1. 找到一个本方棋子，再做以下判断：
		pcSrc = board[sqSrc];
		if ((pcSrc & pcSelfSide) == 0) {
			continue;
		}

		// 2. 根据棋子确定走法
		switch (pcSrc - pcSelfSide) {
		case PIECE_KING:
			for (i = 0; i < 4; i++) {
				sqDst = sqSrc + ccKingDelta[i];
				if (!IN_FORT(sqDst)) {
					continue;
				}
				pcDst = board[sqDst];
				if (bCapture ?
						(pcDst & pcOppSide) != 0 : (pcDst & pcSelfSide) == 0) {
					mvs[nGenMoves] = MOVE(sqSrc, sqDst);
#if defined(ANDROID_DEBUG)
					if (mvs[nGenMoves] > 65535) {
						LOG1("KING move out of ranges: %d, ", mvs[nGenMoves]);
						exit(-1);
					}
#endif
					nGenMoves++;
				}
			}
			break;
		case PIECE_ADVISOR:
			for (i = 0; i < 4; i++) {
				sqDst = sqSrc + ccAdvisorDelta[i];
				if (!IN_FORT(sqDst)) {
					continue;
				}
				pcDst = board[sqDst];
				if (bCapture ?
						(pcDst & pcOppSide) != 0 : (pcDst & pcSelfSide) == 0) {
					mvs[nGenMoves] = MOVE(sqSrc, sqDst);
#if defined(ANDROID_DEBUG)
					if (mvs[nGenMoves] > 65535) {
						LOG1("ADVISOR move value out of ranges: %d", mvs[nGenMoves]);
						exit(-1);
					}
#endif
					nGenMoves++;
				}
			}
			break;
		case PIECE_BISHOP:
			for (i = 0; i < 4; i++) {
				sqDst = sqSrc + ccAdvisorDelta[i];
				if (!(IN_BOARD(sqDst) && HOME_HALF(sqDst, player)
						&& board[sqDst] == 0)) {
					continue;
				}
				LOG1("BISHOP debug sqDst: %d", sqDst);
				sqDst += ccAdvisorDelta[i];
#if defined(ANDROID_DEBUG)
				if (sqDst > 255) {
					LOG1("BISHOP i: %d", i);
					LOG1("BISHOP ccAdvisorDelta[i]: %d", ccAdvisorDelta[i]);
				}
#endif
				pcDst = board[sqDst];
				if (bCapture ?
						(pcDst & pcOppSide) != 0 : (pcDst & pcSelfSide) == 0) {
					mvs[nGenMoves] = MOVE(sqSrc, sqDst);
#if defined(ANDROID_DEBUG)
					if (mvs[nGenMoves] > 65535) {
						LOG1("BISHOP sqSrc: %d", sqSrc);
						LOG1("BISHOP sqDst: %d", sqDst);
						LOG1("BISHOP move value out of ranges: %d", mvs[nGenMoves]);
						exit(-1);
					}
#endif
					nGenMoves++;
				}
			}
			break;
		case PIECE_KNIGHT:
			for (i = 0; i < 4; i++) {
				sqDst = sqSrc + ccKingDelta[i];
				if (board[sqDst] != 0) {
					continue;
				}
				for (j = 0; j < 2; j++) {
					sqDst = sqSrc + ccKnightDelta[i][j];
					if (!IN_BOARD(sqDst)) {
						continue;
					}
					pcDst = board[sqDst];
					if (bCapture ?
							(pcDst & pcOppSide) != 0 :
							(pcDst & pcSelfSide) == 0) {
						mvs[nGenMoves] = MOVE(sqSrc, sqDst);
#if defined(ANDROID_DEBUG)
					if (mvs[nGenMoves] > 65535) {
						LOG1("KNIGHT move value out of ranges: %d", mvs[nGenMoves]);
						exit(-1);
					}
#endif
						nGenMoves++;
					}
				}
			}
			break;
		case PIECE_ROOK:
			for (i = 0; i < 4; i++) {
				nDelta = ccKingDelta[i];
				sqDst = sqSrc + nDelta;
				while (IN_BOARD(sqDst)) {
					pcDst = board[sqDst];
					if (pcDst == 0) {
						if (!bCapture) {
							mvs[nGenMoves] = MOVE(sqSrc, sqDst);
#if defined(ANDROID_DEBUG)
					if (mvs[nGenMoves] > 65535) {
						LOG1("ROOK move value out of ranges: %d", mvs[nGenMoves]);
						exit(-1);
					}
#endif
							nGenMoves++;
						}
					} else {
						if ((pcDst & pcOppSide) != 0) {
							mvs[nGenMoves] = MOVE(sqSrc, sqDst);
#if defined(ANDROID_DEBUG)
					if (mvs[nGenMoves] > 65535) {
						LOG1("ROOK move value out of ranges: %d", mvs[nGenMoves]);
						exit(-1);
					}
#endif
							nGenMoves++;
						}
						break;
					}
					sqDst += nDelta;
				}
			}
			break;
		case PIECE_CANNON:
			for (i = 0; i < 4; i++) {
				nDelta = ccKingDelta[i];
				sqDst = sqSrc + nDelta;
				while (IN_BOARD(sqDst)) {
					pcDst = board[sqDst];
					if (pcDst == 0) {
						if (!bCapture) {
							mvs[nGenMoves] = MOVE(sqSrc, sqDst);
#if defined(ANDROID_DEBUG)
					if (mvs[nGenMoves] > 65535) {
						LOG1("CANNON move value out of ranges: %d", mvs[nGenMoves]);
						exit(-1);
					}
#endif
							nGenMoves++;
						}
					} else {
						break;
					}
					sqDst += nDelta;
				}
				sqDst += nDelta;
				while (IN_BOARD(sqDst)) {
					pcDst = board[sqDst];
					if (pcDst != 0) {
						if ((pcDst & pcOppSide) != 0) {
							mvs[nGenMoves] = MOVE(sqSrc, sqDst);
#if defined(ANDROID_DEBUG)
					if (mvs[nGenMoves] > 65535) {
						LOG1("CANNON move value out of ranges: %d", mvs[nGenMoves]);
						exit(-1);
					}
#endif
							nGenMoves++;
						}
						break;
					}
					sqDst += nDelta;
				}
			}
			break;
		case PIECE_PAWN:
			sqDst = SQUARE_FORWARD(sqSrc, player);
			if (IN_BOARD(sqDst)) {
				pcDst = board[sqDst];
				if (bCapture ?
						(pcDst & pcOppSide) != 0 : (pcDst & pcSelfSide) == 0) {
					mvs[nGenMoves] = MOVE(sqSrc, sqDst);
#if defined(ANDROID_DEBUG)
					if (mvs[nGenMoves] > 65535) {
						LOG1("PAWN move value out of ranges: %d", mvs[nGenMoves]);
						exit(-1);
					}
#endif
					nGenMoves++;
				}
			}
			if (AWAY_HALF(sqSrc, player)) {
				for (nDelta = -1; nDelta <= 1; nDelta += 2) {
					sqDst = sqSrc + nDelta;
					if (IN_BOARD(sqDst)) {
						pcDst = board[sqDst];
						if (bCapture ?
								(pcDst & pcOppSide) != 0 :
								(pcDst & pcSelfSide) == 0) {
							mvs[nGenMoves] = MOVE(sqSrc, sqDst);
#if defined(ANDROID_DEBUG)
					if (mvs[nGenMoves] > 65535) {
						LOG1("PAWN move value out of ranges: %d", mvs[nGenMoves]);
						exit(-1);
					}
#endif
							nGenMoves++;
						}
					}
				}
			}
			break;
		}
	}
	return nGenMoves;
}

// 判断走法是否合理
bool Engine::testMove(int mv) const {
	int sqSrc, sqDst, sqPin;
	int pcSelfSide, pcSrc, pcDst, nDelta;
	// 判断走法是否合法，需要经过以下的判断过程：

	// 1. 判断起始格是否有自己的棋子
	sqSrc = SRC(mv);
	pcSrc = board[sqSrc];
	pcSelfSide = SIDE_TAG(player);
	if ((pcSrc & pcSelfSide) == 0) {
		return false;
	}

	// 2. 判断目标格是否有自己的棋子
	sqDst = DST(mv);
	pcDst = board[sqDst];
	if ((pcDst & pcSelfSide) != 0) {
		return false;
	}

	// 3. 根据棋子的类型检查走法是否合理
	switch (pcSrc - pcSelfSide) {
	case PIECE_KING:
		return IN_FORT(sqDst) && KING_SPAN(sqSrc, sqDst);
	case PIECE_ADVISOR:
		return IN_FORT(sqDst) && ADVISOR_SPAN(sqSrc, sqDst);
	case PIECE_BISHOP:
		return SAME_HALF(sqSrc, sqDst) && BISHOP_SPAN(sqSrc, sqDst)
				&& board[BISHOP_PIN(sqSrc, sqDst)] == 0;
	case PIECE_KNIGHT:
		sqPin = KNIGHT_PIN(sqSrc, sqDst);
		return sqPin != sqSrc && board[sqPin] == 0;
	case PIECE_ROOK:
	case PIECE_CANNON:
		if (SAME_RANK(sqSrc, sqDst)) {
			nDelta = (sqDst < sqSrc ? -1 : 1);
		} else if (SAME_FILE(sqSrc, sqDst)) {
			nDelta = (sqDst < sqSrc ? -16 : 16);
		} else {
			return false;
		}
		sqPin = sqSrc + nDelta;
		while (sqPin != sqDst && board[sqPin] == 0) {
			sqPin += nDelta;
		}
		if (sqPin == sqDst) {
			return pcDst == 0 || pcSrc - pcSelfSide == PIECE_ROOK;
		} else if (pcDst != 0 && pcSrc - pcSelfSide == PIECE_CANNON) {
			sqPin += nDelta;
			while (sqPin != sqDst && board[sqPin] == 0) {
				sqPin += nDelta;
			}
			return sqPin == sqDst;
		} else {
			return false;
		}
	case PIECE_PAWN:
		if (AWAY_HALF(sqDst, player)
				&& (sqDst == sqSrc - 1 || sqDst == sqSrc + 1)) {
			return true;
		}
		return sqDst == SQUARE_FORWARD(sqSrc, player);
	default:
		return false;
	}
}

// 判断是否被将军
bool Engine::checked() const {
	int i, j, sqSrc, sqDst;
	int pcSelfSide, pcOppSide, pcDst, nDelta;
	pcSelfSide = SIDE_TAG(player);
	pcOppSide = OPP_SIDE_TAG(player);
	// 找到棋盘上的帅(将)，再做以下判断：

	for (sqSrc = 0; sqSrc < 256; sqSrc++) {
		if (board[sqSrc] != pcSelfSide + PIECE_KING) {
			continue;
		}

		// 1. 判断是否被对方的兵(卒)将军
		if (board[SQUARE_FORWARD(sqSrc, player)] == pcOppSide + PIECE_PAWN) {
			return true;
		}
		for (nDelta = -1; nDelta <= 1; nDelta += 2) {
			if (board[sqSrc + nDelta] == pcOppSide + PIECE_PAWN) {
				return true;
			}
		}

		// 2. 判断是否被对方的马将军(以仕(士)的步长当作马腿)
		for (i = 0; i < 4; i++) {
			if (board[sqSrc + ccAdvisorDelta[i]] != 0) {
				continue;
			}
			for (j = 0; j < 2; j++) {
				pcDst = board[sqSrc + ccKnightCheckDelta[i][j]];
				if (pcDst == pcOppSide + PIECE_KNIGHT) {
					return true;
				}
			}
		}

		// 3. 判断是否被对方的车或炮将军(包括将帅对脸)
		for (i = 0; i < 4; i++) {
			nDelta = ccKingDelta[i];
			sqDst = sqSrc + nDelta;
			while (IN_BOARD(sqDst)) {
				pcDst = board[sqDst];
				if (pcDst != 0) {
					if (pcDst == pcOppSide + PIECE_ROOK
							|| pcDst == pcOppSide + PIECE_KING) {
						return true;
					}
					break;
				}
				sqDst += nDelta;
			}
			sqDst += nDelta;
			while (IN_BOARD(sqDst)) {
				int pcDst = board[sqDst];
				if (pcDst != 0) {
					if (pcDst == pcOppSide + PIECE_CANNON) {
						return true;
					}
					break;
				}
				sqDst += nDelta;
			}
		}
		return false;
	}
	return false;
}

// 判断是否被杀
bool Engine::isMate(void) {
	int i, nGenMoveNum, pcCaptured;
	std::vector<int> mvs(MAX_GEN_MOVES);
#if defined(ANDROID_DEBUG)
	if (mvs.size() != (unsigned)MAX_GEN_MOVES) {
		LOG("Gen mvs did not allocate enough memory in isMate.");
		exit(-1);
	}
#endif
	nGenMoveNum = generateMoves(begin_ptr(mvs));
#if defined(ANDROID_DEBUG)
	if (nGenMoveNum > 127) {
		LOG1("nGenMoveNum out of ranges: %d in isMate", nGenMoveNum);
		exit(-1);
	}
#endif
	for (i = 0; i < nGenMoveNum; i++) {
		pcCaptured = putPiece(mvs[i]);
		if (!checked()) {
			undoPut(mvs[i], pcCaptured);
			return false;
		} else {
			undoPut(mvs[i], pcCaptured);
		}
	}
	return true;
}

// 检测重复局面
int Engine::getRepeatPos(int nRecur) const {
	bool bSelfSide, bPerpCheck, bOppPerpCheck;
	const MoveStruct *lpmvs;

	bSelfSide = false;
	bPerpCheck = bOppPerpCheck = true;
	lpmvs = mvsList + nMoveNum - 1;
	while (lpmvs->wmv != 0 && lpmvs->ucpcCaptured == 0) {
		if (bSelfSide) {
			bPerpCheck = bPerpCheck && lpmvs->ucbCheck;
			if (lpmvs->dwKey == zobr.dwKey) {
				nRecur--;
				if (nRecur == 0) {
					return 1 + (bPerpCheck ? 2 : 0) + (bOppPerpCheck ? 4 : 0);
				}
			}
		} else {
			bOppPerpCheck = bOppPerpCheck && lpmvs->ucbCheck;
		}
		bSelfSide = !bSelfSide;
		lpmvs--;
	}
	return 0;
}

// 对局面镜像
void Engine::mirror(Engine &posMirror) const {
	int sq, pc;
	posMirror.clear();
	for (sq = 0; sq < 256; sq++) {
		pc = board[sq];
		if (pc != 0) {
			posMirror.addPiece(MIRROR_SQUARE(sq), pc);
		}
	}
	if (player == 1) {
		posMirror.changeSide();
	}
	posMirror.clearHistory();
}

// 初始化棋盘
void Engine::startup(int direction) {
	int sq, pc;
	clear();
	for (sq = 0; sq < 256; sq++) {
		pc = cucpcStartup[sq];
		if (pc != 0) {
			addPiece(sq, pc);
		}
	}
	clearHistory();
	history.clear();
	this->direction = direction;
}

/////////////////////////////////////////////////////////////////////////
// 搜索相关函数

static int compareBook(const void *lpbk1, const void *lpbk2) {
	return ((BookItem *) lpbk1)->dwLock - ((BookItem *) lpbk2)->dwLock;
}

// 搜索开局库
int Engine::searchBook() {
	ENTER("searchBook");
	int nBookSize = book_length / sizeof(BookItem);	// 开局库大小
	if (nBookSize > BOOK_SIZE) {
		nBookSize = BOOK_SIZE;
	}
	BookItem* bookTable = (BookItem*)book; 		   	// 开局库
	int i, vl, nBookMoves, mv;
	std::vector<int> mvs(MAX_GEN_MOVES), vls(MAX_GEN_MOVES);
	bool bMirror;
	BookItem bkToSearch, *lpbk;

	// 搜索开局库的过程有以下几个步骤

	// 1. 如果没有开局库，则立即返回
	if (nBookSize == 0) {
		return 0;
	}
	// 2. 搜索当前局面
	bMirror = false;
	bkToSearch.dwLock = zobr.dwLock1;
	lpbk = (BookItem *) bsearch(&bkToSearch, bookTable, nBookSize,
			sizeof(BookItem), compareBook);
	// 3. 如果没有找到，那么搜索当前局面的镜像局面
	if (lpbk == 0) {
		bMirror = true;
		Engine* posMirror = new Engine();
		mirror(*posMirror);
		bkToSearch.dwLock = posMirror->zobr.dwLock1;
		lpbk = (BookItem *) bsearch(&bkToSearch, bookTable,
				nBookSize, sizeof(BookItem), compareBook);
		delete posMirror;
	}
	// 4. 如果镜像局面也没找到，则立即返回
	if (lpbk == 0) {
		return 0;
	}
	// 5. 如果找到，则向前查第一个开局库项
	while (lpbk >= bookTable && lpbk->dwLock == bkToSearch.dwLock) {
		lpbk--;
	}
	lpbk++;
	// 6. 把走法和分值写入到"mvs"和"vls"数组中
	vl = nBookMoves = 0;
	while (lpbk < bookTable + nBookSize
			&& lpbk->dwLock == bkToSearch.dwLock) {
		mv = (bMirror ? MIRROR_MOVE(lpbk->wmv) : lpbk->wmv);
		if (testMove(mv)) {
			mvs[nBookMoves] = mv;
			vls[nBookMoves] = lpbk->wvl;
			vl += vls[nBookMoves];
			nBookMoves++;
			if (nBookMoves == MAX_GEN_MOVES) {
				break; // 防止"BOOK.DAT"中含有异常数据
			}
		}
		lpbk++;
	}
	if (vl == 0) {
		return 0; // 防止"BOOK.DAT"中含有异常数据
	}
	// 7. 根据权重随机选择一个走法
	vl = rand() % vl;
	for (i = 0; i < nBookMoves; i++) {
		vl -= vls[i];
		if (vl < 0) {
			break;
		}
	}
	return mvs[i];
}

// 提取置换表项
int Engine::probeHash(int vlAlpha, int vlBeta, int nDepth,
		int &mv) {
	bool bMate; // 杀棋标志：如果是杀棋，那么不需要满足深度条件
	HashItem hsh;

	hsh = search.hashTable[zobr.dwKey & (HASH_SIZE - 1)];
	if (hsh.dwLock0 != zobr.dwLock0 || hsh.dwLock1 != zobr.dwLock1) {
		mv = 0;
		return -MATE_VALUE;
	}
	mv = hsh.wmv;
	bMate = false;
	if (hsh.svl > WIN_VALUE) {
		if (hsh.svl < BAN_VALUE) {
			// 可能导致搜索的不稳定性，立刻退出，但最佳着法可能拿到
			return -MATE_VALUE;
		}
		hsh.svl -= nDistance;
		bMate = true;
	} else if (hsh.svl < -WIN_VALUE) {
		if (hsh.svl > -BAN_VALUE) {
			return -MATE_VALUE; // 同上
		}
		hsh.svl += nDistance;
		bMate = true;
	}
	if (hsh.ucDepth >= nDepth || bMate) {
		if (hsh.ucFlag == HASH_BETA) {
			return (hsh.svl >= vlBeta ? hsh.svl : -MATE_VALUE);
		} else if (hsh.ucFlag == HASH_ALPHA) {
			return (hsh.svl <= vlAlpha ? hsh.svl : -MATE_VALUE);
		}
		return hsh.svl;
	}
	return -MATE_VALUE;
}

// 保存置换表项
void Engine::recordHash(int nFlag, int vl, int nDepth, int mv) {
	HashItem hsh;
	hsh = search.hashTable[zobr.dwKey & (HASH_SIZE - 1)];
	if (hsh.ucDepth > nDepth) {
		return;
	}
	hsh.ucFlag = nFlag;
	hsh.ucDepth = nDepth;
	if (vl > WIN_VALUE) {
		if (mv == 0 && vl <= BAN_VALUE) {
			return; // 可能导致搜索的不稳定性，并且没有最佳着法，立刻退出
		}
		hsh.svl = vl + nDistance;
	} else if (vl < -WIN_VALUE) {
		if (mv == 0 && vl >= -BAN_VALUE) {
			return; // 同上
		}
		hsh.svl = vl - nDistance;
	} else {
		hsh.svl = vl;
	}
	hsh.wmv = mv;
	hsh.dwLock0 = zobr.dwLock0;
	hsh.dwLock1 = zobr.dwLock1;
	search.hashTable[zobr.dwKey & (HASH_SIZE - 1)] = hsh;
}


// MVV/LVA每种子力的价值
static byte cucMvvLva[24] = {
	0, 0, 0, 0, 0, 0, 0, 0,
	5, 1, 1, 3, 4, 3, 2, 0,
	5, 1, 1, 3, 4, 3, 2, 0
};

// 按MVV/LVA值排序的比较函数
class CompareMvvLva
{
public:
	inline CompareMvvLva(byte* board);
	inline bool operator()(uint32 mv1, uint32 mv2);
	// 求MVV/LVA值
	inline int32 mvv_lva(uint32 mv);
private:
	byte* board;
};

CompareMvvLva::CompareMvvLva(byte* board)
	: board(board)
{
}

inline bool CompareMvvLva::operator()(uint32 mv1, uint32 mv2)
{
	return mvv_lva(mv1) > mvv_lva(mv2);
}

// 得到走法的终点
inline uint32 to(uint32 mv)
{
	return mv >> 8;
}

// 得到走法的起点
inline uint32 from(uint32 mv)
{
	return mv & 0xFF;
}

// 求MVV/LVA值
inline int32 CompareMvvLva::mvv_lva(uint32 mv)
{
	return (cucMvvLva[board[to(mv)]] << 3) - cucMvvLva[board[from(mv)]];
}


// 按历史表排序的比较函数
class CompareHistory
{
public:
	inline CompareHistory(int32* history_table);
	inline bool operator()(uint32 mv1, uint32 mv2);
private:
	int32* _history_table;
};
inline CompareHistory::CompareHistory(int32* history_table)
: _history_table(history_table)
{
}
inline bool CompareHistory::operator()(uint32 mv1, uint32 mv2)
{
#if defined(ANDROID_DEBUG)
	if (mv1 > 65535) {
		LOG1("CompareHistory::operator() mv1 of range: %d", mv1);
		exit(-1);
	}
	if (mv2 > 65535) {
		LOG1("CompareHistory::operator() mv2 out of range: %d", mv2);
		exit(-1);
	}

#endif
	return _history_table[mv1] > _history_table[mv2];
}

// 走法排序阶段
const int PHASE_HASH = 0;
const int PHASE_KILLER_1 = 1;
const int PHASE_KILLER_2 = 2;
const int PHASE_GEN_MOVES = 3;
const int PHASE_REST = 4;

// 走法排序结构
typedef struct SortStruct {
	Engine& engine;
	Search& search;
	int mvHash, mvKiller1, mvKiller2; // 置换表走法和两个杀手走法
	int nPhase, nIndex, nGenMoves;    // 当前阶段，当前采用第几个走法，总共有几个走法
	std::vector<int> mvs;           // 所有的走法

	// 初始化，设定置换表走法和两个杀手走法
	inline SortStruct(Engine& engine, Search& search, int mvHash);
	// 得到下一个走法
	int next(void);
} SortStruct;

// 初始化，设定置换表走法和两个杀手走法
inline SortStruct::SortStruct(Engine& engine, Search& search, int mvHash) :
		engine(engine), search(search), nIndex(0), nGenMoves(0),
		mvs(MAX_GEN_MOVES) {
	this->mvHash = mvHash;
#if defined(ANDROID_DEBUG)
	if (engine.nDistance >= 64 || engine.nDistance < 0) {
		LOG("engine.nDistance out of range.");
		exit(-1);
	}
#endif
	mvKiller1 = search.mvKillers[engine.nDistance][0];
	mvKiller2 = search.mvKillers[engine.nDistance][1];
	nPhase = PHASE_HASH;
}

// 得到下一个走法
int SortStruct::next(void) {
	int mv;
	CompareHistory compareHistory(search.nHistoryTable);

	// "nPhase"表示着法启发的若干阶段，依次为：

	// 0. 置换表着法启发，完成后立即进入下一阶段；
	if (nPhase == PHASE_HASH) {
		nPhase = PHASE_KILLER_1;
		if (mvHash != 0) {
			return mvHash;
		}
	}

	// 1. 杀手着法启发(第一个杀手着法)，完成后立即进入下一阶段；
	if (nPhase == PHASE_KILLER_1) {
		nPhase = PHASE_KILLER_2;
		if (mvKiller1 != mvHash && mvKiller1 != 0
				&& engine.testMove(mvKiller1)) {
			return mvKiller1;
		}
	}

	// 2. 杀手着法启发(第二个杀手着法)，完成后立即进入下一阶段；
	if (nPhase == PHASE_KILLER_2) {
		nPhase = PHASE_GEN_MOVES;
		if (mvKiller2 != mvHash && mvKiller2 != 0
				&& engine.testMove(mvKiller2)) {
			return mvKiller2;
		}
	}

	// 3. 生成所有着法，完成后立即进入下一阶段；
	if (nPhase == PHASE_GEN_MOVES) {
		nPhase = PHASE_REST;
		nGenMoves = engine.generateMoves(begin_ptr(mvs));
		LOG1("nGenMoves: %d SortStruct::next", nGenMoves);
		std::sort(begin_ptr(mvs), begin_ptr(mvs) + nGenMoves, compareHistory);
		nIndex = 0;
	}

	// 4. 对剩余着法做历史表启发；
	if (nPhase == PHASE_REST) {
		while (nIndex < nGenMoves) {
			mv = mvs[nIndex];
			nIndex++;
			if (mv != mvHash && mv != mvKiller1 && mv != mvKiller2) {
				return mv;
			}
		}
	}
	// 5. 没有着法了，返回零。
	return 0;
}


// 静态(Quiescence)搜索过程
int Engine::searchQuiescence(int vlAlpha, int vlBeta) {
	ENTER("searchQuiescence");
	int i, nGenMoves;
	int vl, vlBest;
	std::vector<int> mvs(MAX_GEN_MOVES);

	CompareHistory compareHistory(search.nHistoryTable);
	CompareMvvLva compareMvvLva(board);

	// 一个静态搜索分为以下几个阶段

	// 1. 检查重复局面
	vl = getRepeatPos();
	if (vl != 0) {
		LOG("searchQuiescence done!");
		return getRepeatValue(vl);
	}

	// 2. 到达极限深度就返回局面评价
	if (nDistance == LIMIT_DEPTH) {
		LOG("searchQuiescence done!");
		return evaluate();
	}

	// 3. 初始化最佳值
	vlBest = -MATE_VALUE; // 这样可以知道，是否一个走法都没走过(杀棋)

	if (inCheck()) {
		// 4. 如果被将军，则生成全部走法
#if defined(ANDROID_DEBUG)
	if (mvs.size() != (unsigned)MAX_GEN_MOVES) {
		LOG("Gen mvs did not allocate enough memory in searchQuiescence.");
		exit(-1);
	}
#endif
		nGenMoves = generateMoves(begin_ptr(mvs));
#if defined(ANDROID_DEBUG)
	if (nGenMoves > 127) {
		LOG1("nGenMoveNum out of ranges: %d  in searchQuiescence.", nGenMoves);
		exit(-1);
	}
#endif
		ENTER("SORT");
		LOG1("nGenMoves: %d searchQuiescence 1", nGenMoves);
		std::sort(begin_ptr(mvs), begin_ptr(mvs) + nGenMoves, compareHistory);
	} else {
		// 5. 如果不被将军，先做局面评价
		vl = evaluate();
		if (vl > vlBest) {
			vlBest = vl;
			if (vl >= vlBeta) {
				return vl;
			}
			if (vl > vlAlpha) {
				vlAlpha = vl;
			}
		}
		// 6. 如果局面评价没有截断，再生成吃子走法
#if defined(ANDROID_DEBUG)
	if (mvs.size() != (unsigned)MAX_GEN_MOVES) {
		LOG("Gen mvs did not allocate enough memory in searchQuiescence.");
		exit(-1);
	}
#endif
		nGenMoves = generateMoves(begin_ptr(mvs), true);
#if defined(ANDROID_DEBUG)
	if (nGenMoves > 127) {
		LOG1("nGenMoveNum out of ranges: %d  in searchQuiescence.", nGenMoves);
		exit(-1);
	}
#endif
		ENTER("SORT");
		LOG1("nGenMoves: %d searchQuiescence 2", nGenMoves);
		std::sort(begin_ptr(mvs), begin_ptr(mvs) + nGenMoves, compareMvvLva);
	}

	// 7. 逐一走这些走法，并进行递归
	for (i = 0; i < nGenMoves; i++) {
		if (move(mvs[i])) {
			vl = -searchQuiescence(-vlBeta, -vlAlpha);
			undoMove();

			// 8. 进行Alpha-Beta大小判断和截断
			if (vl > vlBest) {    // 找到最佳值(但不能确定是Alpha、PV还是Beta走法)
				vlBest = vl;        // "vlBest"就是目前要返回的最佳值，可能超出Alpha-Beta边界
				if (vl >= vlBeta) { // 找到一个Beta走法
					return vl;        // Beta截断
				}
				if (vl > vlAlpha) { // 找到一个PV走法
					vlAlpha = vl;     // 缩小Alpha-Beta边界
				}
			}
		}
	}
	LOG("searchQuiescence done!");
	// 9. 所有走法都搜索完了，返回最佳值
	return vlBest == -MATE_VALUE ? nDistance - MATE_VALUE : vlBest;
}

// 对最佳走法的处理
void Engine::setBestMove(int mv, int nDepth) {
	int *lpmvKillers;
#if defined(ANDROID_DEBUG)
	if (mv > 65535 || mv < 0) {
		LOG("setBestMove mv out of range");
		exit(mv);
	}
#endif
	search.nHistoryTable[mv] += nDepth * nDepth;
	lpmvKillers = search.mvKillers[nDistance];
	if (lpmvKillers[0] != mv) {
		lpmvKillers[1] = lpmvKillers[0];
		lpmvKillers[0] = mv;
	}
}

// 超出边界(Fail-Soft)的Alpha-Beta搜索过程
int Engine::searchFull(int vlAlpha, int vlBeta, int nDepth,
		bool bNoNull) {
	ENTER("searchFull");
	int nHashFlag, vl, vlBest;
	int mv, mvBest, mvHash, nNewDepth;

	// 一个Alpha-Beta完全搜索分为以下几个阶段

	// 1. 到达水平线，则调用静态搜索(注意：由于空步裁剪，深度可能小于零)
	if (nDepth <= 0) {
		LOG("searchFull done!");
		return searchQuiescence(vlAlpha, vlBeta);
	}

	// 1-1. 检查重复局面(注意：不要在根节点检查，否则就没有走法了)
	vl = getRepeatPos();
	if (vl != 0) {
		LOG("searchFull done!");
		return getRepeatValue(vl);
	}

	// 1-2. 到达极限深度就返回局面评价
	if (nDistance == LIMIT_DEPTH) {
		LOG("searchFull done!");
		return evaluate();
	}

	// 1-3. 尝试置换表裁剪，并得到置换表走法
	vl = probeHash(vlAlpha, vlBeta, nDepth, mvHash);
	if (vl > -MATE_VALUE) {
		LOG("searchFull done!");
		return vl;
	}
	LOG("searchFull probeHash passed!");

	// 1-4. 尝试空步裁剪(根节点的Beta值是"MATE_VALUE"，所以不可能发生空步裁剪)
	if (!bNoNull && !inCheck() && allowNullMove()) {
		nullMove();
		vl = -searchFull(-vlBeta, 1 - vlBeta, nDepth - NULL_DEPTH - 1,
				true);
		undoNullMove();
		if (vl >= vlBeta) {
			return vl;
		}
	}
	LOG("searchFull allowNullMove passed!");

	// 2. 初始化最佳值和最佳走法
	nHashFlag = HASH_ALPHA;
	vlBest = -MATE_VALUE; // 这样可以知道，是否一个走法都没走过(杀棋)
	mvBest = 0;           // 这样可以知道，是否搜索到了Beta走法或PV走法，以便保存到历史表

	// 3. 初始化走法排序结构
	SortStruct sort(*this, search, mvHash);
	LOG("searchFull sort passed!");

	// 4. 逐一走这些走法，并进行递归
	while ((mv = sort.next()) != 0) {
		if (move(mv)) {
			// 将军延伸
			nNewDepth = inCheck() ? nDepth : nDepth - 1;
			// PVS
			if (vlBest == -MATE_VALUE) {
				vl = -searchFull(-vlBeta, -vlAlpha, nNewDepth);
			} else {
				vl = -searchFull(-vlAlpha - 1, -vlAlpha, nNewDepth);
				if (vl > vlAlpha && vl < vlBeta) {
					vl = -searchFull(-vlBeta, -vlAlpha, nNewDepth);
				}
			}
			undoMove();

			// 5. 进行Alpha-Beta大小判断和截断
			if (vl > vlBest) {    // 找到最佳值(但不能确定是Alpha、PV还是Beta走法)
				vlBest = vl;        // "vlBest"就是目前要返回的最佳值，可能超出Alpha-Beta边界
				if (vl >= vlBeta) { // 找到一个Beta走法
					nHashFlag = HASH_BETA;
					mvBest = mv;      // Beta走法要保存到历史表
					break;            // Beta截断
				}
				if (vl > vlAlpha) { // 找到一个PV走法
					nHashFlag = HASH_PV;
					mvBest = mv;      // PV走法要保存到历史表
					vlAlpha = vl;     // 缩小Alpha-Beta边界
				}
			}
		}
	}
	LOG("searchFull all move tested!");


	// 5. 所有走法都搜索完了，把最佳走法(不能是Alpha走法)保存到历史表，返回最佳值
	if (vlBest == -MATE_VALUE) {
		// 如果是杀棋，就根据杀棋步数给出评价
		LOG("searchFull done!");
		return nDistance - MATE_VALUE;
	}
	// 记录到置换表
	recordHash(nHashFlag, vlBest, nDepth, mvBest);
	if (mvBest != 0) {
		// 如果不是Alpha走法，就将最佳走法保存到历史表
		setBestMove(mvBest, nDepth);
	}
	LOG("searchFull done!");
	return vlBest;
}

// 根节点的Alpha-Beta搜索过程
int Engine::searchRoot(int nDepth) {
	ENTER("searchRoot");
	int vl, vlBest, mv, nNewDepth;
	vlBest = -MATE_VALUE;
	SortStruct sort(*this, search, search.mvResult);
	while ((mv = sort.next()) != 0) {
		if (move(mv)) {
			nNewDepth = inCheck() ? nDepth : nDepth - 1;
			if (vlBest == -MATE_VALUE) {
				vl = -searchFull(-MATE_VALUE, MATE_VALUE, nNewDepth,
						true);
			} else {
				vl = -searchFull(-vlBest - 1, -vlBest, nNewDepth);
				if (vl > vlBest) {
					vl = -searchFull(-MATE_VALUE, -vlBest, nNewDepth,
							true);
				}
			}
			undoMove();
			if (vl > vlBest) {
				vlBest = vl;
				search.mvResult = mv;
				if (vlBest > -WIN_VALUE && vlBest < WIN_VALUE) {
					vlBest += (rand() & RANDOM_MASK) - (rand() & RANDOM_MASK);
				}
			}
		}
	}
	recordHash(HASH_PV, vlBest, nDepth, search.mvResult);
	setBestMove(search.mvResult, nDepth);
	return vlBest;
}

// 迭代加深搜索过程
int Engine::searchMain(float seconds) {

	ENTER("searchMain");
	int i, t, vl, nGenMoves;
	std::vector<int> mvs(MAX_GEN_MOVES);

	// 初始化一个搜索
	search.init();

	t = clock();       // 初始化定时器
	nDistance = 0; // 初始步数

	// 搜索开局库
	search.mvResult = searchBook();

	if (search.mvResult != 0) {
		move(search.mvResult);
		if (getRepeatPos(3) == 0) {
			undoMove();
			return search.mvResult;
		}
		undoMove();
	}

	// 检查是否只有唯一走法
	vl = 0;
#if defined(ANDROID_DEBUG)
	if (mvs.size() != (unsigned)MAX_GEN_MOVES) {
		LOG("Gen mvs did not allocate enough memory in searchMain.");
		exit(-1);
	}
#endif
	nGenMoves = generateMoves(begin_ptr(mvs));
#if defined(ANDROID_DEBUG)
	if (nGenMoves > 127) {
		LOG1("nGenMoveNum out of ranges: %d  in searchMain.", nGenMoves);
		exit(-1);
	}
#endif
	for (i = 0; i < nGenMoves; i++) {
		if (move(mvs[i])) {
			undoMove();
			search.mvResult = mvs[i];
			vl++;
		}
	}
	if (vl == 1) {
		return search.mvResult;
	}

	// 迭代加深过程
	for (i = 1; i <= LIMIT_DEPTH; i++) {
		vl = searchRoot(i);
		// 搜索到杀棋，就终止搜索
		if (vl > WIN_VALUE || vl < -WIN_VALUE) {
			break;
		}
		// 超过一秒，就终止搜索
		if (clock() - t > (CLOCKS_PER_SEC * seconds)) {
			break;
		}
	}

	return search.mvResult;
}

// 走棋
bool Engine::move(int fromX, int fromY, int toX, int toY) {
	ENTER("move");
	int mv;
	if (direction == 0) { // 红方
		int from = COORD_XY(fromX + 3, fromY + 3);
		int to = COORD_XY(toX + 3, toY + 3);
		mv = MOVE(from, to);
	} else {
		int from = COORD_XY(fromX + 3, RANK_FLIP(fromY + 3));
		int to = COORD_XY(toX + 3, RANK_FLIP(toY + 3));
		mv = MOVE(from, to);
	}
	if (!testMove(mv))
		return false;

	if (isMate())
		return false;

	bool ret = move(mv);
	if (ret) {
		history.push_back(mvsList[nMoveNum - 1]);
		if (captured()) {
			clearHistory();
		}
	}
	return ret;
}

// 悔棋
void Engine::undo() {
	if (nMoveNum > 1) {
		undoMove();
		history.pop_back();
	} else if (history.size() > 0) {
		MoveStruct& mvinfo = history.back();
		changeSide();
		undoPut(mvinfo.wmv, mvinfo.ucpcCaptured);
		history.pop_back();
	}
}

// 得到游戏状态
int Engine::getState(byte* board) {
	if (board != 0) {
		for (int y = RANK_TOP; y <= RANK_BOTTOM; y++) {
			for (int x = FILE_LEFT; x <= FILE_RIGHT; x ++) {
				if (direction == 0)
					*board++ = this->board[COORD_XY(x, y)];
				else
					*board++ = this->board[COORD_XY(x, RANK_FLIP(y))];
			}
		}
	}
	if (isMate()) {
		if (player == 0)
			return BLACK_WIN;
		else
			return RED_WIN;
	}
	int rep = getRepeatPos(3);
	if (rep > 0) {
		rep = getRepeatValue(rep);
		if (rep > WIN_VALUE) {
			if (player == 0)
				return BLACK_WIN | REPEATED;
			else
				return RED_WIN | REPEATED;
		} else if (rep < -WIN_VALUE) {
			if (player == 0)
				return RED_WIN | REPEATED;
			else
				return BLACK_WIN | REPEATED;
		} else {
			return DRAW | REPEATED;
		}
	} else if (nMoveNum > 100) {
		return DRAW | EXCEEDED_100;
	} else {
		// 如果没有分出胜负
		if (inCheck())
			return NORMAL_CHECKED;
		else if (captured())
			return NORMAL_CAPTURED;
		else
			return NORMAL;
	}
}

// 盘面求解
bool Engine::findSolution(float searchSeconds, int& fromX,
		int& fromY, int& toX, int& toY) {
	ENTER("findSolution");
	if (isMate()) {
		return false;
	}
	int mv = searchMain(searchSeconds);
	if (mv == 0) {
		return false;
	}
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


}
