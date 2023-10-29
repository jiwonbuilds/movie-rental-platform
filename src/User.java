import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * This User class  has the username field in this example.
 */
public class User {
    private final String username;
    private final String firstName;
    private final String lastName;
    private final String email;
    private final String password;
    private HashMap<String, CartItem> shoppingCart;

    public User(String username, String firstName, String lastName, String email, String password) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.shoppingCart = new HashMap<>();
    }

    public void addCartItem(CartItem citem) {
        String mid = citem.getMovieId();
        if (this.shoppingCart.containsKey(mid)) {
            CartItem cartItem = this.shoppingCart.get(citem.getMovieId());
            cartItem.setQuantity(cartItem.getQuantity() + 1);
        } else {
            this.shoppingCart.put(mid, citem);
        }
    }

    public void updateCartItem(String mid, int amount) {
        CartItem cartItem = this.shoppingCart.get(mid);
        cartItem.setQuantity(cartItem.getQuantity() + amount);
    }

    public void removeCartItem(String mid) {
        this.shoppingCart.remove(mid);
    }

    public String cartToJson() {
        Gson gson = new Gson();
        return gson.toJson(this.shoppingCart);
    }

}
