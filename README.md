# finite-state-rice-decoder
Java implementation of byte-level finite state decoder for a stream of Rice codes. 

Finite-state machine implementation is done using Enum types (see State.java). The conventional bit-level decoding procedure is also implemented (see BitLevelDecoder.java). Finite-state decoding, which decodes variable length Rice codes bytewise, is faster and comparatively becomes more effective as the mean value of encoded integers increases. Speed gains up to a factor of 2 are observed at the point where optimal k is estimated as 7 (k=7). See Demo.java for a simple demonstration.
