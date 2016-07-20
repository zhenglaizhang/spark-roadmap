// statistics operations on RDDs containing numeric data


val rdd = sc.parallelize(Vector(1, 2, 2, 4, 4, 5, 6, 6, 6))

println(rdd count)

// calculate mean
println(rdd mean)


// calculate mode
rdd.countByValue.toSeq.sortBy(_._2).last._1
rdd.countByValue.toSeq.sortWith(_._2 > _._2).head._1
rdd.groupBy(i=>i).map({ case (i, it) => (it.count(i=>true), i )  }).sortByKey(false, 4).first._2

// calculate median
rdd.zipWithIndex.map(_.swap).lookup((rdd.count / 2).toInt)

println(rdd sum)

println(rdd max)

println(rdd min)

println(rdd variance)

println(rdd sampleVariance)

println(rdd stdev)

println(rdd sampleStdev)

// calculate all
println(rdd.stats)
val stats = rdd.stats
val reasonableDistances = rdd.filter ( x => math.abs(x - stats.mean) < 1 * stats.stdev  ) collect