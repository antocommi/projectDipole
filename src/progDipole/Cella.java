package progDipole;

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

	public void removeFromStack(int pedineDaEliminare){
		this.setnPedine(this.getNPedine()-pedineDaEliminare);
		if(this.getnPedine()==0) this.setColorePedina(-1);
	}
	
	public void base(Cella cellaSorgente, int pedineSpostate){
		int pedineRimanenti = cellaSorgente.getnPedine()-pedineSpostate;
		this.colorePedina = cellaSorgente.colorePedina;
		this.nPedine = pedineSpostate;
		if(pedineRimanenti==0) cellaSorgente.setColorePedina(-1); // imposta a colore vuoto
		cellaSorgente.setnPedine(pedineRimanenti);
	}

	public void mergeFrom(Cella cellaSorgente, int pedineSpostate){
		int pedineRimanenti = cellaSorgente.getnPedine()-pedineSpostate;
		this.nPedine += pedineSpostate;
		if(pedineRimanenti==0) cellaSorgente.setColorePedina(-1); // imposta a colore vuoto
		cellaSorgente.setnPedine(pedineRimanenti);
	}

	public void captureFrom(Cella cellaSorgente, int pedineSpostate){
		int pedineRimanenti = cellaSorgente.getnPedine()-pedineSpostate;
		this.nPedine = pedineSpostate;
		this.colorePedina = this.colorePedina==1 ? 0 : 1;
		if(pedineRimanenti==0) cellaSorgente.setColorePedina(-1); // imposta a colore vuoto
		cellaSorgente.setnPedine(pedineRimanenti);
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


	public Cella() {
	}

	public int getNPedine() {
		return this.nPedine;
	}

	public void setNPedine(int nPedine) {
		this.nPedine = nPedine;
	}

	public Cella riga(int riga) {
		this.riga = riga;
		return this;
	}

	public Cella colonna(int colonna) {
		this.colonna = colonna;
		return this;
	}

	public Cella nPedine(int nPedine) {
		this.nPedine = nPedine;
		return this;
	}

	public Cella coloreCella(int coloreCella) {
		this.coloreCella = coloreCella;
		return this;
	}

	public Cella colorePedina(int colorePedina) {
		this.colorePedina = colorePedina;
		return this;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this)
			return true;
		if (!(o instanceof Cella)) {
			return false;
		}
		Cella cella = (Cella) o;
		return riga == cella.riga && colonna == cella.colonna && nPedine == cella.nPedine && coloreCella == cella.coloreCella && colorePedina == cella.colorePedina;
	}

	

	@Override
	public String toString() {
		return "{" +
			" riga='" + getRiga() + "'" +
			", colonna='" + getColonna() + "'" +
			", nPedine='" + getNPedine() + "'" +
			", coloreCella='" + getColoreCella() + "'" +
			", colorePedina='" + getColorePedina() + "'" +
			"}";
	}


}
