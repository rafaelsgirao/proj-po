package ggc;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.Set;
import java.util.TreeSet;
import ggc.exceptions.ProductUnavailableException;

public class DerivedProduct extends Product {
    private ArrayList<RecipeComponent> _recipe;
    private double _alpha;

    public DerivedProduct(String name, double maxPrice, int totalStock, ArrayList<RecipeComponent> recipe, double alpha){
        super(name, maxPrice, totalStock);
        _recipe = recipe;
        _alpha = alpha;
        setN(3);
    }
    

    
    /** 
     * @return ArrayList<RecipeComponent>
     */
    @Override
    public ArrayList<RecipeComponent> getRecipe() {
        return _recipe;
    }
    
    
    /**
     * @return String
     */
    public String getAllComponents() {
        String returnString = "";

        for(int i = 0; i < _recipe.size() - 1; i++){
            returnString += _recipe.get(i).toString();
            returnString += "#";
        }
        returnString += _recipe.get(_recipe.size() - 1).toString();

        return returnString;
    }


    
    /** 
     * @param amount
     * @param productsStock
     * @throws ProductUnavailableException
     */
    @Override
    public void canDispatchProduct(int amount, Map<String, Integer> productsStock) throws ProductUnavailableException{
        int totalStock = productsStock.get(getProductId());

        if (totalStock >= amount){
            productsStock.remove(getProductId());
            productsStock.put(getProductId(), totalStock - amount);
        }
        else {
            int neededAmount = amount - totalStock;
            productsStock.remove(getProductId());
            productsStock.put(getProductId(), totalStock - amount);
            for (RecipeComponent component: _recipe) {
                Product componentProduct = component.getProduct();
                componentProduct.canDispatchProduct(neededAmount * component.getProductQuantity(), productsStock);
            }
        }  
    }

    
    
    /** 
     * @param batches
     * @return double
     * @throws ProductUnavailableException
     */
    @Override
    public double doDispatchProduct(int amount, double totalPrice, Map<String, TreeSet<Batches>> batches) throws ProductUnavailableException {
        TreeSet<Batches> productBatches = batches.get(this.getProductId());
        
        if (productBatches == null) {
            throw new ProductUnavailableException(getProductId(), amount, getTotalStock());
        }
        
        Set<Batches> orderedByPrice = new TreeSet<Batches>(Batches.PRICE_COMPARATOR);
        orderedByPrice.addAll(productBatches);

        int fulfilledAmount = 0;

        for (Batches b: orderedByPrice) {
            int takeAmount = amount - fulfilledAmount;
            
            if (b.getQuantity() > takeAmount) { //More than we need to complete
                totalPrice += b.getPrice() * takeAmount;
                b.withdraw(takeAmount);
                setTotalStock(getTotalStock() - takeAmount);
                fulfilledAmount = amount; // <=> fulfilledAmount += amount - fulfilledAmount ; we're done here
                break;
            } else if (b.getQuantity() == takeAmount) { //Just what we need - consume, destroy and leave
                fulfilledAmount = amount;
                totalPrice += b.getPrice() * b.getQuantity();
                setTotalStock(getTotalStock() - b.getQuantity());
                productBatches.remove(b);
                break;
            } else { //Not enough quantity in this batch - consume all, destroy and continue
                fulfilledAmount += b.getQuantity();
                totalPrice += b.getPrice() * b.getQuantity();
                setTotalStock(getTotalStock() - b.getQuantity());
                productBatches.remove(b);
            }
        }

        if (fulfilledAmount != amount) { //What we had was not enough for a simple product, throw Exception
            int neededAmount = amount - fulfilledAmount;
            double recipeBasePrice = 1.0 + _alpha;
            double recipePrice = 0; 

            for (RecipeComponent component: _recipe) {
                Product componentProduct = component.getProduct();
                int componentProductQuantity = component.getProductQuantity();
                int neededComponentAmount = (neededAmount * componentProductQuantity);

                double componentPrice = componentProduct.doDispatchProduct(neededComponentAmount, 0, batches);

                recipePrice += componentPrice;
            }
            //update product max price
            if ((recipeBasePrice * recipePrice) / fulfilledAmount > getMaxPrice()) {
                setMaxPrice(recipeBasePrice * recipePrice / fulfilledAmount);
            }
            totalPrice += recipeBasePrice * recipePrice;
        }

        return totalPrice;
    }


    /** 
     * @return String
     */
    @Override
    public String toString(){
        return super.toString() + "|" + _alpha + "|" + getAllComponents();
    }
}