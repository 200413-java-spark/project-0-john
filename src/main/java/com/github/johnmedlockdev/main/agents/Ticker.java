package com.github.johnmedlockdev.main.agents;

import com.github.johnmedlockdev.main.logs.Logger;

import java.io.*;

public class Ticker extends User {
    private final FileStructure fileSymbol;

    public Ticker(String symbol, String price) {
        super(symbol, price);
        fileSymbol = new FileStructure(symbol);
    }

    public void createInput() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileSymbol.getAbsolutePathStr(), true))) {
            if (!fileSymbol.getPath().exists()) {
                OutputStream outputStream = new FileOutputStream(fileSymbol.getPath());
            }
            String priceInput = Double.toString(this.price);
            String newUserInput = super.getSymbol() + "," + priceInput;
            bw.write(newUserInput);
            bw.newLine();
            bw.close();
        } catch (IOException e) {
            Logger logger = new Logger();
            Logger.setup();
            logger.Log(1, e.toString());
        }
    }


}

