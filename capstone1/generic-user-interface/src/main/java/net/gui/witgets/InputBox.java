package net.gui.witgets;

import net.gui.Context;
import net.gui.primitives.Box;
import net.gui.primitives.LayoutConstraint;
import net.gui.primitives.Overflow;
import net.gui.primitives.Text;
import net.gui.primitives.WrapMode;
import net.gui.primitives.dimension.Auto;

public class InputBox {
    public static int create(Context ctx) {
        int text = ctx.insert(new Text("", WrapMode.NoWrap, null));
        int box = ctx.insert(new Box(
            text, Overflow.Hidden, new LayoutConstraint(new Auto(), new Auto()), null, null));
        InputState state = new InputState("", 0);

        ctx.updateState(box, state);
        return box;
    }

    public static void handleInput(Context ctx, int id, char c) {
        InputState state = (InputState) ctx.getState(id);

        String left = state.buffer().substring(0, state.cursor());
        String right = state.buffer().substring(state.cursor());

        InputState newState = new InputState(left + c + right, state.cursor() + 1);

        ctx.updateState(id, newState);

        InputBox.reconstruct(ctx, id, state);
    }

    public static void handleBackspace(Context ctx, int id) {
        throw new UnsupportedOperationException("Not implemented!");
    }

    private static void reconstruct(Context ctx, int id, InputState state) {
        int text = ctx.insert(new Text(state.buffer(), WrapMode.NoWrap, null));
        Box current = (Box) ctx.get(id);

        ctx.update(id, new Box(text, current.overflow(), current.constraints(), null, null));
    }
}
