package DipoleHeuristics;

import java.util.ArrayList;
import java.util.HashMap;

import Dipole.Mossa;
import Dipole.ScacchieraBit;

public class B_Heuristic implements HeuristicInterface {

	private int[] v = { 1, 7, 8, 14, 17, 23, 24, 30, 33, 39, 40, 46, 49, 55, 56, 62 };
	private HashMap<Integer, Integer> POSIZIONI_BORDI;

	public B_Heuristic() {
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

		int spostamentoAvversarioMossaTop = 0;
		if (advMove != null) {
			spostamentoAvversarioMossaTop = stato.calcolaSpostamento(advMove.getiStart(), advMove.getjStart(), advMove.getiEnd(),
					advMove.getjEnd());
		} else {
			spostamentoAvversarioMossaTop = -1;
		}
		
		

		if (stato.checkPosOut(prec.getjEnd(), prec.getiEnd())) {
			e = e - 12;
		}

		// ---------------------------------------------------------------

		// VALUTIAMO UNA MOSSA ANCHE IN BASE ALLO SPOSTAMENTO
		boolean possoMangiareENonMangia = possoMangiareENonMiMangia(prec, giocatore, oldBoard, listaMosseMiMangia);
		
		
		if (numPedineSpostateNOSTRE < 1 && possoMangiareENonMangia) {
			e = e + 21;
		} else if (numPedineSpostateNOSTRE < 2 && possoMangiareENonMangia) {
			e = e + 16;
		} else if (numPedineSpostateNOSTRE >= 3 && possoMangiareENonMangia) {
			e = e + 8;
		}

		// MOSSE IN CUI CI MANGIA
		if (advMove != null && esisteMossaIndietro(listaMosseMiMangia, giocatoreAdversary)) {
			e = e - 31;
		}
		if (advMove != null) {
			e = e - 26;
		}

		int potenzaCella = powerCell(prec, 1-giocatore, stato);
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

		if (potenzaCella <= 8) {
			e = e - 8;
		}

		// MOSSA MERGE
		if (prec.getTipo() == 1 && campoNostro(prec, giocatore)) {
			e = e + 35;
		} else {
			e = e + 17;
		}

		if (possoMangiareENonMangia && !miMangiaStackRestante(prec, giocatore, stato))
			e = e + 12;

		// Se posso mangiare una pedina con un numero consistente di pedine e da
		// scegliere
		if (possoMangiareENonMangia	&& !miMangiaStackRestante(prec, giocatore, stato) && getNumeroPedineMangiateAdv(prec, stato) > 1)
			e = e + 10;

		// Posso mangiare, non vengo mangiato, non mi sposto molto in avanti, non lascio
		// pedine scoperte nella posizone in cui mi trovavo
		if (possoMangiareENonMangia && numPedineSpostateNOSTRE <2 && !miMangiaStackRestante(prec, giocatore, stato))
			e = e + 11;
//		if (possoMangiareENonMangia	&& !miMangiaStackRestante(prec, giocatore, stato) && numPedineSpostateNOSTRE <2)
//			e = e + 10;

		if (miMangiaPochePedineEritornoAmangiarlo(prec, giocatore, stato)) {
			e = e + 40;
		}

		if (possoMangiareENonMangia && numPedineSpostateNOSTRE < 5 && numPedineSpostateNOSTRE <2) {
			e = e + 5;
		}

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

			if (giocatore == 1) {
				if (pos < 32) {
					e += 2;
				} else {
					e -= 1;
				}
			} else {
				if (pos >= 32) {
					e += 1;
				}

				else {
					e -= 1;
				}
			}

			if (nPedine < 2) {
				e = e - 1;
			}

			else {
				e = e + 1;
			}

			if (POSIZIONI_BORDI.containsKey((Object) pos)) {
				e = e + 2;
			}
		}
		// System.out.println("VALORE EURISTICA " + (-e) + " MOSSA " +
		// prec.oldtoString());
		return e;
//		}
//		return 0;
	}

}
