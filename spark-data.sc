
case class Person(first: String, last: String, age: Int)
val data = Seq(
  Person("first1", "last1", 12),
  Person("first2", "last2", 20),
  Person("first3", "last3", 30)
)
val rdd = sc.parallelize(data)

val text = sc.textFile("file:///Users/Zhenglai/Data/salesFiles/*")

