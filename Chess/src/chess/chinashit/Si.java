package chess.chinashit;

import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.Icon;
import javax.swing.ImageIcon;

public class Si extends QuanCo {
	public Si (int x,int y,boolean type){
		this.x =x;
		this.y =y;
		this.quando =type;
		this.caneat =false;
	}

	public void move(int x, int y){
		int temp1 =(Math.abs(x-this.x));
		int temp2 =(Math.abs(y-this.y));
		if(this.quando == true){ 
			if(x<=315 && x>=190 && y<=145 && y>=25){
				if(temp1>=50 && temp1<=70 && temp2>=50 && temp2<=70){
					setLayout(null);
					this.x =x;
					this.y =y;
					this.setBounds(new Rectangle(new Point(this.x, this.y), this.getPreferredSize()));
					this.caneat =true;
				}
			}
		}
		else if(this.quando == false){
			if(x<=315 && x >= 190 && y>=425 && y <= 542){
				if(temp1>=50 && temp1<=70 && temp2>=50 && temp2<=70){
					setLayout(null);
					this.x =x;
					this.y =y;
					this.setBounds(new Rectangle(new Point(this.x, this.y), this.getPreferredSize()));
					this.caneat =true;
				}
			}
		}
	}

	@Override
	public void draw() {
		// TODO Auto-generated method stub
		Icon h = this.hinh;
		if (this.quando==true){
			h = new ImageIcon("img\\si.gif");
		}
		else {h = new ImageIcon("img\\si2.gif");}
		this.setIcon(h);
		this.setBounds(new Rectangle(new Point(this.x, this.y), this.getPreferredSize()));
	}

	@Override
	public void eat(QuanCo co,BanCo banco) {
		// TODO Auto-generated method stub
		this.move(co.x, co.y);
		if(this.caneat){
			banco.getImage().remove(co);
			banco.getImage().repaint();
			banco.getImage().validate();}
	  }
}
