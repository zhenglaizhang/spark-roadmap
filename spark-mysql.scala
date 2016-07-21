//run:  spark-shell --jars ~/.m2/repository/mysql/mysql-connector-java/5.1.39/mysql-connector-java-5.1.39.jar
//      scala> :load /path/to/this/file
import java.sql.{DriverManager, ResultSet}

val data = new JdbcRDD(sc, createConnection, "SELECT * FROM Cf_User WHERE ? <= id AND id <= ?",
  lowerBound = 1, upperBound = 120, numPartitions = 2, mapRow = extractValues)

def createConnection() = {
  val userName = sys.env.get("MYSQL.USERNAME").orElse("root").get
  val password = sys.env.get("MYSQL.PASSWORD").orElse("password").get
  Class.forName("com.mysql.jdbc.Driver").newInstance()
  DriverManager.getConnection(s"jdbc:mysql://121.40.53" +
    ".70:3306/jxcdb?user=${userName}&password=${password}&useUnicode=yes&characterEncoding=UTF-8")

}

def extractValues(r: ResultSet) = {
  r.getInt(1) -> r.getString(2)

}

println(data.collect.toList) 