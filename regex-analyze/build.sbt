import AssemblyKeys._

import SonatypeKeys._

sonatypeSettings

name := "regex-analyze"

organization := "com.github.luzhuomi"

version := "0.0.2"

scalaVersion := "2.11.8"

crossScalaVersions := Seq("2.9.2", "2.10.3", "2.11.3")

resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"

resolvers += "Maven Repository" at "http://mvnrepository.com/artifact/"

resolvers += "OSS Sonatype" at "https://oss.sonatype.org/content/repositories/snapshots"

resolvers += "luzhuomi github repo" at "https://raw.githubusercontent.com/luzhuomi/mavenrepo/master/"

// resolvers += "Local Ivy Repository" at "file://"+Path.userHome.absolutePath+"/.ivy2/local"


libraryDependencies += "org.scalaz" %% "scalaz-core" % "7.2.4" // scala license
libraryDependencies += "com.github.luzhuomi" %% "scalazparsec" % "0.1.2" 
libraryDependencies += "mysql" % "mysql-connector-java" % "5.1.24"
libraryDependencies += "org.apache.spark" %% "spark-core" % "2.1.0"
libraryDependencies += "org.apache.spark" %% "spark-sql" % "2.1.0"

// libraryDependencies += "org.apache.spark" %% "spark-hivecontext-compatibility" % "2.0.0-preview"
libraryDependencies += "com.github.luzhuomi" %% "scalapderiv" % "0.0.8"  // apache license
libraryDependencies += "com.github.luzhuomi" %% "scaladeriv" % "0.0.2"

seq(assemblySettings: _*)


mergeStrategy in assembly <<= (mergeStrategy in assembly) { (old) =>
  {
    case PathList("log4j.properties") => MergeStrategy.discard
    case PathList("META-INF", xs @ _*) => MergeStrategy.discard
    case _ => MergeStrategy.last // leiningen build files
  }
}

publishTo := Some(Resolver.file("mavenLocal",  new File(Path.userHome.absolutePath+"/git/mavenrepo/")))
