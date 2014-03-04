package chess.chinashit;

import javax.swing.Icon;
import javax.swing.JLabel;

public abstract class QuanCo extends JLabel{
	protected int x,y;
	protected Icon hinh;
	protected boolean quando;
	protected boolean caneat;
	protected BanCo board;
	
	public  boolean getType(){
		return this.quando;
	}
	public int Getx(){
		return this.x;
	}
	public int Gety(){
		return this.y;
	}
	public void Setcaneat(){
		this.caneat =false;
	}
	public abstract void draw();
	public abstract void move(int x, int y);
	public abstract void eat(QuanCo co,BanCo banco);
}
