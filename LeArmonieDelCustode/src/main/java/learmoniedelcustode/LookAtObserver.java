package learmoniedelcustode;

import Other.GameDescription;
import Other.GameObserver;
import Parser.ParserOutput;
import Tipi.CommandType;

public class LookAtObserver implements GameObserver {

    @Override
    public String update(GameDescription description, ParserOutput parserOutput) {
        String msg = "";
        if (parserOutput.getCommand().getType() == CommandType.LOOK_AT) {
            msg = description.getCurrentCasella().getDescrizione();
        }
        return msg;
    }

}
