package jets.projects.dal;

import java.util.ArrayList;
import java.util.List;

import jets.projects.beans.ProductBean;

public class ProductsDAL {
    public List<ProductBean> getAllProducts() {
        return new ArrayList<>();
    }

    public ProductBean addProduct(ProductBean newProduct) {
        return newProduct;
    }

    public Boolean updateProductInfo(ProductBean updatedProduct) {
        return true;
    }

    public Boolean addToProductStock(int change) {
        return true;
    }

    public Boolean removeFromProductStock(int change) {
        return true;
    }

    public Boolean markProductAsUnavailable(int productID) {
        return true;
    }

    public List<ProductBean> searchProducts(String key, boolean isAdmin) {
        return new ArrayList<>();
    }
}
