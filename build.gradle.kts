plugins {
    id 'java'
    id 'application'
}

repositories {
    mavenCentral()
}

application {
    mainClass = 'com.glegoux.grpc.ServerMain'
}