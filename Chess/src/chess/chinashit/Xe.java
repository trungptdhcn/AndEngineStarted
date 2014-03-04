package chess.chinashit;

import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.Icon;
import javax.swing.ImageIcon;

public class Xe extends QuanCo{
	public Xe(int x,int y,boolean type){
		this.x =x;
		this.y =y;
		this.quando =type;
	}
	public void move(int x , int y){
		int temp1 =(Math.abs(x-this.x));
		int temp2 =(Math.abs(y-this.y));
		if(x<=510 && x>=10 && y>=20 && y<=545  ){
			if(temp1 % 60 <=60 && temp1 %60 >=0 && y<=this.y+5 && y>= this.y -5 ){	
				setLayout(null);
				this.x =x;
				this.setBounds(new Rectangle(new Point(this.x, this.y), this.getPreferredSize()));

			}
			else if(temp2 % 60 <=60 && temp2 %60 >=0 && x<=this.x+5 && x>= this.x -5){
				setLayout(null);
				this.y =y;
				this.setBounds(new Rectangle(new Point(this.x, this.y), this.getPreferredSize()));

			}
		}
	}

	@Override
	public void draw() {
		// TODO Auto-generated method stub
		Icon h = this.hinh;
		if (this.quando==true){
			h = new ImageIcon("img\\xe.gif");
		}
		else {h = new ImageIcon("img\\xe2.gif");}
		this.setIcon(h);
		this.setBounds(new Rectangle(new Point(this.x, this.y), this.getPreferredSize()));
	}

	@Override
	public void eat(QuanCo co,BanCo banco) {
		// TODO Auto-generated method stub
		this.move(co.x, co.y);
		//if(this.caneat ==true && this.getType()!= co.getType()){
			banco.getImage().remove(co);
			banco.getImage().repaint();
			banco.getImage().validate();
		//}
	}
}
