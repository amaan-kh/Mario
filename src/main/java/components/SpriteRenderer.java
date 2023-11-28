package components;

import jade.Component;

import java.sql.SQLOutput;

public class SpriteRenderer extends Component {
    private boolean firstTime = false;
    @Override
    public void start() {
        System.out.println("I am startign");
    }
    @Override
    public void update(float dt) {
        if (!firstTime) {
            System.out.println("I am updating");
            firstTime = true;
        }
    }

}
