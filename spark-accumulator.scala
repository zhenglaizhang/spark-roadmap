val file = sc.textFile("file:///Users/Zhenglai/git/spark/README.md")
val blankLines = sc.accumulator(0)
val lines = file flatMap { line => {
  if (line == "") {
    blankLines += 1
  } 
    line.split(" ")
  }
}

// lines.saveAsTextFile("wordList.txt")
println(s"Blank lines: ${blankLines.value}")


// another way of 2 passes
val file2 = sc.textFile("file:///Users/Zhenglai/git/spark/README.md")

file2 filter (_ == "") count

file2 filter (_ != "") flatMap (_.split(" "))
