package chess.chinashit;


import java.awt.event.*;

import javax.swing.*;

public class BanCo extends JPanel implements MouseListener {
	private JLabel image= new JLabel(new ImageIcon("img\\board.gif")) ;
	private static BanCo InstanceBanCo;
	private int isChoose =0;
	private boolean turnRed =true,turnBlack = false;
	private QuanCo tuong,tuong2,totdo1,totdo2,totdo3,totdo4,totdo5,
	totden1,totden2,totden3,totden4,totden5, phaodo1,phaodo2,phaoden1,phaoden2,
	sido1,sido2,siden1,siden2,elephantdo1,elephantdo2,elephantden1,elephantden2,
	mado1,mado2,maden1,maden2,xedo1,xedo2,xeden1,xeden2;
	
	
	private BanCo(){
		this.add(this.image);
		this.tuong = new Tuong (250,25,true);
		this.tuong.draw();
		tuong2=new Tuong(250,540,false);
		tuong2.draw();
		//quan do
		this.totdo1 = new Tot(250,200,true);
		this.totdo1.draw();
		this.totdo2 = new Tot (370,200,true);
		this.totdo2.draw();
		this.totdo3 = new Tot (485,200,true);
		this.totdo3.draw();
		this.totdo4 = new Tot (140,200,true);
		this.totdo4.draw();
		this.totdo5 = new Tot (20,200,true);
		this.totdo5.draw();
		//quan phao do
		this.phaodo1 = new Phao (80,140,true);
		this.phaodo1.draw();
		this.phaodo2 = new Phao (425,140,true);
		this.phaodo2.draw();
		//quan si do
		this.sido1 =new Si (310,25,true);
		this.sido1.draw();
		this.sido2 = new Si(190,25,true);
		this.sido2.draw();
		//quan elephant do
		this.elephantdo1 = new Elephant(370,25,true);
		this.elephantdo1.draw();
		this.elephantdo2 = new Elephant(135,25,true);
		this.elephantdo2.draw();
		//quan ma do
		this.mado1 = new Ma(425,25,true);
		this.mado1.draw();
		this.mado2 = new Ma(80,25,true);
		this.mado2.draw();
		//quan xe do
		this.xedo1 = new Xe(20,25,true);
		this.xedo1.draw();
		this.xedo2 =new Xe(485,25,true);
		this.xedo2.draw();
		// quan xe den
		this.xeden1 = new Xe(20,540,false);
		this.xeden1.draw();
		this.xeden2 =new Xe(485,540,false);
		this.xeden2.draw();
		//quan ma den
		this.maden1 = new Ma(425,540,false);
		this.maden1.draw();
		this.maden2 = new Ma(80,540,false);
		this.maden2.draw();
		//quan elephant den
		this.elephantden1 =new Elephant(370,540,false);
		this.elephantden1.draw();
		this.elephantden2 = new Elephant(135,540,false);
		this.elephantden2.draw();
		//quan si den 
		this.siden1 =new Si(310,540,false);
		this.siden1.draw();
		this.siden2 =new Si(190,540,false);
		this.siden2.draw();
		//quan phao den
		this.phaoden1 = new Phao(80,430,false);
		this.phaoden1.draw();
		this.phaoden2 = new Phao(425,430,false);
		this.phaoden2.draw();
		//quan den
		this.totden1 = new Tot(250,370,false);
		this.totden1.draw();
		this.totden2 = new Tot(370,370,false);
		this.totden2.draw();
		this.totden3 = new Tot(485,370,false);
		this.totden3.draw();
		this.totden4 = new Tot(135,370,false);
		this.totden4.draw();
		this.totden5 = new Tot(20,370,false);
		this.totden5.draw();

		this.image.add(tuong);
		this.image.add(tuong2);
		//quan do
		this.image.add(totdo1);
		this.image.add(totdo2);
		this.image.add(totdo3);
		this.image.add(totdo4);
		this.image.add(totdo5);
		//phao do
		this.image.add(phaodo1);
		this.image.add(phaodo2);
		//si do
		this.image.add(sido1);
		this.image.add(sido2);
		//elephant do
		this.image.add(elephantdo1);
		this.image.add(elephantdo2);
		//ma do
		this.image.add(mado1);
		this.image.add(mado2);
		// xe do
		this.image.add(xedo1);
		this.image.add(xedo2);
		//xe den
		this.image.add(xeden1);
		this.image.add(xeden2);
		//ma den
		this.image.add(maden1);
		this.image.add(maden2);
		//elephant den
		this.image.add(elephantden1);
		this.image.add(elephantden2);
		//si den
		this.image.add(siden1);
		this.image.add(siden2);
		//phao den
		this.image.add(phaoden1);
		this.image.add(phaoden2);
		//quan den
		this.image.add(totden1);
		this.image.add(totden2);
		this.image.add(totden3);
		this.image.add(totden4);
		this.image.add(totden5);

		this.image.addMouseListener(this);
		this.tuong.addMouseListener(this);
		this.tuong2.addMouseListener(this);
		//quan do
		this.totdo1.addMouseListener(this);
		this.totdo2.addMouseListener(this);
		this.totdo3.addMouseListener(this);
		this.totdo4.addMouseListener(this);
		this.totdo5.addMouseListener(this);
		//phao do
		this.phaodo1.addMouseListener(this);
		this.phaodo2.addMouseListener(this);
		//si do
		this.sido1.addMouseListener(this);
		this.sido2.addMouseListener(this);
		//elephant do
		this.elephantdo1.addMouseListener(this);
		this.elephantdo2.addMouseListener(this);
		//ma do 
		this.mado1.addMouseListener(this);
		this.mado2.addMouseListener(this);
		//xe do
		this.xedo1.addMouseListener(this);
		this.xedo2.addMouseListener(this);
		//xe den
		this.xeden1.addMouseListener(this);
		this.xeden2.addMouseListener(this);
		//ma den
		this.maden1.addMouseListener(this);
		this.maden2.addMouseListener(this);
		//elephant den
		this.elephantden1.addMouseListener(this);
		this.elephantden2.addMouseListener(this);
		//si den
		this.siden1.addMouseListener(this);
		this.siden2.addMouseListener(this);
		//phao den
		this.phaoden1.addMouseListener(this);
		this.phaoden2.addMouseListener(this);
		//quan den
		this.totden1.addMouseListener(this);
		this.totden2.addMouseListener(this);
		this.totden3.addMouseListener(this);
		this.totden4.addMouseListener(this);
		this.totden5.addMouseListener(this);
		
	}
	public static  BanCo getInstance(){
		if (InstanceBanCo==null)
		{return InstanceBanCo = new BanCo();}

		else return InstanceBanCo;
	}
	public JLabel getImage(){
		return this.image;
	}
	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		//victory condition
		if(!this.tuong.isShowing()){
			JOptionPane.showMessageDialog(this,"Quan den thang");
			System.exit(0);
		}
		else if(!this.tuong2.isShowing()){
			JOptionPane.showMessageDialog(this,"Quan do thang");
			System.exit(0);
		}
		//action tuong1
		if(e.getSource() == this.tuong && turnRed){
			this.tuong = (QuanCo) e.getSource();
			System.out.println("chon tuong");
			this.isChoose=1;

		}

		if(e.getSource() == this.image && this.isChoose==1){

			System.out.println("chon diem");
			this.tuong.move(e.getX()-25,e.getY()-25);
			this.isChoose=0;
			this.turnRed = false;
			this.turnBlack = true;
			this.tuong.Setcaneat();
		}
		if( e.getSource() instanceof QuanCo && e.getSource()!=this.tuong && this.isChoose==1){
			System.out.println("Co co");
			if(!e.getSource().equals(this.tuong.getType())){
				this.tuong.eat((QuanCo) e.getSource(), this);
			}
			this.turnRed = false;
			this.turnBlack = true;
			this.isChoose=0;
		}
		//action tuong2
		else if(e.getSource() == this.tuong2 && turnBlack){
			this.tuong2 = (QuanCo) e.getSource();
			System.out.println("chon tuong");
			this.isChoose=2;

		}
		if(e.getSource() == this.image && this.isChoose==2 ){
			System.out.println("chon diem");
			this.tuong2.move(e.getX()-25,e.getY()-25);
			this.isChoose =0;
			this.turnBlack =false;
			this.turnRed = true;
			this.tuong2.Setcaneat();
		}
		if( e.getSource() instanceof QuanCo && e.getSource()!=this.tuong2 && this.isChoose==2){
			System.out.println("Co co");
			if(!e.getSource().equals(this.tuong2.getType())){
				this.tuong2.eat((QuanCo) e.getSource(), this);
			}
			this.isChoose=0;
			this.turnBlack =false;
			this.turnRed = true;
		}
		//action totdo1
		else if(e.getSource() == this.totdo1 && this.turnRed){
			this.totdo1 = (QuanCo) e.getSource();
			System.out.println("Chon tot");
			this.isChoose =3;

		}
		if(e.getSource() == this.image && this.isChoose==3){
			System.out.println("chon diem");
			this.totdo1.move(e.getX()-25, e.getY()-25);
			this.isChoose =0;
			this.turnRed =false;
			this.turnBlack = true;
			this.totdo1.Setcaneat();
		}
		if( e.getSource() instanceof QuanCo && e.getSource()!=this.totdo1 && this.isChoose==3){
			System.out.println("Co co");
			if(!e.getSource().equals(this.totdo1.getType())){
				this.totdo1.eat((QuanCo) e.getSource(), this);
			}
			this.isChoose=0;
			this.turnRed =false;
			this.turnBlack = true;
		}
		//action totdo2
		else if(e.getSource() == this.totdo2 && this.turnRed){
			this.totdo2 = (QuanCo) e.getSource();
			System.out.println("Chon tot");
			this.isChoose =4;

		}
		if(e.getSource() == this.image && this.isChoose==4){
			System.out.println("chon diem");
			this.totdo2.move(e.getX()-25, e.getY()-25);
			this.isChoose =0;
			this.turnRed =false;
			this.turnBlack = true;
			this.totdo2.Setcaneat();
		}
		if( e.getSource() instanceof QuanCo && e.getSource()!=this.totdo2 && this.isChoose==4){
			System.out.println("Co co");
			if(!e.getSource().equals(this.totdo2.getType())){
				this.totdo2.eat((QuanCo) e.getSource(), this);
			}
			this.isChoose=0;
			this.turnRed =false;
			this.turnBlack = true;
		}
		//action tot do 3
		else if(e.getSource() == this.totdo3 && this.turnRed){
			this.totdo3 = (QuanCo) e.getSource();
			System.out.println("Chon tot");
			this.isChoose =5;

		}
		if(e.getSource() == this.image && this.isChoose==5){
			System.out.println("chon diem");
			this.totdo3.move(e.getX()-25, e.getY()-25);
			this.isChoose =0;
			this.turnRed =false;
			this.turnBlack = true;
			this.totdo3.Setcaneat();
		}
		if( e.getSource() instanceof QuanCo && e.getSource()!=this.totdo3 && this.isChoose==5){
			System.out.println("Co co");
			if(!e.getSource().equals(this.totdo3.getType())){
				this.totdo3.eat((QuanCo) e.getSource(), this);
			}
			this.isChoose=0;
			this.turnRed =false;
			this.turnBlack = true;
		}
		//action tot do 4
		else if(e.getSource() == this.totdo4 && this.turnRed){
			this.totdo4 = (QuanCo) e.getSource();
			System.out.println("Chon tot");
			this.isChoose =6;

		}
		if(e.getSource() == this.image && this.isChoose==6){
			System.out.println("chon diem");
			this.totdo4.move(e.getX()-25, e.getY()-25);
			this.isChoose =0;
			this.turnRed =false;
			this.turnBlack = true;
			this.totdo4.Setcaneat();
		}
		if( e.getSource() instanceof QuanCo && e.getSource()!=this.totdo4 && this.isChoose==6){
			System.out.println("Co co");
			if(!e.getSource().equals(this.totdo4.getType())){
				this.totdo4.eat((QuanCo) e.getSource(), this);
			}
			this.isChoose=0;
			this.turnRed =false;
			this.turnBlack = true;
		}
		//action tot do 5
		else if(e.getSource() == this.totdo5 && this.turnRed){
			this.totdo5 = (QuanCo) e.getSource();
			System.out.println("Chon tot");
			this.isChoose =7;

		}
		if(e.getSource() == this.image && this.isChoose==7){
			System.out.println("chon diem");
			this.totdo5.move(e.getX()-25, e.getY()-25);
			this.isChoose =0;
			this.turnRed =false;
			this.turnBlack = true;
			this.totdo5.Setcaneat();
		}
		if( e.getSource() instanceof QuanCo && e.getSource()!=this.totdo5 && this.isChoose==7){
			System.out.println("Co co");
			if(!e.getSource().equals(this.totdo5.getType())){
				this.totdo5.eat((QuanCo) e.getSource(), this);
			}
			this.isChoose=0;
			this.turnRed =false;
			this.turnBlack = true;
		}
		//action tot den 1
		else if(e.getSource() == this.totden1 && this.turnBlack){
			this.totden1 = (QuanCo) e.getSource();
			System.out.println("Chon tot 2");
			this.isChoose =8;

		}
		if(e.getSource() == this.image && this.isChoose==8){
			System.out.println("chon diem");
			this.totden1.move(e.getX()-25, e.getY()-25);
			this.isChoose =0;
			this.turnBlack =false;
			this.turnRed = true;
			this.totden1.Setcaneat();
		}
		if( e.getSource() instanceof QuanCo && e.getSource()!=this.totden1 && this.isChoose==8){
			System.out.println("Co co");
			if(!e.getSource().equals(this.totden1.getType())){
				this.totden1.eat((QuanCo) e.getSource(), this);
			}
			this.isChoose=0;
			this.turnBlack =false;
			this.turnRed = true;
		}
		//action tot den 2
		else if(e.getSource() == this.totden2 && this.turnBlack){
			this.totden2 = (QuanCo) e.getSource();
			System.out.println("Chon tot 2");
			this.isChoose =9;

		}
		if(e.getSource() == this.image && this.isChoose==9){
			System.out.println("chon diem");
			this.totden2.move(e.getX()-25, e.getY()-25);
			this.isChoose =0;
			this.turnBlack =false;
			this.turnRed = true;
			this.totden2.Setcaneat();
		}
		if( e.getSource() instanceof QuanCo && e.getSource()!=this.totden2 && this.isChoose==9){
			System.out.println("Co co");
			if(!e.getSource().equals(this.totden2.getType())){
				this.totden2.eat((QuanCo) e.getSource(), this);
			}
			this.isChoose=0;
			this.turnBlack =false;
			this.turnRed = true;
		}
		//action tot den 3
		else if(e.getSource() == this.totden3 && this.turnBlack){
			this.totden3 = (QuanCo) e.getSource();
			System.out.println("Chon tot 2");
			this.isChoose =10;

		}
		if(e.getSource() == this.image && this.isChoose==10){
			System.out.println("chon diem");
			this.totden3.move(e.getX()-25, e.getY()-25);
			this.isChoose =0;
			this.turnBlack =false;
			this.turnRed = true;
			this.totden3.Setcaneat();
		}
		if( e.getSource() instanceof QuanCo && e.getSource()!=this.totden3 && this.isChoose==10){
			System.out.println("Co co");
			if(!e.getSource().equals(this.totden3.getType())){
				this.totden3.eat((QuanCo) e.getSource(), this);
			}
			this.isChoose=0;
			this.turnBlack =false;
			this.turnRed = true;
		}
		//action tot den 4
		else if(e.getSource() == this.totden4 && this.turnBlack){
			this.totden4 = (QuanCo) e.getSource();
			System.out.println("Chon tot 2");
			this.isChoose =11;

		}
		if(e.getSource() == this.image && this.isChoose==11){
			System.out.println("chon diem");
			this.totden4.move(e.getX()-25, e.getY()-25);
			this.isChoose =0;
			this.turnBlack =false;
			this.turnRed = true;
			this.totden4.Setcaneat();
		}
		if( e.getSource() instanceof QuanCo && e.getSource()!=this.totden4 && this.isChoose==11){
			System.out.println("Co co");
			if(!e.getSource().equals(this.totden4.getType())){
				this.totden4.eat((QuanCo) e.getSource(), this);
			}
			this.isChoose=0;
			this.turnBlack =false;
			this.turnRed = true;
		}
		//action tot den 5
		else if(e.getSource() == this.totden5 && this.turnBlack){
			this.totden5 = (QuanCo) e.getSource();
			System.out.println("Chon tot 2");
			this.isChoose =12;

		}
		if(e.getSource() == this.image && this.isChoose==12){
			System.out.println("chon diem");
			this.totden5.move(e.getX()-25, e.getY()-25);
			this.isChoose =0;
			this.turnBlack =false;
			this.turnRed = true;
			this.totden5.Setcaneat();
		}
		if( e.getSource() instanceof QuanCo && e.getSource()!=this.totden5 && this.isChoose==12){
			System.out.println("Co co");
			if(!e.getSource().equals(this.totden5.getType())){
				this.totden5.eat((QuanCo) e.getSource(), this);
			}
			this.isChoose=0;
			this.turnBlack =false;
			this.turnRed = true;
		}
		//action phao do 1
		else if(e.getSource() == this.phaodo1 && turnRed){
			this.phaodo1 = (QuanCo) e.getSource();
			System.out.println("Chon phao");
			this.isChoose =13;
		}
		if(e.getSource() == this.image &&  !(e.getSource() instanceof QuanCo) && this.isChoose==13 ){
			System.out.println("chon diem");
			this.phaodo1.move(e.getX()-25, e.getY()-25);
			this.isChoose =0;
			this.turnBlack =true;
			this.turnRed = false;
			this.phaodo1.Setcaneat();
		}
		if( e.getSource() instanceof QuanCo && e.getSource()!=this.phaodo1 && this.isChoose==13){
			System.out.println("Co co");
			int temp1 =(Math.abs(e.getX()-this.phaodo1.Getx()));
			int temp2 =(Math.abs(e.getY()-this.phaodo1.Gety()));
			int countx =0;
			int county=0;
			for(int i=0;i<=temp1;i++){
				if(this.getImage().getComponentAt(i, this.phaodo1.Gety()) instanceof QuanCo){
					countx++;
				}
			}
			for(int i=0;i<=temp2;i++){
				if(this.getImage().getComponentAt(this.phaodo1.Getx(),i ) instanceof QuanCo){
					county++;
				}
			}
			if(!e.getSource().equals(this.phaodo1.getType()) &&(countx<=55 || county<=55)){
				this.phaodo1.eat((QuanCo) e.getSource(), this);
			}
			this.isChoose=0;
			this.turnBlack =true;
			this.turnRed = false;
		}
		//action phao do 2
		else if(e.getSource() == this.phaodo2 && turnRed){
			this.phaodo2 = (QuanCo) e.getSource();
			System.out.println("Chon phao");
			this.isChoose =14;
		}
		if(e.getSource() == this.image && !(e.getSource() instanceof QuanCo) && this.isChoose==14 ){
			System.out.println("chon diem");
			this.phaodo2.move(e.getX()-25, e.getY()-25);
			this.isChoose =0;
			this.turnBlack =true;
			this.turnRed = false;
			this.phaodo2.Setcaneat();
		}
		if( e.getSource() instanceof QuanCo && e.getSource()!=this.phaodo2 && this.isChoose==14){
			System.out.println("Co co");
			int temp1 =(Math.abs(e.getX()-this.phaodo2.Getx()));
			int temp2 =(Math.abs(e.getY()-this.phaodo2.Gety()));
			int countx =0;
			int county=0;
			for(int i=0;i<=temp1;i++){
				if(this.getImage().getComponentAt(i, this.phaodo2.Gety()) instanceof QuanCo){
					countx++;
				}
			}
			for(int i=0;i<=temp2;i++){
				if(this.getImage().getComponentAt(this.phaodo2.Getx(),i ) instanceof QuanCo){
					county++;
				}
			}
			if(!e.getSource().equals(this.phaodo2.getType()) &&(countx<=55 || county<=55)){
				this.phaodo2.eat((QuanCo) e.getSource(), this);
			}
			this.isChoose=0;
			this.turnBlack =true;
			this.turnRed = false;
		}
		//action phao den 1
		else if(e.getSource() == this.phaoden1 && turnBlack){
			this.phaoden1 = (QuanCo) e.getSource();
			System.out.println("Chon phao");
			this.isChoose =15;
		}
		if(e.getSource() == this.image && !(e.getSource() instanceof QuanCo) && this.isChoose==15 ){
			System.out.println("chon diem");
			this.phaoden1.move(e.getX()-25, e.getY()-25);
			this.isChoose =0;
			this.turnBlack =false;
			this.turnRed = true;
			this.phaoden1.Setcaneat();
		}
		if( e.getSource() instanceof QuanCo && e.getSource()!=this.phaoden1 && this.isChoose==15){
			System.out.println("Co co");
			int temp1 =(Math.abs(e.getX()-this.phaoden1.Getx()));
			int temp2 =(Math.abs(e.getY()-this.phaoden1.Gety()));
			int countx =0;
			int county=0;
			for(int i=0;i<=temp1;i++){
				if(this.getImage().getComponentAt(i, this.phaoden1.Gety()) instanceof QuanCo){
					countx++;
				}
			}
			for(int i=0;i<=temp2;i++){
				if(this.getImage().getComponentAt(this.phaoden1.Getx(),i ) instanceof QuanCo){
					county++;
				}
			}
			if(!e.getSource().equals(this.phaoden1.getType()) &&(countx<=55 || county<=55)){
				this.phaoden1.eat((QuanCo) e.getSource(), this);
			}
			this.isChoose=0;
			this.turnBlack =false;
			this.turnRed = true;
		}
		//action phao den 2
		else if(e.getSource() == this.phaoden2 && turnBlack){
			this.phaoden2 = (QuanCo) e.getSource();
			System.out.println("Chon phao");
			this.isChoose =16;
		}
		if(e.getSource() == this.image && !(e.getSource() instanceof QuanCo) && this.isChoose==16 ){
			System.out.println("chon diem");
			this.phaoden2.move(e.getX()-25, e.getY()-25);
			this.isChoose =0;
			this.turnBlack =false;
			this.turnRed = true;
			this.phaoden2.Setcaneat();
		}
		if( e.getSource() instanceof QuanCo && e.getSource()!=this.phaoden2 && this.isChoose==16){
			System.out.println("Co co");
			int temp1 =(Math.abs(e.getX()-this.phaoden2.Getx()));
			int temp2 =(Math.abs(e.getY()-this.phaoden2.Gety()));
			int countx =0;
			int county=0;
			for(int i=0;i<=temp1;i++){
				if(this.getImage().getComponentAt(i, this.phaoden2.Gety()) instanceof QuanCo){
					countx++;
				}
			}
			for(int i=0;i<=temp2;i++){
				if(this.getImage().getComponentAt(this.phaoden2.Getx(),i ) instanceof QuanCo){
					county++;
				}
			}
			if(!e.getSource().equals(this.phaoden2.getType()) &&(countx<=55 || county<=55)){
				this.phaoden2.eat((QuanCo) e.getSource(), this);
			}
			this.isChoose=0;
			this.turnBlack =false;
			this.turnRed = true;
		}
		//action si do 1
		else if(e.getSource() == this.sido1  && turnRed){
			this.sido1 = (QuanCo) e.getSource();
			System.out.println("Chon si");
			this.isChoose =17;
		}
		if(e.getSource() == this.image  && this.isChoose ==17){
			System.out.println("chon diem");
			this.sido1.move(e.getX()-25, e.getY()-25);
			this.isChoose =0;
			this.turnBlack =true;
			this.turnRed = false;
			this.sido1.Setcaneat();
		}
		if( e.getSource() instanceof QuanCo && e.getSource()!=this.sido1 && this.isChoose==17){
			System.out.println("Co co");
			if(!e.getSource().equals(this.sido1.getType())){
				this.sido1.eat((QuanCo) e.getSource(), this);
			}
			this.isChoose=0;
			this.turnBlack =true;
			this.turnRed = false;
		}
		//action si do 2
		else if(e.getSource() == this.sido2  && turnRed){
			this.sido2 = (QuanCo) e.getSource();
			System.out.println("Chon si");
			this.isChoose = 18;
		}
		if(e.getSource() == this.image  && this.isChoose ==18){
			System.out.println("chon diem");
			this.sido2.move(e.getX()-25, e.getY()-25);
			this.isChoose =0;
			this.turnBlack =true;
			this.turnRed = false;
			this.sido2.Setcaneat();
		}
		if( e.getSource() instanceof QuanCo && e.getSource()!=this.sido2 && this.isChoose==18){
			System.out.println("Co co");
			if(!e.getSource().equals(this.sido2.getType())){
				this.sido2.eat((QuanCo) e.getSource(), this);
			}
			this.isChoose=0;
			this.turnBlack =true;
			this.turnRed = false;
		}
		//action si den 1
		else if(e.getSource() == this.siden1  && turnBlack){
			this.siden1 = (QuanCo) e.getSource();
			System.out.println("Chon si");
			this.isChoose = 19;
		}
		if(e.getSource() == this.image  && this.isChoose ==19){
			System.out.println("chon diem");
			this.siden1.move(e.getX()-25, e.getY()-25);
			this.isChoose =0;
			this.turnBlack =false;
			this.turnRed = true;
			this.siden1.Setcaneat();
		}
		if( e.getSource() instanceof QuanCo && e.getSource()!=this.siden1 && this.isChoose==19){
			System.out.println("Co co");
			if(!e.getSource().equals(this.siden1.getType())){
				this.siden1.eat((QuanCo) e.getSource(), this);
			}
			this.isChoose=0;
			this.turnBlack =false;
			this.turnRed = true;
		}
		//action si den 2
		else if(e.getSource() == this.siden2  && turnBlack){
			this.siden2 = (QuanCo) e.getSource();
			System.out.println("Chon si");
			this.isChoose = 20;
		}
		if(e.getSource() == this.image  && this.isChoose ==20){
			System.out.println("chon diem");
			this.siden2.move(e.getX()-25, e.getY()-25);
			this.isChoose =0;
			this.turnBlack =false;
			this.turnRed = true;
			this.siden2.Setcaneat();
		}
		if( e.getSource() instanceof QuanCo && e.getSource()!=this.siden2 && this.isChoose==20){
			System.out.println("Co co");
			if(!e.getSource().equals(this.siden2.getType())){
				this.siden2.eat((QuanCo) e.getSource(), this);
			}
			this.isChoose=0;
			this.turnBlack =false;
			this.turnRed = true;
		}
		//action elephant do 1
		else if(e.getSource() == this.elephantdo1  && turnRed){
			this.elephantdo1 = (QuanCo) e.getSource();
			System.out.println("Chon voi");
			this.isChoose = 21;
		}
		if(e.getSource() == this.image  && this.isChoose ==21){
			System.out.println("chon diem");
			this.elephantdo1.move(e.getX()-25, e.getY()-25);
			this.isChoose =0;
			this.turnBlack =true;
			this.turnRed = false;
			this.elephantdo1.Setcaneat();
		}
		if( e.getSource() instanceof QuanCo && e.getSource()!=this.elephantdo1 && this.isChoose==21){
			System.out.println("Co co");
			if(!e.getSource().equals(this.elephantdo1.getType())){
				this.elephantdo1.eat((QuanCo) e.getSource(), this);
			}
			this.isChoose=0;
			this.turnBlack =true;
			this.turnRed = false;
		}
		//action elephant do 2
		else if(e.getSource() == this.elephantdo2  && turnRed){
			this.elephantdo2 = (QuanCo) e.getSource();
			System.out.println("Chon voi");
			this.isChoose = 22;
		}
		if(e.getSource() == this.image  && this.isChoose ==22){
			System.out.println("chon diem");
			this.elephantdo2.move(e.getX()-25, e.getY()-25);
			this.isChoose =0;
			this.turnBlack =true;
			this.turnRed = false;
			this.elephantdo2.Setcaneat();
		}
		if( e.getSource() instanceof QuanCo && e.getSource()!=this.elephantdo2 && this.isChoose==22){
			System.out.println("Co co");
			if(!e.getSource().equals(this.elephantdo2.getType())){
				this.elephantdo2.eat((QuanCo) e.getSource(), this);
			}
			this.isChoose=0;
			this.turnBlack =true;
			this.turnRed = false;
		}
		//action elephant den 1
		else if(e.getSource() == this.elephantden1  && turnBlack){
			this.elephantden1 = (QuanCo) e.getSource();
			System.out.println("Chon voi");
			this.isChoose = 23;
		}
		if(e.getSource() == this.image  && this.isChoose ==23){
			System.out.println("chon diem");
			this.elephantden1.move(e.getX()-25, e.getY()-25);
			this.isChoose =0;
			this.turnBlack =false;
			this.turnRed = true;
			this.elephantden1.Setcaneat();
		}
		if( e.getSource() instanceof QuanCo && e.getSource()!=this.elephantden1 && this.isChoose==23){
			System.out.println("Co co");
			if(!e.getSource().equals(this.elephantden1.getType())){
				this.elephantden1.eat((QuanCo) e.getSource(), this);
			}
			this.isChoose=0;
			this.turnBlack =false;
			this.turnRed = true;
		}
		//action elephant den 2
		else if(e.getSource() == this.elephantden2  && turnBlack){
			this.elephantden2 = (QuanCo) e.getSource();
			System.out.println("Chon voi");
			this.isChoose = 24;
		}
		if(e.getSource() == this.image  && this.isChoose ==24){
			System.out.println("chon diem");
			this.elephantden2.move(e.getX()-25, e.getY()-25);
			this.isChoose =0;
			this.turnBlack =false;
			this.turnRed = true;
			this.elephantden2.Setcaneat();
		}
		if( e.getSource() instanceof QuanCo && e.getSource()!=this.elephantden2 && this.isChoose==24){
			System.out.println("Co co");
			if(!e.getSource().equals(this.elephantden2.getType())){
				this.elephantden2.eat((QuanCo) e.getSource(), this);
			}
			this.isChoose=0;
			this.turnBlack =false;
			this.turnRed = true;
		}
		// ma do 1
		else if(e.getSource() == this.mado1 && turnRed ){
			this.mado1 = (QuanCo) e.getSource();
			System.out.println("Chon ma");
			this.isChoose = 25;
		}
		if(e.getSource() == this.image  && this.isChoose ==25){
			System.out.println("chon diem");
			this.mado1.move(e.getX()-25, e.getY()-25);
			this.isChoose =0;
			this.turnBlack =true;
			this.turnRed = false;
			this.mado1.Setcaneat();
		}
		if( e.getSource() instanceof QuanCo && e.getSource()!=this.mado1 && this.isChoose==25){
			System.out.println("Co co");
			if(!e.getSource().equals(this.mado1.getType())){
				this.mado1.eat((QuanCo) e.getSource(), this);
			}
			this.isChoose=0;
			this.turnBlack =true;
			this.turnRed = false;
		}
		//action ma do 2
		else if(e.getSource() == this.mado2 && turnRed ){
			this.mado2 = (QuanCo) e.getSource();
			System.out.println("Chon ma");
			this.isChoose = 26;
		}
		if(e.getSource() == this.image  && this.isChoose ==26){
			System.out.println("chon diem");
			this.mado2.move(e.getX()-25, e.getY()-25);
			this.isChoose =0;
			this.turnBlack =true;
			this.turnRed = false;
			this.mado2.Setcaneat();
		}
		if( e.getSource() instanceof QuanCo && e.getSource()!=this.mado2 && this.isChoose==26){
			System.out.println("Co co");
			if(!e.getSource().equals(this.mado2.getType())){
				this.mado2.eat((QuanCo) e.getSource(), this);
			}
			this.isChoose=0;
			this.turnBlack =true;
			this.turnRed = false;
		}
		//action ma den 1
		else if(e.getSource() == this.maden1  && turnBlack){
			this.maden1 = (QuanCo) e.getSource();
			System.out.println("Chon ma");
			this.isChoose = 27;
		}
		if(e.getSource() == this.image  && this.isChoose ==27){
			System.out.println("chon diem");
			this.maden1.move(e.getX()-25, e.getY()-25);
			this.isChoose =0;
			this.turnBlack =false;
			this.turnRed = true;
			this.maden1.Setcaneat();
		}
		if( e.getSource() instanceof QuanCo && e.getSource()!=this.maden1 && this.isChoose==27){
			System.out.println("Co co");
			if(!e.getSource().equals(this.maden1.getType())){
				this.maden1.eat((QuanCo) e.getSource(), this);
			}
			this.isChoose=0;
			this.turnBlack =false;
			this.turnRed = true;
		}
		//action ma den 2
		else if(e.getSource() == this.maden2  && turnBlack){
			this.maden2 = (QuanCo) e.getSource();
			System.out.println("Chon ma");
			this.isChoose = 28;
		}
		if(e.getSource() == this.image  && this.isChoose ==28){
			System.out.println("chon diem");
			this.maden2.move(e.getX()-25, e.getY()-25);
			this.isChoose =0;
			this.turnBlack =false;
			this.turnRed = true;
			this.maden2.Setcaneat();
		}
		if( e.getSource() instanceof QuanCo && e.getSource()!=this.maden2 && this.isChoose==28){
			System.out.println("Co co");
			if(!e.getSource().equals(this.maden2.getType())){
				this.maden2.eat((QuanCo) e.getSource(), this);
			}
			this.isChoose=0;
			this.turnBlack =false;
			this.turnRed = true;
		}
		//action xe do1
		else if(e.getSource() == this.xedo1 && turnRed){
			this.xedo1 = (QuanCo) e.getSource();
			System.out.println("Chon xe");
			this.isChoose =29;

		}
		if(e.getSource() == this.image && !(e.getSource() instanceof QuanCo) && this.isChoose==29 ){
			System.out.println("chon diem");
			boolean canmove =true;
			if(e.getX()>this.xedo1.Getx()){
				for(int i=this.xedo1.Getx()+55;i<e.getX();i++){
					if(this.getImage().getComponentAt(i, e.getY()) instanceof QuanCo){
						canmove =false;
					}
				}
			}
			if(e.getY()>this.xedo1.Getx()){
				for(int i=this.xedo1.Gety()+55;i<e.getY();i++){
					if(this.image.getComponentAt(e.getX(), i) instanceof QuanCo){
						canmove=false;
					}
				}
			}
			if(e.getX()<this.xedo1.Getx()){
				for(int i=this.xedo1.Getx()-1;i>=e.getX();i--){
					if(this.getImage().getComponentAt(i, e.getY()) instanceof QuanCo){
						canmove =false;
					}
				}
			}
			if(e.getY()>this.xedo1.Getx()){
				for(int i=this.xedo1.Gety()-1;i>=e.getY();i--){
					if(this.image.getComponentAt(e.getX(), i) instanceof QuanCo){
						canmove=false;
					}
				}
			}
			if(canmove)
			this.xedo1.move(e.getX()-25, e.getY()-25);
			
			this.isChoose =0;
			this.turnBlack =true;
			this.turnRed = false;

		}
		if( e.getSource() instanceof QuanCo && e.getSource()!=this.xedo1 && this.isChoose==29){
			System.out.println("Co co");
			boolean caneat =true;
			if(e.getX()>this.xedo1.Getx()){
				for(int i=this.xedo1.Getx()+55;i<e.getX();i++){
					if(this.getImage().getComponentAt(i, e.getY()) instanceof QuanCo){
						caneat =false;
					}
				}
			}
			if(e.getY()>this.xedo1.Getx()){
				for(int i=this.xedo1.Gety()+55;i<e.getY();i++){
					if(this.image.getComponentAt(e.getX(), i) instanceof QuanCo){
						caneat=false;
					}
				}
			}
			if(e.getX()<this.xedo1.Getx()){
				for(int i=this.xedo1.Getx();i>e.getX();i--){
					if(this.getImage().getComponentAt(i, e.getY()) instanceof QuanCo){
						caneat =false;
					}
				}
			}
			if(e.getY()>this.xedo1.Getx()){
				for(int i=this.xedo1.Gety()-1;i>e.getY();i--){
					if(this.image.getComponentAt(e.getX(), i) instanceof QuanCo){
						caneat=false;
					}
				}
			}
			if(!e.getSource().equals(this.xedo1.getType()) && caneat){
				this.xedo1.eat((QuanCo) e.getSource(), this);
			}
			this.isChoose=0;
			this.turnBlack =true;
			this.turnRed = false;
		}
		//action xe do 2
		else if(e.getSource() == this.xedo2 && turnRed){
			this.xedo2 = (QuanCo) e.getSource();
			System.out.println("Chon xe");
			this.isChoose =30;

		}
		if(e.getSource() == this.image && !(e.getSource() instanceof QuanCo) && this.isChoose==30 ){
			System.out.println("chon diem");
			boolean canmove =true;
			if(e.getX()>this.xedo2.Getx()){
				for(int i=this.xedo2.Getx()+55;i<e.getX();i++){
					if(this.getImage().getComponentAt(i, e.getY()) instanceof QuanCo){
						canmove =false;
					}
				}
			}
			if(e.getY()>this.xedo2.Getx()){
				for(int i=this.xedo2.Gety()+55;i<e.getY();i++){
					if(this.image.getComponentAt(e.getX(), i) instanceof QuanCo){
						canmove=false;
					}
				}
			}
			if(e.getX()<this.xedo2.Getx()){
				for(int i=this.xedo2.Getx()-1;i>=e.getX();i--){
					if(this.getImage().getComponentAt(i, e.getY()) instanceof QuanCo){
						canmove =false;
					}
				}
			}
			if(e.getY()>this.xedo2.Getx()){
				for(int i=this.xedo2.Gety()-1;i>=e.getY();i--){
					if(this.image.getComponentAt(e.getX(), i) instanceof QuanCo){
						canmove=false;
					}
				}
			}
			if(canmove)
			this.xedo2.move(e.getX()-25, e.getY()-25);
			
			this.isChoose =0;
			this.turnBlack =true;
			this.turnRed = false;

		}
		if( e.getSource() instanceof QuanCo && e.getSource()!=this.xedo2 && this.isChoose==30){
			System.out.println("Co co");
			boolean caneat =true;
			if(e.getX()>this.xedo2.Getx()){
				for(int i=this.xedo2.Getx()+55;i<e.getX();i++){
					if(this.getImage().getComponentAt(i, e.getY()) instanceof QuanCo){
						caneat =false;
					}
				}
			}
			if(e.getY()>this.xedo2.Getx()){
				for(int i=this.xedo2.Gety()+55;i<e.getY();i++){
					if(this.image.getComponentAt(e.getX(), i) instanceof QuanCo){
						caneat=false;
					}
				}
			}
			if(e.getX()<this.xedo2.Getx()){
				for(int i=this.xedo2.Getx();i>e.getX();i--){
					if(this.getImage().getComponentAt(i, e.getY()) instanceof QuanCo){
						caneat =false;
					}
				}
			}
			if(e.getY()>this.xedo2.Getx()){
				for(int i=this.xedo2.Gety()-1;i>e.getY();i--){
					if(this.image.getComponentAt(e.getX(), i) instanceof QuanCo){
						caneat=false;
					}
				}
			}
			if(!e.getSource().equals(this.xedo2.getType()) && caneat){
				this.xedo1.eat((QuanCo) e.getSource(), this);
			}
			this.isChoose=0;
			this.turnBlack =true;
			this.turnRed = false;
		}
		//action xe den 1
		else if(e.getSource() == this.xeden1 && turnBlack){
			this.xeden1 = (QuanCo) e.getSource();
			System.out.println("Chon xe");
			this.isChoose =31;

		}
		if(e.getSource() == this.image && !(e.getSource() instanceof QuanCo) && this.isChoose==31 ){
			System.out.println("chon diem");
			boolean canmove =true;
			if(e.getX()>this.xeden1.Getx()){
				for(int i=this.xeden1.Getx()+55;i<e.getX();i++){
					if(this.getImage().getComponentAt(i, e.getY()) instanceof QuanCo){
						canmove =false;
					}
				}
			}
			if(e.getY()>this.xeden1.Getx()){
				for(int i=this.xeden1.Gety()+55;i<e.getY();i++){
					if(this.image.getComponentAt(e.getX(), i) instanceof QuanCo){
						canmove=false;
					}
				}
			}
			if(e.getX()<this.xeden1.Getx()){
				for(int i=this.xeden1.Getx()-1;i>=e.getX();i--){
					if(this.getImage().getComponentAt(i, e.getY()) instanceof QuanCo){
						canmove =false;
					}
				}
			}
			if(e.getY()>this.xeden1.Getx()){
				for(int i=this.xeden1.Gety()-1;i>=e.getY();i--){
					if(this.image.getComponentAt(e.getX(), i) instanceof QuanCo){
						canmove=false;
					}
				}
			}
			if(canmove)
			this.xeden1.move(e.getX()-25, e.getY()-25);
			
			this.isChoose =0;
			this.turnBlack =false;
			this.turnRed = true;

		}
		if( e.getSource() instanceof QuanCo && e.getSource()!=this.xeden1 && this.isChoose==31){
			System.out.println("Co co");
			boolean caneat =true;
			if(e.getX()>this.xeden1.Getx()){
				for(int i=this.xeden1.Getx()+55;i<e.getX();i++){
					if(this.getImage().getComponentAt(i, e.getY()) instanceof QuanCo){
						caneat =false;
					}
				}
			}
			if(e.getY()>this.xeden1.Getx()){
				for(int i=this.xeden1.Gety()+55;i<e.getY();i++){
					if(this.image.getComponentAt(e.getX(), i) instanceof QuanCo){
						caneat=false;
					}
				}
			}
			if(e.getX()<this.xeden1.Getx()){
				for(int i=this.xeden1.Getx();i>e.getX();i--){
					if(this.getImage().getComponentAt(i, e.getY()) instanceof QuanCo){
						caneat =false;
					}
				}
			}
			if(e.getY()>this.xeden1.Getx()){
				for(int i=this.xeden1.Gety()-1;i>e.getY();i--){
					if(this.image.getComponentAt(e.getX(), i) instanceof QuanCo){
						caneat=false;
					}
				}
			}
			if(!e.getSource().equals(this.xeden1.getType()) && caneat){
				this.xeden1.eat((QuanCo) e.getSource(), this);
			}
			this.isChoose=0;
			this.turnBlack =false;
			this.turnRed = true;
		}
		//action xe den 2
		else if(e.getSource() == this.xeden2 && turnBlack){
			this.xeden2 = (QuanCo) e.getSource();
			System.out.println("Chon xe");
			this.isChoose =32;

		}
		if(e.getSource() == this.image && !(e.getSource() instanceof QuanCo) && this.isChoose==32 ){
			System.out.println("chon diem");
			boolean canmove =true;
			if(e.getX()>this.xeden2.Getx()){
				for(int i=this.xeden2.Getx()+55;i<e.getX();i++){
					if(this.getImage().getComponentAt(i, e.getY()) instanceof QuanCo){
						canmove =false;
					}
				}
			}
			if(e.getY()>this.xeden2.Getx()){
				for(int i=this.xeden2.Gety()+55;i<e.getY();i++){
					if(this.image.getComponentAt(e.getX(), i) instanceof QuanCo){
						canmove=false;
					}
				}
			}
			if(e.getX()<this.xeden2.Getx()){
				for(int i=this.xeden2.Getx()-1;i>=e.getX();i--){
					if(this.getImage().getComponentAt(i, e.getY()) instanceof QuanCo){
						canmove =false;
					}
				}
			}
			if(e.getY()>this.xeden2.Getx()){
				for(int i=this.xeden2.Gety()-1;i>=e.getY();i--){
					if(this.image.getComponentAt(e.getX(), i) instanceof QuanCo){
						canmove=false;
					}
				}
			}
			if(canmove)
			this.xeden2.move(e.getX()-25, e.getY()-25);
			
			this.isChoose =0;
			this.turnBlack =false;
			this.turnRed = true;

		}
		if( e.getSource() instanceof QuanCo && e.getSource()!=this.xeden2 && this.isChoose==32){
			System.out.println("Co co");
			boolean caneat =true;
			if(e.getX()>this.xeden2.Getx()){
				for(int i=this.xeden2.Getx()+55;i<e.getX();i++){
					if(this.getImage().getComponentAt(i, e.getY()) instanceof QuanCo){
						caneat =false;
					}
				}
			}
			if(e.getY()>this.xeden2.Getx()){
				for(int i=this.xeden2.Gety()+55;i<e.getY();i++){
					if(this.image.getComponentAt(e.getX(), i) instanceof QuanCo){
						caneat=false;
					}
				}
			}
			if(e.getX()<this.xeden2.Getx()){
				for(int i=this.xeden2.Getx();i>e.getX();i--){
					if(this.getImage().getComponentAt(i, e.getY()) instanceof QuanCo){
						caneat =false;
					}
				}
			}
			if(e.getY()>this.xeden2.Getx()){
				for(int i=this.xeden2.Gety()-1;i>e.getY();i--){
					if(this.image.getComponentAt(e.getX(), i) instanceof QuanCo){
						caneat=false;
					}
				}
			}
			if(!e.getSource().equals(this.xeden2.getType()) && caneat){
				this.xeden2.eat((QuanCo) e.getSource(), this);
			}
			this.isChoose=0;
			this.turnBlack =false;
			this.turnRed = true;
		}
		
	}
		
 
	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

	}
}
