[![Docker](https://github.com/SpleefX/Backend/actions/workflows/docker-publish.yml/badge.svg)](https://github.com/SpleefX/Backend/actions/workflows/docker-publish.yml)
# SpleefX Backend
The implementation of SpleefX's web backend, used for statistics, debug reports, and wiki

# Building
The project uses Gradle's build system. To build, run `gradle build`.

# Running
This project runs on [docker-compose](https://docs.docker.com/compose/). To run, you need to install `docker-ce` on your system.

1. Create a directory to run the backend, for emample `~/spleefx-web`.
2. Create a `docker-compose.yml` file, with its content identical to the [`docker-compose.yml`](/docker-compose.yml) in this repository.
3. Run `docker-compose up` to start the web server on port 443.