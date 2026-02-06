package com.revconnectapp.ui;

import com.revconnectapp.model.User;
import com.revconnectapp.model.Post;
import com.revconnectapp.model.Comment;
import com.revconnectapp.service.UserService;
import com.revconnectapp.service.PostService;
import com.revconnectapp.service.ConnectionService;
import com.revconnectapp.service.NotificationService;
import com.revconnectapp.service.LikeService;
import com.revconnectapp.service.CommentService;
import com.revconnectapp.util.InputUtil;
import java.util.List;


public class FeedMenu {
    private User currentUser;
    private PostService postService;
    private UserService userService;
    private ConnectionService connectionService;
    private NotificationService notificationService;
    private LikeService likeService;
    private CommentService commentService;
    
    public FeedMenu(User currentUser) {
        this.currentUser = currentUser;
        this.postService = new PostService();
        this.userService = new UserService();
        this.connectionService = new ConnectionService();
        this.notificationService = new NotificationService();
        this.likeService = new LikeService();
        this.commentService = new CommentService();
    }
    
    public void show() {
        while (true) {
            System.out.println("\n📱 === SOCIAL FEED ===");
            System.out.println("1. ➕ Create New Post");
            System.out.println("2. 📋 View & Manage My Posts");
            System.out.println("3. 🌐 Browse All Public Posts");
            System.out.println("4. 🔍 Search Posts by User");
            System.out.println("5. 🤝 Connections Feed");
            System.out.println("0. Back to Dashboard");
            
            int choice = InputUtil.getInt();
            switch (choice) {
                case 1 -> createPostMenu();
                case 2 -> manageMyPosts();
                case 3 -> browseAllPublicPosts();
                case 4 -> searchPostsByUser();
                case 5 -> showConnectionsFeed();
                case 0 -> { return; }
                default -> System.out.println("❌ Invalid choice!");
            }
        }
    }
    
    private void createPostMenu() {
        System.out.print("New Post: "); 
        String content = InputUtil.getString();
        System.out.print("🏷️ Hashtags (optional): "); 
        String hashtags = InputUtil.getString();
        
        Post post = new Post();
        post.setUserId(currentUser.getId());
        post.setContent(content);
        post.setHashtags(hashtags);
        postService.createPost(post);
        System.out.println("✅ Posted successfully!");
        System.out.println("⏎ Press Enter...");
        InputUtil.getString();
    }
    
    private void manageMyPosts() {
        List<Post> myPosts = postService.getUserFeed(currentUser.getId());
        if (myPosts.isEmpty()) {
            System.out.println("📱 No posts yet! Create one above."); 
            return;
        }

        while (true) {
            System.out.println("\n📱 MY POSTS:");
            for (int i = 0; i < myPosts.size(); i++) {
                Post p = myPosts.get(i);
                int likeCount = likeService.getLikeCount(p.getId());
                int commentCount = commentService.getCommentCount(p.getId());
                String preview = safePreview(p.getContent());
                System.out.println((i+1) + ". " + preview + 
                    " ❤️(" + likeCount + ") 💬(" + commentCount + ")");
            }

            System.out.print("Select post (0=Back): ");
            int index = InputUtil.getInt() - 1;
            if (index < 0) break;
            if (index >= 0 && index < myPosts.size()) {
                interactWithPost(myPosts.get(index));
            }
        }
    }
    
    private void browseAllPublicPosts() {
        System.out.println("\n🌐 === ALL PUBLIC POSTS ===");
        System.out.println("Browse and interact with posts from everyone!");
        
        List<Post> allPosts = postService.getAllPublicPosts();
        
        if (allPosts.isEmpty()) {
            System.out.println("📭 No posts available yet!");
            System.out.println("💡 Be the first to post something!");
            System.out.println("\n⏎ Press Enter...");
            InputUtil.getString();
            return;
        }
        
        allPosts.sort((p1, p2) -> {
            if (p1.getCreatedAt() != null && p2.getCreatedAt() != null) {
                return p2.getCreatedAt().compareTo(p1.getCreatedAt());
            }
            return 0;
        });
        
        System.out.println("\n📊 TOTAL POSTS: " + allPosts.size());
        System.out.println("=".repeat(80));
        
        for (Post post : allPosts) {
            User author = userService.getUserById(post.getUserId());
            String authorName = (author != null) ? "@" + author.getUsername() : "User#" + post.getUserId();
            
            int likeCount = likeService.getLikeCount(post.getId());
            int commentCount = commentService.getCommentCount(post.getId());
            boolean iLiked = likeService.isLikedByUser(post.getId(), currentUser.getId());
            boolean isMyPost = post.getUserId() == currentUser.getId();
            
            System.out.println("\n" + "─".repeat(80));
            System.out.println("POST ID: " + post.getId());
            System.out.println("AUTHOR: " + authorName + (isMyPost ? " (YOU)" : ""));
            System.out.println("TIME: " + (post.getCreatedAt() != null ? post.getCreatedAt() : "Recently"));
            System.out.println("─".repeat(40));
            
            System.out.println("CONTENT:");
            System.out.println(post.getContent());
            
            if (post.getHashtags() != null && !post.getHashtags().isEmpty()) {
                System.out.println("\n🏷️ TAGS: " + post.getHashtags());
            }
            
            System.out.println("\n📊 STATS: ❤️ " + likeCount + (iLiked ? " (You liked)" : "") + 
                             " | 💬 " + commentCount + " comments");
            
            if (isMyPost) {
                System.out.println("🤝 ✅ Your post");
            } else {
                boolean connected = connectionService.areConnected(currentUser.getId(), post.getUserId());
                System.out.println("🤝 " + (connected ? "✅ Connected" : "🔗 Not connected"));
            }
            
            System.out.println("─".repeat(80));
        }
        
        System.out.println("\n🎯 POST ACTIONS:");
        System.out.println("1. Select a post to interact (by ID)");
        System.out.println("2. Like/Unlike a post (by ID)");
        System.out.println("3. Comment on a post (by ID)");
        System.out.println("4. Refresh list");
        System.out.println("0. Back to Feed Menu");
        
        System.out.print("\nYour choice: ");
        int choice = InputUtil.getInt();
        
        switch (choice) {
            case 1 -> selectPostById(allPosts);
            case 2 -> quickLikePost(allPosts);
            case 3 -> quickCommentPost(allPosts);
            case 4 -> browseAllPublicPosts();
            default -> {}
        }
    }
    
    private void showConnectionsFeed() {
        System.out.println("\n🤝 === CONNECTIONS FEED ===");
        System.out.println("See what your connections are posting!");
        
        List<Post> feed = postService.getConnectionsFeed(currentUser.getId());
        
        if (feed.isEmpty()) {
            System.out.println("📭 No posts from your connections yet!");
            System.out.println("💡 Connect with more people or your connections haven't posted.");
            System.out.println("\n⏎ Press Enter...");
            InputUtil.getString();
            return;
        }
        
        System.out.println("\n📊 TOTAL POSTS FROM CONNECTIONS: " + feed.size());
        System.out.println("=".repeat(80));
        
        for (Post post : feed) {
            User author = userService.getUserById(post.getUserId());
            String authorName = (author != null) ? "@" + author.getUsername() : "User#" + post.getUserId();
            
            int likeCount = likeService.getLikeCount(post.getId());
            int commentCount = commentService.getCommentCount(post.getId());
            boolean iLiked = likeService.isLikedByUser(post.getId(), currentUser.getId());
            boolean isMyPost = post.getUserId() == currentUser.getId();
            
            System.out.println("\n" + "─".repeat(80));
            System.out.println("POST ID: " + post.getId());
            System.out.println("AUTHOR: " + authorName + (isMyPost ? " (YOU)" : ""));
            System.out.println("TIME: " + (post.getCreatedAt() != null ? post.getCreatedAt() : "Recently"));
            System.out.println("─".repeat(40));
            
            System.out.println("CONTENT:");
            System.out.println(post.getContent());
            
            if (post.getHashtags() != null && !post.getHashtags().isEmpty()) {
                System.out.println("\n🏷️ TAGS: " + post.getHashtags());
            }
            
            System.out.println("\n📊 STATS: ❤️ " + likeCount + (iLiked ? " (You liked)" : "") + 
                             " | 💬 " + commentCount + " comments");
            
            if (isMyPost) {
                System.out.println("🤝 ✅ Your post");
            } else {
                System.out.println("🤝 ✅ Connected");
            }
            
            System.out.println("─".repeat(80));
        }
        
        System.out.println("\n🎯 POST ACTIONS:");
        System.out.println("1. Select a post to interact (by ID)");
        System.out.println("2. Like/Unlike a post (by ID)");
        System.out.println("3. Comment on a post (by ID)");
        System.out.println("4. Refresh feed");
        System.out.println("0. Back to Feed Menu");
        
        System.out.print("\nYour choice: ");
        int choice = InputUtil.getInt();
        
        switch (choice) {
            case 1 -> selectPostById(feed);
            case 2 -> quickLikePost(feed);
            case 3 -> quickCommentPost(feed);
            case 4 -> showConnectionsFeed();
            default -> {}
        }
    }
    
    private void searchPostsByUser() {
        System.out.println("\n🔍 === SEARCH POSTS BY USER ===");
        System.out.print("Enter username: ");
        String username = InputUtil.getString();
        
        User targetUser = userService.getUserByUsername(username);
        if (targetUser == null) {
            System.out.println("❌ User @" + username + " not found!");
            System.out.println("\n⏎ Press Enter...");
            InputUtil.getString();
            return;
        }
        
        System.out.println("\n📝 POSTS BY @" + targetUser.getUsername() + ":");
        System.out.println("=".repeat(80));
        
        List<Post> userPosts = postService.getUserFeed(targetUser.getId());
        
        if (userPosts.isEmpty()) {
            System.out.println("📭 @" + targetUser.getUsername() + " hasn't posted anything yet.");
        } else {
            userPosts.sort((p1, p2) -> {
                if (p1.getCreatedAt() != null && p2.getCreatedAt() != null) {
                    return p2.getCreatedAt().compareTo(p1.getCreatedAt());
                }
                return 0;
            });
            
            for (Post post : userPosts) {
                int likeCount = likeService.getLikeCount(post.getId());
                int commentCount = commentService.getCommentCount(post.getId());
                boolean iLiked = likeService.isLikedByUser(post.getId(), currentUser.getId());
                boolean isMyPost = post.getUserId() == currentUser.getId();
                
                System.out.println("\n" + "─".repeat(80));
                System.out.println("POST ID: " + post.getId());
                System.out.println("AUTHOR: @" + targetUser.getUsername() + (isMyPost ? " (YOU)" : ""));
                System.out.println("TIME: " + (post.getCreatedAt() != null ? post.getCreatedAt() : "Recently"));
                System.out.println("─".repeat(40));
                
                System.out.println("CONTENT:");
                System.out.println(post.getContent());
                
                if (post.getHashtags() != null && !post.getHashtags().isEmpty()) {
                    System.out.println("\n🏷️ TAGS: " + post.getHashtags());
                }
                
                System.out.println("\n📊 STATS: ❤️ " + likeCount + (iLiked ? " (You liked)" : "") + 
                                 " | 💬 " + commentCount + " comments");
                System.out.println("─".repeat(80));
            }
            
            System.out.println("\n🎯 ACTIONS:");
            System.out.println("1. Select a post to interact (by ID)");
            System.out.println("2. View user profile");
            if (targetUser.getId() != currentUser.getId() && targetUser.getUserType().equals("PERSONAL")) {
                boolean connected = connectionService.areConnected(currentUser.getId(), targetUser.getId());
                if (!connected) {
                    System.out.println("3. Send connection request");
                }
            }
            System.out.println("0. Back");
            
            System.out.print("\nYour choice: ");
            int choice = InputUtil.getInt();
            
            switch (choice) {
                case 1 -> selectPostById(userPosts);
                case 2 -> {
                    System.out.println("\n👤 USER PROFILE:");
                    System.out.println("Username: @" + targetUser.getUsername());
                    System.out.println("Email: " + targetUser.getEmail());
                    System.out.println("Account Type: " + targetUser.getUserType());
                    System.out.println("Total Posts: " + userPosts.size());
                    
                    boolean connected = connectionService.areConnected(currentUser.getId(), targetUser.getId());
                    System.out.println("Connection: " + (connected ? "✅ Connected" : "🔗 Not connected"));
                    
                    System.out.println("\n⏎ Press Enter...");
                    InputUtil.getString();
                }
                case 3 -> {
                    if (targetUser.getId() != currentUser.getId() && targetUser.getUserType().equals("PERSONAL")) {
                        boolean connected = connectionService.areConnected(currentUser.getId(), targetUser.getId());
                        if (!connected) {
                            System.out.print("Send connection request to @" + targetUser.getUsername() + "? (y/n): ");
                            if (InputUtil.getString().equalsIgnoreCase("y")) {
                                boolean success = connectionService.sendRequest(currentUser.getId(), targetUser.getId());
                                if (success) {
                                    System.out.println("✅ Connection request sent!");
                                }
                            }
                        }
                    }
                }
                default -> {}
            }
        }
        
        System.out.println("\n⏎ Press Enter...");
        InputUtil.getString();
    }
    
    private void selectPostById(List<Post> posts) {
        if (posts.isEmpty()) {
            System.out.println("❌ No posts available!");
            System.out.println("\n⏎ Press Enter...");
            InputUtil.getString();
            return;
        }
        
        System.out.print("\nEnter POST ID to interact: ");
        int postId = InputUtil.getInt();
        
        Post selectedPost = posts.stream()
            .filter(post -> post.getId() == postId)
            .findFirst()
            .orElse(null);
        
        if (selectedPost == null) {
            System.out.println("❌ Post ID " + postId + " not found!");
            System.out.println("Available Post IDs: ");
            for (int i = 0; i < Math.min(10, posts.size()); i++) {
                System.out.print(posts.get(i).getId() + " ");
                if ((i + 1) % 10 == 0) System.out.println();
            }
            if (posts.size() > 10) {
                System.out.println("\n... and " + (posts.size() - 10) + " more");
            }
            System.out.println();
        } else {
            System.out.println("✅ Found post! Opening interaction menu...");
            interactWithPost(selectedPost);
        }
        
        System.out.println("\n⏎ Press Enter...");
        InputUtil.getString();
    }
    
    private void quickLikePost(List<Post> posts) {
        if (posts.isEmpty()) {
            System.out.println("❌ No posts available!");
            return;
        }
        
        System.out.print("\nEnter POST ID to like/unlike: ");
        int postId = InputUtil.getInt();
        
        Post selectedPost = posts.stream()
            .filter(post -> post.getId() == postId)
            .findFirst()
            .orElse(null);
        
        if (selectedPost == null) {
            System.out.println("❌ Post ID " + postId + " not found!");
            return;
        }
        
        boolean alreadyLiked = likeService.isLikedByUser(postId, currentUser.getId());
        
        if (alreadyLiked) {
            likeService.unlikePost(postId, currentUser.getId());
            System.out.println("💔 Unliked post ID " + postId);
        } else {
            likeService.likePost(postId, currentUser.getId());
            System.out.println("❤️ Liked post ID " + postId);
            
            if (selectedPost.getUserId() != currentUser.getId()) {
                notificationService.notifyLikedPost(selectedPost.getUserId(), currentUser.getUsername());
            }
        }
        
        System.out.println("\n⏎ Press Enter...");
        InputUtil.getString();
    }
    
    private void quickCommentPost(List<Post> posts) {
        if (posts.isEmpty()) {
            System.out.println("❌ No posts available!");
            return;
        }
        
        System.out.print("\nEnter POST ID to comment on: ");
        int postId = InputUtil.getInt();
        
        Post selectedPost = posts.stream()
            .filter(post -> post.getId() == postId)
            .findFirst()
            .orElse(null);
        
        if (selectedPost == null) {
            System.out.println("❌ Post ID " + postId + " not found!");
            return;
        }
        
        System.out.print("💬 Your comment: ");
        String content = InputUtil.getString();
        
        if (content.trim().isEmpty()) {
            System.out.println("❌ Comment cannot be empty!");
        } else {
            commentService.addComment(postId, currentUser.getId(), content);
            System.out.println("✅ Comment added to post ID " + postId);
            
            if (selectedPost.getUserId() != currentUser.getId()) {
                notificationService.notifyNewComment(selectedPost.getUserId(), currentUser.getUsername());
            }
        }
        
        System.out.println("\n⏎ Press Enter...");
        InputUtil.getString();
    }
    
    private void interactWithPost(Post post) {
        while (true) {
            int likes = likeService.getLikeCount(post.getId());
            int comments = commentService.getCommentCount(post.getId());
            boolean iLiked = likeService.isLikedByUser(post.getId(), currentUser.getId());

            System.out.println("\n═══════════════════════════════════════");
            System.out.println("💬 POST ID: " + post.getId());
            System.out.println("Content: " + post.getContent());
            if (post.getHashtags() != null && !post.getHashtags().isEmpty()) {
                System.out.println("🏷️ Tags: " + post.getHashtags());
            }
            System.out.println("\n📊 STATS:");
            System.out.println("❤️ Likes: " + likes + (iLiked ? " (YOU LIKED)" : ""));
            System.out.println("💬 Comments: " + comments);
            System.out.println("═══════════════════════════════════════");
            
            System.out.println("\n1. " + (iLiked ? "💔 Unlike" : "❤️ Like"));
            System.out.println("2. 💬 Add Comment");
            System.out.println("3. 👁️ View Comments");
            System.out.println("4. 🗑️ Delete My Comment");
            System.out.println("5. 🔄 Share/Repost");
            System.out.println("6. ✏️ Edit Post");
            System.out.println("7. 🗑️ Delete Post");
            System.out.println("0. Back");

            int choice = InputUtil.getInt();
            switch (choice) {
                case 1 -> toggleLike(post, iLiked);
                case 2 -> addComment(post);
                case 3 -> viewComments(post);
                case 4 -> deleteMyComment(post);
                case 5 -> sharePost(post);
                case 6 -> editPost(post);
                case 7 -> {
                    deletePost(post);
                    return;
                }
                default -> {
                    return;
                }
            }
        }
    }
    
    private void toggleLike(Post post, boolean alreadyLiked) {
        if (alreadyLiked) {
            likeService.unlikePost(post.getId(), currentUser.getId());
            System.out.println("💔 Unliked!");
        } else {
            likeService.likePost(post.getId(), currentUser.getId());
            System.out.println("❤️ Liked!");
            notificationService.notifyLikedPost(post.getUserId(), currentUser.getUsername());
        }
        System.out.println("⏎ Press Enter...");
        InputUtil.getString();
    }
    
    private void addComment(Post post) {
        System.out.print("💬 Your comment: ");
        String content = InputUtil.getString();
        if (content.trim().isEmpty()) {
            System.out.println("❌ Comment cannot be empty!");
            return;
        }
        commentService.addComment(post.getId(), currentUser.getId(), content);
        System.out.println("✅ Comment added!");
        notificationService.notifyNewComment(post.getUserId(), currentUser.getUsername());
        System.out.println("⏎ Press Enter...");
        InputUtil.getString();
    }
    
    private void viewComments(Post post) {
        List<Comment> comments = commentService.getComments(post.getId());
        if (comments.isEmpty()) {
            System.out.println("💬 No comments yet!");
        } else {
            System.out.println("\n💬 COMMENTS (" + comments.size() + "):");
            for (int i = 0; i < comments.size(); i++) {
                Comment c = comments.get(i);
                String username = c.getUsername() != null ? c.getUsername() : "User#" + c.getUserId();
                System.out.println((i+1) + ". 👤 " + username + ": " + c.getContent());
                if (c.getUserId() == currentUser.getId()) {
                    System.out.println("   👆 Your comment (ID: " + c.getId() + ")");
                }
            }
        }
        System.out.println("⏎ Press Enter...");
        InputUtil.getString();
    }
    
    private void deleteMyComment(Post post) {
        List<Comment> myComments = commentService.getMyComments(post.getId(), currentUser.getId());
        if (myComments.isEmpty()) {
            System.out.println("❌ You have no comments on this post.");
            return;
        }

        System.out.println("\n🗑️ YOUR COMMENTS ON THIS POST:");
        for (int i = 0; i < myComments.size(); i++) {
            Comment c = myComments.get(i);
            System.out.println((i+1) + ". " + c.getContent());
        }

        System.out.print("Select comment to delete (0=Cancel): ");
        int index = InputUtil.getInt() - 1;
        if (index >= 0 && index < myComments.size()) {
            commentService.deleteComment(myComments.get(index).getId());
            System.out.println("✅ Comment deleted!");
        }
        System.out.println("⏎ Press Enter...");
        InputUtil.getString();
    }
    
    private void sharePost(Post post) {
        System.out.println("\n🔄 SHARE POST");
        System.out.println("Original post by User ID: " + post.getUserId());
        System.out.println("Content: " + safePreview(post.getContent()));
        
        System.out.print("Add your message (optional): ");
        String message = InputUtil.getString();
        
        String shareContent = "[SHARED] " + (message.isEmpty() ? "" : message + "\n\n") + 
                              "Original: " + post.getContent();
        Post sharePost = new Post();
        sharePost.setUserId(currentUser.getId());
        sharePost.setContent(shareContent);
        sharePost.setHashtags(post.getHashtags());
        postService.createPost(sharePost);
        
        System.out.println("✅ Post shared on your profile!");
        System.out.println("⏎ Press Enter...");
        InputUtil.getString();
    }
    
    private void editPost(Post post) {
        System.out.print("New content: "); 
        String content = InputUtil.getString();
        System.out.print("New hashtags: "); 
        String hashtags = InputUtil.getString();
        postService.editPost(post.getId(), content, hashtags);
        System.out.println("✅ Post updated!");
        System.out.println("⏎ Press Enter...");
        InputUtil.getString();
    }
    
    private void deletePost(Post post) {
        System.out.print("Are you sure? (y/n): ");
        if (InputUtil.getString().equalsIgnoreCase("y")) {
            postService.deletePost(post.getId());
            System.out.println("✅ Post deleted permanently!");
        } else {
            System.out.println("Deletion cancelled.");
        }
        System.out.println("⏎ Press Enter...");
        InputUtil.getString();
    }
    
    private String safePreview(String content) {
        if (content == null || content.length() <= 50) return content;
        return content.substring(0, 50) + "...";
    }
}