import sbt._
import Keys._

object ProjectBuild extends Build {
    
    lazy val root = Project(id = "Project",
                            base = file(".")) aggregate(api, gui) dependsOn(api)

    lazy val api = Project(id = "api",
                           base = file("api"))

    lazy val gui = Project(id = "gui",
                           base = file("gui"))

}
