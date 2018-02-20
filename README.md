#Self-driving Automotive System

##Issues
### Interpreting Requirements
In the **Requirements** section of the document *HERE Technologies - Coding 
Exercise.pdf*, I take the strings "cannot fall below" and "cannot be applied"
 to suggest that the response to the events in question should be
1. do nothing
2. return the current speed

Only in the case of a sensor event code greater than 100 should the response 
be **INVALID** rather than the current speed.  
###Speeding?
According to the requirements, a valid Speed Limit Sign sensor event should
cause the speed to be raised to the value indicated
for the drive mode. The value for SPORT mode
is "X+5". This appears to mean that the system is
being told to drive five miles above the limit. 
This is the way it is implemented, because no other
interpretation suggested itself. 