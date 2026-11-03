package net.example.demo;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import net.gui.Context;
import net.gui.commands.Commander;
import net.gui.commands.DrawBoxCommand;
import net.gui.commands.DrawCommand;
import net.gui.commands.DrawTextCommand;
import net.gui.primitives.Box;
import net.gui.primitives.Direction;
import net.gui.primitives.Flex;
import net.gui.primitives.FlexChild;
import net.gui.primitives.JustifySelf;
import net.gui.primitives.LayoutConstraint;
import net.gui.primitives.Text;
import net.gui.primitives.WrapMode;
import net.gui.primitives.dimension.Auto;
import net.gui.primitives.dimension.Fill;
import net.gui.primitives.dimension.Fixed;
import net.renderer.text.Renderer;

public record Demo() {
    public static void render(Context ctx, Renderer render, int root, int width, int height) {
        List<DrawCommand> commandList = new ArrayList<>();
        Commander.compileCommands(ctx, root, width, height, commandList);

        render.clear(' ');

        for (var command : commandList) {
            System.out.println(command);

            if (command instanceof DrawTextCommand cmd)
                render.drawText(cmd.x(), cmd.y(), cmd.content());
            else if (command instanceof DrawBoxCommand cmd)
                render.drawBox(cmd.x(), cmd.y(), cmd.width(), cmd.height(), '#', ' ');
        }

        render.write(new OutputStreamWriter(System.out));
    }

    public static void main(String[] args) {
        Renderer renderer = new Renderer(200, 20);
        Context ctx = new Context();

        int textId = ctx.insert(new Text("Lorem Ipsum", WrapMode.NoWrap));
        int box1Id = ctx.insert(new Box(textId, new LayoutConstraint(new Fill(), new Auto())));
        int box2Id = ctx.insert(new Box(null, new LayoutConstraint(new Auto(), new Auto())));

        int flex1Id = ctx.insert(new Flex(Direction.Row, 0,
            List.of(new FlexChild(box1Id, 1, JustifySelf.Start),
                new FlexChild(box2Id, 1, JustifySelf.Start))));

        int root = flex1Id;

		System.out.println(ctx.get(flex1Id).measure(ctx, Integer.MAX_VALUE, Integer.MAX_VALUE));

        Demo.render(ctx, renderer, root, renderer.width(), renderer.height());

		System.out.println();

        for (int i = 0; i < renderer.width(); i++) {
            if (i % 5 != 0)
                System.out.print("^");
            else
                System.out.print("v");
        }

        System.out.println();
    }
}
