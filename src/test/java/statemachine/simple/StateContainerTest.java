package statemachine.simple;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import statemachine.Transition;

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
        TO_MIDDLE(MyState.MIDDLE, MyState.INITIAL),
        TO_END(MyState.END, MyState.INITIAL, MyState.MIDDLE),
        RESTART(MyState.INITIAL, MyState.END);

        private final EnumSet<MyState> from;
        private final MyState to;

        MyTransition(final MyState to, final MyState... from) {
            this.to = to;
            this.from = EnumSet.copyOf(Arrays.asList(from));
        }

        @Override
        public MyState goFrom(final MyState myState) {
            return from.contains(myState) ? to : myState;
        }
    }

    @Before
    public void setUp() throws Exception {
        stateContainer = new StateContainer<>(MyState.INITIAL, this::onTransition);
    }

    private void onTransition(final MyTransition t, final MyState s) {
        states.add(s);
        switch (t) {
            case RESTART:
                System.out.println("Back to the beginning!");
                break;
            case TO_END:
                System.out.println("All over now!");
                break;
            case TO_MIDDLE:
                System.out.println("Stuck in the middle again!");
                break;
        }
    }

    @Test
    public void testName() throws Exception {
        Assert.assertThat(stateContainer.apply(MyTransition.TO_END), is(true));
        Assert.assertThat(stateContainer.apply(MyTransition.TO_MIDDLE), is(false));
        Assert.assertThat(stateContainer.apply(MyTransition.TO_END), is(false));
        Assert.assertThat(stateContainer.apply(MyTransition.RESTART), is(true));
        Assert.assertThat(stateContainer.apply(MyTransition.TO_MIDDLE), is(true));
        Assert.assertThat(stateContainer.apply(MyTransition.TO_END), is(true));

        Assert.assertThat(states, is(Arrays.asList(MyState.END, MyState.INITIAL, MyState.MIDDLE, MyState.END)));
    }
}