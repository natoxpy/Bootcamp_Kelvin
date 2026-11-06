package net.example.demo.view;

import net.gui.commands.Color;
import net.gui.primitives.AlignSelf;
import net.gui.primitives.Direction;
import net.gui.primitives.JustifySelf;
import net.gui.primitives.dimension.Fixed;
import net.rosu.Rosu;
import net.rosu.Rosuable;
import net.rosu.component.BoxComponent;
import net.rosu.component.ButtonComponent;
import net.rosu.component.Component;
import net.rosu.component.FlexComponent;
import net.rosu.component.FlexItem;
import net.rosu.component.Renderable;

public class SidebarView extends Component implements Rosuable {
    Runnable clickedOverview;
    Runnable clickedTransaction;
    Runnable clickedModifyRecord;
    Rosu rosu;
    ButtonComponent overviewButton = new ButtonComponent("OVERVIEW", () -> this.gotoOverview())
                                         .setBackground(Color.Black)
                                         .setWidth(new Fixed(19))
                                         .setClickedBg(Color.Blue);

    ButtonComponent transactionButton =
        new ButtonComponent("TRANSACTIONS", () -> this.gotoTransactions())
            .setWidth(new Fixed(19))
            .setClickedBg(Color.Blue);

    ButtonComponent modifyRecordButton =
        new ButtonComponent("MODIFY RECORDS", () -> this.gotoModifiedRecord())
            .setWidth(new Fixed(19))
            .setClickedBg(Color.Blue);

    private int active = 0;

    public SidebarView() {}

    public SidebarView onClickOverview(Runnable c) {
        this.clickedOverview = c;
        return this;
    }

    public SidebarView onClickTransaction(Runnable c) {
        this.clickedTransaction = c;
        return this;
    }

    public SidebarView onClickModifyRecord(Runnable c) {
        this.clickedModifyRecord = c;
        return this;
    }

    public void setActive(int active) {
        this.active = active;
        this.redraw();
    }

    @Override
    public Renderable render() {
        var flex = new FlexComponent().setDirection(Direction.Column);
        var pages = new FlexComponent().setDirection(Direction.Column);

        var closeButton = new ButtonComponent("EXIT")
                              .onClick(() -> { this.rosu.quit(); })
							  .setClickedBg(Color.Blue)
                              .setWidth(new Fixed(19));

        pages.addChild(new FlexItem(overviewButton, JustifySelf.Start, AlignSelf.Center, 1));
        pages.addChild(new FlexItem(transactionButton, JustifySelf.Start, AlignSelf.Center, 1));
        pages.addChild(new FlexItem(modifyRecordButton, JustifySelf.Start, AlignSelf.Center, 1));

        flex.addChild(new FlexItem(new BoxComponent(pages).setHeight(new Fixed(3 * 3)),
            JustifySelf.Start, AlignSelf.Center, 1));

        flex.addChild(new FlexItem(closeButton, JustifySelf.End, AlignSelf.Center, 1));

        return flex;
    }

    @Override
    public void takeRosu(Rosu rosu) {
        this.rosu = rosu;
    }

    private void resetColors() {
        this.overviewButton.setBackgroundAndRedraw(this.active == 0 ? Color.Black : null);
        this.transactionButton.setBackgroundAndRedraw(this.active == 1 ? Color.Black : null);
        this.modifyRecordButton.setBackgroundAndRedraw(this.active == 2 ? Color.Black : null);
    }

    private void gotoOverview() {
        if (this.clickedOverview == null)
            return;
        this.clickedOverview.run();
        this.resetColors();
    }

    private void gotoTransactions() {
        if (this.clickedTransaction == null)
            return;
        this.clickedTransaction.run();
        this.resetColors();
    }

    private void gotoModifiedRecord() {
        if (this.clickedModifyRecord == null)
            return;
        this.clickedModifyRecord.run();
        this.resetColors();
    }
}
