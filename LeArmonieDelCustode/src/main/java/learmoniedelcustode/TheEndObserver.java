package learmoniedelcustode;

import Other.GameDescription;
import Other.GameObserver;
import Parser.ParserOutput;
import Tipi.CommandType;


public class TheEndObserver implements GameObserver {

    @Override
    public String update(GameDescription description, ParserOutput parserOutput) {
        String msg = "";
        if (parserOutput.getCommand().getType() == CommandType.THE_END) {
            description.getChrono().stop();
            parserOutput.getInterfacciaGioco().endGame();
        }

        return msg;
    }

}
