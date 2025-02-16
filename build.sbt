name := "phouka"

version := "0.1"

fork := true

scalaVersion := "2.13.6"

javaOptions += "-Xmx2G"

libraryDependencies ++= {
  Seq(
    "ch.qos.logback" % "logback-classic" % "1.1.7",
    "com.typesafe" % "config" % "1.3.4",
    "org.scala-lang.modules" %% "scala-collection-contrib" % "0.2.1",
    "com.intellij" % "forms_rt" % "7.0.3",
    "org.jfree" % "jfreechart" % "1.5.0",
    "org.json4s" %% "json4s-native" % "3.6.9",
    "org.scalatest" %% "scalatest" % "3.2.3" % Test,
    "org.apache.commons" % "commons-math3" % "3.6.1",
    "org.swinglabs" % "swingx-core" % "1.6.2-2",
    "org.apache.poi" % "poi" % "5.1.0",
    "org.apache.poi" % "poi-ooxml" % "5.1.0",
    "org.apache.logging.log4j" % "log4j-slf4j-impl" % "2.14.1"

  )
}

//mappings in (Compile, packageBin) ~= { _.filterNot { case (file, path) => path == "META-INF/MANIFEST.MF" } }
