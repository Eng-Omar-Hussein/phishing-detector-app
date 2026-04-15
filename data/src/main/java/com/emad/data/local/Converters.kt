package com.emad.data.local

import androidx.room.TypeConverter
import com.emad.domain.model.Attachment
import com.emad.domain.model.FolderType
import com.emad.domain.model.User
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {

    private val gson = Gson()

    // -----------------------------------------------------------
    // 1. List<User> Converter (For Recipients, CC, BCC)
    // -----------------------------------------------------------
    @TypeConverter
    fun fromUserList(users: List<User>?): String {
        return gson.toJson(users)
    }

    @TypeConverter
    fun toUserList(data: String?): List<User> {
        if (data == null) return emptyList()
        val listType = object : TypeToken<List<User>>() {}.type
        return gson.fromJson(data, listType)
    }

    // -----------------------------------------------------------
    // 2. Single User Converter (For Sender)
    // -----------------------------------------------------------
    @TypeConverter
    fun fromUser(user: User?): String {
        return gson.toJson(user)
    }

    @TypeConverter
    fun toUser(data: String?): User {
        // If data is null, return a dummy user or handle appropriately
        // For safety, let's assume valid JSON or return a basic object
        val type = object : TypeToken<User>() {}.type
        return gson.fromJson(data, type)
    }

    // -----------------------------------------------------------
    // 3. List<Attachment> Converter
    // -----------------------------------------------------------
    @TypeConverter
    fun fromAttachmentList(attachments: List<Attachment>?): String {
        return gson.toJson(attachments)
    }

    @TypeConverter
    fun toAttachmentList(data: String?): List<Attachment> {
        if (data == null) return emptyList()
        val listType = object : TypeToken<List<Attachment>>() {}.type
        return gson.fromJson(data, listType)
    }

    // -----------------------------------------------------------
    // 4. FolderType Enum Converter
    // -----------------------------------------------------------
    @TypeConverter
    fun fromFolderType(folder: FolderType): String {
        return folder.name // Saves "INBOX", "SENT", etc.
    }

    @TypeConverter
    fun toFolderType(value: String): FolderType {
        return try {
            FolderType.valueOf(value)
        } catch (e: Exception) {
            FolderType.INBOX // Default fallback if something goes wrong
        }
    }
}