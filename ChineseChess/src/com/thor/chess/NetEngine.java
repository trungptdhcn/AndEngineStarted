package com.thor.chess;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.util.Log;


interface IConnectListener {
	public void onCompleted(boolean connectionEstablished);
}

interface IAcceptListener {
	public void onCompleted(boolean connectionEstablished);
}

public class NetEngine extends Engine implements IEngine{
	private static final UUID CHESS_UUID = 
			UUID.fromString("08e2b297-3470-44d2-9457-0787b57f5a21");

	private BluetoothAdapter bluetoothAdapter = null;
	private String partnerName;
	private String playerName;
	private int playerColor;
	private String remoteAddress;
	private BluetoothSocket sock = null;
	private BluetoothServerSocket listenSock = null;
	private InputStream input = null;
	private OutputStream output = null;
	private boolean isServer = false;
	private boolean offeredToClose = false;
	
	@Override
	public synchronized void dispose() {
		super.dispose();
		// Send net message to other player to indicate player has been offline.
		try {
			offeredToClose = true;
			if (sock != null)
				sock.close();
		} catch (IOException e) {
		}
		
	}
	
	public NetEngine(String yourName,int playerColor) {
		super();
		playerName = yourName;
		bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		this.playerColor = playerColor;
		startGame(playerColor);
		isServer = true;
	}

	public NetEngine(String yourName, String remoteAddress) {
		super();
		playerName = yourName;
		bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		this.remoteAddress = remoteAddress;	
		isServer = false;
	}
	
	public boolean isServer() {
		return isServer;
	}
	
	static class NetCommand {
		public static final int CMD_UNKNOW = 0;
		public static final int CMD_START = 1;
		public static final int CMD_MOVE = 2;
		public static final int CMD_UNDO = 3;
		public static final int CMD_UNDO_RESP = (0x100 | 0x3);
		public static final int CMD_RENEW = 4;
		public static final int CMD_RENEW_RESP = (0x100 | 0x4);
		public static final int CMD_GIVE_UP = 5;
		public static final int CMD_SYNC_NAME = 6;
		public static final int CMD_DRAW = 7;
		public static final int CMD_DRAW_RESP = (0x100 | 0x7);
		
		public int pos = 0;
		public int command = CMD_UNKNOW;
		public Object argument = null;
	}
	
	final static Pattern askMove = Pattern.compile("ask:move:([0-9])([0-9])([0-9])([0-9])\\n");
	final static Pattern askUndo = Pattern.compile("ask:undo\\n");
	final static Pattern askDraw = Pattern.compile("ask:draw\\n");
	final static Pattern askGiveup = Pattern.compile("ask:giveup\\n");
	final static Pattern askRenew = Pattern.compile("ask:renew:(0|1)\\n");
	final static Pattern askSyncName = Pattern.compile("ask:sync:(.+)\\n");
	final static Pattern rspUndo = Pattern.compile("rsp:undo:(true|false)\\n");
	final static Pattern rspRenew = Pattern.compile("rsp:renew:(true|false)\\n");
	final static Pattern rspDraw = Pattern.compile("rsp:draw:(true|false)\\n");
	final static Pattern askStart = Pattern.compile("ask:start:(0|1)\\n");
	
	
	private static NetCommand parseCommand(String cmdStr) {
		Log.i("ChineseChess", cmdStr);
		NetCommand cmd = new NetCommand();
		cmd.command = NetCommand.CMD_UNKNOW;
		Matcher matcher = askMove.matcher(cmdStr);
		if (matcher.find()) {
			cmd.command = NetCommand.CMD_MOVE;
			MoveInfo mv = new MoveInfo();
			mv.fromX = Integer.parseInt(matcher.group(1));
			mv.fromY = Integer.parseInt(matcher.group(2));
			mv.toX = Integer.parseInt(matcher.group(3));
			mv.toY = Integer.parseInt(matcher.group(4));
			cmd.argument = mv;
			return cmd;
		}
		matcher = askUndo.matcher(cmdStr);
		if (matcher.find()) {
			cmd.command = NetCommand.CMD_UNDO;
			return cmd;
		}
		matcher = askGiveup.matcher(cmdStr);
		if (matcher.find()) {
			cmd.command = NetCommand.CMD_GIVE_UP;
			return cmd;
		}
		matcher = askDraw.matcher(cmdStr);
		if (matcher.find()) {
			cmd.command = NetCommand.CMD_DRAW;
			return cmd;
		}
		matcher = askStart.matcher(cmdStr);
		if (matcher.find()) {
			cmd.command = NetCommand.CMD_START;
			cmd.argument = Integer.parseInt(matcher.group(1));
			return cmd;
		}	
		matcher = askRenew.matcher(cmdStr);
		if (matcher.find()) {
			cmd.command = NetCommand.CMD_RENEW;
			cmd.argument = Integer.parseInt(matcher.group(1));
			return cmd;
		}
		matcher = askSyncName.matcher(cmdStr);
		if (matcher.find()) {
			cmd.command = NetCommand.CMD_SYNC_NAME;
			cmd.argument = matcher.group(1);
			return cmd;
		}
		matcher = rspUndo.matcher(cmdStr);
		if (matcher.find()) {
			cmd.command = NetCommand.CMD_UNDO_RESP;
			cmd.argument = Boolean.parseBoolean(matcher.group(1));
			return cmd;
		}
		matcher = rspRenew.matcher(cmdStr);
		if (matcher.find()) {
			cmd.command = NetCommand.CMD_RENEW_RESP;
			cmd.argument = Boolean.parseBoolean(matcher.group(1));
			return cmd;
		}
		matcher = rspDraw.matcher(cmdStr);
		if (matcher.find()) {
			cmd.command = NetCommand.CMD_DRAW_RESP;
			cmd.argument = Boolean.parseBoolean(matcher.group(1));
			return cmd;
		}
		return cmd;
	}
	
	private static NetCommand parseCommand(byte[] buffer, int length) {
		ByteBuffer arr = ByteBuffer.wrap(buffer, 0, length);
		for (int i = 0; i < length; i++) {
			if (arr.get(i) == '\n') {
				byte[] cmdBytes = new byte[i + 1];
				arr.position(0);
				arr.get(cmdBytes);
				String cmdStr;
				try {
					cmdStr = new String(cmdBytes, "UTF-8");
				} catch (UnsupportedEncodingException e) {
					NetCommand cmd = new NetCommand();
					cmd.command = NetCommand.CMD_UNKNOW;
					cmd.pos = i + 1;
					return cmd;
				}
				NetCommand cmd = parseCommand(cmdStr);
				cmd.pos = i + 1;
				return cmd;
			} else if (i > 1024) {
				NetCommand cmd = new NetCommand();
				cmd.command = NetCommand.CMD_UNKNOW;
				cmd.pos = i + 1;
				return cmd;
			}
		}
		return null;
	}
	
	private static void rotateMove(MoveInfo mv) {
		mv.fromX = 8 - mv.fromX;
		mv.fromY = 9 - mv.fromY;
		mv.toX = 8 - mv.toX;
		mv.toY = 9 - mv.toY;
	}
	
	private void run(boolean isServer) {
		try {
			input = sock.getInputStream();
			output = sock.getOutputStream();
			
			if (isServer) {
				String msg = String.format("ask:start:%1$s\n", playerColor);
				sendToPartner(msg);
			}
			// Send your name to your partner.
			String syncMessage = String.format("ask:sync:%1$s\n", playerName);
			sendToPartner(syncMessage);
			
			byte[] buffer = new byte[4096];
			int contentSize = 0;
			int readSize = 0;
			while ((readSize = input.read(buffer, contentSize, 
					buffer.length - contentSize)) > 0) {
				contentSize += readSize;
				NetCommand cmd = parseCommand(buffer, contentSize);
				while (cmd != null) {
					contentSize -= cmd.pos;
					System.arraycopy(buffer, cmd.pos, buffer, 0, contentSize);
					switch (cmd.command) {
					case NetCommand.CMD_START:
						playerColor = 1 - (Integer)cmd.argument;
						startGame(playerColor);
						postSync();
						break;
					case NetCommand.CMD_MOVE:
						MoveInfo mv = (MoveInfo)cmd.argument;
						rotateMove(mv); // Rotate move info
						postEndResponse(super.move(mv.fromX, mv.fromY, mv.toX, mv.toY));
						postGameMessage("", 0);
						break;
					case NetCommand.CMD_UNDO:
						postAskUndo();
						break;
					case NetCommand.CMD_RENEW:
						postAskRenew();
						break;
					case NetCommand.CMD_DRAW:
						postAskDraw();
						break;
					case NetCommand.CMD_GIVE_UP:
						postGameMessage("tot", 2);
						break;
					case NetCommand.CMD_SYNC_NAME:
						partnerName = (String)cmd.argument;
						ChessApplication.setSetting(remoteAddress, partnerName);
						postPlayerInfo(playerName, partnerName, getPlayer());
						break;
					case NetCommand.CMD_UNDO_RESP:
						if ((Boolean)cmd.argument) {
							undo();
							undo();
							postGameMessage("tot", 1);
							postEndUndo(true);
						} else {
							postEndUndo(false);
							postGameMessage("", 0);
						}
						break;
					case NetCommand.CMD_RENEW_RESP:
						if ((Boolean)cmd.argument) {
							startGame(playerColor);
							postEndRenew(true);
							postPlayerInfo(playerName, partnerName, getPlayer());
							postGameMessage("tot", 0);
						} else {
							postEndRenew(false);
						}
						break;
					case NetCommand.CMD_DRAW_RESP:
						if ((Boolean)cmd.argument) {
							postEndDraw(true);
							postGameMessage("tot", 2);
						} else {
							postEndRenew(false);
						}
						break;						
					}
					cmd = parseCommand(buffer, contentSize);
				}
			}
		} catch (IOException e) {
			if (!offeredToClose) {
				postGameOver();
				postGameMessage("tot", 3);
			}
		}
	}
	
	public void connect(final IConnectListener listener) {
		Thread thread = new Thread(new Runnable() {
			public void run() {
				// Connect to remote server 
				BluetoothDevice device = 
						bluetoothAdapter.getRemoteDevice(remoteAddress);
				try {
					sock = 
							device.createRfcommSocketToServiceRecord(CHESS_UUID);
					sock.connect();
					listener.onCompleted(true);
					
					NetEngine.this.run(false); // Run net engine.
					
				} catch (IOException e) {
					listener.onCompleted(false);
				}
			}
		});
		thread.start();
	}
	
	public void listen(final IAcceptListener listener) {
		Thread thread = new Thread(new Runnable() {
			public void run() {
				try {
					listenSock = bluetoothAdapter.listenUsingRfcommWithServiceRecord(
							"ChineseChess", CHESS_UUID);
					sock = listenSock.accept();
					remoteAddress = sock.getRemoteDevice().getAddress();
					listenSock.close();
					listener.onCompleted(true);
					
					NetEngine.this.run(true); // Run net engine.
					
				} catch (IOException e) {
					listener.onCompleted(false);
				}
			}
		});
		thread.start();
	}
	
	public void stopListen() {
		try {
			listenSock.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public int getVaildAction() {
		return ACTION_UNDO | ACTION_RENEW |	ACTION_DRAW |
				ACTION_GIVEUP | ACTION_RESPONSE;
	}

	public void syncPlayerInfo() {
		postPlayerInfo(playerName, partnerName, getPlayer());
	}
	
	private void sendToPartner(String msg) {
		if (output != null) {
			try {
				output.write(msg.getBytes("UTF-8"));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
				try {
					sock.close();
				} catch (IOException e1) {
				}
			}
		}
	}
	
	@Override
	public synchronized boolean move(int fromX, int fromY, int toX, int toY) {
		boolean result = super.move(fromX, fromY, toX, toY);
		if (!result)
			postGameMessage("tot", 1);
		postPlayerInfo(playerName, partnerName, getPlayer());
		String msg = String.format("ask:move:%1$d%2$d%3$d%4$d\n", 
				fromX, fromY, toX, toY);
		sendToPartner(msg);
		beginResponse();
		return result;
	}

	public void giveUp() {
		postGameMessage("tot", 2);
		sendToPartner("ask:giveup\n");
	}

	public void responseAskUndo(boolean accept) {
		if (accept) {
			undo();
			undo();
		}
		String rsp = String.format(
				"rsp:undo:%1$s\n", Boolean.toString(accept));
		sendToPartner(rsp);
	}

	public void responseAskRenew(boolean accept) {
		if (accept) {
			startGame(playerColor);
			postPlayerInfo(playerName, partnerName, getPlayer());
			postGameMessage("", 0);
		}
		String rsp = String.format(
				"rsp:renew:%1$s\n", Boolean.toString(accept));
		sendToPartner(rsp);
	}

	public void responseAskDraw(boolean accept) {
		if (accept) {
			postGameOver();
			postGameMessage("tot", 2);
		}
		String rsp = String.format(
				"rsp:draw:%1$s\n", Boolean.toString(accept));
		sendToPartner(rsp);
	}	

	public void beginResponse() {
		if (getPlayer() == getDirection()) {
			postGameMessage("tot..", 0);
		} else {
			postGameMessage("tot..", 0);
		}
	}

	public void beginUndo() {
		postGameMessage("tot...", 0);
		String rsp = String.format("ask:undo\n");
		sendToPartner(rsp);
	}

	public void beginRenew() {
		postGameMessage("tot...", 0);
		String rsp = String.format(
				"ask:renew:%1$d\n", playerColor);
		sendToPartner(rsp);
	}

	public void beginDraw() {
		postGameMessage("tot...", 0);
		String rsp = String.format("ask:draw\n");
		sendToPartner(rsp);
	}
}
