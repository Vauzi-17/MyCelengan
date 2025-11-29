package com.mycelengan

data class TargetItem(
    val id: String = "",
    val title: String = "",
    val subtitle: String = "",
    val icon: String = "",
    val targetAmount: Int = 0,
    val currentAmount: Int = 0,
    val perMonth: Int = 0,
    val createdAt: String = ""
)
