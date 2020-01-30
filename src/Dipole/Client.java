package Dipole;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;

public class Client {

	private Socket socket;
	private BufferedReader in;
	private PrintWriter out;
	private Player g;
	private int player;
	private static final int A = 65;
	private HashMap<String, Integer> dirMap,rowMap;
	
//	public Player(String serverAddress, int port) throws Exception {
//		socket = new Socket(serverAddress, port);
//		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//		out = new PrintWriter(socket.getOutputStream(), true);
//	} // costruttore

	private static String[] DIR = { "N", "S", "NE", "SW", "SE", "NW", "E",
	"W" };
	
	private static String[] RIGHE = {"A","B","C","D","E","F","G","H"};
	
	public Client(String serverAddress, int port) throws Exception {
		socket = new Socket(serverAddress, port);
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		out = new PrintWriter(socket.getOutputStream(), true);
		dirMap = new HashMap<String, Integer>();
		rowMap = new HashMap<String, Integer>();
		for(int i=0;i<8;i++){
			dirMap.put(DIR[i],i);
			rowMap.put(RIGHE[i], i);
		}
	} // costruttore

	private int[] calcola_indici(int i, int j, int dir, int nCelleMove) {
		int[] ris = new int[2];
		switch (dir) {
		case ScacchieraBit.NORTH:
			ris[0] = i - nCelleMove;
			ris[1] = j;
			break;
		case ScacchieraBit.NORTHEAST:
			ris[1] = j + nCelleMove;
			ris[0] = i - nCelleMove;
			break;
		case ScacchieraBit.EAST:
			ris[1] = j + nCelleMove;
			ris[0] = i;
			break;
		case ScacchieraBit.SOUTHEAST:
			ris[1] = j + nCelleMove;
			ris[0] = i + nCelleMove;
			break;
		case ScacchieraBit.SOUTH:
			ris[0] = i + nCelleMove;
			ris[1] = j;
			break;
		case ScacchieraBit.SOUTHWEST:
			ris[1] = j - nCelleMove;
			ris[0] = i + nCelleMove;
			break;
		case ScacchieraBit.WEST:
			ris[1] = j - nCelleMove;
			ris[0] = i;
			break;
		case ScacchieraBit.NORTHWEST:
			ris[1] = j - nCelleMove;
			ris[0] = i - nCelleMove;
			break;
		}
		return ris;
	}
	
	public void play() throws Exception {
		String colour = null;
		String answer;
		ScacchieraBit s = new ScacchieraBit();
		try {
			answer = in.readLine();
			if (answer.startsWith("WELCOME")) {

				colour = answer.substring(8);
				colour = colour.toUpperCase();

				if (colour.equals("BLACK")) {
					player = 1;
					g = new Player(s, player);
					System.out.println("WELCOME BLACK");
				} else {
					player = 0;
					g = new Player(s,player);
					System.out.println("WELCOME WHITE");
				}
			}
			while (true) {
				// answer = sc.nextLine();
				answer = in.readLine();
				if (answer.startsWith("YOUR_TURN")) {
					long init = System.currentTimeMillis();
					Mossa m = g.elaboraProssimaMossa();
					long dur = System.currentTimeMillis() - init;
					out.println(m);
					g.muovi(m,player);
					System.out.println("Ho scelto di fare la mossa " + m + " in " + dur);
					
				} else if (answer.startsWith("VALID_MOVE"))
					System.out.println("Mossa valida, attendi...");
				else if (answer.startsWith("ILLEGAL_MOVE")) {
					System.out.println("Hai effettuato una mossa non consentita");
					break;
				} else if (answer.startsWith("OPPONENT_MOVE")) {
					System.out.println("Mossa dell'avversario: " + answer.substring(2));
					String[] campi = answer.substring(6).split(",");
					int x = rowMap.get(campi[0].substring(0,0));
					int y = Integer.valueOf(campi[0].substring(1))-1;
					int dir = dirMap.get(campi[1]);
					int spostamento = Integer.valueOf(campi[2]);
					int[] dest = calcola_indici(x, y, dir, spostamento);
					Mossa m = new Mossa(x, y, dest[0], dest[1], dir);
					g.muovi(m,1-player);
					// g.draw2();
				} else if (answer.startsWith("VICTORY")) {
					System.out.println("HAI VINTO");
					break;
				} else if (answer.startsWith("TIMEOUT")) {
					System.out.println("TIMEOUT");
					break;
				} else if (answer.startsWith("DEFEAT")) {
					System.out.println("HAI PERSO");
					break;
				} else if (answer.startsWith("TIE")) {
					System.out.println("HAI PAREGGIATO");
					break;
				} else if (answer.startsWith("MESSAGE"))
					System.out.println(answer.substring(8));
			}
		} finally {
			socket.close();
		}
	}

//	public void play2() throws Exception {
//		Scanner sc = new Scanner(System.in);
//		try {
//			sc.nextLine();
//			Player g1 = null;
//			Player g2 = null;
//			g1 = new Player(0, 4);
//			g2 = new Player(1, 4);
//			int turn = 0;
//			while (true) {
//				// answer = sc.nextLine();
//				// if (answer.startsWith("next")) {
//				long init = System.currentTimeMillis();
//				String s = null;
//				String s2 = null;
//				if (turn == 0) {
//					// s2= g11.elaborateMoveM();
//					s = g1.elaborateMove();
//				} else {
//					s = g2.elaborateMove();
//					// s2= g22.elaborateMoveM();
//				}
//
//				long dur = System.currentTimeMillis() - init;
//				// String x = partita.calcolaMossa();
//				char c = s.charAt(0);
//				int x = (int) c - A;
//				int y = Integer.valueOf(s.substring(1)) - 1;
//				Move m = new Move(x, y, turn);
//				g1.makeMove(m);
//				g2.makeMove(m);
//				System.out.println("Ho scelto di fare la mossa " + s + " in " + dur);
//				System.out.println("A profondità 4 avrebbe messo " + s2);
//				// out.println(x);
//				g1.draw();
//				if (g1.endGame) {
//					System.out.println("HA VINTO" + turn);
//					break;
//				}
//				turn = 1 - turn;
//			}
//		} finally {
//			sc.close();
//			// socket.close();
//		}
//	}

	public static void main(String[] args) throws Exception {
		String serverAddress = (args.length >= 1) ? args[0] : "localhost";
		int serverPort = (args.length == 2) ? Integer.valueOf(args[1]) : 8901;
		Client p = new Client(serverAddress, serverPort);
		// Client p= new Client();
		p.play();
	} // main

}
