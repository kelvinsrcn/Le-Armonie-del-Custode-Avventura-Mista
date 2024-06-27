/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package Other;

import Parser.ParserOutput;

/**
 *
 * @author Alessandro Pellegrino
 * @author Kevin Saracino
 */
public interface GameObserver {

    /**
     *
     * @param description
     * @param parserOutput
     * @return
     */
    public String update(GameDescription description, ParserOutput parserOutput);

}
