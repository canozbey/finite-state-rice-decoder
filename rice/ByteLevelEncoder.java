package rice;

import java.util.Collection;
import java.util.LinkedList;

public class ByteLevelEncoder {
	
	
	public static byte[] encode(Collection<Integer> stream, int k) { //k should not be greater than 31
		
		if (k>31 || k<0) throw new IllegalArgumentException(); //k<0 seems like an interesting idea. Could it ever be useful?
		
		LinkedList<Byte> byte_list = new LinkedList<>();
				
		int mask = (1 << k) - 1;
		
		int bit_count = 0;
		
		int last = 0;
		
		for(int i : stream) {
			
			int q = (i >> k) + 1;
			
			int r = i & mask;
			
			int len = q + k;
			
			int to_consume = 8 - (bit_count & 7);
			
			if (len <= to_consume) {
								
				last = (last << len) | ((1 << k) | r);
				
				bit_count += len;
				
				if (len == to_consume) {
					
					byte_list.add((byte)last);
					
					last = 0;
					
				}
				
			}
			else {
				
				if (q <= to_consume) {
					
					last = (last << q) | 1;
					
					int h = to_consume - q; //length of r head to consume
					
					int y = k - h; //remaining length of r
					
					last = (last << h) | (r >> y);
					
					byte_list.add((byte)last); //append this byte;
										
					r &= ((1 << y) - 1); //rest of r
					
					while (y >= 8) {
						
						y -= 8;
						
						byte_list.add((byte) (r >> y)); //append part of r;
						
						r &= ((1 << y) - 1); //rest of r
						
					}
					
					bit_count += len;
					
					last = r;
					
				}
				else {
					
					byte_list.add((byte)(last << to_consume)); //append 0's and send it
										
					int y = q - to_consume;   //remaining length of q
					
					while (y > 8) {
						
						y -= 8;
					
						byte_list.add((byte)0);
					}
					
					to_consume = 8;
					
					last = 0;
					
					if (y == 8) {
					
						byte_list.add((byte)1);

					}
					
					else {
						
						to_consume -= y;
																		
					}
					
					if (to_consume > k) {
						
						last = (1 << k) | r;

						
						bit_count += len;
						
					}
					
					else {
						
						y = k - to_consume;  //remaining length of r
												
						last = (1 << to_consume) | (r >> y);
						
						byte_list.add((byte) last);
						
						r &= ((1 << y) - 1); //rest of r
						
						while (y >= 8) {
							
							y -= 8;
							
							byte_list.add((byte) (r >> y)); //append part of r;
							
							r &= ((1 << y) - 1); //rest of r
							
						}
						
						bit_count += len;
						
						last = r;	
						
					}
					
				}
				
			}
			
		}
				
		int tail = (bit_count & 7);
		
		if (tail != 0) {
			
			byte_list.add((byte) (last << (8 - tail))); // make padding and then add
			
		}
		
		byte[] result = new byte[byte_list.size()];
		
		for(int j=0;j<result.length;++j) {
			result[j] = byte_list.pollFirst();
		}
	
		return result;
	}
	
	public static int select_k(Collection<Integer> stream) { //selects the optimal parameter by Kiely's formula assuming a geometric source
		
		long sum = 0;
		
		for (int i : stream) {
			
			sum += i;
			
		}
		
		double mean = sum/((double)stream.size());
		
		double fi_minus_one = 0.61803398875; //golden ratio minus 1
		
		double ratio = Math.log(fi_minus_one)/Math.log(mean/(mean+1));
		
		return Math.max((int)(Math.log(ratio)/Math.log(2)) + 1, 0);
		
	}
	
	
	public static int code_length(Collection<Integer> stream, int k) {
		
		int s = 0;
				
		for(int i : stream) {
						
			s += (i >> k) + 1 + k;
			
		}
		
		return s;
		
	}

}
