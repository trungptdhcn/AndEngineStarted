package com.thor.chess;

import java.util.EventListener;
import java.util.EventObject;

//import android.graphics.Color;
//import android.util.Log;

class MoveInfo {
	public int fromX = 0;
	public int fromY = 0;
	public int toX = 0;
	public int toY = 0;
}

class ChessEvent extends EventObject
{
	private static final long serialVersionUID = 1L;
		
	private Engine obj;
	private MoveInfo mv;
	private int state;
	
    public ChessEvent(Engine source, MoveInfo mv, int state)
    {
    	super(source);
    	obj = source;
    	this.mv = mv;
    	this.state = state;
    }
    public Engine getSource()
    {
    	return obj;
    }
    public MoveInfo getMove() {
    	return mv;
    }
    public int getState() {
    	return state;
    }
} 

interface ChessEventListener extends EventListener {
	public void onChessEvent(ChessEvent event);
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
	protected ChessEventListener listener = null;
	
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
	/**
	 * Initialize game
	 */
	synchronized public void startup(int direction) {
		jniStartup(__nativePtr, direction);
	}
	
	
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
		return jniMove(__nativePtr, fromX, fromY, toX, toY);
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
	synchronized public int getState(boolean updateBoard) {
		GameState gameState = jniGetState(__nativePtr, updateBoard);
		if (updateBoard) {
			board = gameState.board;
		}
		return gameState.state;
	}

	private static native MoveInfo jniFindSolution(long enginePtr, float searchSeconds);
	/**
	 * Find a solution
	 * @param direction 0: red at bottom, 1: black at bottom
	 * @param searchSeconds How many seconds would be used.
	 * @return a MoveInfo object that contain the move step, return null if failed
	 */
	synchronized public MoveInfo findSolution(float searchSeconds) {
		return jniFindSolution(__nativePtr, searchSeconds);
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
	
	////////////////////////////////////////////////////////////////

	/**
	 * Start a new game
	 * @param direction Chess board direction, 
	 *   	0: red on bottom, 
	 *   	1: black on bottom 
	 */
	synchronized public void startGame(int direction) {
		this.direction = direction;
		startup(direction);
		int state = getState(true);
		if (listener != null)
			listener.onChessEvent(new ChessEvent(this, null, state));
	}
	
	public int getDirection() {
		return direction;
	}
	
	public void setChessEventListener(ChessEventListener listener) {
		this.listener = listener;
	}
}
