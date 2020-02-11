package DipoleHeuristics;

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
		int e = 0;
		byte[] listaPedine = stato.getListaPosizioni(giocatore);
		int giocatoreAdversary = 1 - giocatore;

//		if (stato.nPedine(giocatore) == stato.nPedine(giocatoreAdversary)) {
		Mossa advMove = miMangiaGetMossa(prec, giocatore, stato);
		int pedinePerse = stato.calcolaSpostamento(prec.getiStart(), prec.getjStart(), prec.getiEnd(), prec.getjEnd());
		int spost = 0;
		if (advMove != null) {
			spost = stato.calcolaSpostamento(advMove.getiStart(), advMove.getjStart(), advMove.getiEnd(),
					advMove.getjEnd());
		} else {
			spost = -1;
		}

		// VALUTIAMO UNA MOSSA ANCHE IN BASE ALLO SPOSTAMENTO
		int numPedineSpostate = stato.calcolaSpostamento(prec.getiStart(), prec.getjStart(), prec.getiEnd(),
				prec.getjEnd());
		if (numPedineSpostate < 2 && !possoMangiareENonMiMangia(prec, giocatore, stato)) {
			e = e + 7;
		} else if (numPedineSpostate < 3 && !possoMangiareENonMiMangia(prec, giocatore, stato)) {
			e = e + 5;
		} else {
			e = e - 7;
		}

		// MOSSE IN CUI CI MANGIA
		if (advMove != null && checkMosseIndietro(advMove, giocatoreAdversary)) {
			e = e - 21;
		}
		if (advMove != null) {
			e = e - 21;
		}
		if (advMove != null && spost > 1 && pedinePerse <= 1) {
			int numMosseMangianti = generaMosseSenzaCheck(advMove, giocatoreAdversary, stato);
			if (numMosseMangianti <= 14 && numMosseMangianti > 8) {
				e = e - 16;
			}
			if (numMosseMangianti <= 8) {
				e = e - 8;
			}
		}

		// MOSSA MERGE
		if (prec.getTipo() == 1 && campoNostro(prec, giocatore)) {
			e = e + 12;
		} else {
			e = e + 8;
		}

		// Se posso mangiare una pedina con un numero consistente di pedine e da
		// scegliere
		if (possoMangiareENonMiMangia(prec, giocatore, stato) && miMangiaStackRestante(prec, giocatore, stato)
				&& getNumeroPedineMangiateAdv(prec, stato) > 1)
			e = e + 20;

		// Posso mangiare, non vengo mangiato, non mi sposto molto in avanti, non lascio
		// pedine scoperte nella posizone in cui mi trovavo
		if (possoMangiareENonMiMangia(prec, giocatore, stato) && pedinePerse < 4
				&& miMangiaStackRestante(prec, giocatore, stato))
			e = e + 21;
		if (possoMangiareENonMiMangia(prec, giocatore, stato) && miMangiaStackRestante(prec, giocatore, stato))
			e = e + 10;

		if (miMangiaPochePedineEritornoAmangiarlo(prec, giocatore, stato)) {
			e = e + 40;
		}

		if (possoMangiareENonMiMangia(prec, giocatore, stato) && pedinePerse < 5) {
			e = e + 5;
		}

		for (int pedina = 0; pedina < stato.getNumeroStackGiocatore(giocatore); pedina++) {

			byte pos = listaPedine[pedina];
			int x = pos / 8;
			int y = pos % 8;
			int nPedine = stato.getNumeroPedine(x, y);

			// CONTROLLIAMO IL NUMERO DI PEDINE VICINE
			if (nPedine > 1) {
				int numeroPedineCornice = cornice(x, y, giocatore, stato);
				if (numeroPedineCornice == 1) {
					e = e + 6;
				}
				if (numeroPedineCornice == 2) {
					e = e + 4;
				}
				if (numeroPedineCornice > 2) {
					e = e - 2;
				}
			}

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
		System.out.println("VALORE EURISTICA " + (-e) + " MOSSA " + prec.oldtoString());
		return -e;
//		}
//		return 0;
	}

}
