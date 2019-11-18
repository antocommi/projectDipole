package progDipole;

public class Mossa {
	private int x;
	private int y;
	private int direction;
	private int spostamento;
	private Cella partenza;
	
	public Mossa(Cella partenza,int direction){
		this (partenza.getRiga(),partenza.getColonna(), direction, partenza.getnPedine() );
	}
	public Cella getPartenza() {
		return partenza;
	}
	public void setPartenza(Cella partenza) {
		this.partenza = partenza;
	}
	public Mossa(int x, int y, int direction, int spostamento) {
		super();
		this.x = x;
		this.y = y;
		this.direction = direction;
		this.spostamento = spostamento;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getDirection() {
		return direction;
	}

	public void setDirection(int direction) {
		this.direction = direction;
	}

	public int getSpostamento() {
		return spostamento;
	}

	public void setSpostamento(int spostamento) {
		this.spostamento = spostamento;
	}
	

	
	
}
