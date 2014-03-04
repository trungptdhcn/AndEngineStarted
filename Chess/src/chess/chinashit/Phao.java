package chess.chinashit;

import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.Icon;
import javax.swing.ImageIcon;

public class Phao extends QuanCo{

	public Phao (int x ,int y,boolean type){
		this.x =x;
		this.y =y;
		this.quando =type;
		this.caneat =false;
	}
	public void move(int x, int y){
		int temp1 =(Math.abs(x-this.x));
		int temp2 =(Math.abs(y-this.y));
		BanCo b =BanCo.getInstance();
		boolean canmove =true;
		int count =0;
		if(x>this.x && y<=this.y+5 && y>= this.y -5){
			for(int i=this.x+55;i<x;i++){
				if(b.getImage().getComponentAt(i, y+5) instanceof QuanCo ){
					count++;
					
				}
				if((b.getImage().getComponentAt(i, y+5) instanceof QuanCo ) && !(b.getImage().getComponentAt(x, y) instanceof QuanCo) || count>55 ){
					canmove=false;
				}
			}
			System.out.println(count);
		}
		else if(x<this.x && y<=this.y+10 && y>= this.y -10 ){
			for(int i=x+55;i<this.x;i++){
				if(b.getImage().getComponentAt(i, y+5) instanceof QuanCo ){
					count++;
				}
				if((b.getImage().getComponentAt(i, y+5) instanceof QuanCo) && !(b.getImage().getComponentAt(x, y) instanceof QuanCo)|| count>55){
					canmove=false;
				}
			}
			System.out.println(count);
		}
		else if(y>this.y && x<=this.x+10 && x>= this.x -10){
			for(int i=this.y+55;i<y;i++){
				if(b.getImage().getComponentAt(x+5, i) instanceof QuanCo ){
					count++;
				}
				if((b.getImage().getComponentAt(x+5, i) instanceof QuanCo) && !(b.getImage().getComponentAt(x, y) instanceof QuanCo) || count>55){
					canmove=false;
				}

			}
			System.out.println(count);
		}
		else if(y<this.y &&x<=this.x+10 && x>= this.x -10){
			for(int i=y+55;i<this.y;i++){
				if(b.getImage().getComponentAt(x+5, i) instanceof QuanCo ){
					count++;
				}
				if((b.getImage().getComponentAt(x+5, i) instanceof QuanCo) && !(b.getImage().getComponentAt(x, y) instanceof QuanCo) || count>55){
					canmove=false;
				}
			}
			System.out.println(count);
		}

			if(x<=510 && x>=10 && y>=20 && y<=545 && canmove){
				if(temp1 % 60 <=60 && temp1 %60 >=0 && y<=this.y+5 && y>= this.y -5 ){	
					setLayout(null);
					this.x =x;
					this.setBounds(new Rectangle(new Point(this.x, this.y), this.getPreferredSize()));
					if(count >0 && count<=55)
					this.caneat=true;
				}
				else if(temp2 % 60 <=60 && temp2 %60 >=0 && x<=this.x+5 && x>= this.x -5){
					setLayout(null);
					this.y =y;
					this.setBounds(new Rectangle(new Point(this.x, this.y), this.getPreferredSize()));
					if(count >0 && count<=55)
					this.caneat=true;
				}
			}
	}

	@Override
	public void draw() {
		// TODO Auto-generated method stub
		Icon h = this.hinh;
		if (this.quando==true){
			h = new ImageIcon("img\\phao.gif");
		}
		else {h = new ImageIcon("img\\phao2.gif");}
		this.setIcon(h);
		this.setBounds(new Rectangle(new Point(this.x, this.y), this.getPreferredSize()));
	}

	@Override
	public void eat(QuanCo co,BanCo banco) {
		// TODO Auto-generated method stub
		this.move(co.x, co.y);
		if(this.caneat ==true && this.getType()!= co.getType()){
			banco.getImage().remove(co);
			banco.getImage().repaint();
			banco.getImage().validate();
		}
		
	};
}
