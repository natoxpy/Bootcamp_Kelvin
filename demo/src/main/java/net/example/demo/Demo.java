package net.example.demo;

import java.util.ArrayList;
import java.util.List;
import net.gui.Context;
import net.gui.commands.Commander;
import net.gui.commands.DrawCommand;
import net.gui.primitives.Box;
import net.gui.primitives.LayoutConstraint;
import net.gui.primitives.Text;
import net.gui.primitives.WrapMode;
import net.gui.primitives.dimension.Auto;
import net.gui.primitives.dimension.Fixed;

public record Demo() {
    public static void main(String[] args) {
        Context ctx = new Context();
        List<DrawCommand> commandList = new ArrayList<>();

        int textId = ctx.insert(new Text("Hello, World!", WrapMode.NoWrap));
        int boxId = ctx.insert(new Box(textId, new LayoutConstraint(new Auto(), new Fixed(10))));

		int width = 5;
		int height = 5;

        Commander.compileCommands(ctx, boxId, 0, 0, width, height, commandList);
        System.out.printf("window { %s, %s }\n", width, height);
        System.out.println(ctx.getPrettyTree(boxId));
        System.out.println(commandList);
    }
}
