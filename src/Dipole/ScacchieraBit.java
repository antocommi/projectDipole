package Dipole;


import java.util.HashMap;

import progDipole.Cella;
import util.ByteMap;;

public class ScacchieraBit {
	// Implementazione della scacchiera tramite bitboard. 
	// La scacchiera è 8x8 => 64 celle
	// Due bitboard da 64 bit una per i neri e l'altra per i bianchi
//	private int scacchiere;
	
	private final int DIMENSION = 4;
	private ByteMap scacchiera;
	private boolean turnoGiocatore;
	private int scacchieraBianchi, scacchieraNeri;
	
	public final int SIZE = 8;
	/** Codifica il contenuto di una casella vuota. */
	private static final int VUOTA = -1;
	/** Codifica il contenuto di una casella con pedina bianca. */
	private final int PEDINA_BIANCA = 0;
	/** Codifica il contenuto di una casella con pedina nera. */
	private final int PEDINA_NERA = 1;
	/** Codifica il COLORE DI UNA CELLA BIANCA */
	private final int CELLA_BIANCA = 0;
	/** Codifica il COLORE DI UNA CELLA NERA. */
	private final int CELLA_NERA = 1;
	
	private int STACK_BIANCO = 12;
	private int STACK_NERO = 12;
	public final int NORTH = 0;
	public final int NORTHEAST = 1;
	public final int EAST = 2;
	public final int SOUTHEAST = 3;
	public final int SOUTH = 4;
	public final int SOUTHWEST = 5;
	public final int WEST = 6;
	public final int NORTHWEST = 7;
	private final int NESSUNA_VITTORIA = 0;
	private final int VITTORIA_BIANCO = 1;
	private final int VITTORIA_NERO = 2;
	private int MAX_MOSSE= 60;
	private boolean turnoGiocatore; // Indica il giocatore che deve giocare
	
	
	// isBlack(i,j) -> (((1 << i*8+j) & scacchieraBianchi) >> i*8+j) == 1

	private HashMap<String, Integer> riga;

	
	private int modifyBit(int numero, int posizione, int valBinario) {
		int mask = 1 << posizione;
		return (numero & ~mask) | ((valBinario << posizione) & mask);
	}
	
	private int getPositionOnBoard(int i, int j) {
		return i*4+j;
	}
	
	public void addOnBoard(int i, int j, int color, int qty) {
		if(color==1) {
			scacchieraBianchi = modifyBit(1, getPositionOnBoard(i,j), scacchieraBianchi);
		}
		else if(color==0) {
			scacchieraNeri 	= modifyBit(1, getPositionOnBoard(i,j), scacchieraNeri);
		}
	}
	
	public ScacchieraBit() {
		turnoGiocatore = true;
//		scacchiere = 0b01000000000000000000000000000100;
		scacchieraBianchi = 0;
		scacchieraNeri = 0;
		scacchiera = new ByteMap(DIMENSION*DIMENSION);
		posizionaPedine(0,3,12,PEDINA_NERA);
		posizionaPedine(7,4,12,PEDINA_BIANCA);
		riga = new HashMap<>();
		riga.put("A", 0);
		riga.put("B", 1);
		riga.put("C", 2);
		riga.put("D", 3);
		riga.put("E", 4);
		riga.put("F", 5);
		riga.put("G", 6);
		riga.put("H", 7);
		
	
	}
	
	private String calcolaLettera(int nLettera) {
		return "ABCDEFGH".substring(nLettera,nLettera+1);
	}
	
	public boolean checkPosOut(int i, int j) {
		if (i > 7 || j > 7 || i < 0 || j < 0)
			return true;
		return false;
	}
	
	// converte stringa ("A4") in pos indici 
	private int[] calcola_indici(String posizione) {
		int[] res = new int[2];
		res[0] = riga.get(posizione.charAt(0)+"");// get da il valore della chiave che in questo caso Ã¨ la lettera
		res[1] = Integer.parseInt(posizione.substring(1))-1;
		return res;
	}
	
	public void posizionaPedine(int i, int j, int qty, int color) {
		int a = i - 4;
		int b = j - 4;
		
		if(color==0) {
			scacchieraBianchi = modifyBit(1, getPositionOnBoard(0, 3), scacchieraBianchi);
			scacchiera.setValue(qty, a*4+b);
		}
		else {
			scacchieraNeri = modifyBit(1, getPositionOnBoard(7, 4), scacchieraNeri);
			scacchiera.setValue(qty,a*4+b);
		}
	}
	
	private int[] calcola_indici(int i, int j, int dir, int nCelleMove) {
		int [] ris = new int[2];
		switch(dir){
			case NORTH: 
				ris[0]=i - nCelleMove; 
				ris[1]= j;
				break;
			case NORTHEAST: 
				ris[1]=j + nCelleMove;
				ris[0]=i - nCelleMove;	
				break;
			case EAST: 
				ris[1]=j + nCelleMove;
				ris[0]= i;
				break;
			case SOUTHEAST: 
				ris[1]=j + nCelleMove;
				ris[0]=i + nCelleMove;	
				break;
			case SOUTH:  
				ris[0]=i + nCelleMove;	
				ris[1]= j;
				break;
			case SOUTHWEST:  
				ris[1]=j - nCelleMove;
				ris[0]=i + nCelleMove;	
				break;
			case WEST:  
				ris[1]=j - nCelleMove;
				ris[0]= i;
				break;
			case NORTHWEST: 
				ris[1]=j - nCelleMove;
				ris[0]=i - nCelleMove;	
				break;
		}
		return ris;
	}

	public int calcolaDirezione(int a, int b, int x, int y) {
		int offsetA,offsetB;
		offsetA = a-x;
		offsetB = b-y;
		if(offsetA<0 && offsetB==0) return SOUTH;
		if(offsetA<0 && offsetB<0) return SOUTHEAST;
		if(offsetA<0 && offsetB>0) return SOUTHWEST;
		if(offsetA==0 && offsetB<0) return EAST;
		if(offsetA==0 && offsetB>0) return WEST;
		if(offsetA>0 && offsetB==0) return NORTH;
		if(offsetA>0 && offsetB<0) return NORTHEAST;
		if(offsetA>0 && offsetB>0) return NORTHWEST;
		return -1;
	}
	
	public boolean verDiagonale(int i,int j,int x,int y)
	{
		int offsetA,offsetB;
		offsetA = Math.abs(i-x);
		offsetB = Math.abs(j-y);
		if(offsetA==offsetB) return true;
		if((offsetA%2==0 && offsetB==0) || (offsetA==0 && offsetB%2==0)) return true;
		return false;
	}
	
	public int calcolaSpostamento(int a, int b, int x, int y) {
		int k,m;
		k = Math.abs(a-x);
		m = Math.abs(b-y);
		return k >= m ? k : m;	
	}
	
	public int getColorePedina(int x, int y) {
		int pos = (x-4)*4+(y-4);
		int mask=1;
		if(scacchiera.getValue(pos)==0) return -1;
		int bianco = (scacchieraBianchi & (mask << 16-pos) >>> 16-pos);
		int nero = (scacchieraNeri & (mask << 16-pos) >>> 16-pos);
		return bianco == 1 ? PEDINA_BIANCA : PEDINA_NERA;
	}
	
	public int[] calcolaIndiciEstesi(int x, int y) {
		
	}
	
	public void calcolaMosseAmmissibili(int x, int y) {
		// pre-condizione: x,y in [0,7]
		int a = x-4;
		int b = y-4;
		int pos = a*4+b;
		int color = getColorePedina(x,y);
		if(scacchiera.getValue(pos)==0) return;
		if(turnoGiocatore && color == PEDINA_BIANCA){
			
		}
	}
	
	public static void main(String[] args) {
		System.out.println("Done!");
	}
}
