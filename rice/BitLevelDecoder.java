package rice;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class BitLevelDecoder {
	
	public static Collection<Integer> decode(byte[] stream, int k){ 
		
		List<Integer> integer_stream = new LinkedList<>();
		
		if (stream.length == 0) return integer_stream;
		
		int q = 0;
		
		int i = 0;
		
		int j = 0;
		
		int p = 0;
				
		int len = (stream.length - 1) << 3;
		
		while (true) {
			
		    int b = stream[i] & 0xFF;
			
			while ((b & (128 >> (p & 7))) == 0) {
				
				++q;
				
				i = ++p >> 3;
								
				b = stream[i] & 0xFF;
				
			}
						
			i = ++p >> 3;
						
			while (j++ < k) {
				
				b = stream[i] & 0xFF;
				
				q = (q << 1) | ((b >> (~p & 7)) & 1);
								
				i = ++p >> 3;
				
			}
			
			integer_stream.add(q);
			
			if (p >= len) {
								
				if (i == stream.length || (((stream[i] & 0xFF) & (255 >> (p - len))) == 0)) {
					
					return integer_stream;
					
				}
				
			}
			
			j = 0;
			
			q = 0;
			
		}
				
	}

}
