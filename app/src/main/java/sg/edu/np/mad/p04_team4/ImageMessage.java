package sg.edu.np.mad.p04_team4;

public class ImageMessage extends Message {
    private String imageUrl;

    public ImageMessage() {
        // Default constructor required for calls to DataSnapshot.getValue(ImageMessage.class)
    }

    public ImageMessage(long id, long timestamp, String imageUrl, String userId) {
        super(id, timestamp, userId, "image");
        this.imageUrl = imageUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
