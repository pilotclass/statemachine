package statemachine.builder;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;

public class StateContainerTest {

    private StateContainer<MyState, MyTransition> stateContainer;
    private List<MyState> states = new ArrayList<>();

    enum MyState {
        INITIAL, MIDDLE, END
    }

    enum MyTransition implements Transition<MyState> {
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

    @Before
    public void setUp() throws Exception {
        stateContainer = StateContainer
                .over(MyState.class, MyTransition.class)
                .startingAt(MyState.INITIAL)
                .when(MyTransition.RESTART).then(this::onRestart)
                .when(MyTransition.ANY_TO_END).then(this::onEnd)
                .when(MyTransition.INITIAL_TO_MIDDLE).then(this::onMiddle)
                .build();
    }

    private void onRestart() {
        states.add(MyState.INITIAL);
        System.out.println("Back target the beginning!");
    }

    private void onMiddle() {
        states.add(MyState.MIDDLE);
        System.out.println("Stuck in the middle again!");
    }

    private void onEnd() {
        states.add(MyState.END);
        System.out.println("All over now!");
    }

    @Test
    public void testName() throws Exception {
        Assert.assertThat(stateContainer.apply(MyTransition.ANY_TO_END), is(true));
        Assert.assertThat(stateContainer.apply(MyTransition.INITIAL_TO_MIDDLE), is(false));
        Assert.assertThat(stateContainer.apply(MyTransition.ANY_TO_END), is(false));
        Assert.assertThat(stateContainer.apply(MyTransition.RESTART), is(true));
        Assert.assertThat(stateContainer.apply(MyTransition.INITIAL_TO_MIDDLE), is(true));
        Assert.assertThat(stateContainer.apply(MyTransition.ANY_TO_END), is(true));

        Assert.assertThat(states, is(Arrays.asList(MyState.END, MyState.INITIAL, MyState.MIDDLE, MyState.END)));
    }
}