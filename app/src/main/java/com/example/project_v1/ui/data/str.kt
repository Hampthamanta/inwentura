package com.example.project_v1.ui.data

enum class str {
    address {
        override fun toString() = "Address"
    },
    phone_number {
        override fun toString() = "Phone number"
    },
    date {
        override fun toString() = "Date"
    },
    url {
        override fun toString() = "URL"
    },
    money {
        override fun toString() = "Money"
    },
    main {
        override fun toString() = "main"
    },
    settings {
        override fun toString() = "settings"
    },
    camera {
        override fun toString() = "camera"
    },
    http_query {
        override fun toString() = "http query"
    },

}