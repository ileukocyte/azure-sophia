# Technical Details
## Software Setup
* [Amazon Corretto 17.0.7](https://docs.aws.amazon.com/corretto/latest/corretto-17-ug/downloads-list.html)
* [JavaFX 17.0.7](https://gluonhq.com/products/javafx/)
* [IntelliJ IDEA 2023.1.1](https://www.jetbrains.com/idea/download/)
* [SceneBuilder 19.0.0](https://gluonhq.com/products/scene-builder/)
* [Gradle 7.6](https://gradle.org/releases/)

## Running via an IDE
* In case the project is run via IntelliJ IDEA, all the dependencies should be gotten automatically from Gradle
and one should be able to run the program without any further modifications (such as additional VM arguments)
* In Eclipse IDE it is recommended to use the following VM argument if Gradle does not fetch JavaFX dependencies:
`--module-path "PATH_TO_JAVAFX_SDK/lib" --add-modules=javafx.controls,javafx.fxml`

## Fat JAR Generation
In order to generate a fat JAR, one should run the task named "fatJar" provided in "build.gradle"

## Executable JAR Running
In order to run the executable JAR file, it is enough to open it directly, yet it is also important to keep
the "data" directory in the same directory as it contains JSON data essential for the program to function.

## Application Nuances
* In order to create an account, you must follow these guidelines:
  * No registration field must be empty
  * The username may only contain English alphabet letters, digits, underscores, and full stops
  * The username's length must be in the range from 4 through 32 characters
  * The password must contain at least 8 characters
  * The password cannot repeat the username
* When creating a plan it is only obligatory to provide either a description or an attachment (a tip or a place);
the rest (date and priority) is optional
* A plan card in the user's plan list contains an image in case it has an attachment; no custom image
can be added to a plan without an attachment manually
* An attachment can only be added to a plan directly from the place/tip view or via the view history ("Recently Viewed")
from the plan creation window