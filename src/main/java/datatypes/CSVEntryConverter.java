/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package datatypes;

import java.io.Writer;

/**
 *
 * @author epcpu
 */
public interface CSVEntryConverter<E> {
    public void convertEntry(Writer out);
}
