package com.priyanshparekh.fairshare.model

data class Group(
    val id: Long?,
    val name: String
) {
    constructor(name: String): this(null, name)
}
