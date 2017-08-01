package org.openfact.batch;

import javax.batch.api.AbstractBatchlet;
import javax.inject.Named;

@Named
public class MyBatchlet2 extends AbstractBatchlet {

    @Override
    public String process() {
        System.out.println("Running inside a batchlet 2");

        return "COMPLETED";
    }

}
