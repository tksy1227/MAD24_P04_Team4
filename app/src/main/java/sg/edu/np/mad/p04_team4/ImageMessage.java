package sg.edu.np.mad.p04_team4;

public class ImageMessage extends Message {
    private String imageUrl; // URL of the image

    public ImageMessage() {
        // Default constructor required for calls to DataSnapshot.getValue(ImageMessage.class)
    }

    public ImageMessage(long id, long timestamp, String imageUrl, String userId) {
        super(id, timestamp, userId, "image"); // Call the superclass constructor
        this.imageUrl = imageUrl; // Set the image URL
    }

    public String getImageUrl() {
        return imageUrl; // Get the image URL
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl; // Set the image URL
    }
}
