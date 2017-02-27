package SocketConnection;

import javax.swing.JFrame;
import javax.swing.JLabel;

import RPIgetItem.BarcodeScannerListener;

public class Startserver {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		
	    JFrame.setDefaultLookAndFeelDecorated(true);
	    JFrame frame = new JFrame();
	    frame.setTitle("Fridge Server");
	    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    JLabel label = new JLabel("Server is Running");
	    frame.add(label);
	    frame.pack();
	    frame.setVisible(true);
		
		
		 BarcodeScannerListener.getInstance( );
		SocketServer server = new SocketServer(9000);

		new Thread(server).start();


	}

}

