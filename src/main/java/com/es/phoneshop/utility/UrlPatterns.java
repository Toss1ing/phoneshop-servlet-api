package com.es.phoneshop.utility;

public class UrlPatterns {

    public static class ProductListAddCartItemUrlPattern {
        public static final String PRODUCT_LIST_ADD_CART_ITEM_ERROR_URL =
                "%s/products?error=%s%s&productId=%s&quantity=%s";
        public static final String PRODUCT_LIST_CART_ITEM_SUCCESS_URL = "%s/products?success=%s";
    }

    public static class DeleteCartItemUrlPattern {
        public static final String DELETE_CART_ITEM_SUCCESS_URL = "%s/cart?success=%s";
    }

    public static class ProductDetailUrlPattern {
        public static final String PRODUCT_DETAIL_SUCCESS_URL = "%s/product/%s/success=%s";
        public static final String PRODUCT_DETAIL_ERROR_URL = "%s/products/%s?error=%s%s";
    }

    public static class CartPageUrlPattern {
        public static final String CART_PAGE_SUCCESS_URL = "%s/cart?success=%s";
    }

}
