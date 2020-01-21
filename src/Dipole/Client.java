package Dipole;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {
	
	private Socket socket;
	private BufferedReader in;
	private PrintWriter out;
	private Player g;
	private int player;
	private static final int A=65;


//	public Player(String serverAddress, int port) throws Exception {
//		socket = new Socket(serverAddress, port);
//		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//		out = new PrintWriter(socket.getOutputStream(), true);
//	} // costruttore
    
	public Client(String serverAddress, int port) throws Exception {
		socket = new Socket(serverAddress, port);
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		out = new PrintWriter(socket.getOutputStream(), true);
    } // costruttore
	public void play() throws Exception {
		String colour = null;
		String answer;
		try {
			answer = in.readLine();
			if (answer.startsWith("WELCOME")) {

				colour = answer.substring(8);
				colour = colour.toUpperCase(); 

				if (colour.equals("BLACK")) {
					player=1;
					g= new Player(player,4);
					System.out.println("WELCOME BLACK"); 
				} else {
					player=0;
					g= new Player(player,4);
					System.out.println("WELCOME WHITE");
				}
			}
			while (true) {
			//	answer = sc.nextLine();
				answer = in.readLine();
				if (answer.startsWith("YOUR_TURN")) {
					long init= System.currentTimeMillis();
					String s= g.elaborateMove();
					long dur= System.currentTimeMillis()-init;
	    			out.println("MOVE "+s);
					char c= s.charAt(0);
					int x= (int)c-A;
					int y= Integer.valueOf(s.substring(1))-1;
					Move m= new Move(x,y,player);
					g.makeMove(m);
	    			System.out.println("Ho scelto di fare la mossa "+ s +" in "+dur);
	    			g.draw();
	   				} 
				else if (answer.startsWith("VALID_MOVE")) 
					System.out.println("Mossa valida, attendi...");
				else if (answer.startsWith("ILLEGAL_MOVE")) {
					System.out.println("Hai effettuato una mossa non consentita");
					break;
				}  
				else if (answer.startsWith("OPPONENT_MOVE")) {
					System.out.println("Mossa dell'avversario: " + answer.substring(2));
					//partita.applicaMossa(risposta.substring(14).toUpperCase());
					char c= answer.charAt(14);
					int x= (int)c-A;
					int y= Integer.valueOf(answer.substring(15))-1;
					Move m= new Move(x,y,1-player);
					g.makeMove(m);
					g.draw();
				//	g.draw2();
				} 
				else if (answer.startsWith("VICTORY")) {
					System.out.println("HAI VINTO");
					break;
				} 
				else if (answer.startsWith("DEFEAT")) {
					System.out.println("HAI PERSO");
					break;
				} 
				else if(answer.startsWith("TIE")) {
					System.out.println("HAI PAREGGIATO");
					break;
				}
				else if (answer.startsWith("MESSAGE")) 
					System.out.println(answer.substring(8));
			}
		} finally {
			socket.close();
		}
	}

	public void play2() throws Exception {
		Scanner sc = new Scanner(System.in);
		try {
			sc.nextLine();
			Player g1=null;
			Player g2=null;			
			g1= new Player(0,4);
		    g2= new Player(1,4);
			int turn=0;
			while (true) {
			//	answer = sc.nextLine();
			//	if (answer.startsWith("next")) {
					long init= System.currentTimeMillis();
					String s=null;
					String s2= null;
					if(turn ==0) {
						//s2= g11.elaborateMoveM();
						s= g1.elaborateMove();
					}
					else {
						s= g2.elaborateMove();
						//s2= g22.elaborateMoveM();
					}
					
					long dur= System.currentTimeMillis()-init;
					//String x = partita.calcolaMossa();
					char c= s.charAt(0);
					int x= (int)c-A;
					int y= Integer.valueOf(s.substring(1))-1;
					Move m= new Move(x,y,turn);
					g1.makeMove(m);
					g2.makeMove(m);
	    			System.out.println("Ho scelto di fare la mossa "+ s +" in "+dur);
	    			System.out.println("A profondità 4 avrebbe messo "+s2);
		//			out.println(x);
	    			g1.draw();
	    			if(g1.endGame) {
						System.out.println("HA VINTO"+ turn);
						break;
					}
	    			turn=1-turn;
			}
		} finally {
			sc.close();
			//socket.close();
		}
	}
	public static void main(String[] args) throws Exception {
		String serverAddress = (args.length >= 1) ? args[0] : "localhost" ;
		int serverPort = (args.length == 2) ? Integer.valueOf(args[1]) : 8901;
		Client p = new Client(serverAddress, serverPort);
		//Client p= new Client();
		p.play();
	} // main

}

