package com.github.luzhuomi.regexanalyze

import java.sql.{Connection,DriverManager}

object Main extends App {
    // http://alvinalexander.com/scala/how-to-connect-mysql-database-scala-jdbc-select-query
    val url = "jdbc:mysql://localhost:3306/mysql"
    val driver = "com.mysql.jdbc.Driver"
    val username = "root"
    val password = "root"
    var connection:Connection = _
    try {
        Class.forName(driver)
        connection = DriverManager.getConnection(url, username, password)
        val statement = connection.createStatement
        val rs = statement.executeQuery("SELECT host, user FROM user")  // change the query to the answer table
        while (rs.next) {
            val host = rs.getString("host")
            val user = rs.getString("user") 
	    // access the pre columns 
            println("host = %s, user = %s".format(host,user))
	  /* use pderiv https://github.com/luzhuomi/scala-pderiv to extrac the things between ^ and $
	   if there exists a regex
	   call the evil diagnosis, refer to https://github.com/luzhuomi/scala-deriv/blob/master/src/test/scala/Test.scala 
	   */
	  
        }
    } catch {
        case e: Exception => e.printStackTrace
    }
    connection.close
}
