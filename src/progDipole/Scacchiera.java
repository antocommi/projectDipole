package progDipole;

import java.util.HashMap;
import java.util.LinkedList;

import javax.swing.text.html.HTMLDocument.Iterator;

public class Scacchiera {

	public final int SIZE = 8;
	protected static Cella[][] scacchiera;
	protected static Cella[][] scacchiera2;
	/** Codifica il contenuto di una casella vuota. */
	private static final int VUOTA = -1;
	/** Codifica il contenuto di una casella con pedina bianca. */
	private static final int PEDINA_BIANCA = 0;
	/** Codifica il contenuto di una casella con pedina nera. */
	private static final int PEDINA_NERA = 1;
	/** Codifica il COLORE DI UNA CELLA BIANCA */
	private static final int CELLA_BIANCA = 0;
	/** Codifica il COLORE DI UNA CELLA NERA. */
	private static final int CELLA_NERA = 1;
	private static int STACK_BIANCO = 12;
	private static int STACK_NERO = 12;
	public static final int NORTH = 0;
	public static final int NORTHEAST = 1;
	public static final int EAST = 2;
	public static final int SOUTHEAST = 3;
	public static final int SOUTH = 4;
	public static final int SOUTHWEST = 5;
	public static final int WEST = 6;
	public static final int NORTHWEST = 7;
	private static final int NESSUNA_VITTORIA = 0;
	private static final int VITTORIA_BIANCO = 1;
	private static final int VITTORIA_NERO = 2;
	private static int MAX_MOSSE= 60;
	private static boolean turnoGiocatore; // Indica il giocatore che deve giocare
	private LinkedList<Mossa> BASE;
	private LinkedList<Mossa> MERGE;
	private LinkedList<Mossa> CAPTURE;
	
	private enum tipoMossa {
		BASE, MERGE, CAPTURE
	};

	private HashMap<String, Integer> riga;

	/**
	 * Costruttore di Default che inizializza la sessione di gioco
	 */
	public Scacchiera() {

		BASE = new LinkedList<>();
		MERGE = new LinkedList<>();
		CAPTURE = new LinkedList<>();
		
		turnoGiocatore = true;
		riga = new HashMap<>();
		riga.put("A", 0);
		riga.put("B", 1);
		riga.put("C", 2);
		riga.put("D", 3);
		riga.put("E", 4);
		riga.put("F", 5);
		riga.put("G", 6);
		riga.put("H", 7);
		scacchiera = new Cella[SIZE][SIZE];
	
		inizializzaScacchiera();
	}

	/**
	 * Inizializza la configurazione iniziale della scacchiera. Imposta il colore di
	 * ogni cella e posiziona le pedine iniziali dei due giocatori.
	 */
	public void inizializzaScacchiera() {
		for (int i = 0; i < scacchiera.length; i++) {
			for (int j = 0; j < scacchiera.length; j++) {
				if ((i % 2 == 0 && j % 2 != 0) || (i % 2 != 0 && j % 2 == 0)) {
					Cella c = new Cella(i, j, 0, CELLA_NERA, VUOTA);
					scacchiera[i][j] = c;
				} else {
					Cella c = new Cella(i, j, 0, CELLA_BIANCA, VUOTA);
					scacchiera[i][j] = c;
				}

			}
		}
		Cella c = new Cella(0, 3, STACK_NERO, CELLA_NERA, PEDINA_NERA);
		scacchiera[0][3] = c;
		Cella c1 = new Cella(7, 4, STACK_BIANCO, CELLA_NERA, PEDINA_BIANCA);
		scacchiera[7][4] = c1;
	}

	public Cella[][] getScacchiera() {
		return scacchiera;
	}

	public void setScacchiera(Cella[][] scacchiera) {
		this.scacchiera = scacchiera;
	}

	public int getSIZE() {
		return SIZE;
	}

	
	
	public LinkedList<Mossa> getBASE() {
		return BASE;
	}

	public void setBASE(LinkedList<Mossa> bASE) {
		BASE = bASE;
	}

	public LinkedList<Mossa> getMERGE() {
		return MERGE;
	}

	public void setMERGE(LinkedList<Mossa> mERGE) {
		MERGE = mERGE;
	}

	public LinkedList<Mossa> getCAPTURE() {
		return CAPTURE;
	}

	public void setCAPTURE(LinkedList<Mossa> cAPTURE) {
		CAPTURE = cAPTURE;
	}

	public static int getVuota() {
		return VUOTA;
	}

	public static int getPedinaBianca() {
		return PEDINA_BIANCA;
	}

	public static int getPedinaNera() {
		return PEDINA_NERA;
	}

	public static int getCellaBianca() {
		return CELLA_BIANCA;
	}

	public static int getCellaNera() {
		return CELLA_NERA;
	}

	public static int getSTACK_BIANCO() {
		return STACK_BIANCO;
	}

	public static int getSTACK_NERO() {
		return STACK_NERO;
	}

	public static int getNorth() {
		return NORTH;
	}

	public static int getNortheast() {
		return NORTHEAST;
	}

	public static int getEast() {
		return EAST;
	}

	public static int getSoutheast() {
		return SOUTHEAST;
	}

	public static int getSouth() {
		return SOUTH;
	}

	public static int getSouthwest() {
		return SOUTHWEST;
	}

	public static int getWest() {
		return WEST;
	}

	public static int getNorthwest() {
		return NORTHWEST;
	}

	public static int getNessunaVittoria() {
		return NESSUNA_VITTORIA;
	}

	public static int getVittoriaBianco() {
		return VITTORIA_BIANCO;
	}

	public static int getVittoriaNero() {
		return VITTORIA_NERO;
	}

	public static int getMAX_MOSSE() {
		return MAX_MOSSE;
	}

	public static boolean isTurnoGiocatore() {
		return turnoGiocatore;
	}

	public HashMap<String, Integer> getRiga() {
		return riga;
	}

	/**
	 * Return la cella desiderata
	 */
	public Cella getCella(int i, int j) {
		return scacchiera[i][j];
	}

	/**
	 * Controlla se nella cella in riga i e colonna j ci sono pedine posizionate
	 */
	public boolean isEmpty(int i, int j) {
		return scacchiera[i][j].getnPedine() == 0;
	}

	/**
	 * Restituisce il colore di una pedina se esiste, -1 altrimenti.
	 */
	public int getColorePedina(int i, int j) {
		return scacchiera[i][j].getColorePedina();
	}

	/**
	 * Restituisce il colore della cella di riga i e colonna j.
	 */
	public int getColoreCella(int i, int j) {
		return scacchiera[i][j].getColoreCella();
	}

	/**
	 * Controlla se la riga i e la colonna j sono all'interno della scacchiera.
	 */
	public boolean checkPosOut(int i, int j) {
		if (i > 7 || j > 7 || i < 0 || j < 0)
			return true;
		return false;
	}
	/**
	 *converte stringa ("A4")in pos indici 
	 */
	private int[] calcola_indici(String posizione) {
		int[] res = new int[2];
		
		res[0] = riga.get(posizione.charAt(0)+"");// get da il valore della chiave che in questo caso è la lettera
		res[1] = Integer.parseInt(posizione.substring(1))-1;
		return res;
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
	
	

	/**
	 * Verifica se la posizione della prossima mossa e' valida. ***DA MODIFICARE***
	 */
	public boolean muovi(String pos_iniziale, int dir, int nCelleMove) {
		
		double distanzaCelle;
		int[] pos = calcola_indici(pos_iniziale);
		int[] pos_finale = calcola_indici(pos[0], pos[1], dir, nCelleMove);
		Cella partenza, destinazione ;
		System.out.format("pos: %d, %d \n",pos[0],pos[1]);
		if(turnoGiocatore)System.out.println("bianco gioca");
		else System.out.println("nero gioca");
		System.out.format("pos_finale: %d, %d \n",pos_finale[0],pos_finale[1]);
		partenza = scacchiera[pos[0]][pos[1]];
		//distanzaCelle = distanzaCelle(pos,pos_finale);
		
	 //remove
		if ( checkPosOut(pos_finale[0], pos_finale[1]) ){
			
			if(nCelleMove>partenza.getnPedine()) {System.out.println("problema");return false;}
			else {
				partenza.removeFromStack(nCelleMove);
				if (turnoGiocatore)
					STACK_BIANCO=STACK_BIANCO - nCelleMove;
				else STACK_NERO=STACK_NERO - nCelleMove;
				MAX_MOSSE--;
				return true;
			}
		}
		
		destinazione = scacchiera[pos_finale[0]][pos_finale[1]];
		
		if (destinazione.getColoreCella()!= CELLA_NERA) {
			System.out.println("cella di destinazione bianca non valida");
			return false;
		}
		// GESTIONE DEI TURNI
		if (turnoGiocatore && partenza.getColorePedina() == PEDINA_NERA)
			return false;
		if (!turnoGiocatore && partenza.getColorePedina() == PEDINA_BIANCA)
			return false;
		
		if (partenza.getnPedine() == 0 || nCelleMove > partenza.getnPedine())
			return false;
		if (destinazione.getColoreCella()!= CELLA_NERA)
			return false;
		
		
		//BASE
		if (destinazione.getnPedine() == 0) {
			if(turnoGiocatore && (dir==SOUTH || dir==SOUTHEAST || dir == SOUTHWEST))
				
				return false;
			
			if(!turnoGiocatore && (dir==NORTH || dir==NORTHEAST || dir==NORTHWEST))
				return false;
				
			destinazione.base(partenza, nCelleMove);
			MAX_MOSSE--;
			
		} else if (partenza.getColorePedina() == destinazione.getColorePedina()) {
			// MERGE//
			if(turnoGiocatore && (dir==SOUTH || dir==SOUTHEAST || dir == SOUTHWEST))
				return false;
			
			if(!turnoGiocatore && (dir==NORTH || dir==NORTHEAST || dir==NORTHWEST))
				return false;
			
			
			destinazione.mergeFrom(partenza, nCelleMove);
			MAX_MOSSE--;
		} else {
			// CAPTURE // 
			if (turnoGiocatore && destinazione.getColorePedina() == PEDINA_NERA)
				STACK_NERO=STACK_NERO - nCelleMove;
			else if (!turnoGiocatore && destinazione.getColorePedina() == PEDINA_BIANCA)
				STACK_BIANCO=STACK_BIANCO - nCelleMove;
			destinazione.captureFrom(partenza, nCelleMove);
			MAX_MOSSE--;
		}
		
		turnoGiocatore = !turnoGiocatore;

		return true;
	}
	
	public static int calcolaDirezione(int a, int b, int x, int y) {
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
	
	public static int calcolaSpostamento(int a, int b, int x, int y) {
		int k,m;
		k = Math.abs(a-x);
		m = Math.abs(b-y);
		return k>=m ? k : m;	
	}
	public static boolean verDiagonale(int i,int j,int x,int y)
	{
		int offsetA,offsetB;
		offsetA = Math.abs(i-x);
		offsetB = Math.abs(j-y);
		if(offsetA==offsetB) return true;
		if((offsetA%2==0 && offsetB==0) || (offsetA==0 && offsetB%2==0)) return true;
		return false;
	}
	
	
	public void verificaMosseAmm(int x,int y) {
		System.out.println(BASE);
		if(scacchiera[x][y].getColorePedina()==VUOTA) return;
		if(turnoGiocatore && scacchiera[x][y].getColorePedina() == PEDINA_NERA) {
				for (int i = 0; i < scacchiera.length; i++) {
					for (int j = i%2==0? 1 : 0 ; j < scacchiera.length; j+=2) {
						if (scacchiera[i][j].getColoreCella()==CELLA_NERA) {
							int contaSpost=calcolaSpostamento(x,y,i,j);
							int dir= calcolaDirezione(x,y,i,j);
							System.out.println("i"+ i +"j"+j +"spost" + contaSpost);
							if(scacchiera[x][y].getnPedine()>=contaSpost && verDiagonale(i,j,x,y)) {
								
								System.out.println(dir);
								Mossa m= new Mossa(x,y,dir,contaSpost);
								if(dir==SOUTH || dir==SOUTHEAST||dir==SOUTHWEST){
									if(scacchiera[i][j].getnPedine() == 0 ) BASE.add(m); 
									else if(scacchiera[x][y].getColorePedina() == scacchiera[i][j].getColorePedina()) MERGE.add(m);
								}
								else {
										if(scacchiera[i][j].getColorePedina()==PEDINA_BIANCA) CAPTURE.add(m);
								}
							}
						}
					}
				}
		}				
		else {
			for (int i = 0; i < scacchiera.length; i++) {
				for (int j = 0; j < scacchiera.length; j++) {
					if (scacchiera[i][j].getColoreCella()==CELLA_NERA) {
						int contaSpost=calcolaSpostamento(x,y,i,j);
						int dir= calcolaDirezione(x,y,i,j);
						if(scacchiera[x][y].getnPedine()>=contaSpost && verDiagonale(i,j,x,y)) {
							Mossa m= new Mossa(x,y,dir,contaSpost);
							if(dir==NORTH|| dir==NORTHEAST||dir==NORTHWEST){
								if(scacchiera[i][j].getnPedine() == 0 ) 
									BASE.add(m);
								else if (scacchiera[x][y].getColorePedina() == scacchiera[i][j].getColorePedina()) MERGE.add(m);
							}
							else {
									if(scacchiera[i][j].getColorePedina()==PEDINA_NERA) CAPTURE.add(m);
							}
						}
					}
				}
			}
		}
		
		System.out.println(BASE.size());
		System.out.println(MERGE);
		System.out.println(CAPTURE);
		return;
	}
	public void generatoreMosseAmm() {
		for (int i = 0; i < scacchiera.length; i++) {
			for (int j = 0; j < scacchiera.length; j++) {
				if (scacchiera[i][j].getColorePedina()!=VUOTA) {
					verificaMosseAmm(i,j);
					
				}
			}
		}
		
	}
	
	public HashMap<Integer,LinkedList<Mossa>> calcolaMossePossibiliDa(String posizione, Mossa m){
		int [] pos =calcola_indici(posizione);
		LinkedList<Mossa> BASE= new LinkedList<>();
		LinkedList<Mossa> MERGE= new LinkedList<>();
		LinkedList<Mossa> CAPTURE= new LinkedList<>();
		if(scacchiera[pos[0]][pos[1]].getColoreCella() == VUOTA) return null;
		else if(scacchiera[pos[0]][pos[1]].getColorePedina() == PEDINA_BIANCA){
			if(pos[0]==0 && pos[1]==3 && scacchiera[pos[0]][pos[1]].getnPedine()==12) {
				BASE.add(new Mossa((short) 0,(short) 3, (short) 0, 1));	
				BASE.add(new Mossa((short) 0,(short) 3, (short) 0, 1));
				BASE.add(new Mossa((short) 0,(short) 3, (short) 0, 1));
				BASE.add(new Mossa((short) 0,(short) 3, (short) 0, 1));
				BASE.add(new Mossa((short) 0,(short) 3, (short) 0, 1));
				BASE.add(new Mossa((short) 0,(short) 3, (short) 0, 1));
				BASE.add(new Mossa((short) 0,(short) 3, (short) 0, 1));
				BASE.add(new Mossa((short) 0,(short) 3, (short) 0, 1));
				BASE.add(new Mossa((short) 0,(short) 3, (short) 0, 1));
//					{{1,2},{2,1},{3,0},{2,4},{4,4},{6,4},{1,4},{2,5},{3,6}}
				
			}
		}
		return null;
	}
	
	public int checkVittoria() {
		if (STACK_BIANCO == 0)
			return VITTORIA_NERO;
		else if (STACK_NERO == 0)
			return VITTORIA_BIANCO;
		else if(MAX_MOSSE==0) {
			if(STACK_BIANCO>STACK_NERO) return VITTORIA_BIANCO;
			else return VITTORIA_NERO;
		}
		return NESSUNA_VITTORIA;
	}

	/**
	 * Funzione utile per debug, stampa la scacchiera. 
	 * Oss. Sia le pedine bianche che le nere si trovano su caselle di colore nero.
	 */
	public void stampaScacchiera() {
		System.out.println();
		System.out.println("CONFIGURAZINE SCACCHIERA:");
		System.out.println();
		int r, c;
		for (r = 0; r < SIZE; r++) {
			for (c = 0; c < SIZE; c++)
				System.out.print(" - ");
			System.out.println("-");
			for (c = 0; c < SIZE; c++) {
				if (scacchiera[r][c].getnPedine() == 0)
					if (scacchiera[r][c].getColoreCella() == CELLA_NERA)
						System.out.print(" N ");
					else
						System.out.print(" B ");
				if (scacchiera[r][c].getnPedine() > 0) {
					if (scacchiera[r][c].getColorePedina() == 0)
						System.out.print(scacchiera[r][c].getnPedine() + "B ");
					else
						System.out.print(scacchiera[r][c].getnPedine() + "N ");
				}
			}
			System.out.println("");
		}
		for (c = 0; c < SIZE; c++)
			System.out.print(" - ");
		System.out.println(" - ");
	}

	public static void main(String[] args) {
		Scacchiera s = new Scacchiera();
		// s.stampaScacchiera();
		// Cella b = s.scacchiera[0][3];
		// Cella a = s.scacchiera[7][4];
		// System.out.println(a);
		// System.out.println(b);
		// System.out.println("");
		// System.out.println(a);
		// System.out.println(b);
		// b.captureFrom(a, 12);
		System.out.println(s.muovi("H5", Scacchiera.NORTHWEST, 3));
		System.out.println(s.muovi("A4", Scacchiera.SOUTH, 2));
		//System.out.println(s.muovi("E2", Scacchiera.NORTHEAST, 2));
		//System.out.println(s.muovi("C4", Scacchiera.WEST, 2));
		//System.out.println(s.muovi("E2", Scacchiera.NORTHEAST, 1));
		//System.out.println(s.muovi("C2", Scacchiera.SOUTHWEST, 1));
		//System.out.println(s.muovi("H5", Scacchiera.NORTH, 2));
		//System.out.println(s.muovi("D1", Scacchiera.SOUTHWEST, 1));
		System.out.println("stack nero=" + STACK_NERO);
		System.out.println("stack bianco="+ STACK_BIANCO);
		s.verificaMosseAmm(4,1);
		s.stampaScacchiera();
	}

}
