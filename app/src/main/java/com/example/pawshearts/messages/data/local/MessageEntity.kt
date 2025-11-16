//package com.example.pawshearts.messages.data.local
//
//
//import androidx.room.Entity
//import androidx.room.PrimaryKey
//import androidx.room.TypeConverters
//import com.example.pawshearts.messages.model.ChatMessageUiModel
//import com.example.pawshearts.messages.model.MessageStatus
//import com.example.pawshearts.ui.ChatMessageUiModel
//import com.example.pawshearts.ui.MessageStatus
//
//@Entity(tableName = "messages")
//data class MessageEntity(
//    @PrimaryKey val id: String,
//    val text: String,
//    val time: String,
//    val isMine: Boolean,
//    val status: MessageStatus,
//    val avatarRes: Int? = null,
//    val bubbleColor: String? = null // saved as string, can convert later
//)
//
//fun MessageEntity.toUiModel(): ChatMessageUiModel {
//    return ChatMessageUiModel(
//        id = id,
//        text = text,
//        time = time,
//        isMine = isMine,
//        status = status,
//        avatarRes = avatarRes,
//        bubbleColor = bubbleColor?.let { ChatMessageUiModel.colorFromString(it) }
//    )
//}
//
//fun ChatMessageUiModel.toEntity(): MessageEntity {
//    return MessageEntity(
//        id = id,
//        text = text,
//        time = time,
//        isMine = isMine,
//        status = status,
//        avatarRes = avatarRes
//    )
//}
