package sg.edu.np.mad.p04_team4.Chat;

public class StickerMessage extends Message {
    private String stickerUrl;

    public StickerMessage() {
        // Default constructor required for calls to DataSnapshot.getValue(StickerMessage.class)
    }

    public StickerMessage(long id, long timestamp, String stickerUrl, String userId) {
        super(id, timestamp, userId, "sticker");
        this.stickerUrl = stickerUrl;
    }

    public String getStickerUrl() {
        return stickerUrl;
    }

    public void setStickerUrl(String stickerUrl) {
        this.stickerUrl = stickerUrl;
    }
}
