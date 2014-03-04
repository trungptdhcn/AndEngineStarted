package chess.chinashit;

import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.Icon;
import javax.swing.ImageIcon;

public class Tot extends QuanCo{

	public Tot(int x,int y, boolean type){
		this.x =x;
		this.y =y;
		this.quando = type;
		this.caneat =false;
	}
	@Override
	public void move(int x, int y) {
		// TODO Auto-generated method stub
		if(this.quando == true){
			if(x<=505 && x>=10 && y>=195 && y<=540){
				if(y>=205 && y<= 260  && x<=this.x+25 && x>=this.x-25){
					this.y =this.y+55;
					setLayout(null);
					this.setBounds(new Rectangle(new Point(this.x, this.y), this.getPreferredSize()));
					this.caneat =true;
				}
				else if(y>260)
				{
					if(x-this.x<=80 && x-this.x>=50 && y<=this.y+20 && y>=this.y-20){
						setLayout(null);
						this.x = this.x+55;
						this.setBounds(new Rectangle(new Point(this.x, this.y), this.getPreferredSize()));
						this.caneat =true;
					}
					else if(this.x-x<=80 && this.x-x>=45 && y<=this.y+20 && y>=this.y-20){
						setLayout(null);
						this.x = this.x-55;
						this.setBounds(new Rectangle(new Point(this.x, this.y), this.getPreferredSize()));
						this.caneat =true;
					}

					else if(y-this.y<=80 && y-this.y>=50 && x<=this.x+20 && x>=this.x-20){
						setLayout(null);
						this.y = this.y+55;
						this.setBounds(new Rectangle(new Point(this.x, this.y), this.getPreferredSize()));
						this.caneat =true;
					}
				}
			}
		}
		else if(this.quando == false){
			if(x<=505 && x>=10 && y>=25 && y<=375){
				if( y>=310 && y<= 370 && x<=this.x+25 && x>=this.x-35){
					this.y =this.y-55;
					setLayout(null);
					this.setBounds(new Rectangle(new Point(this.x, this.y), this.getPreferredSize()));
					this.caneat =true;
				}
				else if(y<310)
				{
					if(x-this.x<=80 && x-this.x>=45 && y<=this.y+25 && y>=this.y-25){
						setLayout(null);
						this.x = this.x+55;
						this.setBounds(new Rectangle(new Point(this.x, this.y), this.getPreferredSize()));
						this.caneat =true;
					}
					else if(this.x-x<=80 && this.x-x>=45 && y<=this.y+20 && y>=this.y-20){
						setLayout(null);
						this.x = this.x-55;
						this.setBounds(new Rectangle(new Point(this.x, this.y), this.getPreferredSize()));
						this.caneat =true;
					}

					else if(this.y-y<=80 && this.y-y>=35 && x<=this.x+20 && x>=this.x-20){
						setLayout(null);
						this.y = this.y-60;
						this.setBounds(new Rectangle(new Point(this.x, this.y), this.getPreferredSize()));
						this.caneat =true;
					}
				}
			}
		}
	}

	@Override
	public void draw() {
		// TODO Auto-generated method stub
		Icon h = this.hinh;
		if (this.quando==true){
			h = new ImageIcon("img\\tot.gif");
		}
		else {h = new ImageIcon("img\\tot2.gif");}
		this.setIcon(h);
		this.setBounds(new Rectangle(new Point(this.x, this.y), this.getPreferredSize()));
	}

	@Override
	public void eat(QuanCo co,BanCo banco) {
		// TODO Auto-generated method stub
		this.move(co.x, co.y);
		if(this.caneat==true){
			banco.getImage().remove(co);
			banco.getImage().repaint();
			banco.getImage().validate();
		}
	}

}
