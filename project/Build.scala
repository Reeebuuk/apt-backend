import sbt._
import Keys._

object ProjectBuild extends Build {
    
    lazy val root = Project(id = "Project",
                            base = file(".")) aggregate(api) dependsOn(api)

    lazy val api = Project(id = "api",
                           base = file("api"))

}
