// Internally, Spark SQL uses this extra information - structual schema to perform extra optimizations. There are several ways to interact with Spark SQL including SQL, the DataFrames API and the Datasets API.
// One use of Spark SQL is to execute SQL queries written using either a basic SQL syntax or HiveQL
// A DataFrame is a distributed collection of data organized into named columns with richer optimizations under the hood.
// DataFrames can be constructed from a wide array of sources such as: structured data files, tables in Hive, external databases, or existing RDDs.
//
//
// A Dataset is a new experimental interface added in Spark 1.6 that tries to provide the benefits of RDDs (strong typing, ability to use powerful lambda functions) with the benefits of Spark SQL’s optimized execution engine. 
// A Dataset can be constructed from JVM objects and then manipulated using functional transformations (map, flatMap, filter, etc.).

val sqlContext = new org.apache.spark.sql.SQLContext(sc)

// this is used to implicitly convert an RDD to a DataFrame.
import sqlContext.implicits._

// HiveContext, which provides a superset of the functionality provided by the basic SQLContext

val df = sqlContext.read.json("file:///Users/Zhenglai/git/spark/examples/src/main/resources/people.json")

df show

df printSchema

df select "name" show

df.select(df("name"), df("age")+1).show()

df.filter(df("age") > 21) show

// count people by age
df.groupBy("age").count() show

val sqlContext = ... // An existing SQLContext
val df = sqlContext.sql("SELECT * FROM table")



// datasets
/*
Datasets are similar to RDDs, however, instead of using Java Serialization or Kryo they use a specialized Encoder to serialize the objects for processing or transmitting over the network. While both encoders and standard serialization are responsible for turning an object into bytes, encoders are code generated dynamically and use a format that allows Spark to perform many operations like filtering, sorting and hashing without deserializing the bytes back into an object.
 */
// Encoders for most common types are automatically provided by importing sqlContext.implicits._
val ds = Seq(1, 2, 3).toDS()
ds.map(_ + 1).collect()


// Encoders are also created for case classes
val ds = Seq(Person("Andy", 21), Person("Zhenglai", 26)).toDS()

// DataFrames can be converted to a Dataset by providing a class. Mapping will be done by name.

val path = "file:///Users/Zhenglai/git/spark/examples/src/main/resources/people.json";
val people = sqlContext.read.json(path).as[Person] collect