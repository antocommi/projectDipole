package DipoleHeuristics;

import java.util.HashMap;
import java.util.Random;

import Dipole.Mossa;
import Dipole.ScacchieraBit;

public class NaiveHeuristic implements HeuristicInterface {

	private int[] v = { 1, 7, 8, 14, 17, 23, 24, 30, 33, 39, 40, 46, 49, 55, 56, 62 };

	private HashMap<Integer, Integer> POSIZIONI_BORDI;

	public NaiveHeuristic() {
		POSIZIONI_BORDI = new HashMap<Integer, Integer>();
		for (int i : v) {
			POSIZIONI_BORDI.put(i, i);
		}
	}

	@Override
	public int valuta(ScacchieraBit stato, int giocatore, Mossa prec) {

//		System.out.println("giocatore= "+ giocatore);

		int e = 0;
		byte[] listaPedine = stato.getListaPosizioni(giocatore);
		int giocatoreAdversary = 1 - giocatore;
//		if (stato.nPedine(giocatore) == stato.nPedine(giocatoreAdversary)) {

		Mossa adversaryMove = miMangiaGetMossa(prec, giocatore, stato);// mossa che mi mangia
		int pedinePerse = stato.calcolaSpostamento(prec.getiStart(), prec.getjStart(), prec.getiEnd(), prec.getjEnd());
//			System.out.println("pedine perse _____"+ pedinePerse);
		int spost = adversaryMove != null
				? stato.calcolaSpostamento(adversaryMove.getiStart(), adversaryMove.getjStart(),
						adversaryMove.getiEnd(), adversaryMove.getjEnd())
				: -1;
		if (adversaryMove != null && checkMosseIndietro(adversaryMove, giocatoreAdversary)) {// avversario mangia
//			System.out.println(adversaryMove.oldtoString());																					// e torna indietro
			e = e - 21;
//				System.out.println("qui  1  ");
		}
		if (adversaryMove != null) {
			e = e - 21;

		}

//			FARE SEMPRE UN MERGE VICINO ----> MOLTA IMPORTANZA SE NO SI SPOSTA TROPPO IN AVANTI

		if (adversaryMove != null && spost > 1 && pedinePerse <= 1) {
//			System.out.println("qui  2  ");
			int numMosseMangianti = generaMosseSenzaCheck(adversaryMove, giocatoreAdversary, stato);
//				System.out.println("quanto mangia all indietro andando li "+ numMosseMangianti);
			if (numMosseMangianti <= 14 && numMosseMangianti > 8) {
//					System.out.println("qui  3  ");
				e = e - 20; // secondo me non va bene
			}
			if (numMosseMangianti <= 8) {
//					System.out.println("qui  4 ");
				e = e - 15;
			}

		}
		if (prec.getTipo() == 1) {
			e = e + 12;
//				System.out.println("qui  5  ");
		}

		for (int pedina = 0; pedina < stato.getNumeroStackGiocatore(giocatore); pedina++) {

			byte pos = listaPedine[pedina];
			int nPedine = stato.getNumeroPedine(pos / 8, pos % 8);

			if (giocatore == 1) {
				if (pos < 32) {
					e += 2;
//						System.out.println("qui  6  ");
				} else {
					e -= 1;
//						System.out.println("qui  7  ");
				}
			} else {
				if (pos >= 32) {
//						System.out.println("qui  8  ");
					e += 1;
				}

				else {
//						System.out.println("qui  9  ");
					e -= 1;
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
				e = e + 2;
			}
		}

//		}
		int eu = e + perturbazioneRandom();
//		System.out.println("Valore Euristica " +eu + " Mossa "+prec.oldtoString());
		return -eu;
	}

	public int valutaDepht(ScacchieraBit stato, int giocatore, Mossa prec) {

//		System.out.println("giocatore= "+ giocatore);		
		int e = 0;
		byte[] listaPedine = stato.getListaPosizioni(giocatore);
		int giocatoreAdversary = 1 - giocatore;
//		if (stato.nPedine(giocatore) == stato.nPedine(giocatoreAdversary)) {
		Mossa adversaryMove = miMangiaGetMossa(prec, giocatore, stato);// mossa che mi mangia
//		if (deph == 1)
//			System.out.println(adversaryMove);
		int pedinePerse = stato.calcolaSpostamento(prec.getiStart(), prec.getjStart(), prec.getiEnd(), prec.getjEnd());
//			System.out.println("pedine perse _____"+ pedinePerse);
		int spost = 0;
		if (adversaryMove != null) {
//			System.out.println(adversaryMove.oldtoString());
			spost = stato.calcolaSpostamento(adversaryMove.getiStart(), adversaryMove.getjStart(),
					adversaryMove.getiEnd(), adversaryMove.getjEnd());
		}

		// TODO: Da Fare
//		Eseguire merge vicino ----> MOLTA IMPORTANZA SE NO SI SPOSTA TROPPO IN AVANTI

		// Avversario Mangia e torna indietro
		if (adversaryMove != null && checkMosseIndietro(adversaryMove, giocatoreAdversary)) {
			e = e - 21;
//				System.out.println("qui  1  ");
		}

		if (adversaryMove != null) {
			e = e - 21;
		}

		if (adversaryMove != null && spost > 1 && pedinePerse <= 1) {
//			if (deph == 1)
//				System.out.println("qui  2  ");
			int numMosseMangianti = generaMosseSenzaCheck(adversaryMove, giocatoreAdversary, stato);
//				System.out.println("quanto mangia all indietro andando li "+ numMosseMangianti);

			// Le posizioni che l'avversaro occupa sono in questo intervallo
			if (numMosseMangianti <= 14 && numMosseMangianti > 8) {
//				if(deph==1)System.out.println("QUI �"+ numMosseMangianti);
//					System.out.println("qui  3  ");
				e = e - 20; // secondo me non va bene
			}
			if (numMosseMangianti <= 10) {
//				if(deph==1)System.out.println("QUI 70");
//					System.out.println("qui  4 ");
				e = e - 15;
			}

		}
		if (prec.getTipo() == 1) {
			e = e + 6;
//				System.out.println("qui  5  ");
		}

		for (int pedina = 0; pedina < stato.getNumeroStackGiocatore(giocatore); pedina++) {
			byte pos = listaPedine[pedina];
			int nPedine = stato.getNumeroPedine(pos / 8, pos % 8);

			if (giocatore == 1) {
				if (pos < 32) {
					e += 2;
//						System.out.println("qui  6  ");
				} else {
					e -= 1;
//						System.out.println("qui  7  ");
				}
			} else {
				if (pos >= 32) {
//						System.out.println("qui  8  ");
					e += 1;
				}

				else {
//						System.out.println("qui  9  ");
					e -= 1;
				}
			}

			if (nPedine < 2) {
//					System.out.println("qui  10  ");
				e = e - 4;
			}

			else {
//					System.out.println("qui  11 ");
				e = e + 4;
			}

			if (POSIZIONI_BORDI.containsKey(pos)) {
//					System.out.println("qui  12 ");
				e = e + 2;
			}
		}

//		}
//		if(deph == 1) System.out.println("VAL EURISTICA  "+ (e));
		return e + perturbazioneRandom();
	}

	@SuppressWarnings("unused")
	private int perturbazioneRandom() {
		Random r = new Random();
		return r.nextInt(12) - r.nextInt((6));
	}

//	private int perturbazioneRandom() {
//		Random r = new Random();
//		int soglia = r.nextInt(100);
//		if (soglia > 60 && soglia < 90) {
//			return r.nextInt(16) - 6;
//		}
//		if (soglia > 90) {
//			return r.nextInt(20) - r.nextInt(10);
//		}
//		if (soglia < 60) {
//			return r.nextInt(6) - 3;
//		}
//		return r.nextInt(12) - 4;
//	}

}
