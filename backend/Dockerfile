# ---- Stage 1: Build ----
# Temporary build environment with Maven 3.9 + JDK 21 already installed.
# "AS build" names this stage so Stage 2 can pull the jar out of it later.
# This whole stage is discarded from the final image - it exists only to
# compile the code and produce target/*.jar.
FROM maven:3.9-eclipse-temurin-21 AS build

LABEL maintainer="Octavian <octaviookumu@gmail.com>"

WORKDIR /app

# Copies the backend/ folder (the build context) into /app inside the image.
# See .dockerignore for what's deliberately excluded (target/, .env, etc).
# It's two separate arguments to COPY, — COPY <source> <destination>.
# So COPY . . reads as "copy everything from the build context into whatever directory WORKDIR is currently set to."
COPY . .

# Compiles and packages the app into target/blog-*.jar.
# -DskipTests skips the test suite for faster builds - a failing test won't
# block a deploy this way, so don't rely on this as your safety net.
RUN mvn clean package -DskipTests

# ---- Stage 2: Run ----
# Fresh, separate image - only a JRE (runtime, no compiler/Maven) on Alpine
# Linux (a minimal distro). Nothing from Stage 1 carries over automatically;
# only what we explicitly COPY --from=build below makes it into this image.
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Reaches back into the "build" stage and grabs just the jar it produced,
# renaming it to app.jar. Source code, Maven cache, and the JDK itself are
# left behind - this is what keeps the final image small.
COPY --from=build /app/target/*.jar app.jar

# Documentation only - tells readers (and some tooling) the app listens on
# 8080. Doesn't actually publish the port; Render handles that via its own
# PORT env var and port detection at runtime.
EXPOSE 8080

# The actual startup command: runs the Spring Boot app.
# Array form (not a plain string) runs java directly rather than through a
# shell, so it handles container stop/kill signals cleanly.
ENTRYPOINT ["java", "-jar", "app.jar"]