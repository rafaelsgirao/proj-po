package ggc.app.transactions;

import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;
import ggc.WarehouseManager;
import ggc.exceptions.PartnerUnknownKeyException;
import ggc.exceptions.ProductUnknownKeyException;
import ggc.app.exceptions.UnknownPartnerKeyException;
import ggc.app.exceptions.UnknownProductKeyException;

/**
 * 
 */
public class DoRegisterSaleTransaction extends Command<WarehouseManager> {

  public DoRegisterSaleTransaction(WarehouseManager receiver) {
    super(Label.REGISTER_SALE_TRANSACTION, receiver);
    addStringField("partnerId", Prompt.partnerKey());
    addStringField("productId", Prompt.productKey());
    addIntegerField("paymentDeadline", Prompt.paymentDeadline());
    addIntegerField("transactionAmount", Prompt.amount());
  }

  @Override
  public final void execute() throws CommandException {

    String partnerId = stringField("partnerId");
    String productId = stringField("productId");
    int paymentDeadline = integerField("paymentDeadline");
    int amount = integerField("transactionAmount");
    try {
      _receiver.registerSaleTransaction(partnerId, productId, paymentDeadline, amount);

    }
    catch (ProductUnknownKeyException e){
      throw new UnknownProductKeyException(e.getId());
    }
    catch (PartnerUnknownKeyException e) {
      throw new UnknownPartnerKeyException(e.getId());
    }
  }

}
