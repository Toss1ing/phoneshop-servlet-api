package com.es.phoneshop.service.impl;

import com.es.phoneshop.dao.ProductDao;
import com.es.phoneshop.exception.OutOfStockException;
import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.cart.CartItem;
import com.es.phoneshop.model.product.Product;
import jakarta.servlet.http.HttpSession;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;


public class CartServiceTest {

    private static final String SESSION_ATTRIBUTE = CartServiceImplement.class.getName() + ".cart";

    @Mock
    private HttpSession session;

    @Mock
    private ProductDao productService;

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
        when(product.getPrice()).thenReturn(new BigDecimal(100));


        Cart cart = new Cart();
        when(session.getAttribute(SESSION_ATTRIBUTE)).thenReturn(cart);

        cartService.add(session, productId, quantity);

        assertEquals(1, cart.getItems().size());
        assertEquals(product, cart.getItems().get(0).getProduct());
        assertEquals(quantity, cart.getItems().get(0).getQuantity());
    }

    @Test(expected = OutOfStockException.class)
    public void testAddProductToCartShouldThrowOutOfStockExceptionWhenNotEnoughStock() {
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
        when(product.getPrice()).thenReturn(new BigDecimal(100));

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
    public void testAddProductToCartShouldNotAddProductIfAlreadyInCartAndStockIsSufficient() {
        Long productId = 0L;
        int initialQuantity = 2;
        int additionalQuantity = 3;

        Cart cart = new Cart();
        CartItem cartItem = new CartItem(product, initialQuantity);
        cart.getItems().add(cartItem);

        when(productService.getProduct(productId)).thenReturn(product);
        when(product.getStock()).thenReturn(10);
        when(product.getPrice()).thenReturn(new BigDecimal(100));

        when(session.getAttribute(SESSION_ATTRIBUTE)).thenReturn(cart);

        cartService.add(session, productId, additionalQuantity);

        assertEquals(initialQuantity + additionalQuantity, cart.getItems().get(0).getQuantity());
    }

    @Test
    public void testAddProductToCartShouldAddProductIfNotInCartAndStockIsSufficient() {
        Long productId = 1L;
        int quantity = 2;

        when(productService.getProduct(productId)).thenReturn(product);
        when(product.getStock()).thenReturn(10);
        when(product.getPrice()).thenReturn(new BigDecimal(100));

        Cart cart = new Cart();
        when(session.getAttribute(SESSION_ATTRIBUTE)).thenReturn(cart);

        cartService.add(session, productId, quantity);

        assertEquals(1, cart.getItems().size());
        assertEquals(product, cart.getItems().get(0).getProduct());
        assertEquals(quantity, cart.getItems().get(0).getQuantity());
    }

    @Test
    public void testUpdateCartShouldChangeQuantity() {
        Long productId = 1L;
        int newQuantity = 5;

        Cart cart = new Cart();
        CartItem cartItem = new CartItem(product, 2);
        cart.getItems().add(cartItem);

        when(productService.getProduct(productId)).thenReturn(product);
        when(product.getStock()).thenReturn(10);
        when(product.getPrice()).thenReturn(new BigDecimal(100));
        when(session.getAttribute(SESSION_ATTRIBUTE)).thenReturn(cart);

        cartService.update(session, productId, newQuantity);

        assertEquals("Quantity should be updated", newQuantity, cart.getItems().get(0).getQuantity());
    }

    @Test(expected = OutOfStockException.class)
    public void testUpdateCartShouldThrowOutOfStockExceptionWhenNotEnoughStock() {
        Long productId = 1L;
        int newQuantity = 15;

        Cart cart = new Cart();
        CartItem cartItem = new CartItem(product, 2);
        cart.getItems().add(cartItem);

        when(productService.getProduct(productId)).thenReturn(product);
        when(product.getStock()).thenReturn(10);
        when(product.getPrice()).thenReturn(new BigDecimal(100));

        when(session.getAttribute(SESSION_ATTRIBUTE)).thenReturn(cart);

        cartService.update(session, productId, newQuantity);
    }

    @Test
    public void testDeleteCartShouldRemoveItem() {
        Long productId = 1L;

        Cart cart = new Cart();
        CartItem cartItem = new CartItem(product, 2);
        when(product.getId()).thenReturn(productId);

        cart.getItems().add(cartItem);

        when(session.getAttribute(SESSION_ATTRIBUTE)).thenReturn(cart);
        when(product.getPrice()).thenReturn(new BigDecimal(100));

        cartService.delete(session, productId);

        assertTrue("Cart should be empty after deletion", cart.getItems().isEmpty());
    }

    @Test
    public void testClearShouldEmptyCart() {
        Cart cart = new Cart();
        CartItem cartItem1 = new CartItem(product, 2);
        CartItem cartItem2 = new CartItem(product, 3);
        cart.getItems().add(cartItem1);
        cart.getItems().add(cartItem2);
        cart.setTotalPrice(BigDecimal.valueOf(500));
        cart.setTotalQuantity(5);

        when(session.getAttribute(SESSION_ATTRIBUTE)).thenReturn(cart);

        cartService.clear(session);

        assertTrue("Cart should be empty after clearing", cart.getItems().isEmpty());
        assertEquals("Total price should be zero after clearing", BigDecimal.ZERO, cart.getTotalPrice());
        assertEquals("Total quantity should be zero after clearing", 0, cart.getTotalQuantity());
    }

    @Test
    public void testRecalculateCartShouldUpdateTotalPriceAndQuantity() {
        Cart cart = new Cart();
        CartItem cartItem1 = new CartItem(product, 2);
        CartItem cartItem2 = new CartItem(product, 3);

        when(product.getPrice()).thenReturn(BigDecimal.valueOf(100));

        cart.getItems().add(cartItem1);
        cart.getItems().add(cartItem2);

        cartService.recalculateCart(cart);

        assertEquals("Total price should be 500", BigDecimal.valueOf(500), cart.getTotalPrice());
        assertEquals("Total quantity should be 5", 5, cart.getTotalQuantity());
    }

    @Test
    public void testRecalculateCartShouldHandleEmptyCart() {
        Cart cart = new Cart();

        cartService.recalculateCart(cart);

        assertEquals("Total price should be 0", BigDecimal.ZERO, cart.getTotalPrice());
        assertEquals("Total quantity should be 0", 0, cart.getTotalQuantity());
    }

}
