package net.ecom.views;

import net.ecom.Model;
import net.ecom.components.ComponentUpdate;
import net.ecom.components.Text;
import net.ecom.components.UiComponent;
import net.ecom.components.Window;
import net.ecom.data.Product;

public class ProductDisplayView {
    private UiComponent<Window> parent;
    private UiComponent<Window> self;
    private UiComponent<Text> title;
    private UiComponent<Window> content;

    public ProductDisplayView(UiComponent<Window> parent) {
        this.parent = parent;

        this.self = new Window()
                        .updateUi((c) -> c.setW(((Window) parent).getW() - 29))
                        .updateUi((c) -> c.setH(((Window) parent).getH()))
                        .updateUi(UiComponent.SetPosition(28, -1));

        this.title = new Text("");

        this.content = new Window()
                           .updateUi((c) -> c.setW(((Window) this.self).getW()))
                           .updateUi((c) -> c.setH(((Window) this.self).getH() - 2))
                           .updateUi(UiComponent.SetPosition(-1, 1));

        this.self.addChildren(this.title);
        this.self.addChildren(this.content);

        this.parent.addChildren(this.self);
    }

    public int getSelfW() {
        return ((Window) this.self).getW();
    }

    public int getSelfH() {
        return ((Window) this.self).getH();
    }

    public void update(Model model) {
        this.content.clearChildren();

        if (model.getCategorySelection() == null) {
            this.title.updateUi((c) -> c.setContent(""));
            return;
        }

        this.title.updateUi((c) -> c.setContent("Clothing / Shoes"));

        ComponentUpdate<Text> setCenterX = (c)
            -> c.setX(UiComponent.getCenter(((Window) this.self).getW(), c.getContent().length()));

        this.title.updateUi(setCenterX);

        Window topRow = new Window();
        topRow.updateUi((c) -> c.setW(this.getSelfW() - 12))
            .updateUi((c) -> c.setH(3))
            .updateUi(UiComponent.SetX(UiComponent.getCenter(this.getSelfW(), topRow.getW()) - 1))
            .updateUi(UiComponent.SetY(1))
            .updateUi((c) -> c.setBorder('\\'));

        //

        Window topRowSkuWrap = new Window(15, topRow.getH());
        topRowSkuWrap.updateUi(UiComponent.SetPosition(-1, -1))
            .updateUi((c) -> c.setBorder(topRow.getBorder()));

        Text topRowSku = new Text("SKU").updateUi(UiComponent.SetPosition(
            UiComponent.getCenter(topRowSkuWrap.getW() - 2, "SKU".length()), 0));

        topRowSkuWrap.addChildren(topRowSku);

        //

        Window topRowPriceWrap = new Window(13, topRow.getH());

        topRowPriceWrap.updateUi((c) -> c.setX(topRowSkuWrap.getW() + topRowSkuWrap.getX() - 1))
            .updateUi((c) -> c.setY(-1))
            .updateUi((c) -> c.setBorder(topRow.getBorder()));

        Text topRowPrice = new Text("Price").updateUi(UiComponent.SetPosition(
            UiComponent.getCenter(((Window) topRowPriceWrap).getW() - 2, "Price".length()), 0));

        topRowPriceWrap.addChildren(topRowPrice);

        //

        Window topRowNameWrap = new Window(
            topRow.getW() - topRowSkuWrap.getW() - topRowPriceWrap.getW() + 2, topRow.getH());

        topRowNameWrap.updateUi((c) -> c.setX(topRowPriceWrap.getW() + topRowPriceWrap.getX() - 1))
            .updateUi((c) -> c.setY(-1))
            .updateUi((c) -> c.setBorder(topRow.getBorder()));

        Text topRowName = new Text("Name").updateUi(UiComponent.SetPosition(
            UiComponent.getCenter(((Window) topRowNameWrap).getW() - 2, "Name".length()), 0));

        topRowNameWrap.addChildren(topRowName);

        //

        topRow.addChildren(topRowSkuWrap);
        topRow.addChildren(topRowPriceWrap);
        topRow.addChildren(topRowNameWrap);

        this.content.addChildren(topRow);

        Object contentIndexS = model.getFocusState("content.index");
        int contentIndex = 0;

        if (contentIndexS instanceof Integer)
            contentIndex = (int) contentIndexS;

        if (!model.getFocusArea().startsWith("body"))
            contentIndex = -1;

        int h = 1;
        int i = 0;
        Window selectionProof = null;

        for (Product product : model.getCategoryProducts(model.getCategorySelection())) {
            Window row = new Window(topRow.getW(), topRow.getH());

            row.updateUi((c) -> c.setX(topRow.getX()))
                .updateUi(UiComponent.SetY(topRow.getY() + h++ * 2))
                .updateUi((c) -> c.setBorder('\\'));

            if (i == contentIndex) {
                selectionProof = new Window();
                selectionProof.setX(row.getX());
                selectionProof.setY(row.getY());
                selectionProof.setW(row.getW());
                selectionProof.setH(row.getH());

                selectionProof.updateUi((c) -> c.setBorder('%'));
            }

            Window skuWrap = new Window(topRowSkuWrap.getW(), topRowSkuWrap.getH());
            skuWrap.updateUi((c) -> c.setX(topRowSkuWrap.getX()))
                .updateUi((c) -> c.setY(topRowSkuWrap.getY()))
                .updateUi((c) -> c.setBorder(row.getBorder()));

            skuWrap.addChildren(new Text(product.sku()).updateUi(UiComponent.SetX(1)));

            //

            Window priceWrap = new Window(topRowPriceWrap.getW(), topRowPriceWrap.getH());
            priceWrap.updateUi((c) -> c.setX(topRowPriceWrap.getX()))
                .updateUi((c) -> c.setY(topRowPriceWrap.getY()))
                .updateUi((c) -> c.setBorder(row.getBorder()));

            priceWrap.addChildren(new Text("$" + product.price()).updateUi(UiComponent.SetX(1)));

            //

            Window nameWrap = new Window(topRowNameWrap.getW(), topRowNameWrap.getH());
            nameWrap.updateUi((c) -> c.setX(topRowNameWrap.getX()))
                .updateUi((c) -> c.setY(topRowNameWrap.getY()))
                .updateUi((c) -> c.setBorder(row.getBorder()));

            String productNameStr = product.name();

            Text productTxt = new Text();
            productTxt
                .updateUi((c)
                              -> c.setContent(productNameStr.substring(
                                  0, Math.min(productNameStr.length(), nameWrap.getW() - 4))))
                .updateUi(UiComponent.SetX(1));

            nameWrap.addChildren(productTxt);

            //

            row.addChildren(skuWrap);
            row.addChildren(priceWrap);
            row.addChildren(nameWrap);

            this.content.addChildren(row);

            i++;
        }

        if (selectionProof != null)
            this.content.addChildren(selectionProof);
    }
}
