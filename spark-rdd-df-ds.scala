

// load spark--data.sc
// val rdd = sc.parallelize(data)

object rdd_holder {
  /*
  The main advantage of RDDs is that they are simple and well understood because they deal with concrete classes, providing a familiar object-oriented programming style with compile-time type-safety.

  The main disadvantage to RDDs is that they don’t perform particularly well. Whenever Spark needs to distribute the data within the cluster, or write the data to disk, it does so using Java serialization by default (although it is possible to use Kryo as a faster alternative in most cases). The overhead of serializing individual Java and Scala objects is expensive and requires sending both data and structure between nodes (each serialized object contains the class structure as well as the values). There is also the overhead of garbage collection that results from creating and destroying individual objects.
   */


  // 2 transformations + 1 action
  rdd filter (_.age > 21) map (_.last) saveAsObjectFile ("file:///tmp/user21.bin")
}

object df_holder {
  /*
  The DataFrame API introduces the concept of a schema to describe the data, allowing Spark to manage the schema and only pass data between nodes, in a much more efficient way than using Java serialization.

   There are also advantages when performing computations in a single process as Spark can serialize the data into off-heap storage in a binary format and then perform many transformations directly on this off-heap memory, avoiding the garbage-collection costs associated with constructing individual objects for each row in the data set. Because Spark understands the schema, there is no need to use Java serialization to encode the data.

   The DataFrame API is radically different from the RDD API because it is an API for building a relational query plan that Spark’s Catalyst optimizer can then execute.

    The query plan can be built from SQL expressions in strings or from a more functional approach using a fluent-style API.


    Because the code is referring to data attributes by name, it is not possible for the compiler to catch any errors. If attribute names are incorrect then the error will only detected at runtime, when the query plan is created.

    Another downside with the DataFrame API is that it is very scala-centric and while it does support Java, the support is limited.

     when creating a DataFrame from an existing RDD of Java objects, Spark’s Catalyst optimizer cannot infer the schema and assumes that any objects in the DataFrame implement the scala.Product interface

     Scala case classes work out the box because they implement this interface.
   */

  val df = rdd toDF

  // sql style
  df.filter("age > 21") collect
  // Array[org.apache.spark.sql.Row] = Array([first3,last3,30])

  // expression builder style
  df.filter(df.col("age").gt(21))
  //
}

object ds_holder {
  /*
  The Dataset API, released as an API preview in Spark 1.6, aims to provide the best of both worlds; the familiar object-oriented programming style and compile-time type-safety of the RDD API but with the performance benefits of the Catalyst query optimizer. Datasets also use the same efficient off-heap storage mechanism as the DataFrame API.


  When it comes to serializing data, the Dataset API has the concept of encoders which translate between JVM representations (objects) and Spark’s internal binary format. Spark has built-in encoders which are very advanced in that they generate byte code to interact with off-heap data and provide on-demand access to individual attributes without having to de-serialize an entire object.

  Additionally, the Dataset API is designed to work equally well with both Java and Scala. When working with Java objects, it is important that they are fully bean-compliant.
   */
  val ds = sqlContext.createDataset(rdd)

  // Transformations with the Dataset API look very much like the RDD API and deal with the Person class rather than an abstraction of a row.

  ds.filter(_.age < 21) collect

  /*
  Despite the similarity with RDD code, this code is building a query plan, rather than dealing with individual objects, and if age is the only attribute accessed, then the rest of the the object’s data will not be read from off-heap storage.
   */

  /*
  If you are developing primarily in Java then it is worth considering a move to Scala before adopting the DataFrame or Dataset APIs. Although there is an effort to support Java, Spark is written in Scala and the code often makes assumptions that make it hard (but not impossible) to deal with Java objects.

  If you need your code to go into production with Spark 1.6.0 then the DataFrame API is clearly the most stable option available and currently offers the best performance.

  However, the Dataset API preview looks very promising and provides a more natural way to code.
   */

}
