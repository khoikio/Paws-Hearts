import {onDocumentCreated} from "firebase-functions/v2/firestore";
import * as logger from "firebase-functions/logger";
import * as admin from "firebase-admin";

admin.initializeApp();

const db = admin.firestore();

export const createNotificationOnEvent = onDocumentCreated("pending_notifications/{pendingId}", async (event) => {
  const snapshot = event.data;
  if (!snapshot) {
    logger.log("No data associated with the event");
    return;
  }
  const data = snapshot.data();

  const receiverId = data.receiverId;
  const senderId = data.senderId;

  // Lấy thông tin người gửi (sender)
  const senderDoc = await db.collection("users").doc(senderId).get();
  const senderData = senderDoc.data();
  if (!senderData) {
    logger.log(`Sender with ID ${senderId} not found. Deleting pending notification.`);
    return snapshot.ref.delete();
  }

  const actorName = senderData.username || "Ai đó";
  const actorAvatarUrl = senderData.profilePictureUrl || null;
  const type = data.type;
  let message = "";

  if (type === "LIKE") {
    message = "đã thích bài viết của bạn.";
  } else if (type === "COMMENT") {
    const commentText = data.commentText || "";
    message = `đã bình luận về bài viết của bạn: "${commentText}"`;
  } else {
    logger.log(`Unknown notification type: ${type}. Deleting pending notification.`);
    return snapshot.ref.delete();
  }

  const notificationPayload = {
    userId: receiverId,
    actorId: senderId,
    actorName: actorName,
    actorAvatarUrl: actorAvatarUrl,
    type: type,
    message: message,
    postId: data.postId,
    isRead: false,
    createdAt: admin.firestore.FieldValue.serverTimestamp(),
  };

  const newNotiRef = db.collection("notifications").doc();

  await newNotiRef.set({
    ...notificationPayload,
    id: newNotiRef.id,
  });

  return snapshot.ref.delete();
});
