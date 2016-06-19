import sbt._
import Keys._

object ProjectBuild extends Build {
    
    lazy val root = Project(id = "Project",
                            base = file(".")) aggregate(priceApi, gui) dependsOn(priceApi)

    lazy val priceApi = Project(id = "priceApi",
                           base = file("priceApi"))

    lazy val gui = Project(id = "gui",
                           base = file("gui"))

}
