package pente;

public class Board {
	
	private static final int DIMENSION= 19;
	private static final int OFFSET=4;
	private static final int START= (DIMENSION+OFFSET)*OFFSET + OFFSET;
	private static final int END= START + DIMENSION+ (DIMENSION-1)*(DIMENSION+OFFSET);
	private static final int SIZE= START+END;
	private static final int WHITE_CELL=0;
	private static final int BLACK_CELL=1;
	private static final int NONE_CELL=2;
	private static final int BOUNDARY_CELL =3;
	private static final int NORTH = 0;
	private static final int WEST= 1;
	private static final int NORTHEAST= 2;
	private static final int NORTHWEST= 3;
	private static final int SOUTH= 4;
	private static final int EAST= 5;
	private static final int SOUTWEST= 6;
	private static final int SOUTHEAST= 7;
	private static final int[] DIRECTION= {-(DIMENSION+OFFSET),-1,-(DIMENSION+OFFSET)+1,-(DIMENSION+OFFSET)-1,DIMENSION+OFFSET,1,DIMENSION+OFFSET-1,DIMENSION+OFFSET+1};
	private int[] board;
	private int[] checkPoints;
	
	public Board() {
		
		board= new int[SIZE];
		checkPoints= new int[SIZE];
		
		for(int i=0; i<START;i++){
			board[i]= BOUNDARY_CELL;
		}
		for(int i=0;i<DIMENSION;i++) {
			for(int j=0;j<DIMENSION;j++) 
				board[START+i*(DIMENSION+OFFSET)+j]=NONE_CELL;
			for(int j=DIMENSION;j<DIMENSION+OFFSET;j++)
				board[START+i*(DIMENSION+OFFSET)+j]=BOUNDARY_CELL;
		}
		
		for(int i=END;i<SIZE;i++)
			board[i]= BOUNDARY_CELL;
	}

	public int getCheckValue(int x, int y) {
	    return checkPoints[START+x*(DIMENSION+OFFSET)+y];
	}
	
	
	public void upCheckValue(int x,int y) {
		checkPoints[START+x*(DIMENSION+OFFSET)+y]++;
	}
	
	public void downCheckValue(int x,int y) {
		checkPoints[START+x*(DIMENSION+OFFSET)+y]--;
	}
	
	public int getCellValue(int x, int y) {
	    return board[START+x*(DIMENSION+OFFSET)+y];
	}
	
	public void setCellValue(int x,int y,int value) {
		board[START+x*(DIMENSION+OFFSET)+y]=value;
	}


	public boolean[] checkCaptures(int x, int y,int player) {
		int pos= START+x*(DIMENSION+OFFSET)+y;
		boolean captures[]= new boolean[8];
		for(int i=0;i<8;i++) {
			int dir= DIRECTION[i];
			if(board[pos+dir]==1-player && board[pos+dir+dir]==1-player && board[pos+dir+dir+dir]==player) {
				board[pos+dir]=NONE_CELL;
		        board[pos+dir+dir]= NONE_CELL;
		        captures[i]=true;
			}
		}
		return captures;
	}
	
	public boolean checkWins(int x,int y,int player) {
		int pos= START+x*(DIMENSION+OFFSET)+y;
		int cont=1;
		for(int i=0;i<4;i++) {
			for(int j=0;j<8;j+=4) {
				int dir= DIRECTION[i+j];
				int k=1;
				while(board[pos+dir*k]==player) {
					k++;
					cont++;
				}
			}
			if(cont >= 5) return true;
			else cont=1;
		}
		return false;
	}
	
	public void reverseCaptures(Move m) {
		int x= m.getX();
		int y= m.getY();
		int player= 1-m.getPlayer();
		boolean[] captures = m.getCaptures();
		int pos= START+x*(DIMENSION+OFFSET)+y;
		
		for(int i=0;i<8;i++) {
			if(captures[i]) {
				int dir= DIRECTION[i];
				board[pos+dir]= player;
				board[pos+dir+dir]= player;
			}
		}
		
	}
    
	public int indexTable(int i, int j,int k) {
        int dir= DIRECTION[k];
		int pos= START+i*(DIMENSION+OFFSET)+j;
		return (board[pos+dir+dir+dir+dir]<<14)|(board[pos+dir+dir+dir] << 12)|(board[pos+dir+dir] << 10)|(board[pos+dir] << 8) |
                (board[pos-dir] << 6) |(board[pos-dir-dir] << 4) |(board[pos-dir-dir-dir] << 2) |board[pos-dir-dir-dir-dir];
	}

 

	

	
	
	

}
