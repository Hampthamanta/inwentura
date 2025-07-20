package com.example.project_v1.data.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TestPostResult(
    @Json(name = "amount")
    val amount: String,
    @Json(name = "code")
    val code: String,
    @Json(name = "mess")
    val mess: String,
    @Json(name = "name")
    val name: String
)


@JsonClass(generateAdapter = true)
data class PostSendBarcodeResult(
    @Json(name = "username")
    val username: String,
    @Json(name = "filename")
    val filename: String,
    @Json(name = "barcode")
    val barcode: String,
    @Json(name = "barcode_recognized")
    val barcode_recognized: String,
    @Json(name = "already_knowed_barcode")
    val already_knowed_barcode: String,
    @Json(name = "product_name")
    val product_name: String,
    @Json(name = "amount")
    val amount: String
)


@JsonClass(generateAdapter = true)
data class PostAddToInventoryResult(
    @Json(name = "username")
    val username: String,
    @Json(name = "filename")
    val filename: String,
    @Json(name = "barcode")
    val barcode: String,
    @Json(name = "already_knowed_barcode")
    val already_knowed_barcode: String,
    @Json(name = "barcode_recognized")
    val barcode_recognized: String,
    @Json(name = "amount")
    val amount: String,
    @Json(name = "how_much_to_add")
    val how_much_to_add: String,
    @Json(name = "errorcode")
    val errorcode: String
)




data class PostSendBarcodeResultDataClass(
    var username: String,
    var filename: String,
    var barcode: String,
    var barcode_recognized: String,
    var already_knowed_barcode: String,
    var product_name: String,
    var amount: String
)


data class PostAddToInventoryResultDataClass(
    var username: String,
    var filename: String,
    var barcode: String,
    var already_knowed_barcode: String,
    var barcode_recognized: String,
    var amount: String,
    var how_much_to_add: String,
    var errorcode: String
)