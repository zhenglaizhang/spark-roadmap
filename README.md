# spark-roadmap
Personal learning road map of Spark

# Preparing spark code reading
* fork spark repo and clone to local
* update pom.xml to set the `java.version` as 1.8
* `build/mvn -DskipTests clean package` to build spark
* import the spark to Intellij 

DONE 

# Play with spark-shell

```bash
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
```



## Tips

* Avoid `collect` on large data set 
* `persist` or `cache` wisely

## Lineage Graph