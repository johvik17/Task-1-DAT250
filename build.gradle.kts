plugins {
    java
}

group = "com.example"
version = "0.0.1-SNAPSHOT"

java {
    toolchain { languageVersion = JavaLanguageVersion.of(21) }
}

repositories { mavenCentral() }

dependencies {
    // Kun det vi trenger for JPA-oppgaven
    implementation("org.hibernate.orm:hibernate-core:7.1.1.Final")
    implementation("jakarta.persistence:jakarta.persistence-api:3.2.0")
    implementation("com.h2database:h2:2.3.232")

    testImplementation("org.junit.jupiter:junit-jupiter:5.10.3")
}

tasks.test { useJUnitPlatform() }


sourceSets {
    main {
        java {
            setSrcDirs(listOf("src/main/java"))
            // ekskluder Spring Boot app + web + service
            exclude(
                "com/example/demo/DemoApplication.java",
                "com/example/demo/web/**",
                "com/example/demo/service/**"
            )
        }
        resources {
            setSrcDirs(listOf("src/main/resources"))
        }
    }
    test {
        java.setSrcDirs(listOf("src/test/java"))
        resources.setSrcDirs(listOf("src/test/resources"))
    }
}
