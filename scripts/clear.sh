#!/bin/bash

curl -s -X POST http://localhost:8080/cache/clear | jq .
