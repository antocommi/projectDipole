package progDipole;

public class Mossa {
	private short x;
	private short y;
	private short direction;
	private int spostamento;
	
	public Mossa(short x, short y, short direction, int spostamento) {
		this.x = x;
		this.y = y;
		this.direction = direction;
		this.spostamento = spostamento;
	}
	public short getX() {
		return x;
	}
	public void setX(short x) {
		this.x = x;
	}
	public short getY() {
		return y;
	}
	public void setY(short y) {
		this.y = y;
	}
	public short getDirection() {
		return direction;
	}
	public void setDirection(short direction) {
		this.direction = direction;
	}
	public int getSpostamento() {
		return spostamento;
	}
	public void setSpostamento(int spostamento) {
		this.spostamento = spostamento;
	}
	
	
}
