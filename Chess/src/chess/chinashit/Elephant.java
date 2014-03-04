package chess.chinashit;

import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.Icon;
import javax.swing.ImageIcon;

public class Elephant extends QuanCo{

	public Elephant (int x,int y,boolean type){
		this.x =x;
		this.y =y;
		this.quando =type;
	}

	@Override
	public void move(int x, int y) {
		// TODO Auto-generated method stub
		int temp1 =(Math.abs(x-this.x));
		int temp2 =(Math.abs(y-this.y));
		boolean canmove=true;
		BanCo b =BanCo.getInstance();
		for(int i= 58;i<=62;i++){
			for(int j= 58;j<=62;j++){
				if(b.getImage().getComponentAt(this.x-i, this.y+j) instanceof QuanCo && x<this.x && y>this.y ){
					canmove =false;
					System.out.println((b.getImage().getComponentAt(this.x-i, this.y+j)));
				}
				else if(b.getImage().getComponentAt(this.x+i, this.y+j) instanceof QuanCo && x>this.x && y>this.y){
					canmove =false;
					System.out.println((b.getImage().getComponentAt(this.x+i, this.y+j)));
				}
				else if(b.getImage().getComponentAt(this.x+i, this.y-j) instanceof QuanCo && x>this.x && y<this.y){
					canmove =false;
					System.out.println((b.getImage().getComponentAt(this.x+i, this.y-j)));
				}
				else if(b.getImage().getComponentAt(this.x-i, this.y-j) instanceof QuanCo && x<this.x && y<this.y){
					canmove =false;
					System.out.println((b.getImage().getComponentAt(this.x-i, this.y-j)));
				}
			}
		}
		
		if(this.quando ==true){
			if(x>=20 && x<=500 && y>=25 && y<=260 &&canmove ){
				if(temp1>=100 && temp1<=120 && temp2>=100 && temp2<=120){
					setLayout(null);
					this.x =x;
					this.y =y;
					this.setBounds(new Rectangle(new Point(this.x, this.y), this.getPreferredSize()));
					this.caneat =true;
				}
			}
		}
		else if(this.quando==false){
			if(x>=20 && x<=500 && y<=540 && y>=310 ){
				if(temp1>=100 && temp1<=120 && temp2>=100 && temp2<=120){
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
			h = new ImageIcon("img\\elephant.gif");
		}
		else {h = new ImageIcon("img\\elephant2.gif");}
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
