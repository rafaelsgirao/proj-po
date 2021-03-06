package ggc;

import java.io.Serializable;

public class RecipeComponent implements Serializable{
    private Product _product;
    private int _productQuantity;

    public RecipeComponent(Product product, int productQuantity){
        _product = product;
        _productQuantity = productQuantity;
    }


    
    /** 
     * @return Product
     */
    public Product getProduct() {
        return _product;
    }


    
    /** 
     * @return int
     */
    public int getProductQuantity() {
        return _productQuantity;
    }

    
    /** 
     * @return String
     */
    @Override
    public String toString(){
        return _product.getProductId() + ":" + _productQuantity;
    }
}