package dipoleF;

public class Cella {

	private int riga;
	private int colonna;
	private int nPedine;
	private int coloreCella;
	private int colorePedina;

	public Cella(int riga, int col, int nPedine, int colCella, int colPedina) {
		this.riga = riga;
		this.colonna = col;
		this.nPedine = nPedine;
		this.coloreCella = colCella;
		this.colorePedina = colPedina;
	}

	public int getRiga() {
		return riga;
	}

	public void setRiga(int riga) {
		this.riga = riga;
	}

	public int getColonna() {
		return colonna;
	}

	public void setColonna(int colonna) {
		this.colonna = colonna;
	}

	public int getnPedine() {
		return nPedine;
	}

	public void setnPedine(int nPedine) {
		this.nPedine = nPedine;
	}

	public int getColoreCella() {
		return coloreCella;
	}

	public void setColoreCella(int coloreCella) {
		this.coloreCella = coloreCella;
	}

	public int getColorePedina() {
		return colorePedina;
	}

	public void setColorePedina(int colorePedina) {
		this.colorePedina = colorePedina;
	}

}
