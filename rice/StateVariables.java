package rice;

import java.util.Collection;
import java.util.LinkedList;

public class StateVariables {
	
	public final int[] log2 = {-1, 0, 1, 1, 2, 2, 2, 2, 3, 3, 3, 3, 3, 3, 3, 3, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7};		//lookup table to get the value of floor(log2(byte_value)) fast; masking can also be used alternatively
	
	public int k = 0;	//the Golomb parameter (constant)

	public int q = 0;   //value of the quotient
	
	public int i = 0;	//byte stream access index
	
	public int d = 0;	//dynamic variable that keeps the length of residual to consume
	
	public int r = 0;	//value of the residual
	
	public int m = 0;	//the value of the remaining part in the current byte after r
	
	public int c = 8;	//the length of the remaining part in the current byte after q
	
	public Collection<Integer> integer_stream = new LinkedList<>();	//decoded integers are sent here
	
	StateVariables(int k){	//initialize variables with the Golomb parameter provided
		
		this.k = k;
		
		d = k;
		
	}
	
	public void emit() {	//decode integer and store in a data structure when q and r are ready
		
		integer_stream.add( (q << k) | r);
		
	}
	
}
