package statemachine;

import java.util.Arrays;
import java.util.EnumSet;

public enum MyTransition implements Transition<MyState> {
    INITIAL_TO_MIDDLE(from(MyState.INITIAL), to(MyState.MIDDLE)),
    ANY_TO_END(from(MyState.INITIAL, MyState.MIDDLE), to(MyState.END)),
    RESTART(from(MyState.END), to(MyState.INITIAL));

    private final EnumSet<MyState> source;
    private final MyState target;

    MyTransition(final EnumSet<MyState> source, final MyState target) {
        this.target = target;
        this.source = source;
    }

    private static EnumSet<MyState> from(final MyState... from) {
        return EnumSet.copyOf(Arrays.asList(from));
    }

    private static MyState to(final MyState state) {
        return state;
    }

    @Override
    public MyState goFrom(final MyState currentState) {
        return source.contains(currentState) ? target : currentState;
    }
}
