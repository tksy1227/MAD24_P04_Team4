package sg.edu.np.mad.p04_team4.Chat;

// StickerMessage class extending Message class to handle sticker messages
public class StickerMessage extends Message {
    private String stickerUrl; // URL of the sticker

    // Default constructor required for Firebase calls to DataSnapshot.getValue(StickerMessage.class)
    public StickerMessage() {
    }

    // Constructor to initialize StickerMessage with id, timestamp, sticker URL, and userId
    public StickerMessage(long id, long timestamp, String stickerUrl, String userId) {
        // Call the superclass constructor with the provided values and set the type to "sticker"
        super(id, timestamp, userId, "sticker", stickerUrl);
        this.stickerUrl = stickerUrl; // Set the sticker URL
    }

    // Getter method for sticker URL
    public String getStickerUrl() {
        return stickerUrl;
    }

    // Setter method for sticker URL
    public void setStickerUrl(String stickerUrl) {
        this.stickerUrl = stickerUrl;
    }
}
