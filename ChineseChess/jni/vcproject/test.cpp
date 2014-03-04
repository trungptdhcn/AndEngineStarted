#include "../engine.h"
#if defined(WIN32)
#	define _CRTDBG_MAP_ALLOC
#	include <stdlib.h>
#	include <crtdbg.h>
#	include <Windows.h>
#else
#	include <stdlib.h>
#endif
#include <stdio.h>
#if defined(GCC_DEBUG)
#include <unistd.h>
#endif


void showBoard(unsigned char* board) {
	static const char* chessName[24] = {
		0, 0, 0, 0, 0, 0, 0, 0,
		"帅","仕","相","马","车","炮","兵", 0,
		"将","士","象","马","车","炮","卒", 0
	};
#if defined(WIN32)
	system("cls");
#else
	write(1,"\E[H\E[2J",7);
#endif
	for (int y = 0; y < 10; y++) {
		for (int x = 0; x < 9; x++) {
			if (board[y * 9 + x] == 0)
				printf("   ");
			else
				printf("%s ", chessName[board[y * 9 + x]]);
		}
		printf("\n");
	}
	printf("--------------------------------\n");
}

bool youMove(chess::Engine* engine) {
	int fromX, fromY, toX, toY;
	printf("input: ");
	int readSize = scanf("%d %d %d %d", &fromX, &fromY, &toX, &toY);
	if (engine->move(fromX, fromY, toX, toY)) {
		return true;
	} else {
		char buffer[512];
		if (readSize == 0) {
			scanf("%s", buffer);
			if (stricmp(buffer, "exit") == 0) {
				return false;
			}
		}
		return true;
	}
}

bool aiMove(chess::Engine* engine) {
	int fromX, fromY, toX, toY;
	if (engine->findSolution(0.5, fromX, fromY, toX, toY)) {
		return engine->move(fromX, fromY, toX, toY);
	} else {
		return false;
	}
}

int main(int argc, char* argv[]) {

#if defined(WIN32)
	_CrtSetDbgFlag(_CRTDBG_ALLOC_MEM_DF | _CRTDBG_LEAK_CHECK_DF);
#endif
	chess::Engine* engine = new chess::Engine();
	int player = 1;
	engine->startup(player);
	unsigned char board[90];
	int state = engine->getState(board);
	while ((state & 0xf) < 3) {
		showBoard(board);
		if (engine->getPlayer() == player) {
			if (!youMove(engine))
				break;
		} else {
			if (!aiMove(engine))
				break;
		}
		state = engine->getState(board);
	}
	showBoard(board);
	if ((state & 0xF) == chess::Engine::BLACK_WIN) {
		if (player == 1)
			printf("You win!\n");
		else
			printf("You lost!\n");
	} else if ((state & 0xF) == chess::Engine::RED_WIN) {
		if (player == 0)
			printf("You win!\n");
		else
			printf("You lost!\n");
	} else if ((state & 0xF) == chess::Engine::DRAW) {
		printf("Draw with computer!\n");
	} else {
		printf("User cancel or error!\n");
	}
	
	delete engine;
#if defined(WIN32)
	_CrtDumpMemoryLeaks();
#endif
	return 0;
}