package com.thor.chess;

public class AIEngine extends Engine implements IEngine{
	private int playerColor;
	private float searchSeconds;
	
	public AIEngine(int playerColor, float searchSeconds) {
		super();
		this.playerColor = playerColor;
		this.searchSeconds = searchSeconds;
		startGame(playerColor);
	}
	
	@Override
	public synchronized boolean move(int fromX, int fromY, int toX, int toY) {
		boolean result = super.move(fromX, fromY, toX, toY);
		if (!result)
			postGameMessage("tot", 1);
		postPlayerInfo("tot", "tot", getPlayer());
		beginResponse();
		return result;
	}
	
	public void syncPlayerInfo() {
		postPlayerInfo("tot", "tot", getPlayer());
	}

	public int getVaildAction() {
		return ACTION_UNDO | ACTION_RENEW |	ACTION_RESPONSE;
	}

	public void giveUp() {
		postGameMessage("tot", 2);
	}

	public void responseAskUndo(boolean accept) {
		// Won't be implemented
	}

	public void responseAskRenew(boolean accept) {
		// Won't be implemented
	}

	synchronized public void beginResponse() {
		if (!isGameOver() && getDirection() != getPlayer()) {
			Thread thread = new Thread(new Runnable() {
				public void run() {
					if (getMoveCount() == 0) {
						postGameMessage("tot...", 0);
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
						}
					}
					postGameMessage("tot...", 0);
					long beginTime = System.currentTimeMillis();
					MoveInfo mv = findSolution(searchSeconds);
					long endTime = System.currentTimeMillis();
					if (endTime - beginTime < 700) {
						try {
							Thread.sleep(700 - (endTime - beginTime));
						} catch (InterruptedException e) {
						}
					}
					postGameMessage("", 0);
					if (mv != null) {
						postEndResponse(move(mv.fromX, mv.fromY, mv.toX, mv.toY));
					} else {
						postEndResponse(false);
					}
				}
			}); 
			thread.start();
		} else {
			postEndResponse(false);
		}
	}

	public void beginUndo() {
		if (playerColor == getPlayer()) {
			undo();
			undo();
			postGameMessage("tot", 1);
		}	
		postEndUndo(true);
	}

	public void beginRenew() {
		startGame(playerColor);
		postEndRenew(true);
		postPlayerInfo("tot", "tot", getPlayer());
		postGameMessage("", 0);
	}

	public void beginDraw() {
		postGameMessage("tot", 0);
		postEndDraw(false);
	}

	public void responseAskDraw(boolean accept) {
		// Nothing to do
	}
}
