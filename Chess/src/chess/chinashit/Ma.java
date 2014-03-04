package chess.chinashit;

import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.Icon;
import javax.swing.ImageIcon;

public class Ma extends QuanCo {

	public Ma (int x,int y, boolean type){
		this.x =x;
		this.y =y;
		this.quando =type;
	}
	public void move(int x, int y){
		int temp1 =(Math.abs(x-this.x));
		int temp2 =(Math.abs(y-this.y));
		BanCo b = BanCo.getInstance();
		boolean canmove =true;
		for(int i= 58;i<=62;i++){
			if(b.getImage().getComponentAt(this.x, this.y-i) instanceof QuanCo &&  this.y-y>=115 && this.y-y<=120){
				canmove =false;
			}
			else if(b.getImage().getComponentAt(this.x, this.y+i) instanceof QuanCo && y-this.y>=115 && y-this.y<=120){
				canmove =false;
			}
			else if(b.getImage().getComponentAt(this.x+i, this.y) instanceof QuanCo && x-this.x>=115 &&x-this.x<=120 ){
				canmove =false;
				System.out.println(b.getImage().getComponentAt(this.x+i, this.y));
			}
			else if(b.getImage().getComponentAt(this.x-i, this.y) instanceof QuanCo && this.x -x>=115 && this.x -x<=120){
				canmove =false;
				System.out.println(b.getImage().getComponentAt(this.x-i, this.y) );
			}
		}
		if(x<=510 && x>=10 && y>=20 && y<=545 &&canmove ){
			if(temp1>50 && temp1<70 && temp2>100 && temp2< 125 || (temp1>100 && temp1<125 && temp2>50 && temp2<70) ){
				setLayout(null);
				this.x =x;
				this.y =y;
				this.setBounds(new Rectangle(new Point(this.x, this.y), this.getPreferredSize()));
				this.caneat =true;
			}
		}
	}

	@Override
	public void draw() {
		// TODO Auto-generated method stub
		Icon h = this.hinh;
		if (this.quando==true){
			h = new ImageIcon("img\\ma.gif");
		}
		else {h = new ImageIcon("img\\ma2.gif");}
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
			banco.getImage().validate();
		}
	}
}
