package com.example.householder.data
import java.time.LocalDateTime

data class Product (var name: String, var count: Int, val time: LocalDateTime = LocalDateTime.now())