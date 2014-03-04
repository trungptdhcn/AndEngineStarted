package chess.chinashit;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.*;

public class CuaSo extends JFrame implements MouseListener{
	private Container cont;
	private JToolBar status;
	private JButton newgame = new JButton ("Bat dau");
	private JButton exit = new JButton ("Thoat game");
	private BanCo board ;
	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		if(e.getSource() == this.exit){
			System.exit(0);
		}
		else if(e.getSource() == this.newgame){
			
			this.board.setVisible(true);
		}
		
	}

	public CuaSo(){
		super("Co Tuong");
		cont = this.getContentPane();
		cont.setLayout(null);

		this.status = new JToolBar();
		this.status.setLayout(new GridLayout(0,4));
		this.status.setBounds(0, 0, 558, 35);
		this.status.add(this.newgame);
		this.status.add(this.exit);
		
		board = BanCo.getInstance();
		this.cont.add(this.board);
		this.board.setBounds(0, 30, 560, 620);
		this.board.setVisible(false);
		this.cont.add(this.status);
		
		
		
		this.newgame.addMouseListener(this);
		this.exit.addMouseListener(this);

	}


	


	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}
