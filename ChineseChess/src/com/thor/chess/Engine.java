package com.thor.chess;

class MoveInfo {
	public int fromX = 0;
	public int fromY = 0;
	public int toX = 0;
	public int toY = 0;
}

interface IEngineEventListener {
	public void onGameMessage(String message, int withNotify);
	public void onSyncGame();
	public void onSyncPlayerInfo(String player, String partner, int goSide);
	public void onAskUndo();
	public void onAskRenew();
	public void onAskDraw();
	public void onGameOver();
	public void onMove(int state);

	public void onEndResponse(boolean result);
	public void onEndUndo(boolean result);
	public void onEndRenew(boolean result);
	public void onEndDraw(boolean result);
}

// Used by UI side
interface IEngine {
	public static final int ACTION_UNDO = 1;
	public static final int ACTION_RENEW = 2;
	public static final int ACTION_DRAW = 4;
	public static final int ACTION_GIVEUP = 8;
	public static final int ACTION_RESPONSE = 16;
	
	public int getVaildAction();
	public void syncPlayerInfo();
	public void giveUp();
	
	public void responseAskUndo(boolean accept);
	public void responseAskRenew(boolean accept);
	public void responseAskDraw(boolean accept);
	
	public void beginResponse();
	public void beginUndo();
	public void beginRenew();
	public void beginDraw();

}

public class Engine {
	// Chess man definition
	public final static byte SHUAI 	= 0;
	public final static byte SHI 	= 1;
	public final static byte XIANG 	= 2;
	public final static byte MA 	= 3;
	public final static byte CHE 	= 4;
	public final static byte PAO 	= 5;
	public final static byte BING 	= 6;
	
	// Game state
	public final static int NORMAL = 0;
	public final static int NORMAL_CAPTURED = 1;
	public final static int NORMAL_CHECKED = 2;
	public final static int RED_WIN = 3;
	public final static int BLACK_WIN = 4;
	public final static int DRAW = 5;
	public final static int REPEATED = 0x100;
	public final static int EXCEEDED_100 = 0x200;
	
	// Chess board
	protected byte[] board = new byte[90];
	protected int direction = 0;
	private int state = 0;
	private IEngineEventListener listener;
	
	protected void postPlayerInfo(String player, String partner, int goSide) {
		if (listener != null)
			listener.onSyncPlayerInfo(player, partner, goSide);
	}
	
	/**
	 * 
	 * @param message
	 * @param withNotify Whether need a toast or dialog to show notify this message
	 * 		0: Don't need any prompt
	 * 		1: Use toast to prompt
	 * 		2: Use dialog to prompt 
	 */
	protected void postGameMessage(String message, int withNotify) {
		if (listener != null)
			listener.onGameMessage(message, withNotify);
	}
	
	protected void postEndRenew(boolean result) {
		if (listener != null)
			listener.onEndRenew(result);
	}
	
	protected void postEndDraw(boolean result) {
		if (listener != null)
			listener.onEndDraw(result);
	}
	
	protected void postEndResponse(boolean result) {
		if (listener != null)
			listener.onEndResponse(result);
	}
	
	protected void postEndUndo(boolean result) {
		if (listener != null)
			listener.onEndUndo(result);
	}
	
	protected void postAskUndo() {
		if (listener != null)
			listener.onAskUndo();
	}
	
	protected void postAskRenew() {
		if (listener != null)
			listener.onAskRenew();
	}
	
	protected void postAskDraw() {
		if (listener != null)
			listener.onAskDraw();
	}	
	
	protected void postGameOver() {
		if (listener != null)
			listener.onGameOver();
	}
	
	protected void postSync() {
		if (listener != null)
			listener.onSyncGame();
	}
	
	/////////////////////////////////////////////////////////////////
	// Native APIs
	protected long __nativePtr = 0;
	static {
		System.loadLibrary("engine");
	}
	
	/**
	 * Initialize native engine
	 */
	private static native long jniInitialize();
	
	
	private static native void jniStartup(long enginePtr, int direction);
	
	private static native boolean jniMove(long enginePtr, int fromX, int fromY,
			int toX, int toY);
	/**
	 * Do a move
	 * @param direction 0: red at bottom, 1: black at bottom
	 * @param mv MoveInfo object that contained from and to information
	 * @return true: succeeded, false: failed, maybe movement is illegal or
	 * can not do it in current situation
	 */
	synchronized public boolean move(int fromX, int fromY, int toX, int toY) {
		boolean result = jniMove(__nativePtr, fromX, fromY, toX, toY);
		if (result) {
			state = getState(true);
			if (listener != null)
				listener.onMove(state);
			notifyOver();
		}
		return result;
	}
	
	synchronized public int getChessmanColor(int x, int y) {
		return getChessmanColor(board[y * 9 + x]);
	}
	
	private static native void jniUndo(long enginePtr);
	/**
	 * Undo move
	 */
	synchronized public void undo () {
		jniUndo(__nativePtr);
		state = getState(true);
	}
	
	private static native int jniGetMoveCount(long enginePtr);
	/**
	 * Get steps count
	 * @return steps count
	 */
	synchronized public int getMoveCount() {
		return jniGetMoveCount(__nativePtr);
	}
	
	private static native int jniGetPlayer(long enginePtr);
	/**
	 * Obtain which player should go
	 * @return 0: red 1: black
	 */
	synchronized public int getPlayer() {
		return jniGetPlayer(__nativePtr);
	}
	
	private static class GameState {
		public int state;
		public byte[] board;
	}
	private static native GameState jniGetState(long enginePtr, boolean updateBoard);
	/**
	 * Get current game state, and if updateBoard is true will also 
	 * update board information.
	 * @param updateBoard true: update board info, false: no update required
	 * @return Current game state that defined in 
	 */
	synchronized private int getState(boolean updateBoard) {
		GameState gameState = jniGetState(__nativePtr, updateBoard);
		if (updateBoard) {
			board = gameState.board;
		}
		return gameState.state;
	}

	private static native MoveInfo jniFindSolution(long enginePtr, float searchSeconds);
	/**
	 * Find a solution
	 * @param searchSeconds How many seconds would be used.
	 * @return a MoveInfo object that contain the move step, return null if failed
	 */
	synchronized public MoveInfo findSolution(float searchSeconds) {
		return jniFindSolution(__nativePtr, searchSeconds);
	}
	
	private static native MoveInfo jniGetLastMove(long enginePtr);
	
	/**
	 * Get last move step
	 * @return a MoveInfo object that contain the move step, 
	 * return null if currently has no move history
	 */	
	synchronized public MoveInfo getLastMove() {
		return jniGetLastMove(__nativePtr);
	}
	
	private static native void jniDispose(long enginePtr);
	/**
	 * Release memory that allocated by native APIs
	 */
	synchronized public void dispose() {
		jniDispose(__nativePtr);
	}
	
	/////////////////////////////////////////////////////////////////
	
	public Engine() {
		__nativePtr = jniInitialize();
		
	}

	public static byte getChessmanType(byte chessman) {
		if (chessman < 16)
			return (byte)(chessman - 8);
		else
			return (byte)(chessman - 16);
	}
	
	public static int getChessmanColor(byte chessman) {
		if (chessman == 0)
			return -1;
		else if (chessman < 16)
			return 0;
		else
			return 1;
	}
	
	public interface IChessmanEnumerator {
		void enumChessman(int x, int y, byte chessman, Object param);
	}
	
	/**
	 * Enumerate all chessmen
	 * @param enumerator
	 */
	synchronized public void enumChessman(IChessmanEnumerator enumerator, Object param) {
		if (board == null)
			return;
		for (int y = 0; y < 10; y++) {
			for (int x = 0; x < 9; x++) {
				if (board[y * 9 + x] != 0 && enumerator != null)
					enumerator.enumChessman(x, y, board[y * 9 + x], param);
			}
		}
	}
	
	synchronized public void syncBoard(byte[] board) {
		System.arraycopy(this.board, 0, board, 0, 90);
	}
	
	////////////////////////////////////////////////////////////////

	/**
	 * Start a new game
	 * @param direction Chess board direction, 
	 *   	0: red on bottom, 
	 *   	1: black on bottom 
	 */
	synchronized public void startGame(int direction) {
		this.direction = direction;
		jniStartup(__nativePtr, direction);
		state = getState(true);
	}
	
	public int getDirection() {
		return direction;
	}
	
	public int getState() {
		return state;
	}
	
	public boolean isGameOver() {
		return ((state & 0xF) >= 3);
	}

	public String getOverDesc() {
		if (isGameOver()) {
			int state = getState();
			if ((state & 0xF) == Engine.RED_WIN) {
				if (getDirection() == 0) {
					if ((state & Engine.REPEATED) > 0)
						return "dsf";
					else
						return "fsad";
				} else {
					if ((state & Engine.REPEATED) > 0)
						return "fsd";
					else
						return "fds";
				}
			} else if ((state & 0xF) == Engine.BLACK_WIN) {
				if (getDirection() == 0) {
					if ((state & Engine.REPEATED) > 0)
						return "fds";
					else
						return "fsd";
				} else {
					if ((state & Engine.REPEATED) > 0)
						return "dfs";
					else
						return "fds";
				}														
			} else {
				return "df";
			}
		} else
			return null;
	}
	

	private void notifyOver() {
		String msg = getOverDesc();
		if (msg != null && listener != null) {
			listener.onGameMessage(msg, 2);
		}
	}	
	
	public void setEventListener(IEngineEventListener listener) {
		this.listener = listener;
	}
}
