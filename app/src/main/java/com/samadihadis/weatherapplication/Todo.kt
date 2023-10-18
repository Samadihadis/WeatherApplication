package com.samadihadis.weatherapplication

data class Todo (val userId : Int , val id : Int , val title : String ,val completed : Boolean){
    override fun toString(): String {
        return "Todo(userId=$userId, id=$id, title='$title', completed=$completed)"
    }
}