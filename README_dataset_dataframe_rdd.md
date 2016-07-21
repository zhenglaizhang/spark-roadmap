## Apache Spark: RDD, DataFrame or DataSet

* Spark 1.0 used the RDD API.
* Spark 1.3 introduced DataFrame API
* Spark 1.6 recently released Dataset API.

### RDD's (Resilient Distributed Dataset)

#### Features
* From the developers view an RDD is just a simple set of java or Scala objects representing data.
* This interface has Java equivalent,
* RDD’s provides many transformations such as map(), filter() and reduce() for performing computations on the data.
* Each of these methods results in a new RDD representing the transformed data. However the action is not performed until an action method is called as collect() and saveAsObjectFile()

#### Word count

```scala
val textFile = sc.textFile("hdfs://")
val counts = textFile.flatMap(line => line.split(" ")).map(word => (word, 1)).reduceByKey(_ + _)
counts.take(20).foreach(println)
counts.saveAsTextFile("hdfs://")
```

#### Pros and cons of RDD’s:

* they are simple and well understood because they deal with concrete classes providing a familiar oop style with compile time type safety.
* The basic disadvantage of RDD’s is in terms of performance.
  * Whenever Spark needs to distribute the data within the cluster or write the data to disk, it does so using **Java serialization.** 
  * The overhead of serializing individual Java and Scala objects is very expensive.
  * There is also the overhead of **garbage collection** that results from creating and destroying individual objects.


### DataFrame API

#### Features
* This API introduced the concept of schema to describe the data, 
* allowing Spark to manage the schema and only pass the data between nodes, which is much efficient than Java serialization.

#### Word count
```scala
:load /Users/Zhenglai/git/spark-roadmap/spark-data.sc
val linesDF = text.toDF("line")
val wordsDF = linesDF.explode("line", "word")((line: String) => line.split(" "))
val wordCountDF = wordsDF.groupBy("word").count()
wordCountDF show
```

#### Pros and cons of DataFrame API

1. When performing computations in a single process as _Spark can serialize the data into off-heap storage in a binary format and perform transformations directly on this off-heap memory avoiding the garbage collection costs_.
2. Spark understands the **schema** and hence there is **no need of java serialization to encode the data**.
3. The disadvantage could be DataFrame API is different from the RDD’s as **DataFrame API build a relational query that Spark’s catalyst optimizer can execute,** this could be familiar with building query plans but not for majority of developers.
4. Also since **code is referring to data attribute by name,** it is not possible for compiler to catch any errors until runtime.
5. DataFrame API is **very much Scala centric **and limited support to Java.
6. **Spark’s catalyst optimizer cannot infer the schema and assumes that any objects in the DataFrames implement scala.product.**

### Dataset API

1. Provides the object oriented programming style and compile time type safety of the RDD API but with the performance benefits of catalyst query optimizer.
2. Datasets also provides the same *efficient off-heap storage mechanism* as the DataFrame API.
3. Dataset API has the concept of **encoders** which translate between *JVM representations(objects) and Spark’s internal binary format* when it comes to serialization. 
4  Spark has built in encoders which are very advanced in that **they generate byte code to interact with off-heap data and provide on-demand access to individual attributes without having to de-serialize an entire object.**

#### Word count
```scala
val ds = sqlContext.read.text("file:///users/zhenglai/git/spark/readme.md").as[String]
val wordCount = ds.filter(_ != "").flatMap(_.split(" ")).groupBy($"value").count()

```

#### Pros and Cons of Dataset API
1. As similar to RDD code, **even Dataset code is building a query plan, rather than dealing with individual objects**, and **if only one attribute is used in the code, only that attribute is accessed and the rest of the object’s data will not be read from the off-heap storage.**
2. The Dataset API is designed to work equally well with both Java and Scala.
3. Spark does not yet provide the API for implementing custom encoders, but that is planned for a future release.


## Conclusion
1. If we are developing primarily in Java then it is worth considering a move to Scala before adopting the DataFrame or Dataser API’s.
2. If we are developing in Scala and need code to go into production with Spark 1.6.0 then the DataFrame API is clearly the most stable option available and currently offers the best performance.
3. However, Dataset API preview looks very promising and provides a more natural way to code. Given the rapid evolution of Spark, it is likely that this API will mature very quickly for developing new applications.