package chess.chinashit;

import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.*;

public class Tuong extends QuanCo {

	public Tuong(int x,int y, boolean type){
		this.x =x;
		this.y =y;
		this.quando = type;
		this.caneat =false;
	}
	@Override
	public void move(int x, int y) {
		// TODO Auto-generated method stub
		if(this.quando == true){ 
			if(x<=315 && x>=190 && y<=145 && y>=25){
				if(x-this.x<=70 && x-this.x>=50 && y<=this.y+10 && y>=this.y-10){
					setLayout(null);
					this.x = this.x+60;
					this.setBounds(new Rectangle(new Point(this.x, this.y), this.getPreferredSize()));
					this.caneat =true;
				}
				else if(this.x-x<=70 && this.x-x>=50 && y<=this.y+10 && y>=this.y-10){
					setLayout(null);
					this.x = this.x-60;
					this.setBounds(new Rectangle(new Point(this.x, this.y), this.getPreferredSize()));
					this.caneat =true;
				}

				else if(y-this.y<=70 && y-this.y>=50 && x<=this.x+10 && x>=this.x-10){
					setLayout(null);
					this.y = this.y+60;
					this.setBounds(new Rectangle(new Point(this.x, this.y), this.getPreferredSize()));
					this.caneat =true;
				}
				else if(this.y-y<=70 && this.y-y>=50 && x<=this.x+10 && x>=this.x-10){
					setLayout(null);
					this.y = this.y-60;
					this.setBounds(new Rectangle(new Point(this.x, this.y), this.getPreferredSize()));
					this.caneat =true;
				}
				else this.setBounds(new Rectangle(new Point(this.x, this.y), this.getPreferredSize()));
				this.caneat =true;
			}
		}
		else if(this.quando == false){
			if(x<=315 && x >= 190 && y>=425 && y <= 542){
				if(x-this.x<=70 && x-this.x>=50 && y<=this.y+10 && y>=this.y-10){
					setLayout(null);
					this.x = this.x+60;
					this.setBounds(new Rectangle(new Point(this.x, this.y), this.getPreferredSize()));
					this.caneat =true;
				}
				else if(this.x-x<=70 && this.x-x>=50 && y<=this.y+10 && y>=this.y-10){
					setLayout(null);
					this.x = this.x-60;
					this.setBounds(new Rectangle(new Point(this.x, this.y), this.getPreferredSize()));
					this.caneat =true;
				}

				else if(y-this.y<=70 && y-this.y>=50 && x<=this.x+10 && x>=this.x-10){
					setLayout(null);
					this.y = this.y+60;
					this.setBounds(new Rectangle(new Point(this.x, this.y), this.getPreferredSize()));
					this.caneat =true;
				}
				else if(this.y-y<=70 && this.y-y>=50 && x<=this.x+10 && x>=this.x-10){
					setLayout(null);
					this.y = this.y-60;
					this.setBounds(new Rectangle(new Point(this.x, this.y), this.getPreferredSize()));
					this.caneat =true;
				}
				else this.setBounds(new Rectangle(new Point(this.x, this.y), this.getPreferredSize()));
				this.caneat =true;
			}
		}
	}

	@Override
	public void draw() {
		// TODO Auto-generated method stub
		Icon h = this.hinh;
		if (this.quando==true){
			h = new ImageIcon("img\\tuong.gif");
		}
		else {h = new ImageIcon("img\\tuong2.gif");}
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
