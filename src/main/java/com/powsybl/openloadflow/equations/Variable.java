/**
 * Copyright (c) 2019, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.openloadflow.equations;

import java.io.IOException;
import java.io.Writer;
import java.util.Objects;

/**
 * @author Geoffroy Jamgotchian <geoffroy.jamgotchian at rte-france.com>
 */
public class Variable<T extends Enum<T> & Quantity> implements Comparable<Variable<T>> {

    private final int elementNum;

    private final T type;

    private int row = -1;

    Variable(int elementNum, T type) {
        this.elementNum = elementNum;
        this.type = Objects.requireNonNull(type);
    }

    public int getElementNum() {
        return elementNum;
    }

    public T getType() {
        return type;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    @Override
    public int hashCode() {
        return elementNum + type.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof Variable) {
            return compareTo((Variable) obj) == 0;
        }
        return false;
    }

    @Override
    public int compareTo(Variable<T> o) {
        if (o == this) {
            return 0;
        }
        int c = elementNum - o.elementNum;
        if (c == 0) {
            c = type.ordinal() - o.type.ordinal();
        }
        return c;
    }

    public void write(Writer writer) throws IOException {
        writer.write(type.getSymbol());
        writer.write(Integer.toString(elementNum));
    }

    @Override
    public String toString() {
        return "Variable(elementNum=" + elementNum + ", type=" + type + ", row=" + row + ")";
    }
}
