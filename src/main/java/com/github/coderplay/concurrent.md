Concurrent
==========

An attempt to implements j.u.c whereby other alogrithms

# Getting Started

## Pre-requirement
- JDK 6 +

## Installation
    git clone https://github.com/coderplay/concurrent.git
    mvn clean package

## Examples
FastArrayBlockingQueue

## SingleThread and MultiThread
单线程的策略:
  SingleThreadedClaimStrategy
  SingleThreadedWaitStrategy

多线程的策略:
  MultiThreadedClaimStrategy
  MultiThreadedWaitStrategy

  MultiThreadedLowContentionClaimStrategy
  MultiThreadedLowContentionWaitStrategy




