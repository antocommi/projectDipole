package pente;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Random;

public class Player {
	
	private static final int NOFSHAPES= (int)Math.pow(2, 16);
    private static final int WHITE=0;
    private static final int BLACK=1; 
    private static final int NONE=2;
	private static final int A=65;
    private static final int MAX= Integer.MAX_VALUE;
    private static final int EXACT_VALUE=0;
    private static final int UPPER_BOUND=1;
    private static final int LOWER_BOUND=2;
	private static final int DIMENSION=15;
	private static final int MAX_BRANCH_FACTOR=30;
	private static final int[] DIRECTION= {-DIMENSION,-1,-DIMENSION+1,-DIMENSION-1,DIMENSION,1,DIMENSION-1,DIMENSION+1};
    private static final int OPEN4= (int) Math.pow(2, 18);
    private static final int OPEN4AVV= -(int) Math.pow(2, 17);
    private static final int FIVEINROW= (int) Math.pow(2, 20);
    private static final int FIVEINROWAVV= -(int) Math.pow(2, 19);

    private long[][] randVal;
    private long[] randCapt;
    private long hashCode;
	private int player;
	private int depth;
	private int numberMove;
	private Board b;
	private int[][] shapeValues;
	private int[][] captValues;
	private int[] captureWeights;
	private int[] captureWeightsAvv;
	private int[] captures;
	private TTValue[] transpositionTable;
	private int dim;
	private boolean elapsed=false;
	boolean endGame;
	private int[] cut;
	private long timeout;
	
	public Player(int player,int depth) {
		this.player= player;
		this.depth=depth;
		if(player==0) timeout=900;
		else timeout=960;
		cut= new int[depth+1];
		randVal= new long [2][15*15];
		randCapt= new long [36];
		Random r= new Random();
		for(int i=0;i<2;i++)
			for(int j=0;j<225;j++) {
				long rand= r.nextLong();
				while(rand <0) rand=r.nextLong();
				randVal[i][j]= rand;
			}
		for(int i=0;i<36;i++){
			long rand= r.nextLong();
			while(rand <0) rand=r.nextLong();
			randCapt[i]= rand;
		}
		hashCode=hashCode ^ randCapt[0];
		dim = (int)Math.pow(2, 22);
		transpositionTable = new TTValue[dim];
		captures = new int[2];
		b= new Board();
		numberMove=0;
		shapeValues= new int[2][NOFSHAPES];
		captValues= new int[2][NOFSHAPES];
		captureWeights= new int[6];
		captureWeightsAvv= new int[6];
		for(int i=0; i<5;i++) {
			captureWeights[i]= 4 * (int)Math.pow(i*2, 5);
			captureWeightsAvv[i]= 2*(int)(Math.pow(i*2, 5));
		}
		captureWeights[5]= 2000000;
		captureWeightsAvv[5]= 800000;
		buildValues();
		
	}
	
	private void buildValues() {
		int[] curShape= new int[9];
		for(int i=0;i<NOFSHAPES;i++) {
		   String cur= Integer.toBinaryString(i);
		   int z=8;
		   for(int j= cur.length();j>1;j-=2) {
			   String s= cur.substring(j-2,j);
			   int x=0;
			   if(s.charAt(0)=='1')x+=2;
			   if(s.charAt(1)=='1')x+=1;
			   curShape[z]=x;
			   z--;
			   if(z==4)z--;
		   }
		   if (cur.length()%2!=0) {
			   if(cur.charAt(0)=='1')curShape[z]=1;
			   if(z==4)z--;
		   }
		   for(int j=0; j<z;j++) {
			   if(j!=4)curShape[j]=0;
		   }
		   int[] valuePlayer1= evaluate(curShape,player);
           int[] valuePlayer2= evaluate(curShape,1-player);
           captValues[player][i]= valuePlayer1[0];
           captValues[1-player][i]= valuePlayer2[0];
           shapeValues[player][i]= (int)Math.floor(Math.pow(2,valuePlayer1[1])) - (int)Math.floor(Math.pow(2,valuePlayer2[1]-1))+
        		   captureWeights[valuePlayer1[0]]-captureWeights[valuePlayer2[0]];
           shapeValues[1-player][i]= (int)Math.floor(Math.pow(2,valuePlayer2[1])) - (int)Math.floor(Math.pow(2,valuePlayer1[1]-1))+
        		   captureWeights[valuePlayer2[0]]-captureWeights[valuePlayer1[0]];
        		   
		}
	}


	private int[] evaluate(int[] curShape, int player) {
    	curShape[4]=player;
    	int[] ris = new int[2];
    	int c=0;
    	if(curShape[1]== player && curShape[2]== 1-player && curShape[3]==1-player) {
    		c+=1;
    		curShape[2]=NONE;
    		curShape[3]=NONE;
    	}
    	if(curShape[5]== 1-player && curShape[6]==1-player && curShape[7]==player) {
    		c+=1;
    		curShape[5]=NONE;
    		curShape[6]=NONE;
    	}
    	ris[0]=c;
    	int[] win= {player,player,player,player,player};
    	int[] openFour= {NONE,player,player,player,player,NONE};
    	int[] cappedFour1= {NONE,player,player,player,player};
    	int[] cappedFour2= {player,NONE,player,player,player};
    	int[] cappedFour3= {player,player,player,NONE,player};
    	int[] cappedFour4= {player,player,player,player,NONE};
    	int[] openThree1= {NONE,player,player,player,NONE,NONE}; 
    	int[] openThree2= {NONE,NONE,player,player,player,NONE};
    	int[] splitThree1= {NONE,player,NONE,player,player,NONE};
    	int[] splitThree2= {player,player,NONE,player,player,NONE};
    	int[] splitThree3= {NONE,player,player,NONE,player,NONE};
    	int[] splitThree4= {NONE,player,player,NONE,player,player};
    	int[] cappedThree1= {NONE,NONE,player,player,player};
    	int[] cappedThree2= {player,player,player,NONE,NONE};
    	int[] splitTwo1= {NONE,player,NONE,player,NONE,NONE};
    	int[] splitTwo2= {NONE,player,NONE,player,NONE,player};
    	int[] splitTwo3= {NONE,NONE,player,NONE,player,NONE};
    	int[] splitTwo4= {player,NONE,player,NONE,player,NONE};
        
    	if(contains(curShape,win)) 
    		ris[1]=20;
    	else if (contains(curShape,openFour))
    		ris[1]=18;
    	else if (contains(curShape,openFour))
    		ris[1]=18;
    	else if (contains(curShape,cappedFour1))
    		ris[1]=7;
    	else if (contains(curShape,cappedFour2))
    		ris[1]=7;
    	else if (contains(curShape,cappedFour3))
    		ris[1]=7;
    	else if (contains(curShape,cappedFour4))
    		ris[1]=7;
    	else if (contains(curShape,openThree1))
    		ris[1]=6;
    	else if (contains(curShape,openThree2))
    		ris[1]=6;
    	else if (contains(curShape,splitThree1))
    		ris[1]=5;
    	else if (contains(curShape,splitThree2))
    		ris[1]=5;
    	else if (contains(curShape,splitThree3))
    		ris[1]=5;
    	else if (contains(curShape,splitThree4))
    		ris[1]=5;
    	else if (contains(curShape,cappedThree1))
    		ris[1]=4;
    	else if (contains(curShape,cappedThree2))
    		ris[1]=4;
    	else if (contains(curShape,splitTwo1))
    		ris[1]=0;
    	else if (contains(curShape,splitTwo2))
    		ris[1]=0;
    	else if (contains(curShape,splitTwo3))
    		ris[1]=0;
    	else if (contains(curShape,splitTwo4))
    		ris[1]=0;
    	else
    		ris[1]=-1;
    	return ris;
	}
    
    private static boolean contains(int[]v1, int[]v2) {
    	for(int i=0;i<= v1.length- v2.length;i++) {
    		boolean b= true;
    		for(int j=0;j<v2.length;j++) {
    			if(v1[i+j]!= v2[j]) {b=false;break;}
    		}
    		if(b) return true;
    	}
    	return false;
    }

	public void makeMove(Move m) {
		int x= m.getX();
		int y= m.getY();
		int player= m.getPlayer();
		int pos= 15*x+y;
		hashCode= hashCode ^ randVal[player][pos];
		b.setCellValue(x, y, player);
		for(int i=-2;i<3;i++) 
			for(int j=-2;j<3;j++)
				b.upCheckValue(x+i, y+j);
       
		boolean[] capt= b.checkCaptures(x,y,player);
		hashCode= hashCode ^ randCapt[captures[this.player]*6+captures[1-this.player]];
		for(int i=0;i<8;i++) {
			if(capt[i]) {
				captures[player]++;
				int dir= DIRECTION[i];
				hashCode= hashCode ^ randVal[player][pos+dir];
				hashCode= hashCode ^ randVal[player][pos+dir+dir];	
			}
		}
		m.setCaptures(capt);
		if(captures[player]>= 5 ){ 
			if(player==this.player)
				hashCode= hashCode ^ randCapt[5*6+captures[1-this.player]];
			else
				hashCode= hashCode ^ randCapt[captures[this.player]*6+5]; 
			endGame=true;
		}
		else hashCode= hashCode ^ randCapt[captures[this.player]*6+captures[1-this.player]];
		if(b.checkWins(x,y,player)) endGame=true;
		numberMove++;
	}
	
	private void reverseMove(Move m) {
		int x= m.getX();
		int y= m.getY();
		int player= m.getPlayer();
		int pos= 15*x+y;
		hashCode= hashCode ^ randVal[player][pos];
		for(int i=-2;i<3;i++) 
			for(int j=-2;j<3;j++)
				b.downCheckValue(x+i, y+j);
		b.reverseCaptures(m);
		boolean capt[] = m.getCaptures();
		int cont=0;
		for(int i=0;i<8;i++) {
			if(capt[i]) {
				cont++;
				int dir= DIRECTION[i];
				hashCode= hashCode ^ randVal[player][pos+dir];
				hashCode= hashCode ^ randVal[player][pos+dir+dir];	
			}
		}
		if(captures[player] >=5 && captures[player]-cont <5) {
			endGame=false;
			if(player==this.player)
				hashCode= hashCode ^ randCapt[5*6+captures[1-this.player]];
			else
				hashCode= hashCode ^ randCapt[captures[this.player]*6+5]; 
		}
		else hashCode= hashCode ^ randCapt[captures[this.player]*6+captures[1-this.player]];
		captures[player]-=cont;
		hashCode= hashCode ^ randCapt[captures[this.player]*6+captures[1-this.player]];
		if(b.checkWins(x, y, player)) endGame =false;
		b.setCellValue(x, y, NONE);
		numberMove--;
	}

	
	public String elaborateMove() {
		String s= null;
		long start = System.currentTimeMillis();
		cut= new int[depth];
		if(numberMove==0 && player== WHITE) {
			s="H8";
	
		}
		else{
			Move[] moves= generateMoves(player);
			Move best=moves[0];
			int x= best.getX()+A;
			int y= best.getY()+1;
			s= (char)x+""+y;
			for(int i=2;i<=depth;i++){
				if(i==depth) {
					best= negaMaxDeepeningCut(start,i,moves);
					if(best != null) {
						x= best.getX()+A;
						y= best.getY()+1;
						s= (char)x+""+y;
					}
					else break;
				}			
			   else {
					best= negaMaxDeepening(start,i,moves);
					if(best != null) {
						x= best.getX()+A;
						y= best.getY()+1;
						s= (char)x+""+y;
					}
					else break;
				}
			}
			timeout=960;
		}
		elapsed=false;
		return s;
		
	}
	
	private Move negaMaxDeepeningCut(long start,int depth,Move[]moves) {
		int alfa= -MAX;
		int beta= MAX;
		int i=0;
		int best=0;
		Move m= moves[i];
		int bf=MAX_BRANCH_FACTOR;
		if(bf < cut[depth-1])
			bf= cut[depth-1];
		while(m!=null && i<bf) {
			makeMove(m);
			LinkedList<Move> parents= new LinkedList<>();
			parents.add(m);
			int score= -negamaxCut(depth-1,-beta,-alfa,1-player,start);
		    if(score> alfa) {
				alfa=score;
				best=i;
			}
			reverseMove(m);
		    if(elapsed) return null;
			i++;
			m= moves[i];
		}
		return moves[best];
	}
	
	private Move negaMaxDeepening(long start,int depth,Move[]moves) {
		int alfa= -MAX;
		int beta= MAX;
		int i=0;
		int best=0;
		Move m= moves[i];
		while(m!=null) {
			makeMove(m);
			int score=-negamax(depth-1,-beta,-alfa,1-player,start);
		    if(score> alfa) {
				alfa=score;
				best=i;
			}
			reverseMove(m);
		    if(elapsed) return null;
			i++;
			m= moves[i];
		}
		cut[depth]= best;
		return moves[best];
	}

	private int negamaxCut(int depth, int alfa, int beta,int player,long start) {
	    if(System.currentTimeMillis()-start > timeout){ elapsed=true; return -1;}
		Move curBest= null;
		boolean hit= false;
		Move[]moves =null;
		int flag = UPPER_BOUND;
		int index= (int) (hashCode % dim);
		TTValue ttconf= transpositionTable[index];
		if(ttconf != null && ttconf.getKey() == hashCode) {
			if(ttconf.getDepth() >= depth) {
				hit=true;
				int value= ttconf.getValue();
				if(ttconf.getFlags()== EXACT_VALUE) {
					return value;
				}
				else if(ttconf.getFlags() == UPPER_BOUND ) {
					if(value <= alfa)
						return value;
					if(value<beta) 
						beta=value;
				}
				else if(ttconf.getFlags() == LOWER_BOUND) {
					if(value >=beta)
						return value;
					if(value>alfa)
						alfa=value;
				}
			}
			moves= ttconf.getMoves();
		}
		if(moves == null) moves= generateMoves(player);
		if(depth == 1) {
			int score= moves[0].getE();
			if(score> alfa)
				alfa= score;
			return alfa;
		}
        int best=0;
		int bf=MAX_BRANCH_FACTOR;;
		if(bf < cut[depth-1])
			bf= cut[depth-1];	
		int i=0;
		Move m= moves[i];
		while(m!=null && i<bf) {
			makeMove(m);
			int score=0;
			if(endGame)
			    score=m.getE();
		    else 
		       score= -negamaxCut(depth-1,-beta,-alfa,1-player,start);
			
			if(elapsed) {reverseMove(m); return -1;}
			if(score> alfa) {
				best=i;
				flag= EXACT_VALUE;
				alfa=score;
				curBest= moves[i];
			}
			reverseMove(m);
			if(alfa>=beta) {
				if(!hit) 
					store(alfa,depth,LOWER_BOUND,moves,0);
				return beta;
			}
			i++;
			m= moves[i];
		}
		
	if(!hit) {
		store(alfa,depth,flag,moves,best);
	}
	return alfa;
    }
	
	private int negamax(int depth, int alfa, int beta,int player,long start) {
		if(System.currentTimeMillis()-start > timeout){ elapsed=true; return -1;}
		Move curBest= null;
		boolean hit= false;
		int flag = UPPER_BOUND;
		int best=0;
		Move[]moves =null;
		int index= (int) (hashCode % dim);
		TTValue ttconf= transpositionTable[index];
		if(ttconf != null && ttconf.getKey() == hashCode) {
			if(ttconf.getDepth() >= depth) {
				hit=true;
				int value= ttconf.getValue();
				if(ttconf.getFlags()== EXACT_VALUE) {
					return value;
				}
				else if(ttconf.getFlags() == UPPER_BOUND ) {
					if(value <= alfa)
						return value;
					if(value<beta) 
						beta=value;
				}
				else if(ttconf.getFlags() == LOWER_BOUND) {
					if(value >=beta)
						return value;
					if(value>alfa)
						alfa=value;
				}
			}
			moves=ttconf.getMoves();
		}
		
			if(moves==null)moves= generateMoves(player);
			if(depth == 1) {
				int score= moves[0].getE();
				if(score> alfa)
					alfa= score;
				return alfa;
			}
			int i=0;
			Move m= moves[i];
			while(m!=null) {
				makeMove(m);
				int score=0;
			    if(endGame)
			    	score=m.getE();
			    else 
			    	score= -negamax(depth-1,-beta,-alfa,1-player,start);
				
				if(elapsed) {reverseMove(m); return -1;}

				
				if(score> alfa) {
					best=i;
					flag= EXACT_VALUE;
					alfa=score;
					curBest= moves[i];
 				}
				reverseMove(m);
				if(alfa >= beta) {
					if(!hit) 
						store(alfa,depth,LOWER_BOUND,moves,0);
					return beta;
					
				}
				i++;
				m= moves[i];
			}
		if(cut[depth]<best)
			cut[depth]=best;
		if(!hit) {
			store(alfa,depth,flag,moves,best);
		}
		return alfa;
}


	private void store(int value, int depth, int flag, Move[] moves,int ibest) {
       TTValue newEntry = new TTValue(hashCode,depth,flag,value,moves,ibest);
       int index = (int) (hashCode % dim);
       transpositionTable[index]= newEntry;
	}

	private int heuristic(int player) {
		if(endGame) {
			return -MAX+numberMove;
		}
		boolean openFour=false;
		boolean five=false;
		int fiveAvv=0;
		int e=0;
		int capt1=0;
		int capt2=0;
		for(int i=0;i<15;i++) {
			for(int j=0;j<15;j++) {
				if(b.getCellValue(i, j)== NONE && b.getCheckValue(i, j)!= 0) {
					int val=0;
					boolean o4=false;
					int fA=0;
					for(int dir=0;dir<4;dir++) {
						int indexTable = b.indexTable(i,j,dir);
						val= shapeValues[player][indexTable];
						if(val== OPEN4) o4=true;
						else if(val == FIVEINROWAVV ) fA++;
						else if(val == FIVEINROW) {five= true; break;}
						e+= val;
						int c1= captValues[player][indexTable];
						int c2= captValues[1-player][indexTable];
						capt1= capt1|c1;
						capt2= capt2|c2;
					}
					if(o4) openFour=true;
					else fiveAvv+=fA;
					
				}
				
			}
		}
		if(five) e= FIVEINROW;
		else if(openFour&& fiveAvv==0) e= OPEN4;
		else if(fiveAvv==1) {
			e-= OPEN4AVV;
		}
		else if(fiveAvv>1) {
			e= fiveAvv*FIVEINROWAVV;
			if(openFour) e+=10;
		}
		if(capt1==3)capt1=2;
		if(capt2==3)capt2=2;
		capt1= (captures[player]+capt1>=5)? 5: captures[player]+capt1;
		capt2= (captures[1-player]+capt2>=5)? 5: captures[1-player]+ capt2;
		if(capt1 >3 || capt2>3)
			e+= captureWeights[capt1]- captureWeightsAvv[capt2];
		else
			e+= captureWeights[captures[player]]- captureWeights[captures[1-player]];
		return e;
	}
    
	private Move[] generateMoves(int player){
		Move[] moves= new Move[15*15];
		int k=0;
		if(numberMove==2 && player == WHITE) {
			for(int x=4;x<11;x++)
				for(int y=4;y<11;y++) {
					if(!(x<=9 && x>=5 && y<=9 && y>=5) && b.getCellValue(x, y)==NONE && b.getCheckValue(x, y)==0 ) {
						Move m= new Move(x,y,player);
						makeMove(m);
						int e= -heuristic(1-player);
						reverseMove(m);
						m.setE(e);
						int i=k;
						while(moves[i]!= null && moves[i].getE()>=e) {
							i++;
						}
						if(moves[i]==null)
							moves[i]=m;
						else {
						    int j=i;
							Move temp= moves[j];
							moves[j]=m;
							Move temp2=null;
							while(j<224 && moves[j+1]!=null ) {
								temp2= moves[j+1];
								moves[j+1]= temp;
								temp=temp2;
								j++;
							}
							moves[j+1]= temp;
							
						}
					}
					
				}
			
		}
		else if(numberMove == 1 && player == BLACK) {
			for(int x=5;x<9;x++) {
				for(int y=5;y<9;y++) {
					if(x!=7 && y!=7) {
						Move m= new Move(x,y,player);
						makeMove(m);
						int e= -heuristic(1-player);
						reverseMove(m);
						m.setE(e);
						int i=k;
						while(moves[i]!= null && moves[i].getE()>=e) {
							i++;
						}
						if(moves[i]==null)
							moves[i]=m;
						else {
							 int j=i;
								Move temp= moves[j];
								moves[j]=m;
								Move temp2=null;
								while(j<224 && moves[j+1]!=null ) {
									temp2= moves[j+1];
									moves[j+1]= temp;
									temp=temp2;
									j++;
								}
								moves[j+1]= temp;
						}
					}
				}
				
			}
		}
		else {
			for(int x=0;x<15;x++)
				for(int y=0;y<15;y++) {
					if(b.getCheckValue(x, y)!=0 && b.getCellValue(x, y)==NONE) {
						Move m= new Move(x,y,player);
						makeMove(m);
						int e= -heuristic(1-player);
						reverseMove(m);
						m.setE(e);
						int i=k;
						while(moves[i]!= null && moves[i].getE()>e) {
							i++;
						}
						if(moves[i]==null)
							moves[i]=m;
						else {
							 int j=i;
							Move temp= moves[j];
							moves[j]=m;
							Move temp2=null;
							while(moves[j+1]!=null ) {
								temp2= moves[j+1];
								moves[j+1]= temp;
								temp=temp2;
								j++;
							}
							moves[j+1]= temp;
						}
					}
					
				}
		}
	    if(moves[0].getE() == MAX -numberMove-1) {
	    	Move[] ret = {moves[0],null};
	    	return ret;
	    }
	    return moves;
	}
	
   public void draw(){
	   System.out.print("   ");
	   for(int i=1;i<16;i++){
		   if(i<9) System.out.print(i+"   ");
		   else System.out.print(i+"  ");	   
	   }
	   System.out.println();
	   for(int i=0;i<15;i++){
		   char c=(char) (i+A);
		   System.out.print(c+"  ");
		   for(int j=0;j<15;j++){
			   if(b.getCellValue(i, j)!=2) System.out.print(b.getCellValue(i, j));
			   else System.out.print(" ");
			   if(j!=14) System.out.print(" | ");
		   }
		   System.out.println();
	   }
	   System.out.println(Arrays.toString(captures));
   }
   public void draw2(){
	   System.out.print("   ");
	   for(int i=1;i<16;i++){
		   if(i<9) System.out.print(i+"   ");
		   else System.out.print(i+"  ");	   
	   }
	   System.out.println();
	   for(int i=0;i<15;i++){
		   char c=(char) (i+A);
		   System.out.print(c+"  ");
		   for(int j=0;j<15;j++){
			   if(b.getCheckValue(i, j)!=0) System.out.print(b.getCheckValue(i, j));
			   else System.out.print(" ");
			   if(j!=14) System.out.print(" | ");
		   }
		   System.out.println();
	   }
   }
 
}

