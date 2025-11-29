import {onDocumentCreated} from "firebase-functions/v2/firestore";
import * as logger from "firebase-functions/logger";
import * as admin from "firebase-admin";

admin.initializeApp();
const db = admin.firestore();

// =========================================================================================
// BOT 1: XỬ LÝ YÊU CẦU NOTIFICATION TỪ pending_notifications
// =========================================================================================
export const createNotificationOnEvent = onDocumentCreated("pending_notifications/{pendingId}", async (event) => {
  const snapshot = event.data;
  if (!snapshot) {
    logger.log("No data from pending_notifications event");
    return;
  }
  const data = snapshot.data();
  const receiverId = data.receiverId;
  const senderId = data.senderId;

  const senderDoc = await db.collection("users").doc(senderId).get();
  const senderData = senderDoc.data();
  if (!senderData) {
    logger.log(`Sender with ID ${senderId} not found.`);
    return snapshot.ref.delete();
  }

  const actorName = senderData.username || senderData.displayName || "Ai đó";
  const actorAvatarUrl = senderData.profilePictureUrl || senderData.photoUrl || null;
  const type = data.type;
  let message = "";

  if (type === "LIKE") {
    message = "đã thích bài viết của bạn.";
  } else if (type === "COMMENT") {
    const commentText = data.commentText || "";
    message = `đã bình luận về bài viết của bạn: "${commentText}"`;
  } else {
    logger.log(`Unknown notification type: ${type}`);
    return snapshot.ref.delete();
  }

  const notificationPayload = {
    userId: receiverId,
    actorId: senderId,
    actorName,
    actorAvatarUrl,
    type,
    message,
    postId: data.postId,
    isRead: false,
    createdAt: admin.firestore.FieldValue.serverTimestamp(),
  };
  
  const newNotiRef = db.collection("notifications").doc();
  await newNotiRef.set({ ...notificationPayload, id: newNotiRef.id });

  return snapshot.ref.delete();
});

// =========================================================================================
// BOT 2: XỬ LÝ YÊU CẦU FOLLOW/UNFOLLOW TỪ pending_actions
// =========================================================================================
export const processFollowAction = onDocumentCreated("pending_actions/{actionId}", async (event) => {
  const snapshot = event.data;
  if (!snapshot) {
    logger.log("No data from pending_actions event");
    return;
  }
  const action = snapshot.data();

  if (action.type !== "TOGGLE_FOLLOW") {
    logger.log("Not a TOGGLE_FOLLOW action.");
    return snapshot.ref.delete();
  }

  const { actorId, targetId } = action;
  if (!actorId || !targetId) {
    logger.log("Missing actorId or targetId.");
    return snapshot.ref.delete();
  }

  const actorRef = db.collection("users").doc(actorId);
  const targetRef = db.collection("users").doc(targetId);

  const [actorDoc, targetDoc] = await Promise.all([actorRef.get(), targetRef.get()]);
  const actorData = actorDoc.data();
  const targetData = targetDoc.data();

  if (!actorData || !targetData) {
    logger.log("Actor or Target user data not found.");
    return snapshot.ref.delete();
  }

  const isCurrentlyFollowing = (actorData.following || []).includes(targetId);
  const batch = db.batch();

  if (isCurrentlyFollowing) {
    batch.update(actorRef, { following: admin.firestore.FieldValue.arrayRemove(targetId) });
    batch.update(targetRef, { followers: admin.firestore.FieldValue.arrayRemove(actorId) });
  } else {
    batch.update(actorRef, { following: admin.firestore.FieldValue.arrayUnion(targetId) });
    batch.update(targetRef, { followers: admin.firestore.FieldValue.arrayUnion(actorId) });
    
    const actorName = actorData.username || actorData.displayName || "Ai đó";
    const actorAvatarUrl = actorData.profilePictureUrl || actorData.photoUrl || null;
    
    const notiRef = db.collection("notifications").doc();
    batch.set(notiRef, {
      id: notiRef.id,
      userId: targetId,
      actorId,
      actorName,
      actorAvatarUrl,
      type: "FOLLOW",
      message: "đã bắt đầu theo dõi bạn.",
      isRead: false,
      createdAt: admin.firestore.FieldValue.serverTimestamp(),
    });
  }
  
  await batch.commit();
  return snapshot.ref.delete();
});

// =========================================================================================
// BOT 3: THÔNG BÁO BÀI ĐĂNG MỚI CHO FOLLOWERS
// =========================================================================================
export const notifyFollowersOnNewPost = onDocumentCreated("posts/{postId}", async (event) => {
  const snapshot = event.data;
  if (!snapshot) {
    logger.log("No data from new post event");
    return;
  }
  const post = snapshot.data();
  const { userId: authorId, userName: authorName } = post;
  
  const authorDoc = await db.collection("users").doc(authorId).get();
  const followers = authorDoc.data()?.followers || [];

  if (followers.length === 0) {
    logger.log(`No followers to notify for post by ${authorId}.`);
    return;
  }

  const batch = db.batch();
  followers.forEach((followerId: string) => {
    if (followerId === authorId) return;
    
    const notiRef = db.collection("notifications").doc();
    batch.set(notiRef, {
      id: notiRef.id,
      userId: followerId,
      actorId: authorId,
      actorName: authorName || "Ai đó",
      type: "NEW_POST",
      message: "đã đăng một bài viết mới.",
      postId: snapshot.id,
      isRead: false,
      createdAt: admin.firestore.FieldValue.serverTimestamp(),
    });
  });

  return batch.commit();
});
