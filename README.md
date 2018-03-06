# Self-driving Automotive System

## Execution
Maven builds **selfDrive.jar** which is executable. 

The bash script **build.sh** will build the jar and make it runnable under 
the name **selfDrive**. It requires:
1. *nix OS.
2. Write permission on the **target** directory.
2. Maven executable.

This will probably work on **OSX** but if you're running **Windows**, you'll 
have to find a different way to set it up.

## Abstract Hexagonal Architecture
**CliPort** and **DefaultInputAdapter**, used in the **CommandLine** CLI, are 
instantiations of refined abstractions of the ports and adapters discussed in 
[Hexagonal Architecture](http://alistair.cockburn.us/Hexagonal+architecture).
I call this elaboration **AHA** (Abstract Hexagonal Architecture).

An AHA **Port** handles packaging and transportation of non-domain-specific 
objects
  like strings and JSON; an AHA **Adapter** translates between those objects 
  and domain objects of the app's 
  [bounded context](https://martinfowler.com/bliki/BoundedContext.html). The 
  conversation between the two allows no domain objects to cross the boundary.
  
  An AHA **Ujoint** (universal joint) is an 
  orchestrator/[presenter](https://en.wikipedia.org/wiki/Presenter_first_ 
  (software_approach)) that binds a port and an adapter. **CommandLine** plays 
  that role here.
  
  The Port, Ujoint and Adapter together form a 
  [monad](https://en.wikipedia.org/wiki/Monad_(functional_programming))-like 
  uni- or bidirectional pipeline.

## OOFP and Java
Object Oriented Functional Programming (OOFP) is my term for the combination 
of the best practices of OOP and FP. Practicing OOFP using Java requires that
 we mitigate two of the biggest mistakes in Java: exceptions and nulls.
 
### Results Instead of Exceptions
See the **Result** class for an example of returning failure information 
without using exceptions. It is similar to the Java 8 **Optional** class, but
 specialized for function returns.
 
 Since constructors have no way to indicate failure other than throwing 
 exceptions, I have used static factory methods returning Result and private 
 constructors as examples of OOFP in several classes - e.g., 
 **DefaultInputAdapter**.
 
 ### Specialized Instances Instead of Nulls
 In a few cases I have added static instances of enums and other flyweights 
 to represent unspecified/unset/unknown/invalid values. See for example 
 **SensorType.NOT_A_VALID_TYPE**. 
 
 There are still occurrences of null in the code - eventually I hope to make 
 those unnecessary as well.
 
 
 
## Extensibility
### New Events
Adding new events would be relatively straightforward and linear (i.e., 
coding effort would be O(n)):

1. Create a new test class extending **SelfDriverTestBase**
2. Create a handler method in **SelfDriver**

**SelfDriver** is already a big class and should be refactored to put event 
handlers in separate classes.

### New Driving Modes
This becomes a [double dispatch](https://en.wikipedia.org/wiki/Double_dispatch) 
problem and would require a more radical 
refactoring of test classes and mode configuration maps, but not 
**SelfDriver** itself.

A simple change for tests would be to dynamically build expectations from the
 mode configurations ("speed maps") themselves. This would eliminate the 
 current redundancy requires "double entry bookkeeping" when mode 
 configurations change.
 
 Better yet - inject the maps. Tests can then define or randomly generate 
 simple mock maps to isolate specific functionalities. 

## Issues
### Code Coverage
Domain code coverage is over 90%. 

Support code coverage is lower, primarily because some support classes are 
copied from my personal codebases, where they are well tested. If I had 
provided them in an external jar, there would be no issue.

## Comments on Process 
I really enjoyed this exercise because it gave me a chance to try out some 
ideas I've had about the development process. They work!

I'm writing this after a 
refactoring that radically tightened up the code for **CommandLine**. I had 
started writing that class and **InputAdapter** without applying the 
Abstract Hexagonal Architecture principles I had worked out previously. 

I had sketched in the **Port** and **Adapter** abstractions in the **Abstract 
Hexagonal Architecture** section above, but in the first few TDD cycles, a 
**DriveMode** enum - an element of the domain model - was passed between them.

The code worked fine - the unit tests passed. But when I noticed the violation
of the context boundary, I refactored the interfaces so that only strings 
were passed.

The resulting simplicity (I deleted half the methods and lines) and increased 
clarity of the code was - like wow!

