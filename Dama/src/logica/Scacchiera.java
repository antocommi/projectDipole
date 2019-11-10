package logica;

import java.io.*;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;


/** Questa classe rappresenta la scacchiera come situazione a riposo,
 * la sua sottoclasse Gioco definira' anche come si possono muovere
 * i vari pezzi presenti sulla scacchiera.<BR>
 * La scacchiera ha 8x8 caselle alterate bianche e nere.
 * Nelle sole caselle nere puo' essere presente un pezzo, che
 * puo' essere una pedina o una dama, di colore bianco o nero.
 */ 
public class Scacchiera implements Serializable{
	
	
	
	  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** Numero di caselle (sia bianche che nere) su ciascun lato
	  * della scacchiera, vale 8. */
   	  public final int DIM_LATO = 8;

	  /** Codifica il contenuto di una casella vuota. */
	  public final int VUOTA = 0;
	  /** Codifica il contenuto di una casella con pedina bianca. */
	  public final int PEDINA_BIANCA = 1;
	  /** Codifica il contenuto di una casella con pedina nera. */
	  public final int PEDINA_NERA = 2;
	  /** Codifica il contenuto di una casella con dama bianca. */
	  public final int DAMA_BIANCA = 3;
	  /** Codifica il contenuto di una casella con dama nera. */
	  public final int DAMA_NERA = 4;

	  /** Codifica il colore inesistente (ritornato per esempio come 
	   * il colore del pezzo presente in una casella vuota. */
	  public final int NON_COLORE = 0;
	  /** Codifica il colore bianco. */
	  public final int BIANCO = 1;
	  /** Codifica il colore nero. */
	  public final int NERO = 2;
	  
	  public int aChiTocca;
	  
	  public int pezziBianchiMangiati;
	  public int pezziNeriMangiati;

	  public int mosse;
	  
	  /** Matrice bidimensionale di interi, viene dimensionata a 8x8
	   * righe e colonne, ogni elemento della matrice memorizza un intero
	   * che codifica il pezzo contenuto nella casella corrispondente. */
	  protected int[][] contenutoCaselle;
	  
	  

	  /** Costruisce la scacchiera mettendola nello stato iniziale. */  
	  public Scacchiera(){
	    contenutoCaselle = new int[DIM_LATO][DIM_LATO];
	    statoIniziale();
	  }
	  
	  
	  
	  /** Controlla, date riga e colonna, se una casella e' nera. */
	  public boolean eNera(int riga, int colonna){  
		  return ( (riga % 2) == (colonna % 2) );  
	  }
	  
	  

	  /** Controlla, se una casella e' nera. */
	  public boolean eNera(Casella casella){
		  return eNera(casella.riga,casella.colonna);  
	  }
	  
	  
	  
	  /** Ritorna il colore di un pezzo, dato il codice del pezzo. */
	  public int colore(int pezzo)
	  {
	    switch(pezzo)
	    {
	      case PEDINA_BIANCA: case DAMA_BIANCA: return BIANCO;
	      case PEDINA_NERA: case DAMA_NERA: return NERO;
	    }
	    return NON_COLORE;
	  }
	  
	   

	  /** Ritorna il pezzo contenuto in una casella, date riga e colonna.
	   * @param r la riga, deve essere tra 0 e DIM_LATO cioe' 8
	   * @param c la colonna, deve essere tra 0 e DIM_LATO cioe' 8
	   */
	  public int contenuto(int r, int c) {  return contenutoCaselle[r][c];  }

	  /** Ritorna il pezzo contenuto in una casella.
	   * @param cas la casella, che deve avere riga e colonna
	   * comprese tra 0 e DIM_LATO cioe' 8
	   */
	  public int contenuto(Casella cas){
		  if(cas==null){
			  return -1;
		  }
		  return contenutoCaselle[cas.riga][cas.colonna];
		  
	  }

	  /** Mette il pezzo assegnato nella casella di riga e colonna date.
	   * Ritorna true se e' stato possibile metterlo, false altrimenti.
	   * @param r la riga, deve essere tra 0 e DIM_LATO cioe' 8
	   * @param c la colonna, deve essere tra 0 e DIM_LATO cioe' 8
	   * @param pezzo il codice del pezzo che deve essere uno fra: VUOTA,
	   * PEDINA_BIANCA, PEDINA_NERA, DAMA_BIANCA, DAMA_NERA
	   */
	  public boolean inserisci(int r, int c, int pezzo){
	  
	    if ( (pezzo>=0) && (pezzo<=4) )
	    {  contenutoCaselle[r][c] = pezzo; return true;  }
	    else return false;
	  }  

	  /** Mette il pezzo assegnato nella casella data.
	   * Ritorna true se e' stato possibile metterlo, false altrimenti.
	   * @param cas la casella, che deve avere riga e colonna 
	   * compresze tra 0 e DIM_LATO cioe' 8
	   * @param pezzo il codice del pezzo che deve essere uno fra: VUOTA,
	   * PEDINA_BIANCA, PEDINA_NERA, DAMA_BIANCA, DAMA_NERA
	   */
	  public boolean inserisci(Casella cas, int pezzo){
		  return inserisci(cas.riga, cas.colonna, pezzo);  
	  }

	  /** Controlla, date riga e colonna, se una casella e' dentro i
	   * limiti della scacchiera.
	   */
	  public boolean eDentro(int riga, int colonna){
		  return ( (riga>=0) && (riga<DIM_LATO) && (colonna>=0) && (colonna<DIM_LATO) );
	  }
	  
	  /** Controlla, se una casella e' dentro i limiti della scacchiera.
	   */
	  public boolean eDentro(Casella c)  {  return eDentro(c.riga,c.colonna);  }

	  /** Controlla se una casella si trova sul bordo opposto a quello
	   * da sui e' partito il giocatore del colore dato.
	   */
	  public boolean bordoOpposto(Casella c, int col){
	    switch (col)
	    {
	      case NERO: return ( c.riga==(DIM_LATO-1) );
	      case BIANCO: return ( c.riga==0 );
	    }
	    return false;
	  }
	  
	  public boolean bordoOpposto(int riga, int colonna, int col){
		  Casella c=new Casella(riga,colonna);
		  return bordoOpposto(c,col);
	  }
	  
	  /** Ritorna il codice della dama dello stesso colore del pezzo.
	   * Se il pezzo non esiste (codifica la casella vuota) lo ritorna.
	   */
	  public int promossaDama(int pezzo){
		  switch (pezzo)
		    {
		      case PEDINA_NERA: case DAMA_NERA: return DAMA_NERA; 
		      case PEDINA_BIANCA: case DAMA_BIANCA: return DAMA_BIANCA;
		    }
		    return VUOTA;
	  }
	  
	  
	  
	  public void salva(String nomeFile){
		  String dir="./salvataggi/"+nomeFile;
		  ObjectOutputStream oos;
			try {
				oos = new ObjectOutputStream(new FileOutputStream(dir));
				oos.writeObject(this);
				oos.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				return;
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
				
				
		  
	  }
	  
	  
	  public Scacchiera ripristina() 
	  {
		  ObjectInputStream ois;
		  Scacchiera s=null;
		  String nomeFile=null; 
		  JFileChooser jfc=new JFileChooser("./salvataggi/");
		  jfc.setLocation(100,100);
		  int val=jfc.showOpenDialog(null);
		  if(val == JFileChooser.APPROVE_OPTION){
			 nomeFile=jfc.getSelectedFile().getAbsolutePath();
		  }
		  else if(val == JFileChooser.CANCEL_OPTION){	
			  JOptionPane.showMessageDialog(null, "Hai annullato la scelta del file");
			  return null;
		  }
		  
		  try {
			  ois = new ObjectInputStream(new FileInputStream(nomeFile));
			  s= ((Scacchiera)ois.readObject());
			  ois.close();
		  } catch (IOException e) {
			  e.printStackTrace();
			  return null;
		  } catch (ClassNotFoundException e) {
			  e.printStackTrace();
			  return null;
		  } catch (ClassCastException e){
			  JOptionPane.showMessageDialog(null, "Non hai selezionato un file valido");
			  return null;
		  }
		  return s;	
	  }
	  
	  
	  
	  /** Mette la scacchiera nello stato iniziale: le 12 pedine del giocatore
	   * nero stanno sulle caselle nere delle prime tre righe (cioe' quelle in
	   * alto), le 12 pedine del giocatore bianco stanno sulle caselle nere
	   * delle ultime tre righe (cioe' quelle in basso).
	   */
	  public void statoIniziale(){
		   int r, c;
		    for (r=0; r<DIM_LATO; r++)
		    for (c=0; c<DIM_LATO; c++)
		    {
		      if (eNera(r,c))
		      {
		         if (r<3) // le tre righe in alto
		           contenutoCaselle[r][c] = PEDINA_NERA;
		         else if (r>4) // le tre righe in basso
		           contenutoCaselle[r][c] = PEDINA_BIANCA;
		         else contenutoCaselle[r][c] = VUOTA; // le 2 righe centrali
		      }
		      else contenutoCaselle[r][c] = VUOTA; // caselle bianche
		    }
		    aChiTocca=BIANCO;
		    pezziBianchiMangiati=0;
		    pezziNeriMangiati=0;
		    mosse=0;
	  }
	  
	  
	  
	  public void riprendiPartita(Scacchiera s){
		  if(s==null) return;
		  this.contenutoCaselle =s.contenutoCaselle;
		  this.aChiTocca= s.aChiTocca;
		  this.pezziBianchiMangiati= s.pezziBianchiMangiati;
		  this.pezziNeriMangiati= s.pezziNeriMangiati;
		  this.mosse=s.mosse;
	  }
	  
	  
	  
	  /** Funzione utile per debug, stampa la tastiera in formato testo, preceduta da un titolo.
	   * I vari pezzi sono rappresentati dai seguenti casatteri:
	   * b=pedina bianca, n=pedina nera, B=dama bianca, N=dama nera.
	   */
	  public void stampaScacchiera(){
		  System.out.println();
		    int r,c;
		    for (r=0; r<DIM_LATO; r++)
		    {
		      for (c=0; c<DIM_LATO; c++) System.out.print("--");
		      System.out.println("-");
		      for (c=0; c<DIM_LATO; c++)
		      {
		        switch (contenuto(r,c))
		        {
		          case PEDINA_BIANCA: System.out.print("|b"); break;
		          case PEDINA_NERA: System.out.print("|n"); break; 
		          case DAMA_BIANCA: System.out.print("|B"); break; 
		          case DAMA_NERA: System.out.print("|N"); break; 
		          case VUOTA: System.out.print("| "); break;
		        }
		      }
		      System.out.println("|");
		    }
		    for (c=0; c<DIM_LATO; c++)  System.out.print("--");
		    System.out.println("-");
	  }
	  
	  
}
