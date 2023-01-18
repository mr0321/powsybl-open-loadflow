/**
 * Copyright (c) 2022, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.openloadflow.ac.nr;

import com.powsybl.openloadflow.ac.equations.AcEquationType;
import com.powsybl.openloadflow.ac.equations.AcVariableType;
import com.powsybl.openloadflow.equations.EquationSystem;
import com.powsybl.openloadflow.equations.EquationVector;
import com.powsybl.openloadflow.equations.StateVector;
import com.powsybl.openloadflow.equations.TargetVector;

import java.util.Objects;

/**
 * State vector scaling.
 *
 * @author Geoffroy Jamgotchian <geoffroy.jamgotchian at rte-france.com>
 */
public interface StateVectorScaling {

    static StateVectorScaling fromMode(StateVectorScalingMode mode, NewtonRaphsonStoppingCriteria.TestResult initialTestResult) {
        Objects.requireNonNull(mode);
        Objects.requireNonNull(initialTestResult);
        switch (mode) {
            case NONE:
                return new NoneStateVectorScaling();
            case LINE_SEARCH:
                return new LineSearchStateVectorScaling(initialTestResult);
            case MAX_VOLTAGE_CHANGE:
                return new MaxVoltageChangeStateVectorScaling();
            default:
                throw new IllegalStateException("Unknown state vector scaling mode: " + mode);
        }
    }

    StateVectorScalingMode getMode();

    /**
     * Apply scaling to state vector variation before equation mismatches calculation.
     */
    void apply(double[] dx, EquationSystem<AcVariableType, AcEquationType> equationSystem);

    /**
     * Apply scaling to state vector after equation mismatches and norm have been calculated.
     */
    NewtonRaphsonStoppingCriteria.TestResult applyAfter(StateVector stateVector,
                                                        EquationVector<AcVariableType, AcEquationType> equationVector,
                                                        TargetVector<AcVariableType, AcEquationType> targetVector,
                                                        NewtonRaphsonStoppingCriteria stoppingCriteria,
                                                        NewtonRaphsonStoppingCriteria.TestResult testResult);
}