package pente;

public class TTValue {
	
	private long key;
	private int depth;
	private int flags;
	private int value;
	private Move[] moves;
	
	public TTValue(long key, int depth, int flags, int value, Move[] moves,int ibest) {
		this.key = key;
		this.depth = depth;
		this.flags = flags;
		this.value = value;
		if(moves!= null){
			this.moves= new Move[moves.length];
			for(int i=1; i<=ibest;i++){
				this.moves[i]= moves[i-1];
			}
			for(int i=ibest+1;i<moves.length;i++){
				if(moves[i]==null)break;
				this.moves[i]= moves[i];
			}
			this.moves[0]= moves[ibest];
		}
	}

	public Move[] getMoves() {
		return moves;
	}

	public void setMoves(Move[] moves) {
		this.moves = moves;
	}

	public long getKey() {
		return key;
	}

	public void setKey(long key) {
		this.key = key;
	}

	public int getDepth() {
		return depth;
	}

	public void setDepth(int depth) {
		this.depth = depth;
	}

	public int getFlags() {
		return flags;
	}

	public void setFlags(int flags) {
		this.flags = flags;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}
	
	@Override
	public String toString() {
		return "key: "+key+" depth: "+depth+" flag:"+flags+" value: "+value;
	}
	

}
