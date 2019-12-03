package Dipole;

public class Mossa implements Comparable<Mossa> {

	private int riga;
	private int colonna;
	private int player;
	private int euristic;

	public Mossa(int riga, int colonna, int player, int euristic) {
		this.riga = riga;
		this.colonna = colonna;
		this.player = player;
		this.euristic = euristic;
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

	public int getPlayer() {
		return player;
	}

	public void setPlayer(int player) {
		this.player = player;
	}

	public int getEuristic() {
		return euristic;
	}

	public void setEuristic(int euristic) {
		this.euristic = euristic;
	}

	@Override
	public int compareTo(Mossa m) {
		if (euristic > m.getEuristic())
			return 1;
		if (euristic < m.getEuristic())
			return -1;
		return 0;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + colonna;
		result = prime * result + euristic;
		result = prime * result + player;
		result = prime * result + riga;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Mossa other = (Mossa) obj;
		if (colonna != other.colonna)
			return false;
		if (euristic != other.euristic)
			return false;
		if (player != other.player)
			return false;
		if (riga != other.riga)
			return false;
		return true;
	}

}
