
package com.github.luzhuomi.regexanalyze

import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf
import org.apache.spark.rdd._
// import org.apache.spark.sql.hive.HiveContext
// import org.apache.spark.sql.Row
import org.apache.spark.sql.Row
import org.apache.spark.sql.SparkSession


import com.github.luzhuomi.regex.deriv.RE._
import com.github.luzhuomi.regex.deriv.Common._
import com.github.luzhuomi.regex.deriv.diagnosis.Ambiguity._

object AmbigCheckDF {

  case class ResultRecord(id:Int, result:String)

  def main(args: Array[String]) {
    // val conf       = new SparkConf().setAppName("Regular Expression Ambiguity Check using Hive and Data frame")
    // val sc         = new SparkContext(conf)
    // val hiveContext = new HiveContext(sc)
    val spark = SparkSession
      .builder()
      .appName("Spark Hive Example")
      .config("spark.sql.warehouse.dir", "10.1.0.1:10000/user/hive/warehouse")
      .enableHiveSupport()
      .getOrCreate()
    // val dontcare1  = hiveContext.setConf("hive.metastore.warehouse.dir", "10.1.0.1:10000/user/hive/warehouse")
    // import hiveContext.implicits._
    import spark.implicits._
    import spark.sql
    val acceptedanswerDF = sql("SELECT * from stackoverflow.acceptedanswer")
    val regexs = acceptedanswerDF.map {  // partial function
      case Row(id:Int, url:String, pre:String, time_posted:String, author:String, vote:Int) =>  // why this is not declared anywhere?
        val result = checkRegex(pre) match {
          case None => "Not regex"
          case Some(v) => checkAmbig(v) 
        }
        ResultRecord(id,result)
    }
    val dontcare2  = sql("DROP TABLE IF EXISTS stackoverflow.regex_results")
    val dontcare3  = sql("CREATE TABLE stackoverflow.regex_results (id INT, result STRING)")
    regexs.write.mode("overwrite").saveAsTable("stackoverflow.regex_results")

    spark.stop()
  }

  val regex = "(\\/|\\^)[^\\:\\/\\/](.*?)(\\/|\\$|i|g|m)".r

  def checkRegex(x:String):Option[String] = {
    (regex findFirstIn x) match 
    {
    	case Some(v) => Some(v)
    	case None    => None
    }
  }

  def checkAmbig(x:String):String = {
  	isAmbiguous(x) match {
  		case Left(_)  => "Not regex"
  		case Right(true) => "Not ambiguous"
  		case Right(_) => "Ambiguous"
  	}
  }
}

/* # note we need spark 2.0 because of scala 2.11
$ sbt assembly
$ spark-submit --executor-memory 32G --master spark://master:7077 target/scala-2.11/regex-analyze-assembly-0.0.2.jar --class com.github.luzhuomi.regexanalyze.AmbigCheckDF
*/
