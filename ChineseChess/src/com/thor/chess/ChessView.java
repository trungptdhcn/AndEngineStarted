package com.thor.chess;

import com.libsvg.SvgDrawable;
import com.thor.chess.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;


public class ChessView extends View {
	private Bitmap[] svgs = null;
	private Bitmap blackChess = null;
	private Bitmap redChess = null;
	
	private final static PaintFlagsDrawFilter aliasFilter = 
			new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | 
					Paint.FILTER_BITMAP_FLAG);
	
	private final Paint linePaint = new Paint();
	private final Paint movePaint = new Paint();
	private final Paint selectPaint = new Paint();
	
	// Instance variables
	private Engine engine = null;
	private int cellSize = 0;
	private int margin = 0;
	private int selectX = 0;
	private int selectY = 0;
	private boolean selected = false;
	private byte[] board = new byte[90];
	private MoveInfo lastMove = null;
	private boolean gameOver = false;
	private boolean nightMode = false;
	
	
	public boolean isGameOver() {
		return (engine.isGameOver() || gameOver);
	}

	public ChessView(Context context) {
		super(context);
		init();
	}
	
	public void setNightMode(boolean nightMode) {
		this.nightMode = nightMode;
		invalidate();
	}
	
	public boolean getNightMode() {
		return nightMode;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
	    int parentWidth = MeasureSpec.getSize(widthMeasureSpec);
	    int myHeight = (int) (parentWidth / 9 * 10);
	    super.onMeasure(widthMeasureSpec, 
	    		MeasureSpec.makeMeasureSpec(myHeight, MeasureSpec.EXACTLY));
	}

	public ChessView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	
	private static Bitmap loadBmp(int id, int width, int height) {
		SvgDrawable svg = SvgDrawable.getDrawable(
				ChessApplication.getAppContext().getResources(), id);
		svg.setBounds(0, 0, width, height);
		Bitmap bmp = svg.getBitmap();
		return bmp;
	}
	
	public boolean onTouch(MotionEvent event) {
		if (engine == null)
			return true;
		if (event.getActionMasked() != MotionEvent.ACTION_DOWN)
			return false;
		if (gameOver || engine.isGameOver() || 
				engine.getDirection() != engine.getPlayer())
			return true;
		
		float pressX = event.getX();
		float pressY = event.getY();
		int x = -1;
		for (int i = 0; i < 9; i++) {
			if (Math.abs(pressX - (margin + i * cellSize)) < cellSize * 0.485f) {
				x = i;
				break; 
			}
		}
		if (x == -1)
			return true;
		
		int y = -1; 
		for (int i = 0; i < 10; i++) {
			if (Math.abs(pressY - (margin + i * cellSize)) < cellSize * 0.485f) {
				y = i;
				break; 
			}
		}
		if (y == -1)
			return true;
		
		if (engine.getChessmanColor(x, y) == engine.getDirection()) {
			selectX = x;
			selectY = y;
			selected = true;
			invalidate();
		} else if (selected){
			boolean result = engine.move(selectX, selectY, x, y);
			if (result) {
				selected = false;
				sync();
			}
		}
		return true;
	}
	
	private void init() {
		linePaint.setColor(Color.GRAY);
		linePaint.setStyle(Style.STROKE);
		movePaint.setColor(Color.argb(100, 0, 0, 255));
		movePaint.setStrokeWidth(10);
		movePaint.setStyle(Style.STROKE);
		movePaint.setPathEffect(new DashPathEffect(new float[] {20,5}, 0));
		selectPaint.setStyle(Style.FILL);
		selectPaint.setColor(Color.argb(170, 200, 100, 0));
		if (isInEditMode())
			return;

		setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				return ChessView.this.onTouch(event);
			}
		});
	}
	
	public void startGame(Engine engine) {
		if (this.engine != null) {
			// Release memory that allocated by jni.
			this.engine.dispose();
		}
		this.engine = engine;
		if (engine != null) {
			gameOver = false;
			((IEngine)engine).syncPlayerInfo();
			sync();
	    	((IEngine)engine).beginResponse();
		} else {
			board = new byte[90];
			lastMove = null;
		}
	}
	
	public void restart() {
		selected = false;
		if (this.engine != null) {
			gameOver = false;
			((IEngine)engine).syncPlayerInfo();
			sync();
			((IEngine)engine).beginResponse();
		}
	}
	
	public void stopGame() {
		gameOver = true;
	}
	
	public void finishGame() {
		gameOver = true;
		if (this.engine != null) {
			// Release memory that allocated by jni.
			this.engine.dispose();
		}
		this.engine = null;		
	}
	
	public void sync() {
		engine.syncBoard(board);
		lastMove = engine.getLastMove();
		invalidate();
	}

	public void dispose() {
		if (engine != null) {
			engine.dispose();
		}
	}

	private void drawCross(Canvas canvas, int x, int y, int type) {
		int lineLen = cellSize / 6;
		int space = 1 + cellSize / 20;
		if ((type & 1) > 0) { // Left point
			canvas.drawLine(margin + cellSize * x - space - lineLen, margin
					+ cellSize * y - space, margin + cellSize * x - space,
					margin + cellSize * y - space, linePaint);
			canvas.drawLine(margin + cellSize * x - space, margin + cellSize
					* y - space, margin + cellSize * x - space, margin
					+ cellSize * y - space - lineLen, linePaint);
			canvas.drawLine(margin + cellSize * x - space - lineLen, margin
					+ cellSize * y + space, margin + cellSize * x - space,
					margin + cellSize * y + space, linePaint);
			canvas.drawLine(margin + cellSize * x - space, margin + cellSize
					* y + space, margin + cellSize * x - space, margin
					+ cellSize * y + space + lineLen, linePaint);
		}
		if ((type & 2) > 0) { // Right point
			canvas.drawLine(margin + cellSize * x + space + lineLen, margin
					+ cellSize * y - space, margin + cellSize * x + space,
					margin + cellSize * y - space, linePaint);
			canvas.drawLine(margin + cellSize * x + space, margin + cellSize
					* y - space, margin + cellSize * x + space, margin
					+ cellSize * y - space - lineLen, linePaint);
			canvas.drawLine(margin + cellSize * x + space + lineLen, margin
					+ cellSize * y + space, margin + cellSize * x + space,
					margin + cellSize * y + space, linePaint);
			canvas.drawLine(margin + cellSize * x + space, margin + cellSize
					* y + space, margin + cellSize * x + space, margin
					+ cellSize * y + space + lineLen, linePaint);
		}
	}
	
	private void drawBoard(Canvas canvas) {
		margin = cellSize / 2;
		if (nightMode)
			canvas.drawColor(Color.argb(255, 50, 50, 50));
		else
			canvas.drawColor(Color.argb(255, 255, 240, 190));
		
		// Draw v-lines
		for (int i = 0; i <= 8; i++) {
			if (i == 0 || i == 8) {
				canvas.drawLine(margin + cellSize * i, margin, 
						margin + cellSize * i, margin + cellSize * 9, linePaint);
			}
			else {
				canvas.drawLine(margin + cellSize * i, margin, 
						margin + cellSize * i, margin + cellSize * 4, linePaint);
				canvas.drawLine(margin + cellSize * i, margin + cellSize * 5, 
						margin + cellSize * i, margin + cellSize * 9, linePaint);
			}
			// Draw chess put point cross lines.
			if (i == 0)
			{
				drawCross(canvas, i, 3, 2);
				drawCross(canvas, i, 6, 2);
				drawCross(canvas, i + 1, 2, 3);
				drawCross(canvas, i + 1, 7, 3);
			}
			else if (i == 6)
			{
				drawCross(canvas, i + 2, 3, 1);
				drawCross(canvas, i + 2, 6, 1);
				drawCross(canvas, i + 1, 2, 3);
				drawCross(canvas, i + 1, 7, 3);
			}
			else if (i == 1 || i == 3 || i == 5)
			{
				drawCross(canvas, i + 1, 3, 3);
				drawCross(canvas, i + 1, 6, 3);
			}	
		}

		// Draw h-lines
		for (int j = 0; j <= 9; j++) {
			canvas.drawLine(margin, margin + cellSize * j, 
					margin + cellSize * 8, margin + cellSize * j, linePaint);
		}
		// Draw cross-lines
		canvas.drawLine(margin + cellSize * 3, margin, 
				margin + cellSize * 5, margin + cellSize * 2, linePaint);
		canvas.drawLine(margin + cellSize * 5, margin, 
				margin + cellSize * 3, margin + cellSize * 2, linePaint);
		canvas.drawLine(margin + cellSize * 3, margin + cellSize * 7, 
				margin + cellSize * 5, margin + cellSize * 9, linePaint);
		canvas.drawLine(margin + cellSize * 5, margin + cellSize * 7, 
				margin + cellSize * 3, margin + cellSize * 9, linePaint);
		
		linePaint.setStrokeWidth(3);
		// Draw outer line
		canvas.drawRect(margin - 3, margin - 3, 
				margin + cellSize * 8 + 3, 
				margin + cellSize * 9 + 3, linePaint);
		linePaint.setStrokeWidth(1);
	}
	
	private Bitmap getBitmap(byte chessman) {
		byte type = Engine.getChessmanType(chessman);
		int color = Engine.getChessmanColor(chessman);
		if (type == Engine.CHE)
			return svgs[0];
		else if (type == Engine.MA)
			return svgs[1];
		else if (type == Engine.PAO)
			return svgs[2];
		else if (type == Engine.BING && color == 0)
			return svgs[3];
		else if (type == Engine.BING && color == 1)
			return svgs[4];
		else if (type == Engine.SHUAI && color == 0)
			return svgs[5];
		else if (type == Engine.SHUAI && color == 1)
			return svgs[6];
		else if (type == Engine.SHI && color == 0)
			return svgs[7];
		else if (type == Engine.SHI && color == 1)
			return svgs[8];
		else if (type == Engine.XIANG && color == 0)
			return svgs[9];
		else if (type == Engine.XIANG && color == 1)
			return svgs[10];
		else
			return null;
	}
	
	private void drawChessmen(
			Canvas canvas, int x, int y, byte chessman) {
		int cx = margin + x * cellSize;
		int cy = margin + y * cellSize;
		if (chessman < 16) {
			canvas.drawBitmap(redChess, 
					cx - (int)(cellSize * 0.97) / 2, 
					cy - (int)(cellSize * 0.97) / 2, null);
		} else {
			canvas.drawBitmap(blackChess, 
					cx - (int)(cellSize * 0.97) / 2, 
					cy - (int)(cellSize * 0.97) / 2, null);
		}
		
		Bitmap txtBmp = getBitmap(chessman);
		canvas.drawBitmap(txtBmp, 
				cx - (int)(cellSize * 0.8) / 2, 
				cy - (int)(cellSize * 0.8) / 2, null);
	}
	
	private void drawSelect(Canvas canvas) {
		if (selected && engine.getChessmanColor(selectX, selectY) == 
				engine.getDirection()) {
			int cx = margin + selectX * cellSize;
			int cy = margin + selectY * cellSize;			
			canvas.drawRoundRect(new RectF(cx - cellSize / 2 - 2,
					cy - cellSize / 2 - 2,
					cx + cellSize / 2 + 2,
					cy + cellSize / 2 + 2), 4, 4, selectPaint);
		}
	}
	
	@Override
	protected void onDraw(final Canvas canvas) {
		super.onDraw(canvas);
		
		cellSize = this.getWidth() / 9;
		int txtSize = (int)(cellSize * 0.8f);
		int outSize = (int)(cellSize * 0.97f);
		if (!isInEditMode()) {
			if (svgs == null) {
				svgs = new Bitmap[] {
						loadBmp(R.drawable.che, txtSize, txtSize),
						loadBmp(R.drawable.ma, txtSize, txtSize),
						loadBmp(R.drawable.pao, txtSize, txtSize),
						loadBmp(R.drawable.bing, txtSize, txtSize),
						loadBmp(R.drawable.zu, txtSize, txtSize),
						loadBmp(R.drawable.shuai, txtSize, txtSize),
						loadBmp(R.drawable.jiang, txtSize, txtSize),
						loadBmp(R.drawable.r_shi, txtSize, txtSize),
						loadBmp(R.drawable.b_shi, txtSize, txtSize),
						loadBmp(R.drawable.r_xiang, txtSize, txtSize),
						loadBmp(R.drawable.b_xiang, txtSize, txtSize) };
			}
			if (blackChess == null)
				blackChess = loadBmp(R.drawable.black, outSize, outSize);
			if (redChess == null)
				redChess = loadBmp(R.drawable.red, outSize, outSize);
		}
		// Draw chess board.
		drawBoard(canvas);
		
		if (!isInEditMode())
			canvas.setDrawFilter(aliasFilter);

		drawSelect(canvas);
		
		// Draw last move
		if (lastMove != null) {
			int fromX = margin + lastMove.fromX * cellSize;
			int fromY = margin + lastMove.fromY * cellSize;
			int toX = margin + lastMove.toX * cellSize;
			int toY = margin + lastMove.toY * cellSize;
			if (nightMode) {
				movePaint.setColor(Color.argb(150, 180, 180, 180));
				canvas.drawLine(fromX, fromY, toX, toY, movePaint);
			} else {
				movePaint.setColor(Color.argb(100, 0, 0, 255));
				canvas.drawLine(fromX, fromY, toX, toY, movePaint);
			}
		}
		
		// Draw all chessmen.
		for (int y = 0; y < 10; y++) {
			for (int x = 0; x < 9; x++) {
				byte value = board[9 * y + x];
				if (value != 0)
					drawChessmen(canvas, x, y, value);
			}
		}
	}
}
