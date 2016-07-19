// For accumulators used in actions, Spark applies each task’s update to each accumulator only once. 
// For accumulators used in RDD transformations instead of actions, this guarantee does not exist for speculative execution
// Within transformations, accumulators should, conse‐ quently, be used only for debugging purposes.
//  commutative if a op b = b op a 
//  associative if (a op b) op c = a op (b op c) 

val file = sc.textFile("file:///Users/Zhenglai/git/spark/README.md")

// init the accumulator as Accumulator[Int] with 0 init value
val blankLines = sc.accumulator(0)

val lines = file flatMap { line => {
  if (line == "") {
    // add to accumulator, counting blanklines (errors, debugging events... counting)
    // worker code in spark closures, accumulator is write-only here
    blankLines += 1
  } 
    line.split(" ")
  }
}

// lines.saveAsTextFile("wordList.txt")
// driver get the value
println(s"Blank lines: ${blankLines.value}")


// another way of 2 passes
val file2 = sc.textFile("file:///Users/Zhenglai/git/spark/README.md")

file2 filter (_ == "") count

file2 filter (_ != "") flatMap (_.split(" "))
