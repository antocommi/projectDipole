package Dipole;

import java.io.Serializable;

public class Mossa implements MossaI, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int iStart;
	private int jStart;
	private int iEnd;
	private int jEnd;
	private int direction;
	private int tipo;
	private static String[] DIR = { "N", "S", "NE", "SW", "SE", "NW", "E",
			"W" };

	private static String[] RIGHE = {"A","B","C","D","E","F","G","H"};
	
	public String oldtoString() {
		return "Mossa [(" + iStart + ", " + jStart + ") -> (" + iEnd + ", " + jEnd + "); direction=" + DIR[direction]
				+ "]";
	}
	
	public int calcolaSpostamento(int a, int b, int x, int y) {
		int k, m;
		k = Math.abs(a - x);
		m = Math.abs(b - y);
		return k >= m ? k : m;
	}
	
	private int calcolaCelleFuori(int a, int b, int x, int y) {
		int dir = direction;
		if (dir == ScacchieraBit.NORTH)
			return a + Math.abs(x);
		if (dir == ScacchieraBit.NORTHEAST)
			return (y - b);
		if (dir == ScacchieraBit.NORTHWEST)
			return (b + Math.abs(y));
		if (dir == ScacchieraBit.SOUTH)
			return x - a;
		if (dir == ScacchieraBit.SOUTHEAST)
			return (y - b);
		if (dir == ScacchieraBit.SOUTHWEST)
			return (b + Math.abs(y));
		return -1;
	}
	
	public boolean checkPosOut(int i, int j) {
		if (i > 7 || j > 7 || i < 0 || j < 0)
			return true;
		return false;
	}
	
	@Override
	public String toString() {
		int spostamento = 0;
		if(checkPosOut(iEnd,jEnd)) 
			spostamento = calcolaCelleFuori(iStart,jStart,iEnd,jEnd);
		else
			spostamento = calcolaSpostamento(iStart,jStart,iEnd,jEnd);
		
		return "MOVE " + RIGHE[iStart]+(jStart+1)+","+ DIR[direction]+","+(spostamento);
	}
	
	public Mossa(int iStart, int jStart, int iEnd, int jEnd, int direction) {
		super();
		this.iStart = iStart;
		this.jStart = jStart;
		this.iEnd = iEnd;
		this.jEnd = jEnd;
		this.direction = direction;
	}

	public Mossa(int iStart, int jStart, int iEnd, int jEnd, int direction, int tipo) {
		super();
		this.iStart = iStart;
		this.jStart = jStart;
		this.iEnd = iEnd;
		this.jEnd = jEnd;
		this.direction = direction;
		this.tipo = tipo;
	}

	public int getTipo() {
		return tipo;
	}

	public void setTipo(int tipo) {
		this.tipo = tipo;
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

	public int getDirection() {
		return direction;
	}

	public void setDirection(int direction) {
		this.direction = direction;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + direction;
		result = prime * result + iEnd;
		result = prime * result + iStart;
		result = prime * result + jEnd;
		result = prime * result + jStart;
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
		if (direction != other.direction)
			return false;
		if (iEnd != other.iEnd)
			return false;
		if (iStart != other.iStart)
			return false;
		if (jEnd != other.jEnd)
			return false;
		if (jStart != other.jStart)
			return false;
		return true;
	}


}