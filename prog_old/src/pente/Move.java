package pente;

public class Move implements Comparable<Move>{
    private int x,y;
    private int player;
    private boolean[] captures;
    private int e;

	public Move(int x, int y, int player) {
		this.x = x;
		this.y = y;
		this.player = player;
		captures= new boolean[8];
	}
   

	public int getE() {
		return e;
	}

	public void setE(int e) {
		this.e = e;
	}
	
	public boolean[] getCaptures() {
		return captures;
	}

	public void setCaptures(boolean[] captures) {
		this.captures = captures;
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
 
	public int getPlayer() {
		return player;
	} 

	public void setPlayer(int player) {
		this.player = player;
	}
	
	public String toString() {
		char c= (char)(x+65);
		int k=y+1;
		return c+" "+k+" "+e;
	}


	@Override
	public int compareTo(Move m) {
		return (e-m.e);
	}
    
	public boolean equals(Move m){
		return m.x==x && m.y==y && m.player==player;
	}
    
    
    
}
