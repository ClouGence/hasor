@echo off

setlocal
cd %~dp0

rd build
mvn clean package -P export