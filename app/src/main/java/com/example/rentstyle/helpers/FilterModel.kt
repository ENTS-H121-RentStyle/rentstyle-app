package com.example.rentstyle.helpers

object FilterModel {
    fun getExploreFilter() : ArrayList<String> {
        return arrayListOf(
            "Terkait",
            "Terbaru",
            "Terpopuler",
            "Termurah")
    }

    fun getOrderFilter() : ArrayList<String> {
        return arrayListOf(
            "Semua Pesanan",
            "Belum Dibayar",
            "Sedang Dikemas",
            "Sedang Disewa",
            "Belum Dikembalikan",
            "Pesanan Selesai",
            "Pesanan Dibatalkan")
    }
}