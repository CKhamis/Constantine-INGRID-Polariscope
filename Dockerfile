FROM gradle:8.7.0-jdk21 as BUILDER
COPY . .
RUN gradle installBootDist

FROM amazoncorretto:21
WORKDIR /usr/build
COPY --from=BUILDER /home/gradle/build/install/polariscope-boot .
WORKDIR /
EXPOSE 82
CMD ["./usr/build/bin/polariscope"]