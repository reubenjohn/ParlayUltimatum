Parlay Ultimatum
===

Abstract
---
This project is inspired by a need for providing a systematic approach to resolving controversial topics in an unbiased democratic manner.
This project represents the Semester 4 Database Systems Lab project. However, if preliminary results show promise, it can be adapted to form a full-fledged online service.

Functional requirements
---
The service would support multiple users each requiring a login to be able to use the service. Each user can post a question that is to be debated upon. And other users can post their opinions on the topic as either for or against and why. Each of these opinions would also represent a sub-debate which can be further debated. Apart from this the application would also feature contests/challenges, which would be like a live debate.

Data requirements
---
The database must store the list of all users currently registered for the service. Every user has a unique username as well as the attributes first name and last name and the reputation which is a number that represents his activity.
Each user can post a Point which has a title, description, tags, a unique point ID and the timestamp of the time it was posted by the user. Each point can be owned by exactly one user. Each user can post multiple points.
A point can also be voted up or down exactly once by any user except the one who posted it. The timestamp of voting is also recorded.
Each point can also be commented upon by any user multiple times.
Each comment can also be voted up (but not down) by any user except the one who posted it.

Future Work
---
Parlay Ultimatum is still a work in progress and there is still a long way to go.
However, if preliminary results show promise, it can be adapted to form a full-fledged online service.
Moreover, the database would also represent a potential learning set for supervised machine learning algorithms. For instance, the service could showcase the most debated topics to the users based on what topics they have showed interest in in the past. 
