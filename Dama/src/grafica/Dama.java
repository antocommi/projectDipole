package grafica;

import java.awt.*;
import java.awt.event.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.io.*;
import java.net.URL;

import logica.*;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;
import javax.swing.*;

import java.util.*;
import java.util.Timer;


/**Questa classe gestisce la parte grafica del gioco.
 * @author Matteo Campisi & Giuseppe Placanica
 */
public class Dama extends JFrame {
	
	private static final long serialVersionUID = 1L;
	private Gioco logica;
	ScacchieraG scacchieraG;
	private IA ia;
	private Player player;
	MenuListener listener;
	CasellaG[][] cg = new CasellaG[8][8];
	private Casella da=null;
	private Casella a=null;
	Msg msg;
	FinePartita finePartita;
	String coloreGiocatore;
	private static final int COMPUTER=0;
	private static final int GIOCATORE=1;
	int avversario= COMPUTER;
	private static final int FACILE=0;
	private static final int DIFFICILE=1;
	int livello= FACILE;
	boolean timerAttivo= false;
	String testoRegole="la dama ";
	int pezziBianchiMangiati;
	int pezziNeriMangiati;
	private Classifica c=new Classifica();

/**Costruttore della classe Dama che provvede a costruire le due finestre principali.
 * @param logica : � un parametro di tipo Gioco, viene passato nel costruttore in modo tale da legare la parte logica alla parte grafica.
 */
	public Dama(Gioco logica)
	{
		
		this.logica=logica;
		ia = new IA(logica);
		player=new Player(logica);
		setTitle("Dama");
		
		listener = new MenuListener();
		
		//creazione barra dei menu
		JMenuBar menu = new JMenuBar();
		this.setJMenuBar(menu);
		
		//creazione menu
		JMenu file = new JMenu("File");
		menu.add(file);
		
		JMenu about= new JMenu("?");
		menu.add(about);
		
		//sub menu
		JMenu nuovaPartita = new JMenu("Nuova partita");
		nuovaPartita.addActionListener(listener);
		file.add(nuovaPartita);
		
			//sub menu nuovaPartita
			JMenu computer= new JMenu("vs Computer"); 
			computer.addActionListener(listener);
			nuovaPartita.add(computer);
			
				JMenuItem principiante= new JMenuItem("Principiante");
				principiante.addActionListener(listener);
				computer.add(principiante);
				
				JMenuItem esperto= new JMenuItem("Esperto");
				esperto.addActionListener(listener);
				computer.add(esperto);
				
		
			JMenuItem player= new JMenuItem("vs Player"); 
			player.addActionListener(listener);
			nuovaPartita.add(player);
			//fine sub menu
			
			
			
		
			
		//creazione item
		file.addSeparator();
		JMenuItem salva= new JMenuItem("Salva partita");
		salva.addActionListener(listener);
		file.add(salva);
		
		JMenuItem carica= new JMenuItem("Carica partita");
		carica.addActionListener(listener);
		file.add(carica);
		
		file.addSeparator();
		JMenuItem exit= new JMenuItem("Esci");
		exit.addActionListener(listener);
		file.add(exit);
		
		
		JMenuItem regole= new JMenuItem("Regole");
		regole.addActionListener(listener);
		about.add(regole);
		about.addSeparator();
		
		JMenuItem classifica =new JMenuItem("Classifica");
		classifica.addActionListener(listener);
		about.add(classifica);
		about.addSeparator(); 
		
		JMenuItem informazioni= new JMenuItem("Informazioni");
		informazioni.addActionListener(listener);
		about.add(informazioni);
		
		//creazione pannello Scacchiera
		scacchieraG = new ScacchieraG();
		add(scacchieraG);
		
		//creazione finestra Msg per le informazioni
		msg= new Msg();
		
		setLocation(50, 50);
		setSize(638, 675);
		setResizable(false);
		setVisible(true);
		setDefaultCloseOperation (JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(new CloseWarning());
		
		repaint();
	}
	
	
/** Finestra riguardante lo stato della partita.
 */
	@SuppressWarnings("serial")
	class Msg extends JFrame{
		
		JTextPane turno;
		JTextPane pezzi;
		JTextPane vs;
		
		/**Costruttore della finestra delle informazioni sullo stato della partita.
		 */
		public Msg(){
			setTitle("Info partita");
			setLocation(750, 200);
			setSize(200,300);
			setResizable(false);
			setVisible(true);
			setLayout(new BorderLayout());
			
			JPanel p0= new JPanel();
			p0.setBackground(Color.WHITE);
			add(p0, BorderLayout.NORTH);
			vs=new JTextPane();
			vs.setEditable(false);
			p0.add(vs);
			
			JPanel p1= new JPanel();
			p1.setBackground(Color.WHITE);
			add(p1);

			turno=new JTextPane();
			turno.setEditable(false);
			p1.add(turno);
			
			JPanel riempimentoC= new JPanel();
			add(riempimentoC, BorderLayout.SOUTH);
			riempimentoC.setBackground(Color.WHITE);
			
			JPanel p2= new JPanel();
			p2.setBackground(Color.WHITE);
			add(p2, BorderLayout.SOUTH);
			pezzi= new JTextPane();
			pezzi.setEditable(false);
			p2.add(pezzi);
			infoPartita();
			setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		}
		

		
		/**Imposta i valori della finestra delle informazioni della partita attuale.
		 */
		public void infoPartita()
		{
			coloreGiocatore= logica.s.aChiTocca== logica.s.BIANCO ? "Bianco" : "Nero";
			turno.setText("Tocca a "+ coloreGiocatore);
			String s;
			if(livello ==FACILE) s=" Livello facile"; else s="Livello difficile";
			pezzi.setText("pezzi bianchi mangiati: " + String.valueOf(logica.s.pezziBianchiMangiati)+ '\n'+ '\n' +
					"pezzi neri mangiati: " + String.valueOf(logica.s.pezziNeriMangiati));
			if(avversario==COMPUTER) vs.setText("VS Computer \n"+s);
			else vs.setText("VS Player");
		}
		
	}
	
	

	/** Finestra che appare alla fine di ogni partita comunicando l'esito.
	 */
	@SuppressWarnings("serial")
	class FinePartita extends JFrame{
		
		/** Costruttore della finestra FinePartita
		 */
		public FinePartita(){
			
			setTitle("Partita finita");
			setSize(400, 250);
			setLocationRelativeTo(scacchieraG);
			setVisible(true);
			setLayout(new BorderLayout());
			
			EsitoPartita testo= new EsitoPartita();
			testo.setBackground(Color.WHITE);
			add(testo);
			JPanel bottoni= new JPanel();
			bottoni.setBackground(Color.WHITE);
			add(bottoni, BorderLayout.SOUTH);
			JButton gioca= new JButton("Gioca ancora");
			bottoni.add(gioca);
			gioca.addActionListener(listener);
			
			JButton esci= new JButton("Esci dal gioco");
			bottoni.add(esci);
			esci.addActionListener(listener);
			this.repaint();
			if(avversario==COMPUTER){
				Audio audio;
				if(logica.vincitore==logica.s.BIANCO){
					if(livello==FACILE)
						audio= new Audio("TiPiaceVincereFacile.wav");
					else
						audio= new Audio("IlGladiatore.wav");
				}
				else audio= new Audio("Pollo.wav");
				try {
					audio.player();
				} catch (Throwable e) {
					e.printStackTrace();
				}
				
				inserisciInClassifica();
				
			}
			
		}
		
		
		
		/** Verifica se alla fine di una partita il giocatore ha realizzato un nuovo record e va ad inserirlo in classifica.
		 */
		private void inserisciInClassifica(){
			if(avversario==COMPUTER && logica.vincitore==logica.s.BIANCO){
				String nome=null;
				c.ripristina();
				if(c.nuovoRecord(logica.s.mosse)){
					for(;;){
						String input= JOptionPane.showInputDialog(scacchieraG,"Complimenti sei entrato in classifica \n " +
																	          "inserisci il tuo nome", null , JOptionPane.PLAIN_MESSAGE  );
						try{
							nome=input;
							if(nome==null || nome.equals(""))
								JOptionPane.showMessageDialog( scacchieraG, "Nome non valido");
							else break;
						}
						catch(RuntimeException e1){
							if(nome==null || nome.equals(""))
								JOptionPane.showMessageDialog( scacchieraG, "Nome non valido");
							}
					}
				}
				c.aggiungiRecord(nome);
			}
		}
			
	}

	/** Pannello in cui viene comunicato l'esito della partita.
	 */
	@SuppressWarnings("serial")
	class EsitoPartita extends JPanel
	{
		
		String mex;
		
		/** Costruttore della classe EsitoPartita
		 */
		public EsitoPartita(){
			setSize(390, 250);
			if(logica.vincitore== logica.s.NON_COLORE){
				mex=("Pareggio");
			}
			if(avversario==GIOCATORE && logica.vincitore==logica.s.NERO){
				mex=("Il Nero ha vinto!!!");
			}
			if(avversario==COMPUTER && logica.vincitore==logica.s.NERO){
				mex=("Hai perso!!!");
			}
			if(avversario==GIOCATORE && logica.vincitore==logica.s.BIANCO){
				mex=("Il Bianco ha vinto!!!");
			}
			if(avversario==COMPUTER && logica.vincitore==logica.s.BIANCO){
				mex=("Hai vinto!!!");
			}
		}
		
		/** Disegna sul pannello l'esito della partita.
		 */
		public void paintComponent(Graphics g){
			super.paintComponent(g);
			Graphics2D g2= (Graphics2D)g;
			Font f= new Font("Serif", Font.BOLD, 26);
			g2.setFont(f);
			FontRenderContext frc= g2.getFontRenderContext();
			Rectangle2D r= f.getStringBounds(mex, frc);
			double x= (getWidth()- r.getWidth())/2;
			double y= (getHeight()- r.getHeight())/2;
			double ascent= -r.getY();
			double baseY= y+ascent;
			g2.drawString(mex,(int)x, (int)baseY);
			g2.setPaint(Color.BLACK);
		}
	}
	
	
	/** Finestra contenente le regole del gioco.
	 */
	@SuppressWarnings("serial")
	class Regole extends JFrame{
		
		JTextArea regole;
		
		/** Costruisce la finestra delle Regole leggendo il testo da un file ".txt".
		 */
		public Regole(){
			setTitle("Regole");
			setLocation(150,70);
			setAlwaysOnTop(true);
			setSize(480,600);
			setResizable(false);
			setVisible(true);		
			setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			regole=new JTextArea();
			add(regole);
			InputStream path=Dama.class.getResourceAsStream("regole.txt");
			BufferedReader br=null;
			String linea =null;
			try {	
				br = new BufferedReader(new InputStreamReader(path));
				for(;;){
					linea =br.readLine();
					if(linea==null)break;
					regole.append("   "+linea+"\n");
				}	
				br.close();
			} catch (Exception e){
				e.printStackTrace();
				return;
			}
			
			regole.setEditable(false);
		}
	}
	
	/** Finestra contenente la classifica.
	 */
	@SuppressWarnings("serial")
	class ClassificaGrafica extends JFrame{
		
		/**Costruisce la classifica leggendo i valori dalla lista della classe Classifica contenuta nel package logica.
		 */
		public ClassificaGrafica(){
			JTextArea t= new JTextArea();			
			add(t);
			t.setEditable(false);
			setAlwaysOnTop(true);
			setLocation(750, 300);
			setSize(300,500);
			setResizable(false);
			setVisible(true);
			setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			if(c.ripristina()){
				int i=0;
				while(i<c.classifica.size()){
					t.append(i+1+")  "+c.classifica.get(i)+"\n");
					i++;
				}
			}else t.setText("********************CLASSIFICA VUOTA********************");
		}
		
	}

	/** Definisce il bottone all'interno della scacchiera.
	 */
	@SuppressWarnings("serial")
	class CasellaG extends JButton {

		private Image immagineCorrente;
		int riga;
		int colonna;
		Timer t=new Timer();
		long milliSecondi= 2500;
		long inizio;
		long fine;
		
		
		/**Costruisce la casella in base alla posizione e al pezzo contenuto.
		 * @param pari : verifica se una casella � bianca o nera;
		 * @param pezzo : indica il pezzo contenuto all'interno della casella;
		 * @param riga : coordinata x della casella;
		 * @param colonna : coordinata y della casella.
		 */
		public CasellaG(boolean pari, int pezzo, int riga, int colonna) {
			this.riga = riga;
			this.colonna = colonna;
			if(!pari) setBorderPainted(false);
			addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
				
					if (da == null ) {
						da = new Casella(Dama.CasellaG.this.riga, Dama.CasellaG.this.colonna);
						if(logica.s.contenuto(da)==logica.s.VUOTA || logica.s.colore(logica.s.contenuto(da))!= logica.s.aChiTocca) da=null;
						else if(avversario== COMPUTER){
							inizio= System.currentTimeMillis();
							t.schedule(new Tempo(), milliSecondi);
							timerAttivo=true;
						}
						if(logica.deveRimangiare && da.riga!=logica.vecchiaMossa.arrivo.riga ){ 
							da=null;
						}
						
					} else if (a == null) {
						a = new Casella(Dama.CasellaG.this.riga, Dama.CasellaG.this.colonna);
						fine= System.currentTimeMillis();
						if(da.equals(a)){ da=null;a=null;}
					}
			
					if(da!=null && a!=null){
						Mossa tmp=new Mossa(da,a);
						if(logica.mossaPossibile(tmp) ){
							if(timerAttivo && (fine-inizio)< milliSecondi)	t.cancel(); 
							
							timerAttivo=false;
							
							logica.scansione();
							if(logica.partitaFinita()) finePartita= new FinePartita();
							
							player.muovi(new Mossa (da,a));
							
							da=null;	
							a=null;
							logica.s.stampaScacchiera();
		
							coloreGiocatore=logica.s.aChiTocca== logica.s.BIANCO ? "Bianco" : "Nero";
							msg.pezzi.setText("pezzi bianchi mangiati: " + String.valueOf(logica.s.pezziBianchiMangiati)+ '\n'+ '\n' +
									"pezzi neri mangiati: " + String.valueOf(logica.s.pezziNeriMangiati));
							msg.turno.setText("Tocca a "+coloreGiocatore);
							
							if(avversario==COMPUTER && logica.s.aChiTocca==logica.s.NERO){
								Runnable r= new IARunnable();
								Thread t= new Thread(r);
								t.start();
							}
						}
						da=null; a=null;
					}

				}
			}); 
			
		}
		
		

		/** Disegna la casella e il suo contenuto.
		 */
		@Override
		public void paintComponent(Graphics g) {
			if(logica.s.contenuto(this.riga, this.colonna) == logica.s.PEDINA_BIANCA) {
				immagineCorrente = Toolkit.getDefaultToolkit().getImage(getClass().getResource("pedinaBianca.png"));
			}
			if (logica.s.contenuto(this.riga, this.colonna) == logica.s.PEDINA_NERA) {
				immagineCorrente = Toolkit.getDefaultToolkit().getImage(getClass().getResource("pedinaNera.png"));
			}
			if (logica.s.contenuto(this.riga, this.colonna) == logica.s.DAMA_BIANCA) {
				immagineCorrente = Toolkit.getDefaultToolkit().getImage(getClass().getResource("damaBianca.png"));
			}
			if (logica.s.contenuto(this.riga, this.colonna) == logica.s.DAMA_NERA) {
				immagineCorrente = Toolkit.getDefaultToolkit().getImage(getClass().getResource("damaNera.png"));
			}
			if (logica.s.contenuto(this.riga, this.colonna) == logica.s.VUOTA && logica.s.eNera(this.riga,this.colonna)){
				immagineCorrente =Toolkit.getDefaultToolkit().getImage(getClass().getResource("vuotaNera.png"));
			}
			if (logica.s.contenuto(this.riga, this.colonna) == logica.s.VUOTA && !logica.s.eNera(this.riga,this.colonna)){
				immagineCorrente =Toolkit.getDefaultToolkit().getImage(getClass().getResource("vuotaBianca.png"));
			}
			
			g.drawImage(immagineCorrente,0 , 0, null );
			scacchieraG.repaint();
		}
	}
	
	

	/** Pannello contenuto nella finestra Dama in cui viene creata la scacchiera.
	 */
	@SuppressWarnings("serial")
	class ScacchieraG extends JPanel
	{
		
		/** Costruttore della classe Scacchiera
		 */
		public ScacchieraG() {
			statoIniziale();
			disegnaScacchiera();
		}

		/** Imposta la scacchiera nello stato iniziale.
		 */
		public void statoIniziale() {
			for (int i = 0; i < 8; i++) {
				for (int j = 0; j < 8; j++) {
					if (i < 3)
						cg[i][j] = new CasellaG((i + j) % 2 == 0, logica.s.PEDINA_NERA, i, j);
					if (i > 4)
						cg[i][j] = new CasellaG((i + j) % 2 == 0, logica.s.PEDINA_BIANCA, i, j);
					if (i == 3 || i == 4)
						cg[i][j] = new CasellaG((i + j) % 2 == 0, logica.s.VUOTA, i, j);

				}
			}
		}

		/** Disegna la scacchiera facendo riferimento alla matrice cg in cui sono contenute le caselle grafiche.
		 */
		public void disegnaScacchiera() {
			setLayout(new GridLayout(8, 8, 0, 0));
			for (int i = 0; i < 8; i++) {
				for (int j = 0; j < 8; j++) {
					add(cg[i][j]);
				}
			}
		}

	}
	
	

	/** Timer che deseleziona la casella selezionata dal giocatore dopo un lasso di tempo.
	 */
	class Tempo extends TimerTask {
        public void run() {
        	da=null;
        	a=null;
        	timerAttivo= false;
        }
    }
	
	

	/** Gestisce le voci dei menu
	 */
	class MenuListener implements ActionListener {

		@SuppressWarnings("static-access")
		public void actionPerformed(ActionEvent e) {
			
			//Nuova partita vs Player
			if(e.getActionCommand()=="vs Player"){
				avversario= GIOCATORE;
				logica.s.statoIniziale();
				msg.infoPartita();
			}
			
			//Nuova partita vs Computer livello principiante
			if(e.getActionCommand()=="Principiante"){
				avversario=COMPUTER;
				livello=FACILE;
				logica.s.statoIniziale();
				msg.infoPartita();
			}
			
			//Nuova partita vs Computer livello esperto
			if(e.getActionCommand()=="Esperto"){
				avversario=COMPUTER;
				livello=DIFFICILE;
				logica.s.statoIniziale();
				msg.infoPartita();
			}

			//salva
			if(e.getActionCommand()=="Salva partita")
			{
				String nomeFile=null;
				for(;;){
					String input= JOptionPane.showInputDialog("Inserire nome file");
					try{
						nomeFile=input;
						break;
					}
					catch(RuntimeException e1){JOptionPane.showMessageDialog(scacchieraG, "Nome non valido");}
				}
				if(!nomeFile.equals("")) logica.s.salva(nomeFile);
			}
			
			//carica
			if(e.getActionCommand()=="Carica partita"){
				logica.Scacchiera tmp= logica.s.ripristina();
				logica.s.riprendiPartita(tmp);
				msg.infoPartita();
			}
			
			if(e.getActionCommand()=="Gioca ancora"){
				finePartita.dispose();
				logica.s.statoIniziale();
				msg.infoPartita();
				coloreGiocatore="Bianco";
			}
			
			//esci
			if(e.getActionCommand()=="Esci dal gioco"){
				System.exit(0);
			}
			
			//esci con conferma
			if(e.getActionCommand()=="Esci"){
				int uscita = JOptionPane.showConfirmDialog(scacchieraG,"Sei sicuro di voler abbandonare la partita?", "Conferma uscita",JOptionPane.YES_NO_OPTION);
				if(uscita==JOptionPane.YES_OPTION) System.exit(0);
			}
			
			
			if(e.getActionCommand()=="Regole"){
				new Regole();
			}
			
			if(e.getActionCommand()=="Classifica"){
				new ClassificaGrafica();
			}
			
			if(e.getActionCommand()=="Informazioni"){
				JOptionPane infoDama=new JOptionPane();
				infoDama.showMessageDialog(scacchieraG, "<html><h1><i>Dama 1.0</i></h1><hr> Author Giuseppe Placanica & Matteo Campisi</html>" 
						, "about", JOptionPane.PLAIN_MESSAGE );
				infoDama.setVisible(true);
			}
		
		}

	}
	
	
	
	/** Gestisce la chiusura della finestra principale effettuata dal bottone rosso.
	 */
	class CloseWarning extends WindowAdapter{
	      public void windowClosing (WindowEvent e){
	    	  int confermaUscita = JOptionPane.showConfirmDialog (Dama.this, "Sei sicuro di voler abbandonare la partita?","Conferma uscita", JOptionPane.YES_NO_OPTION);
	          if(confermaUscita== JOptionPane.YES_OPTION) System.exit (0);
	      }  
	}
	
	
	/**Runnable IA
	 */
	class IARunnable implements Runnable{
		public void run(){
			if(logica.partitaFinita()){
				finePartita= new FinePartita();
				return;
			}
			try {
				Thread.sleep(2200);
			} catch (InterruptedException e) {
				System.exit(-1);
			}
			if(livello==FACILE) ia.muoviIA1(); 
			else ia.muoviIA2();
			scacchieraG.repaint();
			logica.s.stampaScacchiera();

			coloreGiocatore= logica.s.aChiTocca== logica.s.BIANCO ? "Bianco" : "Nero";
			msg.pezzi.setText("pezzi bianchi mangiati: " + String.valueOf(logica.s.pezziBianchiMangiati)+ '\n'+ '\n' +
					"pezzi neri mangiati: " + String.valueOf(logica.s.pezziNeriMangiati));
			if(logica.partitaFinita()) finePartita= new FinePartita();
			else msg.turno.setText("Tocca a "+coloreGiocatore);

		}
	}
	
	
	
	/** Permette di riprodurre le canzoni
	 */
	class Audio{
		
		String nomeFile;
		
		public Audio(String nomeFile){
			this.nomeFile=nomeFile;
		}
		
		public void player()throws Throwable{
			URL url= Dama.class.getResource(nomeFile);
			AudioInputStream in= AudioSystem.getAudioInputStream(url);
			DataLine.Info info= new DataLine.Info(SourceDataLine.class, in.getFormat());
			SourceDataLine line=(SourceDataLine) AudioSystem.getLine(info);
			line.open(in.getFormat());
			line.start();
			try{
				int buffer = 800;
				byte []buf=new byte [buffer];
				while(true){
					int read=in.read(buf);
					if(read==-1)break;
					line.write(buf,0,read);
				}
			}
			catch(Exception e){e.printStackTrace();}
			line.drain();
			line.close();
		}
		
		public void player(int numVolte)throws Throwable{
			for(int i=0; i<numVolte; i++)
				player();
		}

	}


}

