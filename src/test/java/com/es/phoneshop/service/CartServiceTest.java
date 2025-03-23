package com.es.phoneshop.service;

import com.es.phoneshop.exception.OutOfStockException;
import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.cart.CartItem;
import com.es.phoneshop.model.product.Product;
import jakarta.servlet.http.HttpSession;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;


public class CartServiceTest {

    private static final String SESSION_ATTRIBUTE = CartServiceImplement.class.getName() + ".cart";

    @Mock
    private HttpSession session;

    @Mock
    private ProductService productService;

    @Mock
    private Product product;

    private CartServiceImplement cartService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        cartService = CartServiceImplement.getInstance();
        cartService.productService = productService;

        Mockito.when(session.getId()).thenReturn("session");

        Mockito.doAnswer(invocation -> {
            Object value = invocation.getArgument(1);
            Mockito.when(session.getAttribute(SESSION_ATTRIBUTE)).thenReturn(value);
            return null;
        }).when(session).setAttribute(Mockito.eq(SESSION_ATTRIBUTE), Mockito.any());
    }

    @Test
    public void testGetCartShouldCreateCartIfNotExist() {
        when(session.getAttribute(SESSION_ATTRIBUTE)).thenReturn(null);

        Cart cart = cartService.getCart(session);

        assertNotNull(cart);
        verify(session).setAttribute(eq(SESSION_ATTRIBUTE), any(Cart.class));
    }

    @Test
    public void testGetCartShouldReturnExistingCart() {
        Cart existingCart = new Cart();
        when(session.getAttribute(SESSION_ATTRIBUTE)).thenReturn(existingCart);

        Cart cart = cartService.getCart(session);

        assertSame(existingCart, cart);
    }

    @Test
    public void testAddProductToCartShouldAddNewProduct() {
        Long productId = 1L;
        int quantity = 2;

        when(productService.getProduct(productId)).thenReturn(product);
        when(product.getStock()).thenReturn(10);

        Cart cart = new Cart();
        when(session.getAttribute(SESSION_ATTRIBUTE)).thenReturn(cart);

        cartService.add(session, productId, quantity);

        assertEquals(1, cart.getItems().size());
        assertEquals(product, cart.getItems().get(0).getProduct());
        assertEquals(quantity, cart.getItems().get(0).getQuantity());
    }

    @Test(expected = OutOfStockException.class)
    public void testAddProductToCart_ShouldThrowOutOfStockExceptionWhenNotEnoughStock() {
        Long productId = 1L;
        int quantity = 10;

        when(productService.getProduct(productId)).thenReturn(product);
        when(product.getStock()).thenReturn(5);

        Cart cart = new Cart();
        when(session.getAttribute(SESSION_ATTRIBUTE)).thenReturn(cart);

        cartService.add(session, productId, quantity);
    }

    @Test
    public void testAddProductToCartShouldIncreaseQuantityIfProductAlreadyInCart() {
        Long productId = 0L;
        int initialQuantity = 2;
        int additionalQuantity = 3;

        Cart cart = new Cart();
        CartItem cartItem = new CartItem(product, initialQuantity);
        cart.getItems().add(cartItem);

        when(productService.getProduct(productId)).thenReturn(product);
        when(product.getStock()).thenReturn(10);

        when(session.getAttribute(SESSION_ATTRIBUTE)).thenReturn(cart);

        cartService.add(session, productId, additionalQuantity);

        Cart result = (Cart) session.getAttribute(SESSION_ATTRIBUTE);

        assertEquals("Cart should have only one product", 1, result.getItems().size());
        assertEquals("Product ID should be the same as the one in the cart", productId, result.getItems().get(0).getProduct().getId());
    }




    @Test(expected = OutOfStockException.class)
    public void testAddProductToCartShouldThrowOutOfStockExceptionWhenExceedStock() {
        Long productId = 0L;
        int initialQuantity = 5;
        int additionalQuantity = 6;

        Cart cart = new Cart();
        CartItem cartItem = new CartItem(product, initialQuantity);
        cart.getItems().add(cartItem);

        when(productService.getProduct(productId)).thenReturn(product);
        when(product.getStock()).thenReturn(10);

        when(session.getAttribute(SESSION_ATTRIBUTE)).thenReturn(cart);

        cartService.add(session, productId, additionalQuantity);
    }

    @Test
    public void testAddProductToCart_ShouldNotAddProductIfAlreadyInCartAndStockIsSufficient() {
        Long productId = 0L;
        int initialQuantity = 2;
        int additionalQuantity = 3;

        Cart cart = new Cart();
        CartItem cartItem = new CartItem(product, initialQuantity);
        cart.getItems().add(cartItem);

        when(productService.getProduct(productId)).thenReturn(product);
        when(product.getStock()).thenReturn(10);

        when(session.getAttribute(SESSION_ATTRIBUTE)).thenReturn(cart);

        cartService.add(session, productId, additionalQuantity);

        assertEquals(initialQuantity + additionalQuantity, cart.getItems().get(0).getQuantity());
    }

    @Test
    public void testAddProductToCart_ShouldAddProductIfNotInCartAndStockIsSufficient() {
        Long productId = 1L;
        int quantity = 2;

        when(productService.getProduct(productId)).thenReturn(product);
        when(product.getStock()).thenReturn(10);

        Cart cart = new Cart();
        when(session.getAttribute(SESSION_ATTRIBUTE)).thenReturn(cart);

        cartService.add(session, productId, quantity);

        assertEquals(1, cart.getItems().size());
        assertEquals(product, cart.getItems().get(0).getProduct());
        assertEquals(quantity, cart.getItems().get(0).getQuantity());
    }

}
