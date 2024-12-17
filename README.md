# Ecommerce app 

Microservices Architecture [Spring boot 3 | Spring Cloud]

This application is developed for a business owner to simplify the process and contribute significantly  to 
the overall efficiency and scalability of an online venture.



## Table of Contents
- [Application Architecture](#application-architecture)
- [Containerization](#containerization)
- [Keycloak](#keycloak)
- [Zipkin](#zipkin)

## Application Architecture

This application is composed of 5 microservices, with 2 databases, PostgresSql and Mongo db  (just to use different databases). All the ecosystem is running in a private network. The entry microservice will be the gateway API and only 3 microservices will be exposed publicly through this API. Each microservice will pull its configuration from the config server, and when the app starts, they will register themselves in the discovery server which is in this case our eureka server.

the gateway API will connect to eureka server to get all the registered microservices.In this API, we define routes for the three publicly open microservices (customer, product, order). Neither  the payment nor the notification microservice won't be publicly exposed since the payment is only going to be used by the order microservice, and the notification microservice is an internal service.

When a customer places an order ,the order microservice checks the business requirements from the customer and product microservices. If a problem occurs, business exceptions will be thrown. Otherwise, the order microservice will store the order in the order database and  will send an order confirmation to Kafka, then it will start the payment process. Once the payment is processed, It will be saved in the payment database and then will send async payment confirmation to our broker.

The notification  system will consume two types of messages: the order confirmation and the payment confirmation. It will send emails to the customer and finally persist the emails in the notification database. And all this ecosystem will have a distributed tracing using ZIPKIN.

I'm currently working on the frontend part of this project.

<img src="images/app%20architecture.png" style="max-width: 100%; height: auto;" alt="architecture image">

## Containerization
For this matter, I used docker to optimise the deployment process, using four containers: mongo db, postgresSql, Kafka, and mail dev to manage emails.

<img src="images/docker%20for%20contenairization.PNG" style="max-width: 100%; height: auto;" alt="docker">


## Keycloak
Security plays a crucial role in our solution. We know that all requests go through the Gateway Api, so for that I used keycloak to validate all the requests coming to our system in this API (Gateway). 

<img src="images/keycloak%20for%20security.PNG" style="max-width: 100%; height: auto;" alt="Keycloak">

## Zipkin
For distributed tracing, I used Zipkin to give me all the flow from the entering request to the end of the request.

<img src="images/Zipkin%20for%20distributed%20tracing.PNG" style="max-width: 100%; height: auto;" alt="Zipkin">








