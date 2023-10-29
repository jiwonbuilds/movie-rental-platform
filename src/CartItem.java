/**
 * This User class  has the username field in this example.
 */
public class CartItem {
    private final String movieId;
    private final String movieTitle;
    private final float moviePrice;
    private int quantity;

    public CartItem(String mid, String mtitle, float mprice) {
        this.movieId = mid;
        this.movieTitle = mtitle;
        this.moviePrice = mprice;
        this.quantity = 1;
    }

    public String getMovieId() {
        return this.movieId;
    }

    public int getQuantity() {
        return this.quantity;
    }

    public void setQuantity(int newQuant) {
        this.quantity = newQuant;
    }

}
