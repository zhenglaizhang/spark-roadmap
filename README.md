# spark-roadmap
Personal learning road map of Spark

# Preparing spark code reading
* fork spark repo and clone to local
* update pom.xml to set the `java.version` as 1.8
* `build/mvn -DskipTests clean package` to build spark
* import the spark to Intellij 

DONE 

# Play with spark-shell

```scala
# Driver 
bin/spark-shell

# SparkContext
sc
:type sc
:type -v sc

# Play with first RDD (immutable, multiple-partitions resilient distributed data set)
val lines = sc.textFiles("README.md")

# action returns non RDDs
lines first	# read the first line only!
lines count

# transformation return RDDs
lines filter { _.contains("Spark") } # lazy evaluation
liens filter { _.contains("Spark") } collect
val foo = lines filter { _.contains("Spark") } map { _ charAt 0 toUpper }  # chain of transformations
foo getClass
var bar = foo collect
bar getClass
foo.persist

# create RDD from scala collections
val lines = sc.parallelize(List("zhenglai", "zhang"))

# inefficient way to demo unions on 2 RDDs
val readme = sc.textFile("README.md")
val sparks = readme filter { _.contains("Spark") }
val scalas = readme filter { _.contains("Scala") }
sparks union scalas collect

# save to external storage
sparks.saveAsTextFile("/tmp/output")
```



### RDD operations

```scala
# map & flatMap
val readme = sc.textFile("README.md")
readme filter (_.contains("spark")) collect
readme flatMap ( _.split(" ") ) collect
readme distinct
# sample an RDD, w/o replacement
rdd1.sample(false, 0.5) collect

# Pseudo set operations (RDD are not properly sets)
val rdd1 = sc.parallelize(List(1, 2, 3, 4, 4))
val rdd2 = sc.parallelize(List(1, 2, 5))
rdd1.distinct.collect			# require shuffling
rdd1 union rdd2 collect			# doesn't remove duplicate, no shuffling
rdd1 intersection rdd2 collect  # remove duplicates, shuffling
rdd1 subtract rdd2 collect		# require shuffling	


# Cartesian product
rdd1 cartesian rdd2 collect


# sample
val rdd1 = sc.parallelize(1 to 100)
rdd1.sample(false, 0.5) collect
rdd1.sample(false, 0.5) collect
rdd1.sample(true, 0.5) collect
rdd1.sample(true, 0.5) collect
```

#### Actions

```scala
rdd1 reduce (_+_)
rdd1.fold(0)(_+_)	# zero element should be identity element, zero element is passed to each partition as intial value


val rdd = sc.parallelize(Vector(23, 2, 10, 200))
rdd count
rdd countByValue	# count each element (key -> count)
rdd collect	# all to driver
rdd take 2	# return 2 elements, miniming the no. of partitions accessed
rdd top 2 # with default ordering
rdd.takeSample(false, 2)
rdd foreach (println)	# no data back to driver

```



### calculate average
```scala
# use map + reduce
val rdd = sc.parallelize(1 to 5)
val pair = rdd map (_ -> 1) reduce ((a, b) => (a._1 + b._1) -> (a._2 + b._2))
pair._1 / pair._2.toDouble

# use aggregate
# TODO fix bug here
val sumCount = rdd.aggregate(0->0)(
    (acc, value) => acc._1+value -> acc._2+1, 
    (acc1, acc2) => acc1._1+acc2._1 -> acc1._2+acc2._2)
val avg = sumCount._1 / sumCount._2.toDouble
```


### Conversion

* `mean` or `variance` on numeric RDDs. (`DoubleRDDFunction`)
* `join` on key/value pair RDDs. (`PairRDDFunctions`)

Use `import org.apache.spark.SparkContext._` to import implicit conversions for extra RDD wrapper classes

### Persistence(Caching)

```scala
val result = rdd map (_ * _)
result.persist(Storagelevel.MEMORY_AND_DISK)
result count
result.collect.mkString(","))
```

## Pair RDD
Pair RDDs are allowed to use all thransformations available to standard RDDs
```scala
val readme = sc.textFile("README.md")
readme map (l => (l.split(" ")(0), l)) filter ( _._1 != "") collect

val rdd = sc.parallelize(List(1, 2, 3, 2, 4, 5)) map (x => x -> x*x)
rdd reduceByKey (_ + _) collect
rdd.groupByKey collect

rdd.mapValues(_+2) collect
// same as 
rdd map { case (x,y) => x -> (y+2) }

rdd.flatMapValues(x => x to 5) collect
rdd.keys()
rdd.values()
rdd.sortBykey()
```

#### Aggregation
```scala
val rdd = sc.parallelize(Vector(1, 2, 3, 3, 4))
val mrdd = rdd map (x => x->x*x)
mrdd reduceByKey(_+_) collect	// vs following statement
mrdd reduceByKeyLocally(_+_)

mrdd.mapValues(_->1).reduceByKey((x, y) => (x._1+y._1, x._2+y._2)) mapValues (x => x._1 / x._2) collect

# word count
sc.textFile("README.md") flatMap (_.split(" ")) map (_ -> 1) reduceByKey { (x, y) => x + y } collect	
sc.textFile("README.md") flatMap (_.split(" ")) countByValue
```

### partition

### Grouping

### Lazy evaluation


* `persist` and `unpersist` on RDD
* different storage levels, see `StorageLevel.scala`
* LRU cache policy


## Tips

* Avoid `collect` on large data set 
* `persist` or `cache` wisely for intermediate result to reuse hot data
* Be careful with `collect` and `take` on large data sets

## Lineage Graph