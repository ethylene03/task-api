package com.princess.taskapi

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class TaskapiApplication

fun main(args: Array<String>) {
	runApplication<TaskapiApplication>(*args)
}
