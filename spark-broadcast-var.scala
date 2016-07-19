// broadcast variables, allows the program to efficiently send a large, read-only value to all the worker nodes for use in one or more Spark operations.
//  a large, read-only lookup table 
//  a large feature vector 
//
//
//  Recall that Spark automatically sends all variables referenced in your closures to the worker nodes.
//  it can also be inefficient because =>
//    (1) the default task launching mechanism is optimized for small task sizes, and 
//    (2) you might, in fact, use the same variable in multiple parallel operations, but Spark will send it separately for each operation.
//
//
//    A broadcast variable is simply an object of type spark.broadcast.Broadcast[T], which wraps a value of type T. We can access this value by calling value on the Broadcast object in our tasks. The value is sent to each node only once, using an efficient, BitTorrent-like communication mechanism.
//    Any type works as long as it is also Serializable.
//    The variable will be sent to each node only once, and should be treated as read- only (updates will not be propagated to other nodes).


// fake one large lookup table
def loadLookupTable() = {
  Seq('a', 'b', 'c', 'd') map (c => c -> c.toInt) toMap
}

val lookupTable = sc.broadcast(loadLookupTable())
val data = sc.parallelize(Seq('a' -> "hello a", 'b' -> "hello b", 'c' -> "hello c", 'd' -> "hello d"))  
val result = data map {
  case (char, word) => 
  val int = lookupTable.value(char)
  int -> word
} collectAsMap

