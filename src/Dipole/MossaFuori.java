package Dipole;

import java.io.Serializable;
import java.util.LinkedList;

public class MossaFuori implements MossaI, Serializable {

	private int iStart;
	private int jStart;
	private int iEnd;
	private int jEnd;
	private int direzione;
	private int numPedine;

	private LinkedList<MossaFuori> listaMosse;

	private MossaFuori(int iStart, int jStart, int iEnd, int jEnd, int direzione, int colore, int numPedine) {
		this.iStart = iStart;
		this.jStart = jStart;
		this.iEnd = iEnd;
		this.jEnd = jEnd;
		this.direzione = direzione;
		this.numPedine = numPedine;
	}

	public int getiStart() {
		return iStart;
	}

	public void setiStart(int iStart) {
		this.iStart = iStart;
	}

	public int getjStart() {
		return jStart;
	}

	public void setjStart(int jStart) {
		this.jStart = jStart;
	}

	public int getiEnd() {
		return iEnd;
	}

	public void setiEnd(int iEnd) {
		this.iEnd = iEnd;
	}

	public int getjEnd() {
		return jEnd;
	}

	public void setjEnd(int jEnd) {
		this.jEnd = jEnd;
	}

	public int getDirezione() {
		return direzione;
	}

	public void setDirezione(int direzione) {
		this.direzione = direzione;
	}

	public int getNumPedine() {
		return numPedine;
	}

	public void setNumPedine(int numPedine) {
		this.numPedine = numPedine;
	}

	public LinkedList<MossaFuori> getListaMosse() {
		return listaMosse;
	}

	public void setListaMosse(LinkedList<MossaFuori> listaMosse) {
		this.listaMosse = listaMosse;
	}

}
