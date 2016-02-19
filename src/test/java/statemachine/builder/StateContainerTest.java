package statemachine.builder;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import statemachine.MyState;
import statemachine.MyTransition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;

public class StateContainerTest {

    private StateContainer<MyState, MyTransition> stateContainer;
    private List<MyState> states = new ArrayList<>();

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
        System.out.println("Back to the beginning!");
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