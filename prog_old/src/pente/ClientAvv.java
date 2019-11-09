package pente;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientAvv {
	
	String[] game1= {"H8","L8","J6","I3","D8","K7","G8","J8","K8","M8","C5","H6"};
	String[] game2= {"D7","B7","C7","G9","H9","F9","G7","H12","K7","I10","I8","I10","J7"};
	String[] game3= {"H10","J8","J10","F6","K9","H9","I9","H8","J10","J11","J12","E11","L12","F13","I10","H11","H7","K12","K9"};
	String[] game4= {"H8","K8","H5","F5","H4","E7","H5","F10","D10","H10","E10","H11","E8","I8","F11","H9","H7"};
	String[] game5= {"H8","H11","F9","I6","D6","I4","F7","H7","H2","H7","G8","G10","E7","I5","F8","I2","J13","B4","J2","G6","G7","G5","G4","G3"};
	String[] game6= {"L13","L12","L14","H9","K8","M12","H7","J8","F13","K8","E8","F8","F8","H10","G11","G6","H7"};
	String[] moves;
	
	private Socket socket;
	private BufferedReader in;
	private PrintWriter out;
	private int player;
	
	public ClientAvv(String serverAddress, int port,int game) throws Exception{
		switch(game){
		case 0: moves= game1; break;
		case 1: moves= game2; break;
		case 2: moves= game3; break;
		case 3: moves= game4; break;
		case 4: moves= game5; break;
		default: moves= game6;
		}
		socket = new Socket(serverAddress, port);
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		out = new PrintWriter(socket.getOutputStream(), true);
	}
	
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
					System.out.println("WELCOME BLACK"); 
				} else {
					player=0;
					System.out.println("WELCOME WHITE");
				}
			}
			int move=0;
			while (true) {
				answer = in.readLine();
				if (answer.startsWith("YOUR_TURN")) {
	    			out.println("MOVE "+moves[move]);
	    			move++;
	   			} 
				else if (answer.startsWith("VALID_MOVE")) 
					System.out.println("Mossa valida, attendi...");
				else if (answer.startsWith("ILLEGAL_MOVE")) {
					System.out.println("Hai effettuato una mossa non consentita");
					break;
				}  
				else if (answer.startsWith("OPPONENT_MOVE")) {
					System.out.println("Mossa dell'avversario: " + answer.substring(2));
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
	
	public static void main(String[] args) throws Exception {
		if(args.length>1){
			String serverAddress = (args.length >= 1) ? args[0] : "localhost" ;
			int serverPort = (args.length >= 2) ? Integer.valueOf(args[1]) : 8901;
			int game = (args.length==3)? Integer.valueOf(args[2]): 0;
			ClientAvv c = new ClientAvv(serverAddress, serverPort,game);
			//Client p= new Client();
			c.play();
		}
		else{
			String serverAddress = "localhost" ;
			int serverPort = 8901;
			int game = Integer.valueOf(args[0]);
			ClientAvv c = new ClientAvv(serverAddress, serverPort,game);
			//Client p= new Client();
			c.play();
		}
	} // main

}
