package DipoleHeuristics;

import java.util.ArrayList;
import java.util.HashMap;

import Dipole.Mossa;
import Dipole.ScacchieraBit;

public class Anto_Heuristic implements HeuristicInterface {
	private int[] v = { 1, 7, 8, 14, 17, 23, 24, 30, 33, 39, 40, 46, 49, 55, 56, 62 };
	private HashMap<Integer, Integer> POSIZIONI_BORDI;

	public Anto_Heuristic() {
		POSIZIONI_BORDI = new HashMap<Integer, Integer>();
		for (int i : v) {
			POSIZIONI_BORDI.put(i, i);
		}
	}

	@Override
	public int valuta(ScacchieraBit stato, int giocatore, Mossa prec) {
		return 0;
	}

	@Override
	public int valuta(ScacchieraBit stato, int giocatore, Mossa prec, ScacchieraBit oldBoard) {

		int e = 0;
		byte[] listaPedine = stato.getListaPosizioni(giocatore);
		int giocatoreAdversary = 1 - giocatore;
		Mossa advMove = null;
		int numPedineSpostateNOSTRE = stato.calcolaSpostamento(prec.getiStart(), prec.getjStart(), prec.getiEnd(),
				prec.getjEnd());

//		if (stato.nPedine(giocatore) == stato.nPedine(giocatoreAdversary)) {
		ArrayList<Mossa> listaMosseMiMangia = miMangiaGetMossa(prec, giocatore, oldBoard);

		if (listaMosseMiMangia != null && listaMosseMiMangia.size() != 0) {
			int val = 0;
			int max = 0;
			for (Mossa m : listaMosseMiMangia) {
				
				val = stato.calcolaSpostamento(m.getiStart(), m.getjStart(), m.getiEnd(), m.getjEnd())
						- numPedineSpostateNOSTRE;
				if (prec.getiEnd() == m.getiEnd() && prec.getjEnd() == m.getjEnd() && val > max) {
					max = val;
					advMove = m;
				}
			}
		}

//		System.out.println("");
//		System.out.println("________________________________");
//		if (advMove != null) {
//			System.out.println("MOSSA AVVERSARIO " + advMove.oldtoString());
//		}
//		System.out.println("MOSSA " + prec.oldtoString());
//		System.out.println("________________________________");
//		System.out.println("");

		int spostamentoAvversarioMossaTop = 0;
		if (advMove != null) {
			spostamentoAvversarioMossaTop = stato.calcolaSpostamento(advMove.getiStart(), advMove.getjStart(),
					advMove.getiEnd(), advMove.getjEnd());
		} else {
			spostamentoAvversarioMossaTop = -1;
		}
		
		// VALUTIAMO UNA MOSSA ANCHE IN BASE ALLO SPOSTAMENTO
		boolean possoMangiareENonMangia = possoMangiareENonMiMangia(prec, giocatore, oldBoard, listaMosseMiMangia);


		if (stato.checkPosOut(prec.getiEnd(), prec.getjEnd())) {
			e = e - 12;
		}

		// ---------------------------------------------------------------


		if (numPedineSpostateNOSTRE < 2 && possoMangiareENonMangia) {
			e = e + 7;
//			System.out.println("MI SPOSTO DI 2 MANGIANDO E NON MI MANGIA - numPedineSpostateNOSTRE: "
//					+ numPedineSpostateNOSTRE + " e:" + e);
		} else if (numPedineSpostateNOSTRE < 3 && possoMangiareENonMangia) {
			e = e + 5;
//			System.out.println("MI SPOSTO DI 3 MANGIANDO E NON MI MANGIA - numPedineSpostateNOSTRE: "
//					+ numPedineSpostateNOSTRE + " e:" + e);
		}
//		else {
//			e = e - 7;
//			System.out.println("ELSE MI SPOSTO E MANGIANDO E NON MI MANGIA - numPedineSpostateNOSTRE: "
//					+ numPedineSpostateNOSTRE + " e:" + e);
//		}
		

		// MOSSE IN CUI CI MANGIA
		if ((advMove != null || miMangiaStackRestante(prec, giocatore, stato))  && esisteMossaIndietro(listaMosseMiMangia, 1 - giocatore)) {
			e = e - 21;
//			System.out.println("MI MANGIA E TORNA " + "e: " + e);
		}
		if ((advMove != null || miMangiaStackRestante(prec, giocatore, stato)) ) {
			e = e - 21;
//			System.out.println("MI MANGIA");
		}
		

		if (advMove != null && spostamentoAvversarioMossaTop > 1 && numPedineSpostateNOSTRE <= 1) {
			int potenzaCella = powerCell(advMove, 1 - giocatore, stato);
			if (potenzaCella <= 14 && potenzaCella > 8) {
				e = e - 16;
//				System.out.println("MI MANGIA E SI EVOLVE - potenza:" + potenzaCella + " e:" + e);
			}
			if (potenzaCella <= 8) {
				e = e - 8;
//				System.out.println("MI MANGIA E SI EVOLVE POCO - potenza:" + potenzaCella + " e" + e);
			}
		}

//		if (advMove != null && potenzaCella > 14) {
//			e = e - 18;
//		}
//		else if (advMove != null && potenzaCella < 11 && potenzaCella > 8) {
//			e = e - 14;
//		} else if (advMove != null && potenzaCella <= 8) {
//			e = e - 8;
//		}

//		if (advMove != null && spostamentoAvversarioMossaTop > 3 && numPedineSpostateNOSTRE < 2) {
//			e = e + 8;
//		}

//		if (potenzaCella <= 8) {
//			e = e - 8;
//		}
//		

		// MOSSA MERGE
		if (prec.getTipo() == 1 && campoNostro(prec, giocatore)) {
			e = e + 12;
//			System.out.println("MERGE COMPO NOSTRO e: " + e);
		} else if (prec.getTipo() == 1) {
			e = e + 8;
//			System.out.println("MARGE CAMPO AVVERSARIO e: " + e);
		}

		if (prec.getTipo() == 1 && spostamentoAvversarioMossaTop > 1 ) {
			e = e - 21;
//			System.out.println("MERGE E MI MANGIA e: " + e);
		}
		
		if (possoMangiareENonMangia && !miMangiaStackRestante(prec, giocatore, stato)) {
			e = e + 20;
//			System.out.println("POSSO MANGIARE/NON MANGIA e NON MANGIA STACK " + e);
		}

		// Se posso mangiare una pedina con un numero consistente di pedine e da
		// scegliere
		if (possoMangiareENonMangia && !miMangiaStackRestante(prec, giocatore, stato)
				&& getNumeroPedineMangiateAdv(prec, stato) > 1) {
			e = e + 20;
//			System.out.println("POSSO MANGIARE/NON MANGIA e NON MANGIA STACK e MANGIO >2 PEDINE ADV " + e);
		}

		// Posso mangiare, non vengo mangiato, non mi sposto molto in avanti, non lascio
		// pedine scoperte nella posizone in cui mi trovavo

//		boolean esiteMossaIndietro=!esisteMossaIndietro(listaMosseMiMangia,1-giocatore );
//		if(esiteMossaIndietro ) {
//			e = e -20;
//			System.out.println("ESISTE MOSSA INDIETRO");
//		}
//		

		if (possoMangiareENonMangia && numPedineSpostateNOSTRE < 4 && !miMangiaStackRestante(prec, giocatore, stato)) {
			e = e + 21;
//			System.out.println("POSSO MANGIARE/NON MANGIA e NON MANGIA STACK e MI SPOSTO MAX 3 " + e);
		}
		if (possoMangiareENonMangia && !miMangiaStackRestante(prec, giocatore, stato)) {
			e = e + 10;
//			System.out.println("POSSO MANGIARE/NON MANGIA e NON MANGIA STACK " + e);
		}
//		if (miMangiaPochePedineEritornoAmangiarlo(prec, giocatore, stato)) {
//			e = e + 40;
//			System.out.println("MI MANGIA POCO, LO MANGIO E TORNO " + e);
//		}
		if (possoMangiareENonMangia && checkMosseIndietro(prec, giocatore)) {
			e = e + 21;
//			System.out.println("POSSO MANGIARE/NON MANGIA e CI SPOSTIAMO MAX 4 " + e);
		}
		if (possoMangiareENonMangia && numPedineSpostateNOSTRE < 5) {
			e = e + 5;
//			System.out.println("POSSO MANGIARE/NON MANGIA e CI SPOSTIAMO MAX 4 " + e);
		}

		if (advMove != null && possoMangiare(advMove, giocatore, stato) && numPedineSpostateNOSTRE > 3) {
			e = e - 60;
//			System.out.println("POSSO MANGIARE E MI SPOSTO DA 4 IN SU " + e);
		}

//		if (possoMangiare(prec, giocatore, oldBoard) && advMove!=null) {
//			e = e - 60;
////			System.out.println("POSSO MANGIARE E ma mi mangia" + e);
//		}

		for (int pedina = 0; pedina < stato.getNumeroStackGiocatore(giocatore); pedina++) {

			byte pos = listaPedine[pedina];
			int x = pos / 8;
			int y = pos % 8;
			int nPedine = stato.getNumeroPedine(x, y);

			// CONTROLLIAMO IL NUMERO DI PEDINE VICINE
//			if (nPedine > 1) {
//				int numeroPedineCornice = cornice(x, y, giocatore, stato);
//				if (numeroPedineCornice == 1) {
//					e = e + 6;
//				}
//				if (numeroPedineCornice == 2) {
//					e = e + 4;
//				}
//				if (numeroPedineCornice > 2) {
//					e = e - 2;
//				}
//			}

			if (miMangiaStackCurr(x, y, giocatore, stato)) {
				e = e - 12;
			}
			if (giocatore == 1) {
				if (pos < 32) {
					e += 2*nPedine;
//					System.out.println(" SONO NERO CAMPO MIO e: " + e);
				} else {
					e -= 2*nPedine;
//					System.out.println(" SONO NERO CAMPO ADV e: " + e);
				}
			} else {
				if (pos >= 32) {
					e += 2*nPedine;
//					System.out.println(" SONO BIANCO CAMPO MIO e: " + e);
				}

				else {
					e -= 2*nPedine;
//					System.out.println(" SONO NERO CAMPO ADV e: " + e);
				}
			}

			if (nPedine < 2) {
				e = e - 1;
//				System.out.println(" NUM PEDINA == 1 -- e:" + e);
			}

			else {
				e = e + 1;
//				System.out.println("NUM PEDINE > 2 -- e:" + e);
			}

			if (POSIZIONI_BORDI.containsKey((Object) pos)) {
				e = e + 2*nPedine;
//				System.out.println("SIAMO AL BORDO -- e" + e);
			}
		}
		for (int pedina = 0; pedina < oldBoard.getNumeroStackGiocatore(giocatore); pedina++) {
			byte pos = listaPedine[pedina];
			int x = pos / 8;
			int y = pos % 8;
	
//			if (miMangia(x, y, giocatore, oldBoard) && (prec.getiEnd() == x && prec.getjEnd() == y)
//					&& !miMangia(x, y, giocatore, stato)) {
//				e = e + 30;
//			}
//			
//			if (miMangia(x, y, giocatore, oldBoard) && (prec.getiStart() == x && prec.getjStart() == y) 
//					&& !miMangia(x, y, giocatore, stato)
//					) {
//
//				e = e + 30;
//				System.out.println("qui");
//				
//			}
//			if (miMangia(x, y, giocatore, oldBoard) && prec.getiStart() == x && prec.getjStart() == y &&
//					stato.checkPosOut(prec.getiEnd(), prec.getjEnd()) && numPedineSpostateNOSTRE<3)
//				e = e + 20;
//		}
//		if (win(stato, giocatore) == 0)
//			e = e + 100;
////		if(win(stato, giocatore)==1)
////			e=e+50;
////		if(win(stato, giocatore)==2)
////			e=e+60;
//		if (win(stato, 1 - giocatore) == 0)
//			e = e - 100;
		// System.out.println("VALORE EURISTICA " + (-e) + " MOSSA " +
		// prec.oldtoString());
		}
		return -e;
//		}
//		return 0;
		}
	

}
