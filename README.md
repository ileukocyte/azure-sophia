# Oleksandr Oksanich - Azure Sophia's Advisor
Imagining that the current situation in Eastern Europe is a tad more peaceful, Azure Sofia’s Advisor is
an application that is meant to acquaint a traveler with every fascinating sight spread over the area of
840 km² that belongs to Kyiv—the capital city of Ukraine and the cradle of Eastern European culture.
The purpose is to give insight into the city’s mix of historical and modern architecture, its landmarks
and monuments which have absorbed more than a thousand of years of historic events; its nature,
entertainment options, cultural events, and a bunch of sights to explore for tourists of all ages
for affordable prices. The app's another objective is to let its users organize and schedule a plan for
the trip based on a place or a tip they are interested in.

## Icon Attribution
* [User icons created by kmg design - Flaticon](https://www.flaticon.com/free-icons/user)
* [Home](https://icons8.com/icon/2797/home) icon by [Icons8](https://icons8.com)
* [What I Do](https://icons8.com/icon/95015/what-i-do) icon by [Icons8](https://icons8.com)
* [Customer](https://icons8.com/icon/14736/customer) icon by [Icons8](https://icons8.com)
* [Settings](https://icons8.com/icon/2969/settings) icon by [Icons8](https://icons8.com)
* [Error](https://icons8.com/icon/8122/error) icon by [Icons8](https://icons8.com)
* [Approval](https://icons8.com/icon/11221/approval) icon by [Icons8](https://icons8.com)
* [Info](https://icons8.com/icon/2800/info) icon by [Icons8](https://icons8.com)
* Icon by [manshagraphics](https://freeicons.io/profile/433683) on [freeicons.io](https://freeicons.io)

## Table of Contents

* Project documentation
  * [JavaDoc documentation](documentation/javadoc)
  * [UML diagrams](documentation/001_uml_diagrams.md)
  * [Versions](documentation/002_versions.md)
  * [Technical details](documentation/003_tech_details.md)
  * [Video demonstration](documentation/004_simulation_and_demonstration.md)

## Fulfillment of criteria

### Main criteria

* Polymorphism
  * Place.java - line #34
  * Tip.java - line #32
  * PlaceDatabase.java - lines #29, #33
  * TipDatabase.java - lines #29, #33
* Inheritance
  * Place.java - line #8
  * Tip.java - line #6
  * SelfUser.java - line #12
  * InsufficientPlanDetailException.java - line #7
* Encapsulation
  * PlaceDatabase.java - line #24
  * TipDatabase.java - line #24
  * UserCredentials.java - line #13
  * Place.java - line #9
  * Plan.java - line #9
  * PlanBuilder.java - line #9
  * SelfUser.java - line #13
  * Tip.java - line #7
  * User.java - line #11
  * MainApplication.java - line #14
* Aggregation
  * Place.java - line #75
  * Plan.java - line #77
  * Version.java - line #50

### Secondary criteria

* Design patterns
  * Singleton: MainApplication.java
  * Builder: PlanBuilder.java
* Own exception classes
  * InsufficientPlanDetailException.java (PlanCreationController.java - line #192, ControllerUtilities.java - line #1490)
* Nested classes
  * Place.java - line #75
  * Plan.java - line #77
  * Version.java - line #50
* Lambda expressions
  * SelfUser.java - line #90
  * PlaceDatabase.java - line #49
* RTTI
  * ControllerUtilities.java - lines #375, #1310, #1331
* Default method implementation
  * PlanAttachment.java - line #19
* GUI providing + event handling:
  * ControllerUtilities.java - lines #62, #74, #162, #523, #1437, etc.
  * HomePageController.java - line #163
  * PlanCreationController.java - lines #135, #159