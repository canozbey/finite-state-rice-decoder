package rice;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class FiniteStateDecoder {
	
	
	public static Collection<Integer> decode(byte[] stream, int k){ 
		
		StateVariables var = new StateVariables(k);
		
		if (stream.length == 0) return var.integer_stream;
		
		State state = State.S;	//initial state is set as the start state
		
		while(var.i < stream.length) {
			
			state = state.execute(stream, var);
			
		}
		
		return var.integer_stream;
		
	}
	

}
