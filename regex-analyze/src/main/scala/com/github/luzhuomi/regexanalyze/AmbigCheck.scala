
package com.github.luzhuomi.regexanalyze

import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf
import org.apache.spark.rdd._
import com.github.luzhuomi.regex.deriv.RE._
import com.github.luzhuomi.regex.deriv.Common._
import com.github.luzhuomi.regex.deriv.diagnosis.Ambiguity._

object AmbigCheck {
  def main(args: Array[String]) {
    val inputfiles = "hdfs://10.1.0.1:9000/user/hive/warehouse/stackoverflow.db/acceptedanswer_pre/*" // Should be some file on your system
    val conf = new SparkConf().setAppName("Regular Expression Ambiguity Check")
    val sc = new SparkContext(conf)
    val data = sc.textFile(inputfiles, 2)
    val regexs = data.flatMap(x => checkRegex(x))
    val regexs_results:RDD[(String,String)] = regexs.map( r => (r, checkAmbig(r)) )
    val outputs = regexs_results.map( r_res => r_res._1+"\t"+r_res._2 )
    outputs.saveAsTextFile("hdfs://10.1.0.1:9000/output/regex")
    sc.stop()
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
$ spark-submit --executor-memory 32G --master spark://master:7077 target/scala-2.11/regex-analyze-assembly-0.0.1.jar --class com.github.luzhuomi.regexanalyze.AmbigCheck
*/
