#!/usr/bin/env sh
#
# Generate a gRPC UI available via the web browser
#
# Source code: https://github.com/fullstorydev/grpcui
# Blog post: https://www.fullstory.com/blog/grpcui-dont-grpc-without-it/

GRPC_UI_HOST='localhost'
GRPC_UI_PORT='8080'
GRPC_SERVER_ADR='localhost:8000'

grpcui -open-browser=false -bind ${GRPC_UI_HOST} -port ${GRPC_UI_PORT} -plaintext ${GRPC_SERVER_ADR}