package rice;

public enum State {
	
	S{
		@Override
		public State execute(byte[] stream, StateVariables var) {

			int b = stream[var.i] & 0xFF;
			
			if (b > 1) {
				return Q2;
			}
			if(b == 1) {
				return Q1;
			}
			return Q0;
		}

	},

	Q0{
		@Override
		public State execute(byte[] stream, StateVariables var) {

			int b = stream[++var.i] & 0xFF;
			
			var.q = 8;
			
			if (b > 1) {
				return Q5;
			}
			if(b == 1) {
				return Q4;
			}
			return Q3;
		}

	},
	Q1{
		@Override
		public State execute(byte[] stream, StateVariables var) {
			
			++var.i;
			
			var.q = 7;
			
			if(var.d < 8) {
				return R2;
			}
			if(var.d > 8) {
				return R0;
			}	
			return R1;
		}

	},
	Q2{
		@Override
		public State execute(byte[] stream, StateVariables var) {
			
			int b = stream[var.i] & 0xFF;
			
			var.c = var.log2[b];
			
			var.q = 7 - var.c;
			
			if(var.d < var.c) {
				return R7;
			}
			if(var.d > var.c) {
				return T0;
			}	
			return R6;
		}

	},
	Q3{
		@Override
		public State execute(byte[] stream, StateVariables var) {
			
			int b = stream[++var.i] & 0xFF;
			
			var.q += 8;
			
			if (b > 1) {
				return Q5;
			}
			if(b == 1) {
				return Q4;
			}
			return Q3;
		}
	},
	Q4{
		@Override
		public State execute(byte[] stream, StateVariables var){
			
			++var.i;
			
			var.q += 7;
			
			if(var.d < 8) {
				return R2;
			}
			if(var.d > 8) {
				return R0;
			}	
			return R1;
		}

	},
	Q5{
		@Override
		public State execute(byte[] stream, StateVariables var){
			
			int b = stream[var.i] & 0xFF;
			
			var.c = var.log2[b];
			
			var.q += 7 - var.c;
			
			if(var.d < var.c) {
				return R7;
			}
			if(var.d > var.c) {
				return T0;
			}	
			return R6;
		}
		
	},
	Q6{
		@Override
		public State execute(byte[] stream, StateVariables var){
			
			++var.i;
			
			var.q = var.c - var.d - 1;
			
			var.d = var.k;
			
			if(var.d < 8) {
				return R2;
			}
			if(var.d > 8) {
				return R0;
			}	
			return R1;
		}

	},
	Q7{
		@Override
		public State execute(byte[] stream, StateVariables var){
			
			int t = var.log2[var.m];
						
			var.q = var.c - var.d - t - 1;
			
			var.c = t;
			
			var.d = var.k;
			
			if(var.d < var.c) {
				return R7;
			}
			if(var.d > var.c) {
				return T0;
			}	
			return R6;
		}
	
	},
	T0{
		@Override
		public State execute(byte[] stream, StateVariables var){
			
			int b = stream[var.i++] & 0xFF;
			
			var.r = b & (255 >> (8 - var.c));
			
			var.d -= var.c;
			
			var.c = 8;
			
			if(var.d < 8) {
				return R5;
			}
			if(var.d > 8) {
				return R3;
			}	
			return R4;
		}

	},
	R0{
		@Override
		public State execute(byte[] stream, StateVariables var) {
			
			var.r = stream[var.i++] & 0xFF;
			
			var.d -= 8;
			
			if(var.d < 8) {
				return R5;
			}
			if(var.d > 8) {
				return R3;
			}	
			return R4;
		}

	},
	R1{
		@Override
		public State execute(byte[] stream, StateVariables var) {
			
			int b = stream[var.i++] & 0xFF;
			
			var.r = b;
			
			var.emit();	//accept
			
			if (var.i == stream.length) return F;
			
			b = stream[var.i] & 0xFF;
			
			if (b > 1) {
				return Q2;
			}
			if(b == 1) {
				return Q1;
			}
			return Q0;
		}

	},
	R2{
		@Override
		public State execute(byte[] stream, StateVariables var) {
			
			int b = stream[var.i] & 0xFF;
			
			var.r = b >> (8 - var.d);
			
			var.emit();	//accept
			
			var.m = b & (255 >> var.d);
			
			var.c = 8;
			
			if (var.m > 1) {
				return Q7;
			}
			if(var.m == 1) {
				return Q6;
			}
			return T1;
		}

	},
	R3{
		@Override
		public State execute(byte[] stream, StateVariables var) {
			
			int b = stream[var.i++] & 0xFF;
			
			var.r = (var.r << 8) | b;
			
			var.d -= 8;
			
			if(var.d < 8) {
				return R5;
			}
			if(var.d > 8) {
				return R3;
			}	
			return R4;
		}

		
	},
	R4{
		@Override
		public State execute(byte[] stream, StateVariables var){
			
			int b = stream[var.i++] & 0xFF;
			
			var.r = (var.r << 8) | b;
			
			var.emit();	//accept
			
			var.d = var.k;
			
			if (var.i == stream.length) return F;
			
			b = stream[var.i] & 0xFF;
			
			if (b > 1) {
				return Q2;
			}
			if(b == 1) {
				return Q1;
			}
			return Q0;
		}

	},
	R5{
		@Override
		public State execute(byte[] stream, StateVariables var){

			int b = stream[var.i] & 0xFF;
			
			int t = b >> (8 - var.d);
			
			var.m = b & (255 >> var.d);
			
			var.r = (var.r << var.d) | t;
			
			var.emit();	//accept
			
			if (var.m > 1) {
				return Q7;
			}
			if(var.m == 1) {
				return Q6;
			}
			return T1;
		}

	},
	R6{
		@Override
		public State execute(byte[] stream, StateVariables var){
			
			int b = stream[var.i++] & 0xFF;
			
			var.r = b & (255 >> (8 - var.d));
			
			var.emit();	//accept
			
			if (var.i == stream.length) return F;
			
			b = stream[var.i] & 0xFF;
			
			if (b > 1) {
				return Q2;
			}
			if(b == 1) {
				return Q1;
			}
			return Q0;
		}

	},
	R7{
		@Override
		public State execute(byte[] stream, StateVariables var){
			
			int b = stream[var.i] & 0xFF;

			int t = 8 - var.c;
			
			var.r = (b & (255 >> t)) >> (var.c - var.d);
			
			var.emit();	//accept
			
			var.m = (b & (255 >> (t + var.d)));
			
			if (var.m > 1) {
				return Q7;
			}
			if(var.m == 1) {
				return Q6;
			}
			return T1;
		}

	},
	T1{
		@Override
		public State execute(byte[] stream, StateVariables var){
			
			if (++var.i == stream.length) return F;
			
			int b = stream[var.i] & 0xFF;

			var.q = var.c - var.d;
			
			var.d = var.k;
			
			if (b > 1) {
				return Q5;
			}
			if(b == 1) {
				return Q4;
			}
			return Q3;
		}
	},
	F{	
		@Override
		public State execute(byte[] stream, StateVariables var){
			
			return null;
		}
	};
	
	public abstract State execute(byte[] stream, StateVariables var);
		
	
}
