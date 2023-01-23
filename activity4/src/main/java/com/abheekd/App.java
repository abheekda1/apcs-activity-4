package com.abheekd;

import java.io.IOException;
import java.math.*;

import com.abheekd.util.WebSrc;

public class App {
    public static void main(String[] args) throws IOException {
        final String url = "https://w1.weather.gov/xml/current_obs/KMSP.xml";
        WebSrc webSrc = new WebSrc(url);
        webSrc.fetch();
        try {
            Observation asdf = (Observation)webSrc.toObjWithKey("com.abheekd.Observation", "current_observation", "weather", "temp_f", "wind_degrees");
            System.out.println(asdf);
        } catch (Exception e) {
            System.out.println(e);
        }
        System.out.println(webSrc.getData());
    }
}

class Observation {
    BigDecimal temp; // in fahrenheit
    Integer windDir; // in degrees
    String description;

    Observation(String description, BigDecimal temp, Integer windDir) {
        this.description = description;
        this.temp = temp;
        this.windDir = windDir;
    }

    /* determine if the temperature of this observation is colder than 'that's */
    public boolean colderThan(Observation that) {
        return this.temp.doubleValue() < that.temp.floatValue();
    }

    /* produce a string describing this observation */
    public String toString() {
        return (temp + " degrees; " + description + " (wind: " + windDir + " degrees)");
    }
}