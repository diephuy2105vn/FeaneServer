package com.springboot.server.payload.constants;

import jakarta.persistence.Embeddable;

@Embeddable
public class Size {
    private String letterSize;
    private int numberSize;

    public Size() {
    }
    public Size(String size) {
        try {
            int numberSize = Integer.parseInt(size);
            if(numberSize > 24 && numberSize < 34) {
                this.numberSize = numberSize;
            }
        } catch (NumberFormatException e) {
            if(size.equals("S") || size.equals("M") || size.equals("L") || size.equals("XL") || size.equals("XXL")) {
                this.letterSize = size;
            }
        }
    }
}