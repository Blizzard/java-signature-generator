# Signature Generator (Java Sample Project)

Provided as a sample implementation of the OAuth2 Client Credentials flow, using the Blizzard Battle.net APIs for data.

Creates a web service that generates an image based on a World of Warcraft character profile on demand.

## Setup

### Requirements
 * [Java 8+](https://www.java.com)
 * [Maven](https://maven.apache.org/)

### Environment variables

As we don't wish to commit our secrets into this repository, this project requires a few environment variables set in
order to successfully execute, much like you would expect to see in various hosting services. We ***do not*** recommend
committing your secrets in your project's code repository either.

| Variable Name          | Variable Usage                                                                                                 |
|------------------------|----------------------------------------------------------------------------------------------------------------|
| BLIZZARD_CLIENT_ID     | The Client ID from [https://develop.battle.net/access](https://develop.battle.net/access) for your project     |
| BLIZZARD_CLIENT_SECRET | The Client Secret from [https://develop.battle.net/access](https://develop.battle.net/access) for your project |

For local development, you can set the environment variables yourself.

For Windows users, run the following commands from a command prompt:
```bat
set BLIZZARD_CLIENT_ID=<YOUR_CLIENT_ID_GOES_HERE>
set BLIZZARD_CLIENT_SECRET=<YOUR_CLIENT_SECRET_GOES_HERE>
```

For POSIX users, run the following commands in a terminal:
```bash
export BLIZZARD_CLIENT_ID=<YOUR_CLIENT_ID_GOES_HERE>
export BLIZZARD_CLIENT_SECRET=<YOUR_CLIENT_SECRET_GOES_HERE>
```

## Result

If successful, you will see a [Spring Boot](https://spring.io/projects/spring-boot) application console log which will
allow you to submit a GET request at the following endpoint:
`
http://localhost:8080/signature?characterName=CHARACTERNAME&realmName=REALMNAME
`

Note: make sure to use the programmatic realm name, ex: "Area 52" would be "area-52" in the URL

The result will be a signature generated describing the character.


## Notes
This code makes heavy use of the [Lombok](https://projectlombok.org/) library for both the
[@Data](https://projectlombok.org/features/Data) annotation and [@Log4j2](https://projectlombok.org/features/log)
annotations to save a lot of boilerplate code for POJOs and Logging.

Similarly, we use Spring's [RestTemplate.exchange](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/client/RestTemplate.html#exchange-java.net.URI-org.springframework.http.HttpMethod-org.springframework.http.HttpEntity-java.lang.Class-)
to execute our GET requests, and transform the results into Java objects, described by the above Lombok annotated
classes. This was chosen, to allow the flexibility in submitting the token via header or via url parameter with minimal
forking logic.

We recommend submitting your token in the header on general security principals, as the URL requested is frequently
scraped and logged, which effectively steals an application's identity to make requests into our API system.

This is only intended as a launching point for developers to see an example application. There are many areas that 
could be improved.

There are many solutions for managing secrets and that topic is beyond the scope of this example to delve into. Please
perform your own due-diligence when researching this topic and implementing it.