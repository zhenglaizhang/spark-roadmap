//run:  spark-shell --jars ~/.m2/repository/mysql/mysql-connector-java/5.1.39/mysql-connector-java-5.1.39.jar
//      scala> :load /path/to/this/file
import java.sql.{DriverManager, ResultSet}
import org.apache.spark.rdd.JdbcRDD
def createConnection() = {
    Class.forName("com.mysql.jdbc.Driver").newInstance()
      DriverManager.getConnection("jdbc:mysql://121.40.53.70:3306/jxcdb?user=starfish&password=~@$Starfish666&useUnicode=yes&characterEncoding=UTF-8")

}

def extractValues(r: ResultSet) = {
    r.getInt(1) -> r.getString(2)

}

val data = new JdbcRDD(sc, createConnection, "SELECT * FROM Cf_User WHERE ? <= id AND id <= ?",
    lowerBound = 1, upperBound = 120, numPartitions = 2, mapRow = extractValues)
  
println(data.collect.toList) 