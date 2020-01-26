package DipoleHeuristics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import Dipole.Mossa;
import Dipole.ScacchieraBit;

public class NaiveHeuristic implements HeuristicInterface {

//	@Override
//	public int valuta(ScacchieraBit stato) {
//		int k=0;
//		for(int i=0;i<stato;i++) {
//			
//		}
//		return 0;
//	
	private int[] v = { 1, 7, 8, 14, 17, 23, 24, 30, 33, 39, 40, 46, 49, 55, 56, 62 };

	private HashMap<Integer, Integer> POSIZIONI_BORDI;

	public NaiveHeuristic() {
		POSIZIONI_BORDI = new HashMap<Integer, Integer>();
		for (int i : v) {
			POSIZIONI_BORDI.put(i, i);
		}
	}

	public int valuta(ScacchieraBit stato, int giocatore, Mossa prec) {
		
//		System.out.println("giocatore= "+ giocatore);
		
		int e = 0;
		byte[] listaPedine = stato.getListaPosizioni(giocatore);
		int giocatoreAdversary = 1 - giocatore;
		if (stato.nPedine(giocatore) == stato.nPedine(giocatoreAdversary)) {
			
			Mossa adversaryMove = stato.miMangiaGetMossa(prec, giocatore,stato);// mossa che mi mangia
//			System.out.println(adversaryMove);
			int pedinePerse = stato.calcolaSpostamento(prec.getiStart(), prec.getjStart(), prec.getiEnd(),
					prec.getjEnd());
//			System.out.println("pedine perse _____"+ pedinePerse);
			int spost = adversaryMove != null ? stato.calcolaSpostamento(adversaryMove.getiStart(), adversaryMove.getjStart(),
					adversaryMove.getiEnd(), adversaryMove.getjEnd()):-1;
			if (adversaryMove != null && stato.checkMosseIndietro(adversaryMove, giocatoreAdversary)) {// avversario mangia e torna indietro
				e = e - 21;
//				System.out.println("qui  1  ");
			}
			if (adversaryMove != null && pedinePerse > 1) {
				e=e-41;

			}
				
//			FARE SEMPRE UN MERGE VICINO ----> MOLTA IMPORTANZA SE NO SI SPOSTA TROPPO IN AVANTI
			
			if (adversaryMove != null && spost > 3 && pedinePerse <= 1) {
//				System.out.println("qui  2  ");
				int numMosseMangianti = stato.generaMosseSenzaCheck(adversaryMove,giocatoreAdversary);
//				System.out.println("quanto mangia all indietro andando li "+ numMosseMangianti);
				if (numMosseMangianti <= 14 && numMosseMangianti > 8) {
//					System.out.println("qui  3  ");
					e = e + 35; //secondo me non va bene
				}
				if (numMosseMangianti <= 8) {
//					System.out.println("qui  4 ");
					e = e + 21;
				}

			}
			if (prec.getTipo() == 1) {
				e = e + 21;
//				System.out.println("qui  5  ");
			}

			for (int pedina = 0; pedina < stato.getNumeroStackGiocatore(giocatore); pedina++) {

				byte pos = listaPedine[pedina];
				int nPedine = stato.getNumeroPedine(pos / 8, pos % 8);

				if (giocatore == 1) {
					if (pos < 32) {
						e += nPedine;
//						System.out.println("qui  6  ");
					}
					else {
						e -= nPedine;
//						System.out.println("qui  7  ");
					}
				} else {
					if (pos >= 32) {
//						System.out.println("qui  8  ");
						e += nPedine;
					}
						
					else {
//						System.out.println("qui  9  ");
						e -= nPedine;
					}
						
						
				}

				if (nPedine < 2) {
//					System.out.println("qui  10  ");
					e = e - 1;
				}
					
				else {
//					System.out.println("qui  11 ");
					e = e + 1;
				}
					

				if (POSIZIONI_BORDI.containsKey(pos)) {
//					System.out.println("qui  12 ");
					e += 2 + nPedine;
				}
			}

		}
		
		return e;
	}

	@Override
	public int valuta(ScacchieraBit stato, ArrayList<Mossa> mosse, int giocatore) {
		// TODO Auto-generated method stub
		return 0;
	}

	private int perturbazioneRandom() {
		Random r = new Random();
		return r.nextInt(100) - 50;
	}

	@Override
	public int valuta(ScacchieraBit stato, int giocatore) {
		int altroGiocatore = giocatore == 1 ? 0 : 1; // si pu� calcolare da stato TODO
		int k = 0, nStack = stato.getNumeroStackGiocatore(altroGiocatore);
		byte[] listaPedine = stato.getListaPosizioni(altroGiocatore);
		for (int i = 0; i < nStack; i++) {
			k += stato.getScacchiera().getValue((int) listaPedine[i]);
		}
		return k;
	}

}
