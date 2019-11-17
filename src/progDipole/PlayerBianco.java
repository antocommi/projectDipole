package progDipole;
import java.util.*;
public class PlayerBianco {
		
		private Minimax() {}
	 
	    public static State minimaxDecision(State state) {
	        return state.getActions().stream()
	                .max(Comparator.comparing(Minimax::minValue)).get();
	    }
	 
	    private static double maxValue(State state) {
	        if(state.isTerminal()){
	            return state.getUtility();
	        }
	        return state.getActions().stream()
	                .map(Minimax::minValue)
	                .max(Comparator.comparing(Double::valueOf)).get();
	    }
	 
	    private static double minValue(State state) {
	        if(state.isTerminal()){
	            return state.getUtility();
	        }
	        return state.getActions().stream()
	                .map(Minimax::maxValue)
	                .min(Comparator.comparing(Double::valueOf)).get();
	    }
	 
	    public static class State {
	 
	        final int state;
	        final boolean firstPlayer;
	        final boolean secondPlayer;
	 
	        public State(int state, boolean firstPlayer){
	            this.state = state;
	            this.firstPlayer = firstPlayer;
	            this.secondPlayer = !firstPlayer;
	        }
	 
	        Collection<State> getActions(){
	            List<State> actions = new LinkedList<>();
	            if(state > 4){
	                actions.add(new State(state-5, secondPlayer));
	            }
	            if(state > 3){
	                actions.add(new State(state-4, secondPlayer));
	            }
	            if(state > 2){
	                actions.add(new State(state-3, secondPlayer));
	            }
	            return actions;
	        }
	 
	        boolean isTerminal() {
	            return state < 3;
	        }
	 
	        double getUtility() {
	            if(firstPlayer)
	                return -1;
	            else
	                return 1;
	        }
	    }
	}

}
