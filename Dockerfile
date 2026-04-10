FROM gradle:8.7.0-jdk21 AS BUILDER
WORKDIR /home/gradle/project

# Clone the repo
RUN apt-get update && apt-get install -y git
RUN git clone https://github.com/CKhamis/Constantine-INGRID-Polariscope .

# Build
RUN gradle installBootDist

FROM amazoncorretto:21
WORKDIR /usr/build
COPY --from=BUILDER /home/gradle/project/build/install/polariscope-boot .
WORKDIR /
EXPOSE 82
CMD ["./usr/build/bin/polariscope"]