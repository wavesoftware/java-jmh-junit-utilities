# JMH Utils for JUnit
This micro library holds a set of JUnit utilities for usage with JMH testing framework.

[![Build Status](https://travis-ci.org/wavesoftware/java-jmh-junit-utilities.svg?branch=master)](https://travis-ci.org/wavesoftware/java-jmh-junit-utilities) [![Coverage Status](https://coveralls.io/repos/wavesoftware/java-jmh-junit-utilities/badge.svg?branch=master&service=github)](https://coveralls.io/github/wavesoftware/java-jmh-junit-utilities?branch=master) [![SonarQube Tech Debt](https://img.shields.io/sonar/http/sonar-ro.wavesoftware.pl/pl.wavesoftware:jmh-junit-utilities/tech_debt.svg)](http://sonar-ro.wavesoftware.pl/dashboard/index/3862) [![Maven Central](https://img.shields.io/maven-central/v/pl.wavesoftware/jmh-junit-utilities.svg)](https://search.maven.org/artifact/pl.wavesoftware/jmh-junit-utilities/)

## General use

### `JmhCleaner` extension

JMH is kinda dirty. It leaves after himself a set of tests classes. They are placed in Maven generated sources directory and may cause a false positives in code analysis tools like SonarQube. `JmhCleaner` rule is designed to overcome that and remove any JMH generated classes.
  
Use it as a standard JUnit 5 extension. One needs to pass a test class in constructor.

Example:

```java
@RegisterExtension
static JmhCleaner cleaner = new JmhCleaner(MyClassTest.class);
```

### `JavaAgentSkip` extension

JMH is, as any performance framework, sensitive to all intrusions like running java agents. Those are coverage tools (Jacoco), debuggers, profilers and so on. To overcome that one can use `JavaAgentSkip` rule to automatically skip test is JAva agent is present.

Example:

```java
@RegisterExtension
static JavaAgentSkip javaAgentSkip = JavaAgentSkip.ifPresent();
```

## Maven

```xml
<dependency>
    <groupId>pl.wavesoftware</groupId>
    <artifactId>jmh-junit-utilities</artifactId>
    <version>2.0.0</version>
</dependency>
```

## Contributing

Contributions are welcome!

To contribute, follow the standard [git flow](http://danielkummer.github.io/git-flow-cheatsheet/) of:

1. Fork it
1. Create your feature branch (`git checkout -b feature/my-new-feature`)
1. Commit your changes (`git commit -am 'Add some feature'`)
1. Push to the branch (`git push origin feature/my-new-feature`)
1. Create new Pull Request

Even if you can't contribute code, if you have an idea for an improvement please open an [issue](https://github.com/wavesoftware/java-jmh-junit-utilities/issues).

## Requirements

* JDK >= 1.8

### Releases
 
- 2.0.0
  - Support for JDK >= 1.8
  - Support for JUnit 5
  - Dropped support for JUnit 4
 
- 1.0.0
  - Support for JDK >= 1.6
  - Initial release
