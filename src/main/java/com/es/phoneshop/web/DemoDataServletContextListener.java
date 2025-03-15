package com.es.phoneshop.web;

import com.es.phoneshop.model.product.ArrayListProductDao;
import com.es.phoneshop.model.product.Price;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.product.ProductDao;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Currency;
import java.util.List;

public class DemoDataServletContextListener implements ServletContextListener {
    private final ProductDao productDao;

    public DemoDataServletContextListener() {
        this.productDao = ArrayListProductDao.getInstance();
    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        boolean insertDemoData = Boolean.parseBoolean(sce.getServletContext().getInitParameter("enableDemoDataListener"));
        if (insertDemoData) {
            loadProducts().forEach(productDao::save);
        }
    }

    protected List<Product> loadProducts() {
        List<Product> products = new ArrayList<>();

        Currency usd = Currency.getInstance("USD");

        products.add(new Product("sgs", "Samsung Galaxy S", new BigDecimal(100), usd, 100,
                "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S.jpg",
                Arrays.asList(
                        new Price(new BigDecimal(120), Date.valueOf(LocalDate.of(2023, 3, 15))),
                        new Price(new BigDecimal(110), Date.valueOf(LocalDate.of(2023, 5, 20)))
                )
        ));
        products.add(new Product("sgs2", "Samsung Galaxy S II", new BigDecimal(200), usd, 0,
                "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S%20II.jpg",
                Arrays.asList(
                        new Price(new BigDecimal(250), Date.valueOf(LocalDate.of(2023, 1, 20))),
                        new Price(new BigDecimal(230), Date.valueOf(LocalDate.of(2023, 2, 5)))
                )
        ));
        products.add(new Product("sgs3", "Samsung Galaxy S III", new BigDecimal(300), usd, 5,
                "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S%20III.jpg",
                Arrays.asList(
                        new Price(new BigDecimal(350), Date.valueOf(LocalDate.of(2023, 1, 10))),
                        new Price(new BigDecimal(330), Date.valueOf(LocalDate.of(2023, 2, 18))),
                        new Price(new BigDecimal(310), Date.valueOf(LocalDate.of(2023, 3, 5)))
                )
        ));

        products.add(new Product("iphone", "Apple iPhone", new BigDecimal(200), usd, 10,
                "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Apple/Apple%20iPhone.jpg",
                Arrays.asList(
                        new Price(new BigDecimal(250), Date.valueOf(LocalDate.of(2023, 1, 1))),
                        new Price(new BigDecimal(220), Date.valueOf(LocalDate.of(2023, 2, 10)))
                )
        ));

        products.add(new Product("iphone6", "Apple iPhone 6", new BigDecimal(1000), usd, 30,
                "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Apple/Apple%20iPhone%206.jpg",
                Arrays.asList(
                        new Price(new BigDecimal(1100), Date.valueOf(LocalDate.of(2023, 1, 25))),
                        new Price(new BigDecimal(1050), Date.valueOf(LocalDate.of(2023, 2, 10))),
                        new Price(new BigDecimal(1020), Date.valueOf(LocalDate.of(2023, 3, 10)))
                )
        ));

        products.add(new Product("htces4g", "HTC EVO Shift 4G", new BigDecimal(320), usd, 3,
                "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/HTC/HTC%20EVO%20Shift%204G.jpg",
                Arrays.asList(
                        new Price(new BigDecimal(350), Date.valueOf(LocalDate.of(2023, 1, 12))),
                        new Price(new BigDecimal(340), Date.valueOf(LocalDate.of(2023, 2, 8)))
                )
        ));

        products.add(new Product("sec901", "Sony Ericsson C901", new BigDecimal(420), usd, 30,
                "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Sony/Sony%20Ericsson%20C901.jpg",
                Arrays.asList(
                        new Price(new BigDecimal(450), Date.valueOf(LocalDate.of(2023, 1, 5))),
                        new Price(new BigDecimal(430), Date.valueOf(LocalDate.of(2023, 2, 14))),
                        new Price(new BigDecimal(410), Date.valueOf(LocalDate.of(2023, 3, 12)))
                )
        ));

        products.add(new Product("xperiaxz", "Sony Xperia XZ", new BigDecimal(120), usd, 100,
                "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Sony/Sony%20Xperia%20XZ.jpg",
                Arrays.asList(
                        new Price(new BigDecimal(140), Date.valueOf(LocalDate.of(2023, 1, 7))),
                        new Price(new BigDecimal(130), Date.valueOf(LocalDate.of(2023, 2, 20)))
                )
        ));

        products.add(new Product("nokia3310", "Nokia 3310", new BigDecimal(70), usd, 100,
                "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Nokia/Nokia%203310.jpg",
                Arrays.asList(
                        new Price(new BigDecimal(90), Date.valueOf(LocalDate.of(2023, 1, 3))),
                        new Price(new BigDecimal(80), Date.valueOf(LocalDate.of(2023, 2, 25)))
                )
        ));

        products.add(new Product("palmp", "Palm Pixi", new BigDecimal(170), usd, 30,
                "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Palm/Palm%20Pixi.jpg",
                Arrays.asList(
                        new Price(new BigDecimal(190), Date.valueOf(LocalDate.of(2023, 1, 8))),
                        new Price(new BigDecimal(180), Date.valueOf(LocalDate.of(2023, 2, 12))),
                        new Price(new BigDecimal(175), Date.valueOf(LocalDate.of(2023, 3, 2)))
                )
        ));

        products.add(new Product("simc56", "Siemens C56", new BigDecimal(70), usd, 20,
                "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Siemens/Siemens%20C56.jpg",
                Arrays.asList(
                        new Price(new BigDecimal(85), Date.valueOf(LocalDate.of(2023, 1, 10))),
                        new Price(new BigDecimal(75), Date.valueOf(LocalDate.of(2023, 2, 15)))
                )
        ));

        products.add(new Product("simc61", "Siemens C61", new BigDecimal(80), usd, 30,
                "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Siemens/Siemens%20C61.jpg",
                Arrays.asList(
                        new Price(new BigDecimal(95), Date.valueOf(LocalDate.of(2023, 1, 3))),
                        new Price(new BigDecimal(85), Date.valueOf(LocalDate.of(2023, 2, 20))),
                        new Price(new BigDecimal(82), Date.valueOf(LocalDate.of(2023, 3, 5)))
                )
        ));

        products.add(new Product("simsxg75", "Siemens SXG75", new BigDecimal(150), usd, 40,
                "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Siemens/Siemens%20SXG75.jpg",
                Arrays.asList(
                        new Price(new BigDecimal(170), Date.valueOf(LocalDate.of(2023, 1, 10))),
                        new Price(new BigDecimal(160), Date.valueOf(LocalDate.of(2023, 2, 14)))
                )
        ));

        return products;
    }

}
