/**
 * Copyright (c) 2021, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.openloadflow.ac;

import com.powsybl.commons.datasource.ResourceDataSource;
import com.powsybl.commons.datasource.ResourceSet;
import com.powsybl.ieeecdf.converter.IeeeCdfNetworkFactory;
import com.powsybl.iidm.import_.Importers;
import com.powsybl.iidm.network.Line;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.network.test.EurostagTutorialExample1Factory;
import com.powsybl.math.matrix.DenseMatrixFactory;
import com.powsybl.openloadflow.network.FirstSlackBusSelector;
import com.powsybl.openloadflow.network.LfBus;
import com.powsybl.openloadflow.network.LfNetwork;
import com.powsybl.openloadflow.network.impl.LfNetworkLoaderImpl;
import com.powsybl.openloadflow.network.util.VoltageInitializer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author Geoffroy Jamgotchian <geoffroy.jamgotchian at rte-france.com>
 */
public class VoltageMagnitudeInitializerTest {

    public static void assertBusVoltage(LfNetwork network, VoltageInitializer initializer, String busId, double vRef, double angleRef) {
        LfBus bus = network.getBusById(busId);
        double v = initializer.getMagnitude(bus);
        double angle = initializer.getAngle(bus);
        assertNotNull(bus);
        assertEquals(vRef, v, 1E-6d);
        assertEquals(angleRef, angle, 1E-2d);
    }

    @Test
    void testEsgTuto1() {
        Network network = EurostagTutorialExample1Factory.create();
        LfNetwork lfNetwork = LfNetwork.load(network, new LfNetworkLoaderImpl(), new FirstSlackBusSelector()).get(0);
        VoltageMagnitudeInitializer initializer = new VoltageMagnitudeInitializer(new DenseMatrixFactory());
        initializer.prepare(lfNetwork);
        assertBusVoltage(lfNetwork, initializer, "VLGEN_0", 1.020833, 0);
        assertBusVoltage(lfNetwork, initializer, "VLHV1_0", 1.074561, 0);
        assertBusVoltage(lfNetwork, initializer, "VLHV2_0", 1.074561, 0);
        assertBusVoltage(lfNetwork, initializer, "VLLOAD_0", 1.075994, 0);
    }

    @Test
    void testIeee14() {
        Network network = IeeeCdfNetworkFactory.create14();
        LfNetwork lfNetwork = LfNetwork.load(network, new LfNetworkLoaderImpl(), new FirstSlackBusSelector()).get(0);
        VoltageMagnitudeInitializer initializer = new VoltageMagnitudeInitializer(new DenseMatrixFactory());
        initializer.prepare(lfNetwork);
        assertBusVoltage(lfNetwork, initializer, "VL1_0", 1.06, 0);
        assertBusVoltage(lfNetwork, initializer, "VL2_0", 1.045, 0);
        assertBusVoltage(lfNetwork, initializer, "VL3_0", 1.01, 0);
        assertBusVoltage(lfNetwork, initializer, "VL4_0", 1.035155, 0);
        assertBusVoltage(lfNetwork, initializer, "VL5_0", 1.035618, 0);
        assertBusVoltage(lfNetwork, initializer, "VL6_0", 1.07, 0);
        assertBusVoltage(lfNetwork, initializer, "VL7_0", 1.074078, 0);
        assertBusVoltage(lfNetwork, initializer, "VL8_0", 1.09, 0);
        assertBusVoltage(lfNetwork, initializer, "VL9_0", 1.072362, 0);
        assertBusVoltage(lfNetwork, initializer, "VL10_0", 1.071942, 0);
        assertBusVoltage(lfNetwork, initializer, "VL11_0", 1.070988, 0);
        assertBusVoltage(lfNetwork, initializer, "VL12_0", 1.070186, 0);
        assertBusVoltage(lfNetwork, initializer, "VL13_0", 1.070332, 0);
        assertBusVoltage(lfNetwork, initializer, "VL14_0", 1.071474, 0);
    }

    @Test
    void testZeroImpedanceBranch() {
        Network network = IeeeCdfNetworkFactory.create14();
        network.getLine("L9-14-1").setX(0);
        LfNetwork lfNetwork = LfNetwork.load(network, new LfNetworkLoaderImpl(), new FirstSlackBusSelector()).get(0);
        VoltageMagnitudeInitializer initializer = new VoltageMagnitudeInitializer(new DenseMatrixFactory());
        initializer.prepare(lfNetwork);
        assertBusVoltage(lfNetwork, initializer, "VL1_0", 1.06, 0);
        assertBusVoltage(lfNetwork, initializer, "VL2_0", 1.045, 0);
        assertBusVoltage(lfNetwork, initializer, "VL3_0", 1.01, 0);
        assertBusVoltage(lfNetwork, initializer, "VL4_0", 1.035126, 0);
        assertBusVoltage(lfNetwork, initializer, "VL5_0", 1.0356, 0);
        assertBusVoltage(lfNetwork, initializer, "VL6_0", 1.07, 0);
        assertBusVoltage(lfNetwork, initializer, "VL7_0", 1.073983, 0);
        assertBusVoltage(lfNetwork, initializer, "VL8_0", 1.09, 0);
        assertBusVoltage(lfNetwork, initializer, "VL9_0", 1.072171, 0); // equals VL14_0
        assertBusVoltage(lfNetwork, initializer, "VL10_0", 1.071785, 0);
        assertBusVoltage(lfNetwork, initializer, "VL11_0", 1.070908, 0);
        assertBusVoltage(lfNetwork, initializer, "VL12_0", 1.070274, 0);
        assertBusVoltage(lfNetwork, initializer, "VL13_0", 1.070489, 0);
        assertBusVoltage(lfNetwork, initializer, "VL14_0", 1.072171, 0); // equals VL9_0
    }

    @Test
    void testZeroImpedanceBranchConnectedToPvBus() {
        Network network = IeeeCdfNetworkFactory.create14();
        network.getLine("L6-11-1").setX(0);
        LfNetwork lfNetwork = LfNetwork.load(network, new LfNetworkLoaderImpl(), new FirstSlackBusSelector()).get(0);
        VoltageMagnitudeInitializer initializer = new VoltageMagnitudeInitializer(new DenseMatrixFactory());
        initializer.prepare(lfNetwork);
        assertBusVoltage(lfNetwork, initializer, "VL1_0", 1.06, 0);
        assertBusVoltage(lfNetwork, initializer, "VL2_0", 1.045, 0);
        assertBusVoltage(lfNetwork, initializer, "VL3_0", 1.01, 0);
        assertBusVoltage(lfNetwork, initializer, "VL4_0", 1.035106, 0);
        assertBusVoltage(lfNetwork, initializer, "VL5_0", 1.035587, 0);
        assertBusVoltage(lfNetwork, initializer, "VL6_0", 1.07, 0); // equals target
        assertBusVoltage(lfNetwork, initializer, "VL7_0", 1.073916, 0);
        assertBusVoltage(lfNetwork, initializer, "VL8_0", 1.09, 0);
        assertBusVoltage(lfNetwork, initializer, "VL9_0", 1.072038, 0);
        assertBusVoltage(lfNetwork, initializer, "VL10_0", 1.071415, 0);
        assertBusVoltage(lfNetwork, initializer, "VL11_0", 1.07, 0); // equals VL6_0
        assertBusVoltage(lfNetwork, initializer, "VL12_0", 1.070161, 0);
        assertBusVoltage(lfNetwork, initializer, "VL13_0", 1.070286, 0);
        assertBusVoltage(lfNetwork, initializer, "VL14_0", 1.071272, 0);
    }

    @Test
    void testParallelBranch() {
        Network network = IeeeCdfNetworkFactory.create14();
        Line l9101 = network.getLine("L9-10-1");
        double newX = l9101.getX() * 2; // so that result is the same as initial case when doubling line
        l9101.setX(newX);
        network.newLine()
                .setId("L9-10-2")
                .setVoltageLevel1("VL9")
                .setConnectableBus1("B9")
                .setBus1("B9")
                .setVoltageLevel2("VL10")
                .setConnectableBus2("B10")
                .setBus2("B10")
                .setR(0)
                .setX(newX)
                .setG1(0)
                .setG2(0)
                .setB1(0)
                .setB2(0)
                .add();
        LfNetwork lfNetwork = LfNetwork.load(network, new LfNetworkLoaderImpl(), new FirstSlackBusSelector()).get(0);
        VoltageMagnitudeInitializer initializer = new VoltageMagnitudeInitializer(new DenseMatrixFactory());
        initializer.prepare(lfNetwork);
        assertBusVoltage(lfNetwork, initializer, "VL1_0", 1.06, 0);
        assertBusVoltage(lfNetwork, initializer, "VL2_0", 1.045, 0);
        assertBusVoltage(lfNetwork, initializer, "VL3_0", 1.01, 0);
        assertBusVoltage(lfNetwork, initializer, "VL4_0", 1.035155, 0);
        assertBusVoltage(lfNetwork, initializer, "VL5_0", 1.035618, 0);
        assertBusVoltage(lfNetwork, initializer, "VL6_0", 1.07, 0);
        assertBusVoltage(lfNetwork, initializer, "VL7_0", 1.074078, 0);
        assertBusVoltage(lfNetwork, initializer, "VL8_0", 1.09, 0);
        assertBusVoltage(lfNetwork, initializer, "VL9_0", 1.072362, 0);
        assertBusVoltage(lfNetwork, initializer, "VL10_0", 1.071942, 0);
        assertBusVoltage(lfNetwork, initializer, "VL11_0", 1.070988, 0);
        assertBusVoltage(lfNetwork, initializer, "VL12_0", 1.070186, 0);
        assertBusVoltage(lfNetwork, initializer, "VL13_0", 1.070332, 0);
        assertBusVoltage(lfNetwork, initializer, "VL14_0", 1.071474, 0);
    }

    @Test
    void testZeroImpedanceLoop() {
        Network network = Importers.importData("XIIDM", new ResourceDataSource("init_v_zero_imp_loop", new ResourceSet("/", "init_v_zero_imp_loop.xiidm")), null);
        LfNetwork lfNetwork = LfNetwork.load(network, new LfNetworkLoaderImpl(), new FirstSlackBusSelector()).get(0);
        VoltageMagnitudeInitializer initializer = new VoltageMagnitudeInitializer(new DenseMatrixFactory());
        initializer.prepare(lfNetwork);
        assertBusVoltage(lfNetwork, initializer, "B_0", 0.982318, 0);
        assertBusVoltage(lfNetwork, initializer, "D_0", 0.982318, 0);
        assertBusVoltage(lfNetwork, initializer, "F_0", 0.982315, 0);
        assertBusVoltage(lfNetwork, initializer, "H_0", 0.975, 0);
        assertBusVoltage(lfNetwork, initializer, "K_0", 0.953125, 0);
        assertBusVoltage(lfNetwork, initializer, "N_0", 0.97817, 0);
        assertBusVoltage(lfNetwork, initializer, "P_0", 0.982318, 0);
        assertBusVoltage(lfNetwork, initializer, "R_0", 0.982318, 0);
    }
}