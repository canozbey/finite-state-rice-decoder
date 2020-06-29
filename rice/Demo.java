package rice;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

public class Demo {

	public static void main(String[] args) {

		int k = 5;	//the Golomb parameter
		
		Collection<Integer> input = random(100, 200);	//randomly generate a stream of 100 integers between 0 and 200.

		byte[] encode = ByteLevelEncoder.encode(input, k);
		
		Collection<Integer> decode = FiniteStateDecoder.decode(encode, k);
		
		System.out.println(decode.toString());
		
	}
	
	
	public static Collection<Integer> random(int size, int lim){
		List<Integer> list = new ArrayList<>();
		
		Random r = new Random();
		
		for(int i=0;i<size;i++) {
		
			list.add(r.nextInt(lim));
	
		}
		
		return list;
	}

}
