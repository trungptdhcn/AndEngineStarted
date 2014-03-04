package com.thor.chess;
import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Test {

	public Test() {
		
	}
	
	private final static String[] chessName = new String[] {
		null, null, null, null, null, null, null, null,
		"帅","仕","相","马","车","炮","兵", null,
		"将","士","象","马","车","炮","卒", null
	};
	
	private static void showBoard(Engine engine) {
		try {
			Process p = Runtime.getRuntime().exec("cmd /c cls");
			p.exitValue();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		final byte[] board = new byte[90];
		engine.enumChessman(new Engine.IChessmanEnumerator() {
			@Override
			public void enumChessman(int x, int y, byte chessman, Object param) {
				board[y * 9 + x] = chessman;
			}
		}, null);
		for (int y = 0; y < 10; y++) {
			for (int x = 0; x < 9; x++) {
				if (board[y * 9 + x] == 0)
					System.out.print("   ");
				else {
					System.out.print(chessName[board[y * 9 + x]]);
					System.out.print(" ");
				}
			}
			System.out.print("\n");
		}
		System.out.print("--------------------------------\n");
	}
	
	private static boolean youMove(Engine engine) {
		int fromX, fromY, toX, toY;
		System.out.print("input: ");
		InputStreamReader stdinReader = new InputStreamReader(System.in);
		BufferedReader in = new BufferedReader(stdinReader);
		while (true) {
			while (true) {
				String line;
				try {
					line = in.readLine();
					if (line.equals("exit") || line.equals("quit"))
						return false;
				} catch (IOException e) {
					return false;
				}
				Pattern p = Pattern.compile("\\s*([0-9])\\s*([0-9])\\s*([0-9])\\s*([0-9])\\s*");
				Matcher m = p.matcher(line);
				if (!m.find())
					continue;
				fromX = Integer.parseInt(m.group(1));
				fromY= Integer.parseInt(m.group(2));
				toX = Integer.parseInt(m.group(3));
				toY= Integer.parseInt(m.group(4));
				break;
			}
			if (engine.move(fromX, fromY, toX, toY)) {
				return true;
			}
		}
	}

	private static boolean aiMove(Engine engine) {
		MoveInfo mv = engine.findSolution(0.5f);
		if (mv != null) {
			return engine.move(mv.fromX, mv.fromY, mv.toX, mv.toY);
		} else {
			return false;
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Engine engine = new Engine();
		int player = 1;
		engine.startup(player);
		int state = engine.getState(true);
		while ((state & 0xf) < 3) {
			showBoard(engine);
			if (engine.getPlayer() == player) {
				if (!youMove(engine))
					break;
			} else {
				if (!aiMove(engine))
					break;
			}
			state = engine.getState(true);
		}
		showBoard(engine);
		if ((state & 0xF) == Engine.BLACK_WIN) {
			if (player == 1)
				System.out.println("You win!");
			else
				System.out.println("You lost!");
		} else if ((state & 0xF) == Engine.RED_WIN) {
			if (player == 0)
				System.out.println("You win!");
			else
				System.out.println("You lost!");
		} else if ((state & 0xF) == Engine.DRAW) {
			System.out.println("Draw with computer!");
		} else {
			System.out.println("User cancel or error!");
		}		
	}
}
